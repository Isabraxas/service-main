package cc.viridian.service.statement.model;

import cc.viridian.service.statement.persistence.StatementJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.cayenne.Cayenne;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JobTemplate {
    private Long id;
    private String account;
    private String currency;
    private String type;
    private String frequency;
    private String customerCode;
    private String recipient;
    private String formatAdapter;
    private String sendAdapter;
    private String coreBankAdapter;

    public JobTemplate(StatementJob job) {
        this.id = Cayenne.longPKForObject(job);
        this.account = job.getAccountCode();
        this.currency = job.getAccountCurrency();
        this.type = job.getAccountType();
        this.frequency = job.getFrequency();
        this.customerCode = job.getCustomerCode();
        this.recipient = job.getSendRecipient();
        this.formatAdapter = job.getAdapterFormat();
        this.sendAdapter = job.getAdapterSend();
        this.coreBankAdapter = job.getAdapterCorebank();
    }

    public JobTemplate(StatementJobModel job) {
        this.id = job.getId();
        this.account = job.getAccountCode();
        this.currency = job.getAccountCurrency();
        this.type = job.getAccountType();
        this.frequency = job.getFrequency();
        this.customerCode = job.getCustomerCode();
        this.recipient = job.getRecipient();
        this.formatAdapter = job.getFormat();
        this.sendAdapter = job.getSend();
        this.coreBankAdapter = job.getCorebank();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("id: " + id + " ");
        sb.append("account: " + account + " ");
        sb.append("currency: " + currency + " ");
        sb.append("type: " + type + " ");
        sb.append("frequency: " + frequency + " ");
        sb.append("customerCode: " + customerCode + " ");
        sb.append("recipient: " + recipient + " ");
        sb.append("formatAdapter: " + formatAdapter + " ");
        sb.append("sendAdapter: " + sendAdapter + " ");
        sb.append("coreBankAdapter: " + coreBankAdapter + " ");
        return sb.toString();
    }
}
