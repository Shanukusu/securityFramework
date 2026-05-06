package ru.shanina.securityframework.core_k8s_generator;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SecurityAnalyzer {
    private final Map<String, DependencyGraphNode> graph = new HashMap<>();
    private final Pattern restTemplatePattern = Pattern.compile("new RestTemplate\\(\\)|@Autowired.*RestTemplate");
    private final Pattern httpClientPattern = Pattern.compile("HttpClient|OkHttpClient");
    private final Pattern kafkaPattern = Pattern.compile("KafkaTemplate|@KafkaListener");
    private final Pattern databasePattern = Pattern.compile("JdbcTemplate|@Transactional|Repository");

    public void analyzeProject(File sourceDir) {
        log.info("Starting security analysis of {}", sourceDir);
        analyzeDirectory(sourceDir);
        log.info("Found {} services in dependency graph", graph.size());
    }

    private void analyzeDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                analyzeDirectory(file);
            } else if (file.getName().endsWith(".java")) {
                analyzeJavaFile(file);
            }
        }
    }

    private void analyzeJavaFile(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String className = extractClassName(file, content);

            DependencyGraphNode node = graph.computeIfAbsent(className, k -> new DependencyGraphNode("service", className));

            detectHttpCalls(content, node);
            detectKafka(content, node);
            detectDatabase(content, node);
        } catch (Exception e) {
            log.warn("Failed to analyze {}: {}", file.getName(), e.getMessage());
        }
    }

    private void detectHttpCalls(String content, DependencyGraphNode node) {
        Matcher m = restTemplatePattern.matcher(content);
        if (m.find()) {
            node.externalCalls.add(new DependencyGraphNode.ExternalCall("HTTP", "external-api", 443, "REST API call"));
        }
    }

    private void detectKafka(String content, DependencyGraphNode node) {
        Matcher m = kafkaPattern.matcher(content);
        if (m.find()) {
            node.externalCalls.add(new DependencyGraphNode.ExternalCall("TCP", "kafka-broker", 9092, "Kafka message broker"));
        }
    }

    private void detectDatabase(String content, DependencyGraphNode node) {
        Matcher m = databasePattern.matcher(content);
        if (m.find()) {
            node.externalCalls.add(new DependencyGraphNode.ExternalCall("TCP", "postgresql", 5432, "Database"));
        }
    }

    private String extractClassName(File file, String content) {
        Pattern p = Pattern.compile("class\\s+(\\w+)");
        Matcher m = p.matcher(content);
        return m.find() ? m.group(1) : file.getName().replace(".java", "");
    }

    public Map<String, DependencyGraphNode> getGraph() {
        return graph;
    }
}
