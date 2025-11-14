package io.helidon.examples.student;

import io.helidon.config.Config;
import io.helidon.logging.common.LogConfig;
import io.helidon.webserver.WebServer;

/**
 * Main class for the Student Management Service using Helidon SE 4.x Declarative APIs.
 *
 * This application demonstrates:
 * 1. **Helidon REST Endpoints** - @RestServer.Endpoint with annotations
 * 2. **Helidon Data Repository** - JPA/Hibernate with automatic query generation
 * 3. **Dependency Injection** - Constructor injection via ServiceRegistry
 * 4. **Runtime Service Discovery** - Services discovered at runtime via annotations
 * 5. **OpenTelemetry tracing** (auto-configured)
 *
 * Note: Using runtime discovery instead of ApplicationBinding due to null descriptor issue
 * in helidon-service-maven-plugin. This is a workaround until the root cause is resolved.
 */
public class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments (not used)
     * @throws InterruptedException if the main thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        // Configure logging
        LogConfig.configureRuntime();

        // Load configuration from application.yaml + environment variables
        Config config = Config.create();

        // Start WebServer with runtime service discovery
        // Services (@RestServer.Endpoint, @Data.Repository) are auto-discovered
        WebServer server = WebServer.builder()
                .config(config.get("server"))
                .build()
                .start();

        System.out.println("Student Service started on port: " + server.port());
        System.out.println("Health: http://localhost:" + server.port() + "/observe/health");
        // System.out.println("Metrics: http://localhost:" + server.port() + "/observe/metrics");

        // Keep JVM alive - DO NOT REMOVE
        Thread.currentThread().join();
    }

}
