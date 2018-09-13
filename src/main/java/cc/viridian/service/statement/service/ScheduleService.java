package cc.viridian.service.statement.service;

import cc.viridian.provider.Exception.CorebankException;
import cc.viridian.service.statement.repository.StatementJobRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ResultIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Service
public class ScheduleService {

    private StatementJobRepository statementJobRepository;

    private boolean isProcessing;

    @Autowired
    public ScheduleService(StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
        this.isProcessing = false;
    }

    public void retryJobs() throws CorebankException {
        Long count = statementJobRepository.countJobsToRetryCorebank();
        log.info("" + count);


        Thread thread = new Thread(myRunnable);
        thread.start();

        isProcessing = true;
        try {
            ResultIterator iterator = statementJobRepository.getJobsToRetryCorebankIterator();

            Map row = null;
            do {
                row = statementJobRepository.getJobsToRetryCorebankNextRow(iterator);
                if (row != null) {
                    log.info(row.toString());
                }
            } while (row != null);
            statementJobRepository.getJobsToRetryCorebankNextFinally(iterator);

            //ListJobsResponse listAccountsResponse = statementJobRepository.listJobsToRetryCorebank();
            isProcessing = false;
        } catch (CayenneRuntimeException e) {
            log.error(e.getMessage());
        }
    }
    Runnable myRunnable = new Runnable(){

        public void run(){
            for (int i = 0; i < 20; i++) {
                log.info("Runnable running: " + i);
            }
        }
    };
}

