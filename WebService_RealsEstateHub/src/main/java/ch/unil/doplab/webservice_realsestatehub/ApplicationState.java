package ch.unil.doplab.webservice_realsestatehub;

import ch.unil.doplab.Buyer;
import ch.unil.doplab.Seller;
import ch.unil.doplab.Property;
import ch.unil.doplab.Offer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Application-scoped bean that holds all domain objects
 * Pre-populates demo data on initialization
 */
@ApplicationScoped
public class ApplicationState {
    
    private Map<UUID, Property> properties;
    private Map<UUID, Offer> offers;
    private Map<UUID, Buyer> buyers;
    private Map<UUID, Seller> sellers;
    
    // Pre-loaded demo users
    private Buyer alice;
    private Buyer jonathan;
    private Seller demoSeller;
    
    @PostConstruct
    public void init() {
        // Initialize collections
        properties = new HashMap<>();
        offers = new HashMap<>();
        buyers = new HashMap<>();
        sellers = new HashMap<>();
        
        // Pre-populate demo buyers
        alice = new Buyer("Alice", "Martin", "alice@demo.com", "alice", "pass123", 350000);
        jonathan = new Buyer("Jonathan", "Grossrieder", "jonathan.grossrieder@unil.ch", "Jon", "pass456", 550000);
        
        buyers.put(alice.getUserID(), alice);
        buyers.put(jonathan.getUserID(), jonathan);
        
        // Pre-populate demo seller
        demoSeller = new Seller("John", "Smith", "john.smith@realestate.com", "johnsmith", "seller123");
        sellers.put(demoSeller.getUserID(), demoSeller);
        
        System.out.println("ApplicationState initialized with demo data:");
        System.out.println("  - " + buyers.size() + " buyers");
        System.out.println("  - " + sellers.size() + " sellers");
    }
    
    // Getters for collections
    public Map<UUID, Property> getProperties() {
        return properties;
    }
    
    public Map<UUID, Offer> getOffers() {
        return offers;
    }
    
    public Map<UUID, Buyer> getBuyers() {
        return buyers;
    }
    
    public Map<UUID, Seller> getSellers() {
        return sellers;
    }
    
    // Getters for pre-loaded demo users
    public Buyer getAlice() {
        return alice;
    }
    
    public Buyer getJonathan() {
        return jonathan;
    }
    
    public Seller getDemoSeller() {
        return demoSeller;
    }
    
    // Helper method to get buyer by ID
    public Buyer getBuyerById(UUID buyerId) {
        return buyers.get(buyerId);
    }
    
    // Helper method to get seller by ID
    public Seller getSellerById(UUID sellerId) {
        return sellers.get(sellerId);
    }
}
