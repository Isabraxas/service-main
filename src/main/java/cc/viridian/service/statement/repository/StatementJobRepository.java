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
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.time.ZoneId.SHORT_IDS;

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

    public StatementJob updateStatementJob(StatementJob statementJob) {
        statementJob.getObjectContext().commitChanges();
        return statementJob;
    }

    public StatementJob findById (Long id)
    {
        ObjectContext context = mainServerRuntime.newContext();

        if (id != null && id > 0) {
            StatementJob statementJob = SelectById.query(StatementJob.class, "" + id ).selectOne(context);

            return statementJob;
        }
        return null;
    }

    public StatementJob registerSingleJob(RegisterJobPost body) {

        //save in database
        ObjectContext context = mainServerRuntime.newContext();
        StatementJob statementJob = context.newObject(StatementJob.class);

        statementJob.setAccountCode(body.getAccount());
        statementJob.setAccountCurrency(body.getCurrency());
        statementJob.setAccountType(body.getType());
        statementJob.setCustomerCode(body.getCustomerCode());
        statementJob.setSendRecipient(body.getRecipient());

        statementJob.setFrequency(body.getFrequency());
        statementJob.setProcessDateFrom(body.getDateFrom());
        statementJob.setProcessDateTo(body.getDateTo());

        statementJob.setAdapterCorebank(body.getCorebankAdapter());
        statementJob.setAdapterFormat(body.getFormatAdapter());
        statementJob.setAdapterSend(body.getSendAdapter());

        statementJob.setCorebankErrorCode("");
        statementJob.setCorebankErrorDesc("");
        statementJob.setCorebankRetries(0);
        statementJob.setCorebankTryAgainAt(null);

        statementJob.setSenderErrorCode("");
        statementJob.setSenderErrorDesc("");
        statementJob.setSenderRetries(0);
        statementJob.setSenderTryAgainAt(null);

        statementJob.setStatus("OPEN");

        statementJob.setTimeCreateJob(LocalDateTime.now());
        statementJob.setTimeEndJob(null);
        statementJob.setTimeStartJob(null);

        context.commitChanges();

        return statementJob;
    }

}
