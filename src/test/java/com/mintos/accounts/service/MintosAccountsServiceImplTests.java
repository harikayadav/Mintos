package com.mintos.accounts.service;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mintos.accounts.entity.Account;
import com.mintos.accounts.entity.Transaction;
import com.mintos.accounts.repository.AccountRepository;
import com.mintos.accounts.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class MintosAccountsServiceImplTests {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private CurrencyExchangeService currencyExchangeService;

	@InjectMocks
	private MintosAccountsServiceImpl mintosAccountsService;

	@Test
	void testGetAccountsByClientId() {
		Long clientId = 1L;
		List<Account> mockAccounts = new ArrayList<>();
		when(accountRepository.findByClientId(clientId)).thenReturn(mockAccounts);

		mintosAccountsService.getAccountsByClientId(clientId);

		verify(accountRepository).findByClientId(clientId);
	}

	@Test
	void testGetTransactionHistory() {
		Long accountId = 1L;
		int offset = 0;
		int limit = 10;
		List<Transaction> mockTransactions = new ArrayList<>();
		when(transactionRepository.findByAccountIdOrderByTransDateDesc(accountId, PageRequest.of(offset, limit)))
				.thenReturn(mockTransactions);

		mintosAccountsService.getTransactionHistory(accountId, offset, limit);

		verify(transactionRepository).findByAccountIdOrderByTransDateDesc(accountId, PageRequest.of(offset, limit));
	}

	@Test
	void testTransferFunds() throws Exception {
		String fromAccountId = "98700";
		String toAccountId = "98799";
		BigDecimal amount = new BigDecimal("20");
		String exchangeCurrency = "USD";

		Account fromAccount = new Account();
		fromAccount.setAccountNumber(fromAccountId);
		fromAccount.setBalance(new BigDecimal("100.00"));
		fromAccount.setCurrency("USD");

		Account toAccount = new Account();
		toAccount.setAccountNumber(toAccountId);
		toAccount.setBalance(new BigDecimal("50.00"));
		toAccount.setCurrency("USD");

		when(accountRepository.findByAccountNumber(fromAccountId)).thenReturn(Optional.of(fromAccount));
		when(accountRepository.findByAccountNumber(toAccountId)).thenReturn(Optional.of(toAccount));

		mintosAccountsService.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);

		verify(accountRepository, times(2)).save(any());
	}

	@Test
	void testTransferFundsInsufficientFunds() {
		String fromAccountId = "fromAccount";
		String toAccountId = "toAccount";
		BigDecimal amount = new BigDecimal("300.00");
		String exchangeCurrency = "USD";

		Account fromAccount = new Account();
		fromAccount.setAccountNumber(fromAccountId);
		fromAccount.setBalance(new BigDecimal("200.00"));
		fromAccount.setCurrency("USD");

		Account toAccount = new Account();
		toAccount.setAccountNumber(fromAccountId);
		toAccount.setBalance(new BigDecimal("100.00"));
		toAccount.setCurrency("USD");

		when(accountRepository.findByAccountNumber(fromAccountId)).thenReturn(Optional.of(fromAccount));
		when(accountRepository.findByAccountNumber(toAccountId)).thenReturn(Optional.of(toAccount));

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> mintosAccountsService.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency));

		verify(accountRepository, never()).save(any());
		assertEquals("Insufficient funds in the from account", exception.getMessage());
	}

	@Test
	void testTransferFundsDifferentCurrency() {
		String fromAccountId = "fromAccount";
		String toAccountId = "toAccount";
		BigDecimal amount = new BigDecimal("100.00");
		String exchangeCurrency = "EUR";

		Account fromAccount = new Account();
		fromAccount.setAccountNumber(fromAccountId);
		fromAccount.setBalance(new BigDecimal("200.00"));
		fromAccount.setCurrency("USD");

		Account toAccount = new Account();
		toAccount.setAccountNumber(toAccountId);
		toAccount.setBalance(new BigDecimal("50.00"));
		toAccount.setCurrency("USD");

		when(accountRepository.findByAccountNumber(fromAccountId)).thenReturn(Optional.of(fromAccount));
		when(accountRepository.findByAccountNumber(toAccountId)).thenReturn(Optional.of(toAccount));

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> mintosAccountsService.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency));

		verify(accountRepository, never()).save(any());
		assertEquals("Receiver's Account Currency does not match the Exchange Currency", exception.getMessage());
	}

	@Test
	void testTransferFundsFromAccountNotFound() throws Exception {
		String fromAccountId = "12345";
		String toAccountId = "34567";
		BigDecimal amount = BigDecimal.TEN;
		String exchangeCurrency = "USD";
		try {
			mintosAccountsService.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);
		} catch (RuntimeException e) {
			assertEquals("From account not found", e.getMessage());
		}
	}

	@Test
	void testTransferFundsToAccountNotFound() throws Exception {
		String fromAccountId = "12345";
		String toAccountId = "34567";
		BigDecimal amount = BigDecimal.TEN;
		String exchangeCurrency = "USD";
		
		Account fromAccount = new Account();
		fromAccount.setAccountNumber(fromAccountId);
		fromAccount.setBalance(new BigDecimal("200.00"));
		fromAccount.setCurrency("USD");
		
		when(accountRepository.findByAccountNumber(fromAccountId)).thenReturn(Optional.of(fromAccount));
		
		try {
			mintosAccountsService.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);
		} catch (RuntimeException e) {
			assertEquals("To account not found", e.getMessage());
		}
	}

}
