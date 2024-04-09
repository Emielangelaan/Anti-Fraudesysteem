package antifraud.transactions;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties("info")
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionId;
    private long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;
    private LocalDateTime date;
    private String result;
    private String info = "";
    private String feedback = "";

    public Transaction(long amount, String ip, String number, String region, LocalDateTime date) {
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = date;
    }

    public Transaction() {
    }

    public long getTransactionId() {
        return this.transactionId;
    }

    public long getAmount() {
        return this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String transactionRule) {
        this.result = transactionRule;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        if (this.info.isEmpty() || this.info.equals("none")) {
            this.info = info;
        } else {
            this.info += ", " + info;
        }
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
