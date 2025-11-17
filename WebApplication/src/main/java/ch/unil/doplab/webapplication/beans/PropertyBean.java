package ch.unil.doplab.webapplication.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@SessionScoped
public class PropertyBean implements Serializable {
    
    private static final String API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/properties";
    
    private List<Map<String, Object>> properties;
    private Map<String, Object> selectedProperty;
    
    @PostConstruct
    public void init() {
        loadProperties();
    }
    
    public void loadProperties() {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL);
            properties = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Map<String, Object>>>() {});
            client.close();
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            properties = new ArrayList<>();
        }
    }
    
    public void viewProperty(Map<String, Object> property) {
        this.selectedProperty = property;
    }
    
    public String getPropertyStatus(Map<String, Object> property) {
        Object status = property.get("status");
        return status != null ? status.toString() : "UNKNOWN";
    }
    
    public String formatPrice(Object price) {
        if (price instanceof Number) {
            return String.format("CHF %.2f", ((Number) price).doubleValue());
        }
        return "N/A";
    }
    
    // Getters and Setters
    public List<Map<String, Object>> getProperties() {
        return properties;
    }
    
    public void setProperties(List<Map<String, Object>> properties) {
        this.properties = properties;
    }
    
    public Map<String, Object> getSelectedProperty() {
        return selectedProperty;
    }
    
    public void setSelectedProperty(Map<String, Object> selectedProperty) {
        this.selectedProperty = selectedProperty;
    }
}
