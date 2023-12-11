package com.mintos.accounts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeServiceImplTests {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private CircuitBreaker circuitBreaker;

	@InjectMocks
	private CurrencyExchangeServiceImpl currencyExchangeService;

	@SuppressWarnings("unchecked")
	@Test
	void testConvertCurrency() throws Exception {
		BigDecimal amount = new BigDecimal("100.00");
		String fromCurrency = "USD";
		String toCurrency = "EUR";

		String responseBody = "{\"conversion_rates\": {\"EUR\": 0.85}}";
		ResponseEntity<String> responseEntity = ResponseEntity.ok(responseBody);
		when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(Class.class), any(HttpHeaders.class)))
				.thenReturn(responseEntity);

		BigDecimal result = currencyExchangeService.convertCurrency(amount, fromCurrency, toCurrency);

		assertEquals(new BigDecimal("85.0000"), result);
	}
}
