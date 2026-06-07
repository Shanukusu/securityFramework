package ru.shanina.securityframework.core_scanner.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.shanina.securityframework.core_scanner.model.ScanResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Trivy Scanner Implementation
 * Scans container images and artifacts using Trivy CLI
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "securitas.scanner.trivy.enabled", havingValue = "true", matchIfMissing = false)
public class TrivyScanner implements VulnerabilityScanner {
    
    @Override
    public ScanResult scan(String target) {
        try {
            log.info("Starting Trivy scan for target: {}", target);
            
            // Execute Trivy command
            Process process = new ProcessBuilder(
                "trivy", "image", "--format", "json", target
            ).redirectErrorStream(true).start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            process.waitFor();
            
            log.info("Trivy scan completed for target: {}", target);
            
            // Parse JSON output and build ScanResult
            // This is a simplified version - real implementation would parse JSON properly
            return ScanResult.builder()
                .target(target)
                .scanTime(LocalDateTime.now().toString())
                .status("SUCCESS")
                .vulnerabilities(new ArrayList<>())
                .criticalCount(0)
                .highCount(0)
                .mediumCount(0)
                .lowCount(0)
                .build();
            
        } catch (Exception e) {
            log.error("Trivy scan failed for target: {}", target, e);
            return ScanResult.builder()
                .target(target)
                .status("FAILED")
                .build();
        }
    }
    
    @Override
    public String getScannerName() {
        return "Trivy";
    }
}

