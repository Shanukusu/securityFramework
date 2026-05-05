package ru.shanina.securityframework.securityframework.core_audit;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.vsu.securitas.audit")
public class AuditAutoConfiguration {
}