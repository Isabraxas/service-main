package cc.viridian.service.statement.persistent.auto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _StatementHeader was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _StatementHeader extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> ACCOUNT_ADDRESS = Property.create("accountAddress", String.class);
    public static final Property<String> ACCOUNT_BRANCH = Property.create("accountBranch", String.class);
    public static final Property<String> ACCOUNT_CODE = Property.create("accountCode", String.class);
    public static final Property<String> ACCOUNT_CURRENCY = Property.create("accountCurrency", String.class);
    public static final Property<String> ACCOUNT_NAME = Property.create("accountName", String.class);
    public static final Property<String> ACCOUNT_TYPE = Property.create("accountType", String.class);
    public static final Property<BigDecimal> BALANCE_END = Property.create("balanceEnd", BigDecimal.class);
    public static final Property<BigDecimal> BALANCE_INITIAL = Property.create("balanceInitial", BigDecimal.class);
    public static final Property<String> CUSTOMER_CODE = Property.create("customerCode", String.class);
    public static final Property<LocalDate> DATE_FROM = Property.create("dateFrom", LocalDate.class);
    public static final Property<LocalDate> DATE_TO = Property.create("dateTo", LocalDate.class);
    public static final Property<String> MESSAGE = Property.create("message", String.class);
    public static final Property<String> STATEMENT_TITLE = Property.create("statementTitle", String.class);

    public void setAccountAddress(String accountAddress) {
        writeProperty("accountAddress", accountAddress);
    }
    public String getAccountAddress() {
        return (String)readProperty("accountAddress");
    }

    public void setAccountBranch(String accountBranch) {
        writeProperty("accountBranch", accountBranch);
    }
    public String getAccountBranch() {
        return (String)readProperty("accountBranch");
    }

    public void setAccountCode(String accountCode) {
        writeProperty("accountCode", accountCode);
    }
    public String getAccountCode() {
        return (String)readProperty("accountCode");
    }

    public void setAccountCurrency(String accountCurrency) {
        writeProperty("accountCurrency", accountCurrency);
    }
    public String getAccountCurrency() {
        return (String)readProperty("accountCurrency");
    }

    public void setAccountName(String accountName) {
        writeProperty("accountName", accountName);
    }
    public String getAccountName() {
        return (String)readProperty("accountName");
    }

    public void setAccountType(String accountType) {
        writeProperty("accountType", accountType);
    }
    public String getAccountType() {
        return (String)readProperty("accountType");
    }

    public void setBalanceEnd(BigDecimal balanceEnd) {
        writeProperty("balanceEnd", balanceEnd);
    }
    public BigDecimal getBalanceEnd() {
        return (BigDecimal)readProperty("balanceEnd");
    }

    public void setBalanceInitial(BigDecimal balanceInitial) {
        writeProperty("balanceInitial", balanceInitial);
    }
    public BigDecimal getBalanceInitial() {
        return (BigDecimal)readProperty("balanceInitial");
    }

    public void setCustomerCode(String customerCode) {
        writeProperty("customerCode", customerCode);
    }
    public String getCustomerCode() {
        return (String)readProperty("customerCode");
    }

    public void setDateFrom(LocalDate dateFrom) {
        writeProperty("dateFrom", dateFrom);
    }
    public LocalDate getDateFrom() {
        return (LocalDate)readProperty("dateFrom");
    }

    public void setDateTo(LocalDate dateTo) {
        writeProperty("dateTo", dateTo);
    }
    public LocalDate getDateTo() {
        return (LocalDate)readProperty("dateTo");
    }

    public void setMessage(String message) {
        writeProperty("message", message);
    }
    public String getMessage() {
        return (String)readProperty("message");
    }

    public void setStatementTitle(String statementTitle) {
        writeProperty("statementTitle", statementTitle);
    }
    public String getStatementTitle() {
        return (String)readProperty("statementTitle");
    }

}
