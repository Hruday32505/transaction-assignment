package com.example.transactionstarter.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class TransactionExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidTransaction(
            IllegalArgumentException exception) {

        return Map.of("error", exception.getMessage());
    }
    @ExceptionHandler(TransactionAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateTransaction(
            TransactionAlreadyExistsException exception) {

        return Map.of("error", exception.getMessage());
    }
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTransactionNotFound(
            TransactionNotFoundException exception) {

        return Map.of("error", exception.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleInvalidStatusTransition(
            IllegalStateException exception) {

        return Map.of("error", exception.getMessage());
    }
}