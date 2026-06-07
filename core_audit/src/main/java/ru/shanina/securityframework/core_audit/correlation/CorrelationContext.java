package ru.shanina.securityframework.core_audit.correlation;

import java.util.UUID;

/**
 * Correlation Context для распределённого трейсинга
 */
public class CorrelationContext {

    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();
    private static final ThreadLocal<String> traceId = new ThreadLocal<>();
    private static final ThreadLocal<String> spanId = new ThreadLocal<>();

    /**
     * Установить Correlation ID
     */
    public static void setCorrelationId(String id) {
        correlationId.set(id);
    }

    /**
     * Получить Correlation ID
     */
    public static String getCorrelationId() {
        String id = correlationId.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            setCorrelationId(id);
        }
        return id;
    }

    /**
     * Установить Trace ID
     */
    public static void setTraceId(String id) {
        traceId.set(id);
    }

    /**
     * Получить Trace ID
     */
    public static String getTraceId() {
        String id = traceId.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            setTraceId(id);
        }
        return id;
    }

    /**
     * Установить Span ID
     */
    public static void setSpanId(String id) {
        spanId.set(id);
    }

    /**
     * Получить Span ID
     */
    public static String getSpanId() {
        String id = spanId.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            setSpanId(id);
        }
        return id;
    }

    /**
     * Очистить context (при завершении обработки)
     */
    public static void clear() {
        correlationId.remove();
        traceId.remove();
        spanId.remove();
    }
}

