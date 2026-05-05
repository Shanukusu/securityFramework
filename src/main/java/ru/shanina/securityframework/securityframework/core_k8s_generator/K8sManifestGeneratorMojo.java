package ru.shanina.securityframework.securityframework.core_k8s_generator;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

@Mojo(name = "k8s-generate", defaultPhase = LifecyclePhase.PACKAGE)
public class K8sManifestGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "kubernetes.outputDir", defaultValue = "${project.build.directory}/kubernetes")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) outputDirectory.mkdirs();

        generateDeployment();
        generateNetworkPolicy();

        getLog().info("Kubernetes manifests generated in " + outputDirectory.getAbsolutePath());
    }

    private void generateDeployment() {
        Map<String, Object> deployment = new LinkedHashMap<>();
        deployment.put("apiVersion", "apps/v1");
        deployment.put("kind", "Deployment");
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("name", project.getArtifactId());
        metadata.put("labels", Map.of("app", project.getArtifactId()));
        deployment.put("metadata", metadata);

        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("replicas", 2);
        spec.put("selector", Map.of("matchLabels", Map.of("app", project.getArtifactId())));

        Map<String, Object> podTemplate = new LinkedHashMap<>();
        Map<String, Object> podMetadata = Map.of("labels", Map.of("app", project.getArtifactId()));
        podTemplate.put("metadata", podMetadata);

        Map<String, Object> podSpec = new LinkedHashMap<>();
        podSpec.put("securityContext", Map.of(
                "runAsNonRoot", true,
                "runAsUser", 1000,
                "seccompProfile", Map.of("type", "RuntimeDefault")
        ));
        Map<String, Object> container = new LinkedHashMap<>();
        container.put("name", project.getArtifactId());
        container.put("image", "registry.gitlab.com/mycompany/" + project.getArtifactId() + ":latest");
        container.put("imagePullPolicy", "Always");
        container.put("ports", List.of(Map.of("containerPort", 8080, "name", "http")));
        container.put("securityContext", Map.of(
                "allowPrivilegeEscalation", false,
                "readOnlyRootFilesystem", true,
                "capabilities", Map.of("drop", List.of("ALL"))
        ));
        container.put("livenessProbe", Map.of(
                "httpGet", Map.of("path", "/actuator/health/liveness", "port", 8080),
                "initialDelaySeconds", 30, "periodSeconds", 10
        ));
        container.put("readinessProbe", Map.of(
                "httpGet", Map.of("path", "/actuator/health/readiness", "port", 8080),
                "initialDelaySeconds", 15, "periodSeconds", 5
        ));
        container.put("resources", Map.of(
                "requests", Map.of("memory", "512Mi", "cpu", "200m"),
                "limits", Map.of("memory", "1Gi", "cpu", "500m")
        ));
        container.put("env", List.of(Map.of(
                "name", "SPRING_PROFILES_ACTIVE",
                "value", "k8s"
        )));
        podSpec.put("containers", List.of(container));
        podTemplate.put("spec", podSpec);
        spec.put("template", podTemplate);
        deployment.put("spec", spec);

        writeYaml(deployment, "deployment.yaml");
    }

    private void generateNetworkPolicy() {
        Map<String, Object> policy = new LinkedHashMap<>();
        policy.put("apiVersion", "networking.k8s.io/v1");
        policy.put("kind", "NetworkPolicy");
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("name", project.getArtifactId() + "-isolation");
        policy.put("metadata", metadata);

        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("podSelector", Map.of("matchLabels", Map.of("app", project.getArtifactId())));
        spec.put("policyTypes", List.of("Ingress", "Egress"));
        spec.put("ingress", List.of(Map.of(
                "from", List.of(Map.of("namespaceSelector", Map.of("matchLabels", Map.of("name", "ingress-namespace")))),
                "ports", List.of(Map.of("protocol", "TCP", "port", 8080))
        )));
        spec.put("egress", List.of(
                Map.of("to", List.of(Map.of("podSelector", Map.of("matchLabels", Map.of("app", "postgresql")))),
                        "ports", List.of(Map.of("protocol", "TCP", "port", 5432))),
                Map.of("to", List.of(Map.of("namespaceSelector", Map.of(), "podSelector", Map.of("matchLabels", Map.of("app", "vault-agent")))),
                        "ports", List.of(Map.of("protocol", "TCP", "port", 8200)))
        ));
        policy.put("spec", spec);

        writeYaml(policy, "network-policy.yaml");
    }

    private void writeYaml(Map<String, Object> data, String filename) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(new File(outputDirectory, filename))) {
            yaml.dump(data, writer);
        } catch (Exception e) {
            getLog().error("Failed to write " + filename, e);
        }
    }
}