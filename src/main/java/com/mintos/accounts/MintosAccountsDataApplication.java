package com.mintos.accounts;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@SpringBootApplication
@Configuration
public class MintosAccountsDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(MintosAccountsDataApplication.class, args);
	}

	@Bean
	public static final CircuitBreakerConfig circuitBreakerConfig() {
		return CircuitBreakerConfig.custom().failureRateThreshold(5).slidingWindowSize(2).minimumNumberOfCalls(3)
				.permittedNumberOfCallsInHalfOpenState(2).waitDurationInOpenState(Duration.ofSeconds(5))
				.recordExceptions(RuntimeException.class).build();
	}

	@Bean
	public CircuitBreaker circuitBreaker() {
		return CircuitBreaker.ofDefaults("currencyExchange");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
