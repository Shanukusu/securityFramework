package ru.shanina.securityframework.core_policy.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Policy Engine Resilience Configuration
 * Circuit Breaker для защиты от OPA сбоев
 */
@Slf4j
@Component
public class PolicyEngineResilience {

    private final CircuitBreaker opaCircuitBreaker;

    public PolicyEngineResilience() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)          // 50% ошибок = открыть
            .slowCallRateThreshold(50)         // 50% медленных запросов
            .slowCallDurationThreshold(Duration.ofSeconds(2))  // >2сек = медленный
            .waitDurationInOpenState(Duration.ofSeconds(30))   // 30сек до half-open
            .permittedNumberOfCallsInHalfOpenState(3)         // 3 попытки в half-open
            .minimumNumberOfCalls(5)           // Минимум 5 вызовов для расчета
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.opaCircuitBreaker = registry.circuitBreaker("opa-policy-engine");

        log.info("Policy Engine Circuit Breaker configured");
    }

    /**
     * Выполнить операцию с защитой Circuit Breaker
     */
    public <T> T executeWithCircuitBreaker(Supplier<T> operation) {
        try {
            return opaCircuitBreaker.executeSupplier(operation);
        } catch (Exception e) {
            log.error("Circuit breaker blocked operation", e);
            throw e;
        }
    }

    /**
     * Получить статус Circuit Breaker
     */
    public String getCircuitBreakerStatus() {
        return opaCircuitBreaker.getState().toString();
    }

    /**
     * Получить метрики
     */
    public String getMetrics() {
        return String.format(
            "State: %s, Calls: %d, Failures: %d, Success: %d",
            opaCircuitBreaker.getState(),
            opaCircuitBreaker.getMetrics().getNumberOfBufferedCalls(),
            opaCircuitBreaker.getMetrics().getNumberOfFailedCalls(),
            opaCircuitBreaker.getMetrics().getNumberOfSuccessfulCalls()
        );
    }
}

