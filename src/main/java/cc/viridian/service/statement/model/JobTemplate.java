package cc.viridian.service.statement.model;

import cc.viridian.service.statement.persistence.StatementJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.cayenne.Cayenne;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobTemplate {
    private Long id;
    private String account;
    private String currency;
    private String type;

    private String corebankAdapter;
    private String formatAdapter;
    private String sendAdapter;

    private String customerCode;
    private String recipient;

    private String frequency;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private Integer attemptNumber;

    public JobTemplate(StatementJob job) {
        this.id = Cayenne.longPKForObject(job);

        this.account = job.getAccountCode();
        this.currency = job.getAccountCurrency();
        this.type = job.getAccountType();

        this.corebankAdapter = job.getAdapterCorebank();
        this.formatAdapter = job.getAdapterFormat();
        this.sendAdapter = job.getAdapterSend();

        this.customerCode = job.getCustomerCode();
        this.recipient = job.getSendRecipient();

        this.frequency = job.getFrequency();
        this.dateFrom = job.getProcessDateFrom();
        this.dateTo = job.getProcessDateTo();
        this.attemptNumber = job.getCorebankRetries() +1;
    }

    @Deprecated //not used
    public JobTemplate(StatementJobModel job) {
        this.id = job.getId();
        this.account = job.getAccountCode();
        this.currency = job.getAccountCurrency();
        this.type = job.getAccountType();

        this.corebankAdapter = job.getCorebank();
        this.formatAdapter = job.getFormat();
        this.sendAdapter = job.getSender();

        this.customerCode = job.getCustomerCode();
        this.recipient = job.getRecipient();

        this.frequency = job.getFrequency();
        this.dateFrom = job.getDateFrom();
        this.dateTo = job.getDateTo();
        this.attemptNumber = job.getCorebankRetries() +1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("id: " + id + " ");
        sb.append("account: " + account + " ");
        sb.append("currency: " + currency + " ");
        sb.append("type: " + type + " ");
        sb.append("customerCode: " + customerCode + " ");
        sb.append("recipient: " + recipient + " ");
        sb.append("frequency: " + frequency + " ");
        sb.append("from: " + dateFrom + " ");
        sb.append("to: " + dateTo + " ");
        sb.append("formatAdapter: " + formatAdapter + " ");
        sb.append("sendAdapter: " + sendAdapter + " ");
        sb.append("corebankAdapter: " + corebankAdapter + " ");
        return sb.toString();
    }
}
