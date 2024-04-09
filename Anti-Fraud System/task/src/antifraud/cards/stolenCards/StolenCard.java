package antifraud.cards.stolenCards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class StolenCard {
    @Id
    @Column(name = "stolen_card_id")
    @JsonIgnoreProperties({"maxAllowed", "maxManual"})
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotEmpty
    private String number;

    public StolenCard(String number) {
        this.number = number;
    }

    public StolenCard() {
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}
