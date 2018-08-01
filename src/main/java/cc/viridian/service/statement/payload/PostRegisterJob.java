package cc.viridian.service.statement.payload;

public class PostRegisterJob {

    private String account;
    private String currency;
    private String type;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PostRegisterJob() {

    }

    public PostRegisterJob(String account, String currency, String type) {
        this.account = account;
        this.currency = currency;
        this.type = type;
    }
}
