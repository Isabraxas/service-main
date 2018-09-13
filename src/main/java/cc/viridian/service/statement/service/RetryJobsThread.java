package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.persistence.StatementJob;
import cc.viridian.service.statement.repository.StatementJobProducer;
import cc.viridian.service.statement.repository.StatementJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ResultIterator;
import java.util.Map;

@Slf4j
public class RetryJobsThread extends Thread {

    static final int MILLISECONDS_PER_SECOND = 1000;

    private StatementJobRepository statementJobRepository;

    private ScheduleService scheduleService;

    private StatementJobProducer statementJobProducer;

    public RetryJobsThread(String name) {
        super(name);
    }

    public void setStatementJobRepository(final StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
    }

    public void setStatementJobProducer(final StatementJobProducer statementJobProducer) {
        this.statementJobProducer = statementJobProducer;
    }

    public void setParent(final ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public void run() {
        if (scheduleService == null) {
            log.error("scheduleService is null");
            return;
        }
        if (statementJobRepository == null) {
            log.error("statementJobRepository is null");
            scheduleService.setIdle();
            return;
        }

        ResultIterator iterator = null;

        try {
            iterator = statementJobRepository.getJobsToRetryCorebankIterator();

            Map row = null;
            do {
                row = statementJobRepository.getJobsToRetryCorebankNextRow(iterator);
                if (row != null) {
                    log.info("  sleep 15 secs");
                    log.info("    account " + row.get("ACCOUNT_CODE").toString());
                    try {
                        Long id = Long.valueOf(row.get("ID").toString());
                        StatementJob statementJob = statementJobRepository.findById(id);
                        statementJob.setStatus("QUEUED");
                        statementJobRepository.updateStatementJob(statementJob);

                        JobTemplate jobTemplate = new JobTemplate(statementJob);
                        statementJobProducer.send("" + jobTemplate.getId(), jobTemplate);

                        Thread.sleep(5 * MILLISECONDS_PER_SECOND);

                        log.info("  wake up 15 secs later");
                    } catch (InterruptedException e) {

                    }
                }
            } while (row != null);

        } catch (CayenneRuntimeException e) {
            log.error(e.getMessage());
            log.error(e.getCause().toString());
        } finally {
            statementJobRepository.getJobsToRetryCorebankNextFinally(iterator);

            scheduleService.setIdle();
        }
    }

}
