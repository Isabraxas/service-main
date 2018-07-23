package cc.viridian.service.statement.persistence.auto;

import java.time.LocalDateTime;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _StatementJob was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _StatementJob extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> ACCOUNT_CODE = Property.create("accountCode", String.class);
    public static final Property<Integer> ERROR_BANK_CODE = Property.create("errorBankCode", Integer.class);
    public static final Property<String> ERROR_BANK_DESC = Property.create("errorBankDesc", String.class);
    public static final Property<Integer> ERROR_SEND_CODE = Property.create("errorSendCode", Integer.class);
    public static final Property<String> ERROR_SEND_DESC = Property.create("errorSendDesc", String.class);
    public static final Property<String> FREQUENCY = Property.create("frequency", String.class);
    public static final Property<LocalDateTime> LOCAL_DATE_TIME = Property.create("localDateTime", LocalDateTime.class);
    public static final Property<String> PROCESS_DATE = Property.create("processDate", String.class);
    public static final Property<Integer> RETRY_NUMBER = Property.create("retryNumber", Integer.class);
    public static final Property<Integer> SEND_ID = Property.create("sendId", Integer.class);
    public static final Property<String> SEND_RECIPIENT = Property.create("sendRecipient", String.class);
    public static final Property<Integer> SERVICE_STA_ID = Property.create("serviceStaId", Integer.class);
    public static final Property<String> STATUS = Property.create("status", String.class);
    public static final Property<LocalDateTime> TIME_END_JOB = Property.create("timeEndJob", LocalDateTime.class);
    public static final Property<LocalDateTime> TIME_START_JOB = Property.create("timeStartJob", LocalDateTime.class);

    public void setAccountCode(String accountCode) {
        writeProperty("accountCode", accountCode);
    }
    public String getAccountCode() {
        return (String)readProperty("accountCode");
    }

    public void setErrorBankCode(int errorBankCode) {
        writeProperty("errorBankCode", errorBankCode);
    }
    public int getErrorBankCode() {
        Object value = readProperty("errorBankCode");
        return (value != null) ? (Integer) value : 0;
    }

    public void setErrorBankDesc(String errorBankDesc) {
        writeProperty("errorBankDesc", errorBankDesc);
    }
    public String getErrorBankDesc() {
        return (String)readProperty("errorBankDesc");
    }

    public void setErrorSendCode(int errorSendCode) {
        writeProperty("errorSendCode", errorSendCode);
    }
    public int getErrorSendCode() {
        Object value = readProperty("errorSendCode");
        return (value != null) ? (Integer) value : 0;
    }

    public void setErrorSendDesc(String errorSendDesc) {
        writeProperty("errorSendDesc", errorSendDesc);
    }
    public String getErrorSendDesc() {
        return (String)readProperty("errorSendDesc");
    }

    public void setFrequency(String frequency) {
        writeProperty("frequency", frequency);
    }
    public String getFrequency() {
        return (String)readProperty("frequency");
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        writeProperty("localDateTime", localDateTime);
    }
    public LocalDateTime getLocalDateTime() {
        return (LocalDateTime)readProperty("localDateTime");
    }

    public void setProcessDate(String processDate) {
        writeProperty("processDate", processDate);
    }
    public String getProcessDate() {
        return (String)readProperty("processDate");
    }

    public void setRetryNumber(int retryNumber) {
        writeProperty("retryNumber", retryNumber);
    }
    public int getRetryNumber() {
        Object value = readProperty("retryNumber");
        return (value != null) ? (Integer) value : 0;
    }

    public void setSendId(int sendId) {
        writeProperty("sendId", sendId);
    }
    public int getSendId() {
        Object value = readProperty("sendId");
        return (value != null) ? (Integer) value : 0;
    }

    public void setSendRecipient(String sendRecipient) {
        writeProperty("sendRecipient", sendRecipient);
    }
    public String getSendRecipient() {
        return (String)readProperty("sendRecipient");
    }

    public void setServiceStaId(int serviceStaId) {
        writeProperty("serviceStaId", serviceStaId);
    }
    public int getServiceStaId() {
        Object value = readProperty("serviceStaId");
        return (value != null) ? (Integer) value : 0;
    }

    public void setStatus(String status) {
        writeProperty("status", status);
    }
    public String getStatus() {
        return (String)readProperty("status");
    }

    public void setTimeEndJob(LocalDateTime timeEndJob) {
        writeProperty("timeEndJob", timeEndJob);
    }
    public LocalDateTime getTimeEndJob() {
        return (LocalDateTime)readProperty("timeEndJob");
    }

    public void setTimeStartJob(LocalDateTime timeStartJob) {
        writeProperty("timeStartJob", timeStartJob);
    }
    public LocalDateTime getTimeStartJob() {
        return (LocalDateTime)readProperty("timeStartJob");
    }

}
