package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.StatementJobModel;
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
    StatementJobProducer jobKafkaProducer;

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

        StatementJobModel statementJob = new StatementJobModel(statementJobRepository.registerSingleJob(body));

        //send message to kafka
        JobTemplate jobTemplate = new JobTemplate(statementJob);

        jobKafkaProducer.send(statementJob.getId().toString(), jobTemplate);

        return statementJob;
    }

}
