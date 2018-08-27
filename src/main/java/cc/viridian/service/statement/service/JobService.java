package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.AccountsRegistered;
import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.StatementJobModel;
import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.payload.*;
import cc.viridian.service.statement.persistence.StatementJob;
import cc.viridian.service.statement.repository.StatementJobProducer;
import cc.viridian.service.statement.repository.StatementJobRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Month;

@Slf4j
@NoArgsConstructor
@Service
public class JobService {

    @Autowired
    StatementJobProducer statementJobProducer;

    private StatementJobRepository statementJobRepository;

    @Autowired
    public JobService(StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
    }

    public ListJobsResponse listJobs(Integer start, Integer length)
    {
        return statementJobRepository.listJobs(start, length);
    }


    public StatementJobModel registerSingleJob(RegisterJobPost body) {

        StatementJob statementJob = statementJobRepository.registerSingleJob(body);

        //creates a job template using the business model
        JobTemplate jobTemplate = new JobTemplate(statementJob);

        //send message to kafka
        statementJobProducer.send("" + jobTemplate.getId(), jobTemplate);

        return new StatementJobModel(statementJob);
    }

    public StatementJobModel updateJob(UpdateJobTemplate data) {
        //read the statementJob from database
        StatementJob statementJob = statementJobRepository.findById(data.getId());

        if (data.getAdapterType().equalsIgnoreCase("corebank")) {
            switch (data.getErrorCode()) {
                case "":
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

        if (data.getAdapterType().equalsIgnoreCase("sender")) {
            switch (data.getErrorCode()) {
                case "":
                    statementJob = completeSenderUpdateJob(statementJob, data);
                    break;
            }

        }

        //now, update the record in the database
        statementJobRepository.updateStatementJob(statementJob);

        return new StatementJobModel(statementJob);
    }

    public String processMonthlyAccounts(ListAccountsResponse listAccountsResponse){
        /* recibir lista de accounts que coincidan con el criterio de MONTHLY
         * por cada account en la lista registrar su job en la BD y mandar a kafka */
        for (AccountsRegistered acc : listAccountsResponse.getData()){
            RegisterJobPost body = new RegisterJobPost();
            body.setAccount(acc.getAccountCode());
            body.setCurrency(acc.getAccountCurrency());
            body.setType(acc.getAccountType());
            body.setCustomerCode(acc.getCustomerCode());
            body.setRecipient(acc.getRecipient());
            body.setFrequency(acc.getFrequency());
            body.setDateFrom(LocalDate.now());
            body.setDateTo(LocalDate.of(2018,Month.JULY,28));
            body.setCorebankAdapter("test1");
            body.setFormatAdapter("test2");
            body.setSendAdapter("test3");
            StatementJob statementJob = statementJobRepository.registerSingleJob(body);

            JobTemplate jobTemplate = new JobTemplate(statementJob);
            statementJobProducer.send(""+jobTemplate.getId(),jobTemplate);
        }
        return "processed monthly record";
    }

    //in progress
    public StatementJob inProgressUpdateJob(StatementJob statementJob, UpdateJobTemplate data) {
        statementJob.setStatus("IN PROGRESS");
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(data.getLocalDateTime());
        }
        statementJob.setCorebankErrorCode("ok");
        statementJob.setCorebankErrorDesc("processed");
        return statementJob;
    }

    //completed
    public StatementJob completeUpdateJob(StatementJob statementJob, UpdateJobTemplate data) {
        return statementJob;
    }

    //job processed with error but should retry
    public StatementJob retryLaterUpdateJob(StatementJob statementJob, UpdateJobTemplate data) {
        statementJob.setStatus("WITH ERROR");
        if (statementJob.getTimeStartJob() == null) {
            statementJob.setTimeStartJob(data.getLocalDateTime());
        }
        statementJob.setCorebankErrorCode(data.getErrorCode());
        statementJob.setCorebankErrorDesc(data.getErrorDesc());
        statementJob.setCorebankRetries(statementJob.getCorebankRetries()+1);
        return statementJob;
    }

    //job processed with error but shouldn't retry because state is final and unrecoverable
    public StatementJob completeWithErrorUpdateJob(StatementJob statementJob, UpdateJobTemplate data) {
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
    public StatementJob completeSenderUpdateJob(StatementJob statementJob, UpdateJobTemplate data) {
        statementJob.setStatus("CLOSE");
        if (statementJob.getTimeEndJob() == null) {
            statementJob.setTimeEndJob(data.getLocalDateTime());
        }
        statementJob.setSenderErrorCode("ok");
        statementJob.setSenderErrorDesc(data.getErrorDesc());
        return statementJob;
    }


}
