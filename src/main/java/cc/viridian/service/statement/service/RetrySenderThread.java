package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.persistence.StatementJob;
import cc.viridian.service.statement.repository.SenderProducer;
import cc.viridian.service.statement.repository.StatementJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ResultIterator;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;


@Slf4j
public class RetrySenderThread extends Thread {

    static final int MILLISECONDS_PER_SECOND = 1000;

    private StatementJobRepository statementJobRepository;

    private ScheduleService scheduleService;

    private SenderProducer senderProducer;

    private RetrySenderService retrySenderService;


    public RetrySenderThread(String name) {
        super(name);
    }

    public void setStatementJobRepository(final StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
    }

    public void setSenderProducer(final SenderProducer senderProducer) {
        this.senderProducer = senderProducer;
    }

    public void setRetrySenderService(final RetrySenderService retrySenderService) {
        this.retrySenderService = retrySenderService;
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
            iterator = statementJobRepository.getJobsToRetrySenderIterator();

            Map row = null;
            do {
                row = statementJobRepository.getJobsToRetrySenderNextRow(iterator);
                if (row != null) {
                    log.info("    account " + row.get("ACCOUNT_CODE").toString());
                    Long id = Long.valueOf(row.get("ID").toString());
                    StatementJob statementJob = statementJobRepository.findById(id);
                    statementJob.setStatus("QUEUED");
                    statementJob.setSenderRetries(statementJob.getSenderRetries() + 1);
                    statementJobRepository.updateStatementJob(statementJob);

                    //todo: get offset,(topic and partiton) from Update Queue
                    //todo: get SenderTemplate throght of service and her fuction
                    //todo: send SenderTemplate instead a JobTemplate
                    //todo: increment the AttemptNumber
                    //todo: the offset should be a new field in UpdateJob
                    //Test fake vars
                    String topic = "dev-sender2";
                    Integer partition = statementJob.getPartition();
                    Long offset = Long.valueOf(statementJob.getSenderOffset());

                    SenderTemplate senderTemplate = retrySenderService
                        .getSendersTemplateByOffset(topic, partition, offset);
                    senderTemplate.setAttemptNumber(senderTemplate.getAttemptNumber() + 1);
                    senderProducer.send("" + senderTemplate.getId(), senderTemplate);
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
