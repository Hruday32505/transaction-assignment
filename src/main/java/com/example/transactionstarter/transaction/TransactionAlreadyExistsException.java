package com.example.transactionstarter.transaction;

public class TransactionAlreadyExistsException extends RuntimeException {

    public TransactionAlreadyExistsException(String message) {
        super(message);
    }
}