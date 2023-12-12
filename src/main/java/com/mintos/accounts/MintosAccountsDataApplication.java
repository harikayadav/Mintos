package com.mintos.accounts;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootApplication
@Configuration
public class MintosAccountsDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(MintosAccountsDataApplication.class, args);
	}

	@Bean
	@Qualifier("circuitBreakerCurrencyExchangeConfig")
	public static final CircuitBreakerConfig circuitBreakerConfig() {
		return CircuitBreakerConfig.custom().failureRateThreshold(5).slidingWindowSize(2).minimumNumberOfCalls(3)
				.waitDurationInOpenState(Duration.ofSeconds(5)).build();
	}

	@Bean
	@Qualifier("circuitBreakerCurrencyExchange")
	public CircuitBreaker circuitBreaker(
			@Qualifier("circuitBreakerCurrencyExchangeConfig") CircuitBreakerConfig circuitBreakerConfig) {
		CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
		return circuitBreakerRegistry.circuitBreaker("circuitBreakerCurrencyExchange");
	}

	@Bean
	@Qualifier("currencyExchangeRestTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
