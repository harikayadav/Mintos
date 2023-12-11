package com.mintos.accounts.service;

import java.math.BigDecimal;
import java.util.Collections;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mintos.accounts.MintosAccountsDataApplication;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

	@Value("${exchange.api.url}")
	private String exchangeApiUrl;

	@Autowired
	private final RestTemplate restTemplate;

	@Autowired
	private final CircuitBreaker circuitBreaker;

	public CurrencyExchangeServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.circuitBreaker = CircuitBreaker.of("currencyExchange",
				MintosAccountsDataApplication.circuitBreakerConfig());
	}

	@Override
	public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) throws Exception {
		return circuitBreaker.executeCallable(() -> getConvertedCurrency(amount, fromCurrency, toCurrency));
	}

	public BigDecimal getConvertedCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
		BigDecimal exchangeRate = new BigDecimal(0);
		String url = String.format("%s/49a1430ad3321c9293879278/latest/%s", exchangeApiUrl, fromCurrency);
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
			throw new RuntimeException("Currency conversion service is unavailable");
		}

		return exchangeRate.multiply(amount);
	}
}
