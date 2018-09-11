package cc.viridian.service.statement.service;

import cc.viridian.provider.Exception.CorebankException;
import cc.viridian.service.statement.payload.ListJobsResponse;
import cc.viridian.service.statement.repository.StatementJobRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@NoArgsConstructor
@Service
public class ScheduleService {

    private StatementJobRepository statementJobRepository;

    @Autowired
    public ScheduleService(StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;

        statementJobRepository.listJobsToRetryCorebank();
    }

    public void retryJobs() throws CorebankException {
        ListJobsResponse listAccountsResponse = statementJobRepository.listJobsToRetryCorebank();
    }

}
