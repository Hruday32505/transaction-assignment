package com.example.transactionstarter.transaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServiceTests {

    @Mock
    private TransactionRepository repository;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TransactionService(repository);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {

        Transaction transaction = new Transaction(
                "TXN001",
                "CUS001",
                new BigDecimal("500.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(repository.existsById("TXN001")).thenReturn(false);
        when(repository.save(transaction)).thenReturn(transaction);

        Transaction result = service.createTransaction(transaction);

        assertNotNull(result);
        assertEquals("TXN001", result.getTransactionId());
        assertEquals("CUS001", result.getCustomerId());
        assertEquals(new BigDecimal("500.00"), result.getAmount());

        verify(repository).save(transaction);
    }

    @Test
    void shouldRejectTransactionWhenValidationFails() {

        Transaction transaction = new Transaction(
                "TXN002",
                "CUS001",
                new BigDecimal("-100.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createTransaction(transaction)
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());

        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldRejectDuplicateTransactionId() {

        Transaction transaction = new Transaction(
                "TXN001",
                "CUS001",
                new BigDecimal("500.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(repository.existsById("TXN001")).thenReturn(true);

        TransactionAlreadyExistsException exception = assertThrows(
                TransactionAlreadyExistsException.class,
                () -> service.createTransaction(transaction)
        );

        assertEquals("Transaction ID already exists", exception.getMessage());

        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldRejectRequestForNonExistentTransaction() {

        when(repository.findById("DOESNOTEXIST"))
                .thenReturn(Optional.empty());

        TransactionNotFoundException exception = assertThrows(
                TransactionNotFoundException.class,
                () -> service.getTransaction("DOESNOTEXIST")
        );

        assertEquals("Transaction not found", exception.getMessage());
    }

    @Test
    void shouldGetExistingTransaction() {

        Transaction transaction = new Transaction(
                "TXN003",
                "CUS001",
                new BigDecimal("750.00"),
                "USD",
                "PAYMENT",
                "PENDING"
        );

        when(repository.findById("TXN003"))
                .thenReturn(Optional.of(transaction));

        Transaction result = service.getTransaction("TXN003");

        assertNotNull(result);
        assertEquals("TXN003", result.getTransactionId());
        assertEquals("CUS001", result.getCustomerId());
        assertEquals("PENDING", result.getTransactionStatus());
    }

    @Test
    void shouldAllowPendingToCompletedStatusChange() {

        Transaction transaction = new Transaction(
                "TXN004",
                "CUS001",
                new BigDecimal("100.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(repository.findById("TXN004"))
                .thenReturn(Optional.of(transaction));

        when(repository.save(transaction))
                .thenReturn(transaction);

        Transaction result =
                service.updateTransactionStatus("TXN004", "COMPLETED");

        assertEquals("COMPLETED", result.getTransactionStatus());

        verify(repository).save(transaction);
    }

    @Test
    void shouldRejectStatusChangeFromCompletedToFailed() {

        Transaction transaction = new Transaction(
                "TXN005",
                "CUS001",
                new BigDecimal("100.00"),
                "INR",
                "PAYMENT",
                "COMPLETED"
        );

        when(repository.findById("TXN005"))
                .thenReturn(Optional.of(transaction));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateTransactionStatus("TXN005", "FAILED")
        );

        assertEquals(
                "Status cannot be changed from COMPLETED to FAILED",
                exception.getMessage()
        );

        verify(repository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldGetAllTransactionsForCustomer() {

        Transaction transaction1 = new Transaction(
                "TXN006",
                "CUS002",
                new BigDecimal("100.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        Transaction transaction2 = new Transaction(
                "TXN007",
                "CUS002",
                new BigDecimal("200.00"),
                "INR",
                "TRANSFER",
                "PENDING"
        );

        when(repository.findByCustomerId("CUS002"))
                .thenReturn(List.of(transaction1, transaction2));

        List<Transaction> result =
                service.getCustomerTransactions("CUS002");

        assertEquals(2, result.size());
        assertEquals("TXN006", result.get(0).getTransactionId());
        assertEquals("TXN007", result.get(1).getTransactionId());
        assertEquals("CUS002", result.get(0).getCustomerId());
        assertEquals("CUS002", result.get(1).getCustomerId());
    }
}