package com.example.transactionstarter.transaction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("PAYMENT", "REFUND", "TRANSFER");

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("PENDING", "COMPLETED", "FAILED", "CANCELLED");

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction createTransaction(Transaction transaction) {

        validate(transaction);

        if (repository.existsById(transaction.getTransactionId())) {
        	throw new TransactionAlreadyExistsException("Transaction ID already exists");
        }

        return repository.save(transaction);
    }

    private void validate(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (isBlank(transaction.getTransactionId())) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        if (transaction.getTransactionId().length() > 50) {
            throw new IllegalArgumentException("Transaction ID must not exceed 50 characters");
        }

        if (isBlank(transaction.getCustomerId())) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        if (transaction.getCustomerId().length() > 50) {
            throw new IllegalArgumentException("Customer ID must not exceed 50 characters");
        }

        validateAmount(transaction.getAmount());
        validateCurrency(transaction.getCurrency());
        validateType(transaction.getTransactionType());
        validateStatus(transaction.getTransactionStatus());
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount must have at most 2 decimal places");
        }
    }

    private void validateCurrency(String currency) {

        if (isBlank(currency)) {
            throw new IllegalArgumentException("Currency is required");
        }

        if (!currency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Currency must be a 3-letter uppercase code"
            );
        }
    }

    private void validateType(String type) {

        if (isBlank(type)) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        if (!ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                    "Transaction type must be PAYMENT, REFUND, or TRANSFER"
            );
        }
    }

    private void validateStatus(String status) {

        if (isBlank(status)) {
            throw new IllegalArgumentException("Transaction status is required");
        }

        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                    "Transaction status must be PENDING, COMPLETED, FAILED, or CANCELLED"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
    
    public Transaction getTransaction(String transactionId) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        return repository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found"));
    }
    public Transaction updateTransactionStatus(String transactionId, String newStatus) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Transaction status is required");
        }

        if (!ALLOWED_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Transaction status must be PENDING, COMPLETED, FAILED, or CANCELLED"
            );
        }

        Transaction transaction = repository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found"));

        String currentStatus = transaction.getTransactionStatus();

        if (!isAllowedStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Status cannot be changed from " + currentStatus + " to " + newStatus
            );
        }

        transaction.setTransactionStatus(newStatus);

        return repository.save(transaction);
    }
    private boolean isAllowedStatusTransition(String currentStatus, String newStatus) {

        if ("PENDING".equals(currentStatus)) {
            return "COMPLETED".equals(newStatus)
                    || "FAILED".equals(newStatus)
                    || "CANCELLED".equals(newStatus);
        }

        return false;
    }
    public List<Transaction> getCustomerTransactions(String customerId) {

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        return repository.findByCustomerId(customerId);
    }
}