package com.mintos.accounts.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mintos.accounts.entity.Account;
import com.mintos.accounts.entity.Transaction;
import com.mintos.accounts.repository.AccountRepository;
import com.mintos.accounts.repository.TransactionRepository;

@Service
public class MintosAccountsServiceImpl implements MintosAccountsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private CurrencyExchangeService currencyExchangeService;

	@Override
	public List<Account> getAccountsByClientId(Long clientId) {
		return accountRepository.findByClientId(clientId);
	}

	@Override
	public List<Transaction> getTransactionHistory(Long accountId, int offset, int limit) {
		return transactionRepository.findByAccountIdOrderByTransDateDesc(accountId, PageRequest.of(offset, limit));
	}

	@Override
	public void transferFunds(String fromAccountId, String toAccountId, BigDecimal amount, String exchangeCurrency)
			throws Exception {
		Account fromAccount = accountRepository.findByAccountNumber(fromAccountId)
				.orElseThrow(() -> new RuntimeException("From account not found"));

		Account toAccount = accountRepository.findByAccountNumber(toAccountId)
				.orElseThrow(() -> new RuntimeException("To account not found"));

		if (!toAccount.getCurrency().equalsIgnoreCase(exchangeCurrency)) {
			throw new RuntimeException("Receiver's Account Currency does not match the Exchange Currency");
		}

		if (fromAccount.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient funds in the from account");
		}
		if (!fromAccount.getCurrency().equals(exchangeCurrency)) {
			BigDecimal convertedAmount = currencyExchangeService.convertCurrency(amount, fromAccount.getCurrency(),
					exchangeCurrency);
			fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
			toAccount.setBalance(toAccount.getBalance().add(convertedAmount));
		} else {
			fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
			toAccount.setBalance(toAccount.getBalance().add(amount));
		}

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);
	}

}
