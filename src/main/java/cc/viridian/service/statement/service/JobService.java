package cc.viridian.service.statement.service;

import cc.viridian.provider.payload.ResponseErrorCode;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Service
public class JobService {

    private StatementService statementService;

    private StatementJobProducer statementJobProducer;

    private StatementJobRepository statementJobRepository;

    private enum StatusCode {
        NEW,
        IN_PROGRESS,
        WITH_ERROR,
        SLEEPING,
        QUEUED,
        COMPLETED
    };

    private int[] retryScale = {1, 2, 3};

    @Autowired
    public JobService(StatementService statementService,
                       StatementJobProducer statementJobProducer,
                       StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
        this.statementService = statementService;
        this.statementJobProducer = statementJobProducer;
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

    public StatementJob updateJobCorebank(final String errorCode, final UpdateJobTemplate updateJob) {
        //read the statementJob from database
        StatementJob statementJob = statementJobRepository.findById(updateJob.getId());

        //update startJob time if it is null
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(updateJob.getLocalDateTime());
        }

        statementJob.setCorebankErrorCode(errorCode);
        statementJob.setCorebankErrorDesc(updateJob.getErrorDesc());

        if (ResponseErrorCode.SUCCESS.name().equals(errorCode)) {
            statementJob.setStatus(StatusCode.IN_PROGRESS.name());
        } else {
            statementJob.setStatus(StatusCode.WITH_ERROR.name());
            if (statementJob.getTimeEndJob() == null) {
                statementJob.setTimeEndJob(updateJob.getLocalDateTime());
            }

            if (updateJob.getShouldTryAgain()) {
                statementJob.setCorebankRetries(statementJob.getCorebankRetries() + 1);

                LocalDateTime minutesToWait = calculateWhenToWakeUp(statementJob.getCorebankRetries());

                if (minutesToWait != null) {
                    statementJob.setStatus(StatusCode.SLEEPING.name());
                    statementJob.setCorebankTryAgainAt(minutesToWait);
                } else {
                    statementJob.setStatus(StatusCode.WITH_ERROR.name());
                }
            }
        }

        //now, update the record in the database
        statementJobRepository.updateStatementJob(statementJob);

        return statementJob;
    }

    public StatementJob updateJobFormatter(final String errorCode, final UpdateJobTemplate updateJob) {
        //read the statementJob from database
        StatementJob statementJob = statementJobRepository.findById(updateJob.getId());

        //update startJob time if it is null
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(updateJob.getLocalDateTime());
        }

        statementJob.setFormatterErrorCode(errorCode);
        statementJob.setFormatterErrorDesc(updateJob.getErrorDesc());

        if (ResponseErrorCode.SUCCESS.name().equals(errorCode)) {
            statementJob.setStatus(StatusCode.IN_PROGRESS.name());
        } else {
            statementJob.setStatus(StatusCode.WITH_ERROR.name());
            if (statementJob.getTimeEndJob() == null) {
                statementJob.setTimeEndJob(updateJob.getLocalDateTime());
            }
        }

        //now, update the record in the database
        statementJobRepository.updateStatementJob(statementJob);

        return statementJob;
    }

    public StatementJob updateJobSender(final String errorCode, final UpdateJobTemplate updateJob) {
        //read the statementJob from database
        StatementJob statementJob = statementJobRepository.findById(updateJob.getId());

        //update startJob time if it is null
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(updateJob.getLocalDateTime());
        }

        statementJob.setSenderErrorCode(errorCode);
        statementJob.setSenderErrorDesc(updateJob.getErrorDesc());

        if (ResponseErrorCode.SUCCESS.name().equals(errorCode)) {
            statementJob.setStatus(StatusCode.COMPLETED.name());
        } else {
            //todo: if we should retry or the number of attempts are completed
            statementJob.setStatus(StatusCode.WITH_ERROR.name());
            if (statementJob.getTimeEndJob() == null) {
                statementJob.setTimeEndJob(updateJob.getLocalDateTime());
            }

            if (updateJob.getShouldTryAgain()) {
                statementJob.setCorebankRetries(statementJob.getCorebankRetries() + 1);
                statementJob.setStatus(StatusCode.SLEEPING.name());

                //todo: calculate time to wake up
            }
        }

        //now, update the record in the database
        statementJobRepository.updateStatementJob(statementJob);

        return statementJob;
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

    // calculates the previous month number of the provided date
    private int calculatePreviousMonth(final LocalDate localDate) {
        return (localDate.getMonthValue() - 1);
    }

    // calculates the last day of the month of the provided date
    private int calculateLastDayOfMonth(final LocalDate date) {
        YearMonth month = YearMonth.of(date.getYear(), date.getMonth());
        return month.atEndOfMonth().getDayOfMonth();
    }

    // calculates the previous month number of the provided date
    private LocalDateTime calculateWhenToWakeUp(final int retries) {

        LocalDateTime now = LocalDateTime.now();
        int retryNumber = retries;
        if (retryNumber < 0) {
            return null;
        }

        if (retryNumber > retryScale.length) {
            return null;
        }

        return (now.plusMinutes(retryScale[retryNumber]));
    }

    public Map<String, Object> processTruncate() {
        Map<String, Object> response = new HashMap<>();

        statementJobRepository.truncateJobs();
        return response;
    }
}
