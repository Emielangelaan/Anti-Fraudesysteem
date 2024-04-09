package antifraud.transactions;


import antifraud.cards.stolenCards.StolenCardService;
import antifraud.suspiciousIpAdresses.IpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    @Autowired
    private TransactionService service;

    @PostMapping("transaction")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResult postTransaction(@Valid @RequestBody Transaction transaction) {
        IpService.validate(transaction.getIp());
        StolenCardService.validate(transaction.getNumber());
        return new TransactionResult(service.transactionRules(transaction));
    }

    @PutMapping("transaction")
    @ResponseStatus(HttpStatus.OK)
    public Transaction updateTransaction(@RequestBody TransactionRequest transactionRequest) {
        return service.updateTransaction(transactionRequest.transactionId, transactionRequest.feedback);
    }

    @GetMapping("history")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getTransactionHistory() {
        return service.getTransactionHistory();
    }

    @GetMapping("history/{number}")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getTransactionHistoryByCard(@PathVariable String number) {
        StolenCardService.validate(number);
        return service.getTransactionHistory(number);
    }

    public record TransactionResult(String result, String info) {
        TransactionResult(Transaction transaction) {
            this(transaction.getResult(), transaction.getInfo());
        }
    }

    public record TransactionRequest(long transactionId, String feedback) {
    }
}

