package cc.viridian.service.statement.model;

import cc.viridian.service.statement.persistence.StatementJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.cayenne.Cayenne;
import java.time.LocalDate;
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

    private String corebankErrorCode;
    private String corebankErrorDesc;
    private Integer corebankRetries;
    private LocalDateTime corebankTryAgainAt;

    private String frequency;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    private String formatterErrorCode;
    private String formatterErrorDesc;

    private String senderErrorCode;
    private String senderErrorDesc;
    private Integer senderRetries;
    private LocalDateTime senderTryAgainAt;


    private String corebank;
    private String format;
    private String sender;

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
        recipient = statementJob.getSendRecipient();

        corebankErrorCode = statementJob.getCorebankErrorCode();
        corebankErrorDesc = statementJob.getCorebankErrorDesc();
        corebankRetries = statementJob.getCorebankRetries();
        corebankTryAgainAt = statementJob.getCorebankTryAgainAt();

        frequency = statementJob.getFrequency();
        dateFrom = statementJob.getProcessDateFrom();
        dateTo = statementJob.getProcessDateTo();

        formatterErrorCode = statementJob.getFormatterErrorCode();
        formatterErrorDesc = statementJob.getFormatterErrorDesc();

        senderErrorCode = statementJob.getSenderErrorCode();
        senderErrorDesc = statementJob.getSenderErrorDesc();
        senderRetries = statementJob.getSenderRetries();
        senderTryAgainAt = statementJob.getSenderTryAgainAt();

        corebank = statementJob.getAdapterCorebank();
        format = statementJob.getAdapterFormat();
        sender = statementJob.getAdapterSend();

        status = statementJob.getStatus();
        timeCreateJob = statementJob.getTimeCreateJob();
        timeEndJob = statementJob.getTimeEndJob();
        timeStartJob = statementJob.getTimeStartJob();
    }
}
