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

@Named
@SessionScoped
public class BuyerBean implements Serializable {
    
    private static final String API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/buyers";
    
    private List<Map<String, Object>> buyers;
    private Map<String, Object> selectedBuyer;
    
    @PostConstruct
    public void init() {
        loadBuyers();
    }
    
    public void loadBuyers() {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL);
            buyers = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Map<String, Object>>>() {});
            client.close();
        } catch (Exception e) {
            System.err.println("Error loading buyers: " + e.getMessage());
            buyers = new ArrayList<>();
        }
    }
    
    public void viewBuyer(Map<String, Object> buyer) {
        this.selectedBuyer = buyer;
    }
    
    public String formatBudget(Object budget) {
        if (budget instanceof Number) {
            return String.format("CHF %.2f", ((Number) budget).doubleValue());
        }
        return "N/A";
    }
    
    // Getters and Setters
    public List<Map<String, Object>> getBuyers() {
        return buyers;
    }
    
    public void setBuyers(List<Map<String, Object>> buyers) {
        this.buyers = buyers;
    }
    
    public Map<String, Object> getSelectedBuyer() {
        return selectedBuyer;
    }
    
    public void setSelectedBuyer(Map<String, Object> selectedBuyer) {
        this.selectedBuyer = selectedBuyer;
    }
}
