package ru.shanina.securityframework.securityframework.core_iam;


import org.springframework.context.ApplicationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PublicEndpointHandlerMapping {
    private final ApplicationContext context;
    private RequestMatcher publicEndpointsMatcher;

    public PublicEndpointHandlerMapping(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        List<RequestMatcher> matchers = new ArrayList<>();
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        Map<?, HandlerMethod> map = mapping.getHandlerMethods();

        for (Map.Entry<?, HandlerMethod> entry : map.entrySet()) {
            HandlerMethod handler = entry.getValue();
            if (handler.hasMethodAnnotation(PublicEndpoint.class) ||
                    handler.getBeanType().isAnnotationPresent(PublicEndpoint.class)) {
                // Получаем все паттерны URL из RequestMappingInfo
                var patterns = entry.getKey().toString(); // упрощённо; реально нужно парсить
                matchers.add(new AntPathRequestMatcher(extractPattern(entry.getKey())));
            }
        }
        this.publicEndpointsMatcher = new OrRequestMatcher(matchers);
    }

    private String extractPattern(Object requestMappingInfo) {
        // В реальном проекте: requestMappingInfo.getPatternsCondition().getPatterns().iterator().next()
        return "/**";
    }

    public RequestMatcher getPublicEndpointsMatcher() {
        return publicEndpointsMatcher;
    }
}