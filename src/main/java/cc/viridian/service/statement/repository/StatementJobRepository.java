package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.model.StatementJobModel;
import cc.viridian.service.statement.payload.*;
import cc.viridian.service.statement.persistence.StatementJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.ObjectSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Repository
public class StatementJobRepository {
    private ServerRuntime mainServerRuntime;

    @Autowired
    public StatementJobRepository(ServerRuntime mainServerRuntime) {
        this.mainServerRuntime = mainServerRuntime;
    }

    public ListJobsResponse listJobs(Integer start, Integer length)
    {
        ObjectContext context = mainServerRuntime.newContext();

        //Select all statement
        List<StatementJob> jobs = ObjectSelect.query(StatementJob.class)
                                              .limit(length)
                                              .offset(start)
                                              .select(context);

        List<StatementJobModel> jobsRegistered = new ArrayList<>();

        Iterator<StatementJob> it = jobs.iterator();
        while (it.hasNext()) {
            jobsRegistered.add(new StatementJobModel(it.next()) );
        }

        ListJobsResponse response = new ListJobsResponse();
        response.setData(jobsRegistered);

        response.setRecordsFiltered( countAllJobs() );
        response.setRecordsTotal(countAllJobs());

        return response;
    }

    public Long countAllJobs ()
    {
        ObjectContext context = mainServerRuntime.newContext();

        //Select count(*) from statement_main
        EJBQLQuery query = new EJBQLQuery("select count(job) from StatementJob job");
        List<Long> result = context.performQuery(query);

        return result.get(0);
    }

    public StatementJob registerSingleJob(RegisterJobPost body) {

        //save in database
        ObjectContext context = mainServerRuntime.newContext();
        StatementJob statementJob = context.newObject(StatementJob.class);

        statementJob.setAccountCode(body.getAccount());
        statementJob.setAccountCurrency(body.getCurrency());
        statementJob.setAccountType(body.getType());
        statementJob.setFrequency(body.getFrequency());
        statementJob.setCustomerCode(body.getCustomerCode());
        statementJob.setSendRecipient(body.getRecipient());
        statementJob.setAdapterCorebank(body.getCoreBankAdapter());
        statementJob.setAdapterFormat(body.getFormatAdapter());
        statementJob.setAdapterSend(body.getSendAdapter());

        statementJob.setErrorBankCode(0);
        statementJob.setErrorBankDesc("");
        statementJob.setErrorSendCode(0);
        statementJob.setErrorSendDesc("");

        statementJob.setLocalDateTime(LocalDateTime.now());
        statementJob.setProcessDate("201807"); //todo: fix
        statementJob.setRetryNumber(0);
        statementJob.setStatus("OPEN");

        statementJob.setTimeCreateJob(LocalDateTime.now());
        statementJob.setTimeEndJob(null);
        statementJob.setTimeStartJob(null);

        context.commitChanges();

        return statementJob;
    }

}
