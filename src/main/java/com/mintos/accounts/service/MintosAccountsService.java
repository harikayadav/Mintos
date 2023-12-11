package com.mintos.accounts.service;

import java.math.BigDecimal;
import java.util.List;

import com.mintos.accounts.entity.Account;
import com.mintos.accounts.entity.Transaction;

public interface MintosAccountsService {

	List<Account> getAccountsByClientId(Long clientId);

	List<Transaction> getTransactionHistory(Long accountId, int offset, int limit);

	void transferFunds(String fromAccountId, String toAccountId, BigDecimal amount, String exchangeCurrency)
			throws Exception;

}
