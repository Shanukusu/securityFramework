package ru.shanina.securityframework.core_supply_chain.annotation;

import java.lang.annotation.*;

/**
 * @SupplyChainSecured annotation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SupplyChainSecured {
    
    /**
     * Minimum SLSA level required
     */
    int requireSlsaLevel() default 2;
    
    /**
     * Verify artifact signatures
     */
    boolean verifySignatures() default true;
    
    /**
     * Check transitive CVEs
     */
    boolean checkTransitiveCves() default true;
}

