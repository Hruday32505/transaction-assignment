package com.example.transactionstarter.transaction;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return service.createTransaction(transaction);
    }

    @GetMapping("/{transactionId}")
    public Transaction getTransaction(@PathVariable String transactionId) {
        return service.getTransaction(transactionId);
    }
    @PatchMapping("/{transactionId}/status")
    public Transaction updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestParam String status) {

        return service.updateTransactionStatus(transactionId, status);
    }
    @GetMapping("/customer/{customerId}")
    public List<Transaction> getCustomerTransactions(
            @PathVariable String customerId) {

        return service.getCustomerTransactions(customerId);
    }
}