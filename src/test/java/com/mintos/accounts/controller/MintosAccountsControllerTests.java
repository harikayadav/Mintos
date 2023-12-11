package com.mintos.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mintos.accounts.entity.Account;
import com.mintos.accounts.entity.Transaction;
import com.mintos.accounts.service.MintosAccountsService;

@ExtendWith(MockitoExtension.class)
class MintosAccountsControllerTests {

    @Mock
    private MintosAccountsService mintosAccountsService;

    @InjectMocks
    private MintosAccountsController mintosAccountsController;

    @Test
    void getAccountsByClientId() {
        Long clientId = 1L;
        List<Account> accounts = new ArrayList<>();
        when(mintosAccountsService.getAccountsByClientId(clientId)).thenReturn(accounts);

        ResponseEntity<List<Account>> response = mintosAccountsController.getAccountsByClientId(clientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accounts, response.getBody());
    }

    @Test
    void getTransactionHistory() {
        Long accountId = 1L;
        int offset = 0;
        int limit = 10;
        List<Transaction> transactions = new ArrayList<>();
        when(mintosAccountsService.getTransactionHistory(accountId, offset, limit)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = mintosAccountsController.getTransactionHistory(accountId, offset, limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void transferFunds_Success() throws Exception {
        String fromAccountId = "fromAccountId";
        String toAccountId = "toAccountId";
        BigDecimal amount = new BigDecimal(20);
        String exchangeCurrency = "USD";
        doNothing().when(mintosAccountsService).transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);

        ResponseEntity<String> response = mintosAccountsController.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Funds transferred successfully", response.getBody());
    }

    @Test
    void transferFunds_Failure() throws Exception {
        String fromAccountId = "fromAccountId";
        String toAccountId = "toAccountId";
        BigDecimal amount = BigDecimal.TEN;
        String exchangeCurrency = "USD";
        String errorMessage = "Transfer failed";
        doThrow(new RuntimeException(errorMessage)).when(mintosAccountsService).transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);

        ResponseEntity<String> response = mintosAccountsController.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
}

