package antifraud.cards;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Card {
    @Id
    @Column(name = "card_id")
    @JsonIgnoreProperties({"maxAllowed", "maxManual"})
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotEmpty
    private String number;
    private long maxAllowed = 200;
    private long maxManual = 1500;

    public Card(String number) {
        this.number = number;
    }

    public Card() {
    }

    public long getMaxManual() {
        return maxManual;
    }

    public void setMaxManual(long maxManual) {
        this.maxManual = maxManual;
    }

    public long getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(long maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}
