package antifraud.cards.stolenCards;


import antifraud.cards.Card;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud/stolencard")
public class CardController {
    @Autowired
    private StolenCardService service;

    @PostMapping
    public StolenCard stolenCard(@NotEmpty @RequestBody NumberRequest numberRequest) {
        String number = numberRequest.number;
        StolenCardService.validate(number);
        return service.banCard(number);
    }

    @DeleteMapping("{number}")
    @ResponseStatus(HttpStatus.OK)
    public Status deleteCards(@NotEmpty @PathVariable String number) {
        StolenCardService.validate(number);
        service.delete(number);
        return new Status("Card " + number + " successfully removed!");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StolenCard> getCards() {
        return service.returnAllCards();
    }

    public record NumberRequest(String number){
    }

    public record Status(String status) {
    }
}