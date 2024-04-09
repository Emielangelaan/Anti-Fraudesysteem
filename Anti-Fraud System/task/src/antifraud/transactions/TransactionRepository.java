package antifraud.transactions;


import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByTransactionId(long id);
    Transaction findByTransactionId(long id);
    long countAllByNumber(String number);
    List<Transaction> findAllByNumber(String number);
    List<Transaction> findAllByNumberAndDateBetween(String number, LocalDateTime oneHourPrior, LocalDateTime date);
}
