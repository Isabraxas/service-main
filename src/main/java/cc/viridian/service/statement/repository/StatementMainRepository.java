package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.model.AccountsRegistered;
import cc.viridian.service.statement.payload.ListAccountsResponse;
import cc.viridian.service.statement.payload.RegisterAccountPost;
import cc.viridian.service.statement.persistence.StatementMain;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.ObjectSelect;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Repository
public class StatementMainRepository {

    private ServerRuntime mainServerRuntime;

    //@Autowired
    public StatementMainRepository(ServerRuntime mainServerRuntime) {
        this.mainServerRuntime = mainServerRuntime;
    }

    public StatementMain registerNewAccount(RegisterAccountPost body) {
        ObjectContext context = mainServerRuntime.newContext();
        StatementMain statementMain = context.newObject(StatementMain.class);

        statementMain.setAccountCode(body.getAccount());
        statementMain.setAccountCurrency(body.getCurrency());
        statementMain.setAccountType(body.getType());
        statementMain.setCustomerCode(body.getCustomerCode());
        statementMain.setCustomerName(body.getCustomerName());
        statementMain.setEnabled(true);
        statementMain.setFrequency(body.getFrequency());
        statementMain.setSendRecipient(body.getRecipient());
        statementMain.setAdapterCorebank(body.getCoreBankAdapter());
        statementMain.setAdapterFormat(body.getFormatAdapter());
        statementMain.setAdapterSend(body.getSendAdapter());
        statementMain.setOverwrittenByBank(false);
        statementMain.setTimeCreate(LocalDateTime.now());
        statementMain.setTimeModify(LocalDateTime.now());
        statementMain.setUsrCreate("user1");
        statementMain.setUsrModify("user1");

        context.commitChanges();

        return statementMain;

    }

    public ListAccountsResponse listAccounts(Integer start, Integer length) {
        ObjectContext context = mainServerRuntime.newContext();

        //Select all statement
        List<StatementMain> accounts = ObjectSelect.query(StatementMain.class)
                                                   .limit(length)
                                                   .offset(start)
                                                   .select(context);

        List<AccountsRegistered> accountsRegistered = new ArrayList<>();

        Iterator<StatementMain> it = accounts.iterator();
        while (it.hasNext()) {
            accountsRegistered.add(new AccountsRegistered(it.next()) );
        }

        ListAccountsResponse response = new ListAccountsResponse();
        response.setData(accountsRegistered);

        response.setRecordsFiltered( countAllAccounts() );
        response.setRecordsTotal(countAllAccounts());

        return response;
    }

    public ListAccountsResponse listAccountsFilterByMonthlyFrequency(){
        ObjectContext context = mainServerRuntime.newContext();

        List<StatementMain> accounts = ObjectSelect.query(StatementMain.class)
                                                   .where(StatementMain.FREQUENCY.eq("MONTHLY"))
                                                   .select(context);
        List<AccountsRegistered> accountsRegistered = new ArrayList<>();

        Iterator<StatementMain> it = accounts.iterator();
        while (it.hasNext()){
            accountsRegistered.add(new AccountsRegistered(it.next()));
        }

        ListAccountsResponse response = new ListAccountsResponse();
        response.setData(accountsRegistered);

        response.setRecordsFiltered(countAllAccounts());
        response.setRecordsTotal(countAllAccounts());
        return response;
    }

    public Long countAllAccounts ()
    {
        ObjectContext context = mainServerRuntime.newContext();

        //Select count(*) from statement_main
        EJBQLQuery query = new EJBQLQuery("select count(s) from StatementMain s");
        List<Long> result = context.performQuery(query);

        return result.get(0);
    }

}