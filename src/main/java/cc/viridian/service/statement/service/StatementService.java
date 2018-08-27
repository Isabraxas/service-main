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

@Slf4j
@NoArgsConstructor
@Service
public class StatementService {

    private StatementMainRepository statementMainRepository;

    @Autowired
    public StatementService(StatementMainRepository statementMainRepository) {
        this.statementMainRepository = statementMainRepository;
    }


    public AccountsRegistered registerNewAccount(RegisterAccountPost body) {
        StatementMain account = statementMainRepository.registerNewAccount(body);
        return new AccountsRegistered(account);
    }


    public ListAccountsResponse listAccounts(Integer start, Integer length)
    {
        return statementMainRepository.listAccounts(start, length);
    }

    public ListAccountsResponse listAccountsMonthly(){
        return statementMainRepository.listAccountsFilterByMonthlyFrequency();
    }
}
