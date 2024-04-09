package antifraud.cards.stolenCards;


import antifraud.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    Boolean existsByNumber(String number);

    void deleteStolenCardByNumber(String number);

    List<StolenCard> findByOrderById();
}
