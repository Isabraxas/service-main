package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.AccountsRegistered;
import cc.viridian.service.statement.payload.ListAccountsResponse;
import cc.viridian.service.statement.payload.RegisterAccountPost;
import cc.viridian.service.statement.persistence.StatementMain;
import cc.viridian.service.statement.repository.StatementMainRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Service
public class StatementService {

    private StatementMainRepository statementMainRepository;

    @Autowired
    public StatementService(StatementMainRepository statementMainRepository) {
        this.statementMainRepository = statementMainRepository;
    }

    public AccountsRegistered registerNewAccount(final RegisterAccountPost body) {
        StatementMain account = statementMainRepository.registerNewAccount(body);
        return new AccountsRegistered(account);
    }

    public ListAccountsResponse listAccounts(final Integer start, final Integer length) {
        return statementMainRepository.listAccounts(start, length);
    }

    public ListAccountsResponse listAccountsMonthly() {
        return statementMainRepository.listAccountsFilterByMonthlyFrequency();
    }

    public Map<String, Object> processTruncate() {
        Map<String, Object> response = new HashMap<>();

        statementMainRepository.truncateStatements();
        return response;
    }
}
