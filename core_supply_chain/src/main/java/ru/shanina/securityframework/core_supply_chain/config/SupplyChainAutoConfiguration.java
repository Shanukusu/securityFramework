package ru.shanina.securityframework.core_supply_chain.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-Configuration for Supply Chain Security Module
 */
@AutoConfiguration
@Configuration
@ConditionalOnProperty(name = "securitas.supply-chain.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("ru.shanina.securityframework.core_supply_chain")
public class SupplyChainAutoConfiguration {

    public SupplyChainAutoConfiguration() {
        System.out.println("✅ Supply Chain Security Module initialized");
    }
}

