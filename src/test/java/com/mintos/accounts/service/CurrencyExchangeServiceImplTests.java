package com.mintos.accounts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

	@Test
	void testConvertCurrency() throws Exception {

		when(circuitBreaker.executeCallable(any())).thenReturn(new BigDecimal(1.5));

		BigDecimal result = currencyExchangeService.convertCurrency(new BigDecimal(100), "USD", "EUR");

		assertEquals(new BigDecimal(1.5), result);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testGetConvertedCurrency() {
		when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(Class.class), any(HttpHeaders.class)))
				.thenReturn(ResponseEntity.ok("{\"conversion_rates\":{\"USD\":1.5,\"EUR\":1}}"));

		BigDecimal result = currencyExchangeService.getConvertedCurrency(new BigDecimal(100.0), "USD", "EUR");

		assertEquals(new BigDecimal(100), result);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testGetConvertedCurrencyWithRuntimeException() {

		when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(Class.class), any(HttpHeaders.class)))
				.thenThrow(new RuntimeException("Currency conversion service is unavailable. Please try Later"));

		assertThrows(RuntimeException.class,
				() -> currencyExchangeService.getConvertedCurrency(new BigDecimal(100), "USD", "EUR"));
	}
}
