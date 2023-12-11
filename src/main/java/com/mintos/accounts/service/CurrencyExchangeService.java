package com.mintos.accounts.service;

import java.math.BigDecimal;

public interface CurrencyExchangeService {

	BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws Exception;
}
