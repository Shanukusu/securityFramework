package ru.shanina.securityframework.core_audit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = AuditAutoConfiguration.class)
public class AuditAutoConfigurationTest {

    @Test
    public void contextLoads() {
        // Test that the configuration loads without errors
    }
}
