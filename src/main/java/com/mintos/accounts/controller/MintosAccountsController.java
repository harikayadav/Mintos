package com.mintos.accounts.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mintos.accounts.entity.Account;
import com.mintos.accounts.entity.Transaction;
import com.mintos.accounts.service.MintosAccountsService;

@RestController
@RequestMapping("/api/accounts")
public class MintosAccountsController {

	@Autowired
	private MintosAccountsService mintosAccountsService;

	@GetMapping("/getAccounts/{clientId}")
	public ResponseEntity<List<Account>> getAccountsByClientId(@PathVariable("clientId") Long clientId) {
		return ResponseEntity.ok(mintosAccountsService.getAccountsByClientId(clientId));
	}

	@GetMapping("/getTransactions/{accountId}")
	public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable("accountId") Long accountId,
			@RequestParam(name = "offset", defaultValue = "0") int offset,
			@RequestParam(name = "limit", defaultValue = "10") int limit) {
		return ResponseEntity.ok(mintosAccountsService.getTransactionHistory(accountId, offset, limit));
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> transferFunds(@RequestParam("fromAccountId") String fromAccountId,
			@RequestParam("toAccountId") String toAccountId, @RequestParam("amount") BigDecimal amount,
			@RequestParam("exchangeCurrency") String exchangeCurrency) {
		try {
			mintosAccountsService.transferFunds(fromAccountId, toAccountId, amount, exchangeCurrency);
			return ResponseEntity.ok("Funds transferred successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
