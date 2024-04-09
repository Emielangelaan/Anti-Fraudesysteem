package antifraud.transactions;


import antifraud.cards.Card;
import antifraud.cards.CardRepository;
import antifraud.cards.stolenCards.StolenCardRepository;
import antifraud.suspiciousIpAdresses.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class TransactionService {
    @Autowired
    private IpRepository ipRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private StolenCardRepository stolenCardRepository;

    private Transaction transaction;

    public Transaction transactionRules(Transaction transaction) {
        this.transaction = transaction;
        checkAmount();
        checkCorrelation();
        return transactionRepository.save(this.transaction);
    }

    private void checkCorrelation() {
        LocalDateTime currentDate = this.transaction.getDate();
        LocalDateTime oneHourPrior = currentDate.minusHours(1);
        List<Transaction> transactions = transactionRepository
                .findAllByNumberAndDateBetween(
                        transaction.getNumber(),
                        oneHourPrior,
                        currentDate
                );

        long distinctIp = countDistinctIp(transactions, this.transaction.getIp());
        long distinctRegions = countDistinctRegions(transactions, this.transaction.getRegion());

        if (distinctIp == 2) {
            setTransactionRuleAndInfo("MANUAL_PROCESSING", "ip-correlation");
        }
        if (distinctIp > 2) {
            setTransactionRuleAndInfo("PROHIBITED", "ip-correlation");
        }
        if (distinctRegions == 2) {
            setTransactionRuleAndInfo("MANUAL_PROCESSING", "region-correlation");
        }
        if (distinctRegions > 2) {
            setTransactionRuleAndInfo("PROHIBITED", "region-correlation");
        }
    }

    private long countDistinctIp(List<Transaction> transactions, String ip) {
        return transactions.stream()
                .map(Transaction::getIp)
                .filter(priorIp -> !priorIp.equals(ip))
                .distinct()
                .count();
    }

    private long countDistinctRegions(List<Transaction> transactions, String region) {
        return transactions.stream()
                .map(Transaction::getRegion)
                .filter(priorRegion -> !priorRegion.equals(region))
                .distinct()
                .count();
    }

    void checkAmount() {
        long amount = this.transaction.getAmount();
        Card card;
        if (cardRepository.existsByNumber(transaction.getNumber())) {
            card = cardRepository.findByNumber(transaction.getNumber());
        } else {
            card = cardRepository.save(new Card(transaction.getNumber()));
        }
        long maxManual = card.getMaxManual();
        long maxAllowed = card.getMaxAllowed();
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
        if (amount <= maxAllowed) {
            setTransactionRuleAndInfo("ALLOWED", "none");

        } else if (amount <= maxManual) {
            checkIpAndCard();
            if (this.transaction.getInfo().isEmpty()) {
                setTransactionRuleAndInfo("MANUAL_PROCESSING", "amount");
            }

        } else {
            setTransactionRuleAndInfo("PROHIBITED", "amount");
            checkIpAndCard();
        }
    }

    void checkIpAndCard() {
        if (stolenCardRepository.existsByNumber(this.transaction.getNumber())) {
            setTransactionRuleAndInfo("PROHIBITED", "card-number");
        }
        if (ipRepository.existsByIp(transaction.getIp())) {
            setTransactionRuleAndInfo("PROHIBITED", "ip");
        }
    }

    void setTransactionRuleAndInfo(String rule, String info) {
        this.transaction.setResult(rule);
        this.transaction.setInfo(info);
    }

    public Transaction updateTransaction(long transactionId, String feedback) {
        if(!(feedback.equals("ALLOWED") || feedback.equals("MANUAL_PROCESSING") || feedback.equals("PROHIBITED"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!transactionRepository.existsByTransactionId(transactionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        this.transaction = transactionRepository.findByTransactionId(transactionId);
        processFeedback(feedback);
        this.transaction.setFeedback(feedback);
        return this.transactionRepository.save(this.transaction);
    }

    private void processFeedback(String feedback) {
        if (!this.transaction.getFeedback().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (this.transaction.getResult().equals(feedback)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        BiFunction<Long, Long, Long> increaseLimit = (limit, amount) -> ((long) Math.ceil(0.8 * limit + 0.2 * amount));
        BiFunction<Long, Long, Long> decreaseLimit = (limit, amount) -> ((long) Math.ceil(0.8 * limit - 0.2 * amount));
        Card card = cardRepository.findByNumber(transaction.getNumber());
        long maxManual = card.getMaxManual();
        long maxAllowed = card.getMaxAllowed();
        long amount = this.transaction.getAmount();
        String result = this.transaction.getResult();
        switch (result) {
            case "ALLOWED": {
                if (feedback.equals("MANUAL_PROCESSING")) {
                    maxAllowed = decreaseLimit.apply(maxAllowed, amount);
                } else {
                    maxAllowed = decreaseLimit.apply(maxAllowed, amount);
                    maxManual = decreaseLimit.apply(maxManual, amount);
                }
                break;
            }
            case "MANUAL_PROCESSING": {
                if (feedback.equals("ALLOWED")) {
                    maxAllowed = increaseLimit.apply(maxAllowed, amount);
                } else {
                    maxManual = decreaseLimit.apply(maxManual, amount);
                }
                break;
            }
            case "PROHIBITED": {
                if (feedback.equals("ALLOWED")) {
                    maxAllowed = increaseLimit.apply(maxAllowed, amount);
                    maxManual = increaseLimit.apply(maxManual, amount);
                } else {
                    maxManual = increaseLimit.apply(maxManual, amount);
                }
                break;
            }
        }
        card.setMaxAllowed(maxAllowed);
        card.setMaxManual(maxManual);
        cardRepository.save(card);
    }

    public List<Transaction> getTransactionHistory(String number) {
        if (transactionRepository.countAllByNumber(number) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transactionRepository.findAllByNumber(number);
    }

    public List<Transaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }
}
