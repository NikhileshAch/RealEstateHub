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
public class SellerBean implements Serializable {
    
    private static final String API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/sellers";
    
    private List<Map<String, Object>> sellers;
    private Map<String, Object> selectedSeller;
    
    @PostConstruct
    public void init() {
        loadSellers();
    }
    
    public void loadSellers() {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL);
            sellers = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Map<String, Object>>>() {});
            client.close();
        } catch (Exception e) {
            System.err.println("Error loading sellers: " + e.getMessage());
            sellers = new ArrayList<>();
        }
    }
    
    public void viewSeller(Map<String, Object> seller) {
        this.selectedSeller = seller;
    }
    
    // Getters and Setters
    public List<Map<String, Object>> getSellers() {
        return sellers;
    }
    
    public void setSellers(List<Map<String, Object>> sellers) {
        this.sellers = sellers;
    }
    
    public Map<String, Object> getSelectedSeller() {
        return selectedSeller;
    }
    
    public void setSelectedSeller(Map<String, Object> selectedSeller) {
        this.selectedSeller = selectedSeller;
    }
}
