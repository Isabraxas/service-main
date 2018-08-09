package cc.viridian.service.statement.service;

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
        StatementJob statementJob = statementJobRepository.findById(data.getId());

        if (data.getAdapterType().equalsIgnoreCase("corebank")) {
            statementJob.setStatus("IN PROGRESS");
            statementJob.setTimeStartJob(data.getLocalDateTime());
            statementJob.setCorebankErrorCode(data.getErrorCode());
            statementJob.setCorebankErrorDesc(data.getErrorDesc());
            //statementJob.setCorebankRetries();
        }

        statementJobRepository.updateStatementJob(statementJob);

        return new StatementJobModel(statementJob);
    }
}
