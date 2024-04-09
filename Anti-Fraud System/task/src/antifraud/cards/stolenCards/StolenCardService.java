package antifraud.cards.stolenCards;


import antifraud.cards.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StolenCardService {

    @Autowired
    private StolenCardRepository repository;

    public static void validate(String number) {
        int nDigits = number.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = number.charAt(i) - '0';
            if (isSecond) {
                d = d * 2;
            }
            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
        if (nSum % 10 != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public StolenCard banCard(String number) {
        if (repository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        StolenCard card = new StolenCard(number);
        return repository.save(card);
    }

    @Transactional
    public void delete(String number) {
        if (repository.existsByNumber(number)) {
            repository.deleteStolenCardByNumber(number);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<StolenCard> returnAllCards() {
        return repository.findByOrderById();
    }
}
