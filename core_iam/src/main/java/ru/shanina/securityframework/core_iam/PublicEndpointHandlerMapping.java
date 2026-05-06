package ru.shanina.securityframework.core_iam;


import org.springframework.context.ApplicationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            HandlerMethod handler = entry.getValue();
            if (handler.hasMethodAnnotation(PublicEndpoint.class) ||
                    handler.getBeanType().isAnnotationPresent(PublicEndpoint.class)) {
                Set<String> patterns = entry.getKey().getPatternsCondition().getPatterns();
                for (String pattern : patterns) {
                    matchers.add(new AntPathRequestMatcher(pattern));
                }
            }
        }
        this.publicEndpointsMatcher = new OrRequestMatcher(matchers);
    }


    public RequestMatcher getPublicEndpointsMatcher() {
        return publicEndpointsMatcher;
    }
}