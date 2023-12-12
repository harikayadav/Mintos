package com.mintos.accounts.service;

import java.math.BigDecimal;
import java.util.Collections;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

	@Value("${exchange.api.url}")
	private String exchangeApiUrl;

	@Value("${exchange.api.key}")
	private String exchangeApiKey;

	@Autowired
	@Qualifier("currencyExchangeRestTemplate")
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier("circuitBreakerCurrencyExchange")
	private CircuitBreaker circuitBreaker;

	@Override
	public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws Exception {
		return circuitBreaker.executeCallable(() -> getConvertedCurrency(amount, fromCurrency, toCurrency));
	}

	public BigDecimal getConvertedCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
		BigDecimal exchangeRate = new BigDecimal(0);
		String url = String.format("%s/%s/latest/%s", exchangeApiUrl, exchangeApiKey, fromCurrency);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class, headers);
			if (response.getBody() != null && response.getBody().contains("conversion_rates")) {
				JSONObject json = new JSONObject(response.getBody());
				JSONObject conversionRates = json.getJSONObject("conversion_rates");
				exchangeRate = conversionRates.getBigDecimal(toCurrency);
			}
		} catch (RuntimeException exception) {
			throw new RuntimeException("Currency conversion service is unavailable. Please try Later");
		}

		return exchangeRate.multiply(amount);
	}
}
