package ch.unil.doplab.webservice_realsestatehub;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// Used AI to help write this code - JAX-RS Application configuration

@ApplicationPath("/api")
public class JakartaRestConfiguration extends Application {
    // No additional configuration needed
    // JAX-RS will auto-discover all @Path annotated classes
}
