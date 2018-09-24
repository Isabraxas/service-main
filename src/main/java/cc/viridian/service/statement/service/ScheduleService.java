package cc.viridian.service.statement.service;

import cc.viridian.provider.exception.CorebankException;
import cc.viridian.service.statement.repository.SenderProducer;
import cc.viridian.service.statement.repository.StatementJobProducer;
import cc.viridian.service.statement.repository.StatementJobRepository;
import cc.viridian.service.statement.repository.UpdateJobListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@NoArgsConstructor
@Service
public class ScheduleService {
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = 3600;

    private StatementJobRepository statementJobRepository;

    private StatementJobProducer statementJobProducer;

    private UpdateJobListener updateJobListener;

    private SenderProducer senderProducer;

    private ScheduleServiceStatus status;

    private int threadNumber;
    private LocalDateTime threadStartTime;
    private LocalDateTime threadEndTime;

    @Autowired
    public ScheduleService(StatementJobRepository statementJobRepository, StatementJobProducer statementJobProducer
        , UpdateJobListener updateJobListener, SenderProducer senderProducer) {
        this.statementJobRepository = statementJobRepository;
        this.statementJobProducer = statementJobProducer;
        this.updateJobListener = updateJobListener;
        this.senderProducer = senderProducer;
        this.status = ScheduleServiceStatus.IDLE;
        this.threadNumber = 0;
        this.threadStartTime = null;
        this.threadEndTime = null;
    }

    public void setBusy() {
        status = ScheduleServiceStatus.RUNNING;
        threadStartTime = LocalDateTime.now();
        log.info("setting status to RUNNING for thread number: " + threadNumber);
    }

    public void setIdle() {
        status = ScheduleServiceStatus.IDLE;
        threadEndTime = LocalDateTime.now();
        log.info("setting status to IDLE for thread number: " + threadNumber);
    }

    private String diffTime(final LocalDateTime start, final LocalDateTime end) {
        LocalDateTime time1 = start;
        if (time1 == null) {
            time1 = LocalDateTime.now();
        }
        LocalDateTime time2 = end;
        if (time2 == null) {
            time2 = LocalDateTime.now();
        }
        int seconds = (int) ChronoUnit.SECONDS.between(time1, time2);
        String result = "";
        if (seconds > SECONDS_PER_HOUR) {
            result += seconds / SECONDS_PER_HOUR + " hours ";
            seconds = seconds % SECONDS_PER_HOUR;
        }
        if (seconds > SECONDS_PER_MINUTE) {
            result += seconds / SECONDS_PER_MINUTE + " minutes ";
            seconds = seconds % SECONDS_PER_MINUTE;
        }
        result += seconds + " seconds ";
        return result;
    }

    public boolean isThreadIdle() {
        if (status == ScheduleServiceStatus.IDLE) {
            log.info("no threads in progress, last thread was executed "
                         + diffTime(threadStartTime, LocalDateTime.now()) + " ago");
            return true;
        } else {
            if (threadStartTime != null) {
                log.info(
                    "thread number: " + threadNumber + " started " + diffTime(threadStartTime, LocalDateTime.now()));
            }
            return false;
        }
    }

    public void retryJobs() throws CorebankException {
        if (status == ScheduleServiceStatus.IDLE) {
            Long count = statementJobRepository.countJobsToRetryCorebank();
            log.info("there are " + count + " records to process in retryJobs");

            if (count > 0) {
                threadNumber++;
                String threadName = "retryJob-" + threadNumber;
                setBusy();

                RetryJobsThread retryJobsThread = new RetryJobsThread(threadName);
                retryJobsThread.setStatementJobRepository(statementJobRepository);
                retryJobsThread.setStatementJobProducer(statementJobProducer);
                retryJobsThread.setParent(this);
                retryJobsThread.start();
            }
        } else {
            log.warn("Thread is busy, can't create a new thread");
        }
    }

    public void retrySender() throws CorebankException {
        if (status == ScheduleServiceStatus.IDLE) {
            Long count = statementJobRepository.countJobsToRetrySender();
            log.info("there are " + count + " records to process in retrySender");

            if (count > 0L) {
                threadNumber++;
                String threadName = "retrySender-" + threadNumber;
                setBusy();

                RetrySenderThread retrySenderThread = new RetrySenderThread(threadName);
                retrySenderThread.setStatementJobRepository(statementJobRepository);
                retrySenderThread.setSenderProducer(senderProducer);
                retrySenderThread.setParent(this);
                retrySenderThread.start();
            }
        } else {
            log.warn("Thread is busy, can't create a new thread");
        }
    }
}

