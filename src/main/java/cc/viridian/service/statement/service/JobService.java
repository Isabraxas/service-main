package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.AccountsRegistered;
import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.StatementJobModel;
import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.payload.ListAccountsResponse;
import cc.viridian.service.statement.payload.ListJobsResponse;
import cc.viridian.service.statement.payload.RegisterJobPost;
import cc.viridian.service.statement.persistence.StatementJob;
import cc.viridian.service.statement.repository.StatementJobProducer;
import cc.viridian.service.statement.repository.StatementJobRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Service
public class JobService {

    @Autowired
    StatementService statementService;

    @Autowired
    StatementJobProducer statementJobProducer;

    private StatementJobRepository statementJobRepository;

    @Autowired
    public JobService(StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
    }

    public ListJobsResponse listJobs(final Integer start, final Integer length) {
        return statementJobRepository.listJobs(start, length);
    }

    public StatementJobModel registerSingleJob(final RegisterJobPost body) {

        StatementJob statementJob = statementJobRepository.registerSingleJob(body);

        //creates a job template using the business model
        JobTemplate jobTemplate = new JobTemplate(statementJob);

        //send message to kafka
        statementJobProducer.send("" + jobTemplate.getId(), jobTemplate);

        return new StatementJobModel(statementJob);
    }

    public StatementJobModel updateJob(final UpdateJobTemplate data) {
        //read the statementJob from database
        StatementJob statementJob = statementJobRepository.findById(data.getId());

        if (data.getAdapterType().equalsIgnoreCase("corebank")) {
            switch (data.getErrorCode()) {
                case "":
                case "SUCCESS":
                    statementJob = inProgressUpdateJob(statementJob, data);
                    break;
                case "invalid-account":
                    statementJob = completeWithErrorUpdateJob(statementJob, data);
                    break;
                case "invalid-adapter":
                    statementJob = completeWithErrorUpdateJob(statementJob, data);
                    break;
                case "network-error":
                    statementJob = retryLaterUpdateJob(statementJob, data);
                    break;
                case "database-error":
                    statementJob = retryLaterUpdateJob(statementJob, data);
                    break;
            }
        }

        if (data.getAdapterType().equalsIgnoreCase("formatter")) {
            switch (data.getErrorCode()) {
                case "":
                case "SUCCESS":
                    statementJob = inProgressUpdateJob(statementJob, data);
                    break;
            }
        }

        if (data.getAdapterType().equalsIgnoreCase("sender")) {
            switch (data.getErrorCode()) {
                case "SUCCESS":
                    statementJob = completeSenderUpdateJob(statementJob, data);
                    break;
            }
        }

        //now, update the record in the database
        statementJobRepository.updateStatementJob(statementJob);

        return new StatementJobModel(statementJob);
    }

