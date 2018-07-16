package io.microprofile.showcase.session;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

/**
 * Illustration of new MP-1.2 health check for the session application
 */


/**
 * Test the health check by starting WildFly Swarm and using the RESTClient Firefox plug-in and accessing
 * http://localhost:8080/health URL. By default, the microservice-session microservice does not have any session loaded,
 * which causes the health check to return a DOWN state. To avoid this condition, you may customize the loadSampleData
 * property from the MicroProfile configuration specification to load sessions during the start up process. Use the
 * following command to change the property value:
 *
 * [student@workstation microprofile-conference]$ export loadSampleData=true
 */
@Health
@ApplicationScoped
public class SessionCheck implements HealthCheck {

    @Inject
    private SessionStore sessionStore;

    // Get the name for the session count health data from configuration
    // Use MP 1.2 configuration
    @Inject
    @ConfigProperty(name = "sessionCountName", defaultValue = "sessionCount")
    private String sessionCountName;

    @Override
    public HealthCheckResponse call() {
        // Return a response named 'sessions-check'
        // Return data:
        // - count of sessions contained in the data store
        // - date the health check occurred as 'lastCheckDate'
        // The application is 'up' is session count > 0 otherwise 'down'
        long sessionCount = sessionStore.getSessions().size();

        HealthCheckResponseBuilder healthCheckResponse = HealthCheckResponse.named("sessions-check")
            .withData(sessionCountName, sessionCount)
            .withData("lastCheckData", new Date().toString());

        return (sessionCount > 0) ? healthCheckResponse.up().build() : healthCheckResponse.down().build();
    }
}
