package io.helidon.examples.student.endpoint;

import io.helidon.common.Default;
import io.helidon.config.Configuration;
import io.helidon.http.Http;
import io.helidon.http.Status;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.RestServer;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helidon Declarative Greet Endpoint.
 *
 * Helidon Declarative APIs (4.3+):
 * - @Service.Singleton: Registers this endpoint as a singleton service
 * - @RestServer.Endpoint: Marks this as a REST endpoint for auto-discovery
 * - @Http.Path: Defines the base path for this endpoint
 * - @Configuration.Value: Injects configuration values via constructor
 * - @Default.Value: Provides default values if config key not found
 *
 * Examples:
 * - GET  /greet          → Returns default greeting message
 * - GET  /greet/Joe      → Returns personalized greeting for Joe
 * - PUT  /greet/greeting → Updates the greeting message
 */
@Service.Singleton
@RestServer.Endpoint
@Http.Path("/greet")
public class GreetEndpoint {

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());
    private final AtomicReference<String> greeting = new AtomicReference<>();

    /**
     * Constructor with configuration injection.
     *
     * Helidon Declarative APIs (4.3+):
     * - Configuration values are injected automatically
     * - No @Service.Inject needed - @Service.Singleton on class handles it
     */
    public GreetEndpoint(@Configuration.Value("app.greeting") @Default.Value("Hello") String greeting) {
        this.greeting.set(greeting);
    }

    /**
     * GET /greet - Return a default greeting message.
     *
     * Declarative features:
     * - @Http.GET: HTTP GET method
     * - @Http.Path("/"): Root path of this endpoint
     * - Automatic JSON serialization of JsonObject
     */
    @Http.GET
    @Http.Path("/")
    public void getDefaultMessage(ServerRequest request, ServerResponse response) {
        sendResponse(response, "World");
    }

    /**
     * GET /greet/{name} - Return a personalized greeting message.
     *
     * Declarative features:
     * - @Http.PathParam("name"): Extracts path parameter automatically
     * - Type-safe parameter binding
     */
    @Http.GET
    @Http.Path("/{name}")
    public void getMessage(ServerRequest request,
                          ServerResponse response,
                          @Http.PathParam("name") String name) {
        sendResponse(response, name);
    }

    /**
     * PUT /greet/greeting - Update the greeting message.
     *
     * Declarative features:
     * - @Http.PUT: HTTP PUT method
     * - @Http.Entity: Automatic request body deserialization
     * - Type-safe entity binding (JSON → JsonObject)
     */
    @Http.PUT
    @Http.Path("/greeting")
    public void updateGreeting(ServerRequest request,
                              ServerResponse response,
                              @Http.Entity JsonObject jsonObject) {
        updateGreetingFromJson(jsonObject, response);
    }

    /**
     * Helper method to send greeting response.
     */
    private void sendResponse(ServerResponse response, String name) {
        String msg = String.format("%s %s!", greeting.get(), name);

        JsonObject returnObject = JSON.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }

    /**
     * Helper method to update greeting from JSON.
     */
    private void updateGreetingFromJson(JsonObject jo, ServerResponse response) {
        if (!jo.containsKey("greeting")) {
            JsonObject jsonErrorObject = JSON.createObjectBuilder()
                    .add("error", "No greeting provided")
                    .build();
            response.status(Status.BAD_REQUEST_400)
                    .send(jsonErrorObject);
            return;
        }

        greeting.set(jo.getString("greeting"));
        response.status(Status.NO_CONTENT_204).send();
    }
}
