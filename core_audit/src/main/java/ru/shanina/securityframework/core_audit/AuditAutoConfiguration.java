package ru.shanina.securityframework.core_audit;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("ru.shanina.securityframework.core_audit")
public class AuditAutoConfiguration {
}