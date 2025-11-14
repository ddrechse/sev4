package io.helidon.examples.student;

import io.helidon.http.Status;
import io.helidon.webclient.api.ClientResponseTyped;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;

import org.junit.jupiter.api.Test;
import jakarta.json.JsonObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Abstract test class for Main application.
 *
 * Note: With Helidon Declarative, we no longer need @SetUpRoute
 * because endpoints are auto-discovered and registered by the
 * ServiceRegistryManager. Tests use the full application context.
 */
abstract class AbstractMainTest {
    private final Http1Client client;

    protected AbstractMainTest(Http1Client client) {
        this.client = client;
    }

    @Test
    void testGreeting() {
        ClientResponseTyped<JsonObject> response = client.get("/greet").request(JsonObject.class);
        assertThat(response.status(), is(Status.OK_200));
        assertThat(response.entity().getString("message"), is("Hello World!"));
    }

    // Metrics test disabled - COMMENTED OUT
    // @Test
    // void testMetricsObserver() {
    //     try (Http1ClientResponse response = client.get("/observe/metrics").request()) {
    //         assertThat(response.status(), is(Status.OK_200));
    //     }
    // }
}