    /**
     * Obtains a list of Accounts who match the "MONTHLY" criteria and then registers and process the list to kafka
     * @return a Map Object containing the dates from and to, and the number of records processed
     */
    public Map<String, Object> processMonthlyAccounts() {
        ListAccountsResponse listAccountsResponse = statementService.listAccountsMonthly();
        int records = listAccountsResponse.getData().size();
        LocalDate nowDate = LocalDate.now();
        for (AccountsRegistered acc : listAccountsResponse.getData()) {
            RegisterJobPost body = new RegisterJobPost();
            body.setAccount(acc.getAccountCode());
            body.setCurrency(acc.getAccountCurrency());
            body.setType(acc.getAccountType());
            body.setCustomerCode(acc.getCustomerCode());
            body.setRecipient(acc.getRecipient());
            body.setFrequency(acc.getFrequency());
            body.setDateFrom(
                LocalDate.of(nowDate.getYear(), calculatePreviousMonth(nowDate), 1));    // primer dia del mes
            body.setDateTo(
                LocalDate.of(nowDate.getYear(), calculatePreviousMonth(nowDate), calculateLastDayOfMonth(nowDate)));
            body.setCorebankAdapter(acc.getCorebankAdapter());
            body.setFormatAdapter(acc.getFormatAdapter());
            body.setSendAdapter(acc.getSendAdapter());
            StatementJob statementJob = statementJobRepository.registerSingleJob(body);

            JobTemplate jobTemplate = new JobTemplate(statementJob);
            statementJobProducer.send("" + jobTemplate.getId(), jobTemplate);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("recordsProcessed", records);
        res.put("dateFrom", LocalDate.of(nowDate.getYear(), calculatePreviousMonth(nowDate), 1));
        res.put("dateTo",
                LocalDate.of(nowDate.getYear(), calculatePreviousMonth(nowDate), calculateLastDayOfMonth(nowDate)));
        return res;
    }

    //in progress
    public StatementJob inProgressUpdateJob(final StatementJob statementJob, final UpdateJobTemplate data) {
        String adapterType = data.getAdapterType();
        statementJob.setStatus("IN PROGRESS");
        if (adapterType.equalsIgnoreCase("corebank")) {
            if (statementJob.getTimeStartJob() == null) {
                statementJob.setTimeStartJob(data.getLocalDateTime());
            }
            statementJob.setCorebankErrorCode(data.getErrorCode());
            statementJob.setCorebankErrorDesc(data.getErrorDesc());
            return statementJob;
        }

        if (adapterType.equalsIgnoreCase("formatter")) {
            if (statementJob.getTimeStartJob() == null) {
                statementJob.setTimeStartJob(data.getLocalDateTime());
            }
            statementJob.setFormatterErrorCode(data.getErrorCode());
            statementJob.setFormatterErrorDesc(data.getErrorDesc());
            return statementJob;
        }

        if (adapterType.equalsIgnoreCase("sender")) {
            if (statementJob.getTimeStartJob() == null) {
                statementJob.setTimeStartJob(data.getLocalDateTime());
            }
            statementJob.setSenderErrorCode(data.getErrorCode());
            statementJob.setSenderErrorDesc(data.getErrorDesc());
            return statementJob;
        }
        return statementJob;
    }

    //completed
    public StatementJob completeUpdateJob(final StatementJob statementJob, final UpdateJobTemplate data) {
        return statementJob;
    }

    //job processed with error but should retry
    public StatementJob retryLaterUpdateJob(final StatementJob statementJob, final UpdateJobTemplate data) {
        statementJob.setStatus("WITH ERROR");
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(data.getLocalDateTime());
        }
        statementJob.setCorebankErrorCode(data.getErrorCode());
        statementJob.setCorebankErrorDesc(data.getErrorDesc());
        statementJob.setCorebankRetries(statementJob.getCorebankRetries() + 1);
        return statementJob;
    }

    //job processed with error but shouldn't retry because state is final and unrecoverable
    public StatementJob completeWithErrorUpdateJob(final StatementJob statementJob, final UpdateJobTemplate data) {
        statementJob.setStatus("CLOSE ERROR");
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(data.getLocalDateTime());
        }
        if (statementJob.getTimeEndJob() == null) {
            statementJob.setTimeEndJob(data.getLocalDateTime());
        }
        statementJob.setCorebankErrorCode(data.getErrorCode());
        statementJob.setCorebankErrorDesc(data.getErrorDesc());
        return statementJob;
    }

    //in progress
    public StatementJob completeSenderUpdateJob(final StatementJob statementJob, final UpdateJobTemplate data) {
        statementJob.setStatus("CLOSE");
        if (statementJob.getTimeEndJob() == null) {
            statementJob.setTimeEndJob(data.getLocalDateTime());
        }
        statementJob.setSenderErrorCode("ok");
        statementJob.setSenderErrorDesc(data.getErrorDesc());
        return statementJob;
    }

    // calculates the previous month number of the provided date
    private int calculatePreviousMonth(final LocalDate localDate) {
        return (localDate.getMonthValue() - 1);
    }

    // calculates the last day of the month of the provided date
    private int calculateLastDayOfMonth(final LocalDate date) {
        YearMonth month = YearMonth.of(date.getYear(), date.getMonth());
        return month.atEndOfMonth().getDayOfMonth();
    }

    public Map<String, Object> processTruncate() {
        Map<String, Object> response = new HashMap<>();

        statementJobRepository.truncateJobs();
        return response;
    }
}
