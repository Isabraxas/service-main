package cc.viridian.service.statement.model;

import cc.viridian.service.statement.persistence.StatementJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.cayenne.Cayenne;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StatementJobModel {

    private Long id;

    private String accountCode;
    private String accountCurrency;
    private String accountType;
    private String customerCode;
    private String recipient;
    private Integer errorBankCode;
    private String errorBankDesc;
    private Integer errorSendCode;
    private String errorSendDesc;
    private String frequency;
    private LocalDateTime localDateTime;
    private String processDate;
    private Integer retryNumber;
    private String send;
    private String format;
    private String corebank;
    private String status;
    private LocalDateTime timeCreateJob;
    private LocalDateTime timeEndJob;
    private LocalDateTime timeStartJob;

    public StatementJobModel(StatementJob statementJob) {
        id = Cayenne.longPKForObject(statementJob);
        accountCode = statementJob.getAccountCode();
        accountCurrency = statementJob.getAccountCurrency();
        accountType = statementJob.getAccountType();
        customerCode = statementJob.getCustomerCode();
        errorBankCode = statementJob.getErrorBankCode();
        errorBankDesc = statementJob.getErrorBankDesc();
        errorSendCode = statementJob.getErrorSendCode();
        errorSendDesc = statementJob.getErrorSendDesc();
        frequency = statementJob.getFrequency();
        localDateTime = statementJob.getLocalDateTime();
        processDate = statementJob.getProcessDate();
        retryNumber = statementJob.getRetryNumber();
        recipient = statementJob.getSendRecipient();
        send = statementJob.getAdapterSend();
        format = statementJob.getAdapterFormat();
        corebank = statementJob.getAdapterCorebank();
        status = statementJob.getStatus();
        timeCreateJob = statementJob.getTimeCreateJob();
        timeEndJob = statementJob.getTimeEndJob();
        timeStartJob = statementJob.getTimeStartJob();
    }
}
