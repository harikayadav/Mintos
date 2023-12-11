package com.mintos.accounts;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@SpringBootTest
class MintosAccountsDataApplicationTests {

	@Autowired
	private CircuitBreaker circuitBreaker;

	@Autowired
	private RestTemplate restTemplate;

	@Test
	void contextLoads() {
		assertNotNull(circuitBreaker, "CircuitBreaker bean should not be null");
		assertNotNull(restTemplate, "RestTemplate bean should not be null");
	}

	@Test
	void circuitBreakerBeanNotNull() {
		assertNotNull(circuitBreaker, "CircuitBreaker bean should not be null");
	}

	@Test
	void restTemplateBeanNotNull() {
		assertNotNull(restTemplate, "RestTemplate bean should not be null");
	}

}
