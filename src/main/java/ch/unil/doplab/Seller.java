package ch.unil.doplab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Seller extends User {
    private final List<Property> ownedProperties;
    private final List<Offer> receivedOffers;

    public Seller(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password);
        this.ownedProperties = new ArrayList<>();
        this.receivedOffers = new ArrayList<>();
    }

    public List<Property> getOwnedProperties() { return Collections.unmodifiableList(ownedProperties); }
    public List<Offer> getReceivedOffers() { return Collections.unmodifiableList(receivedOffers); }

    public Property createProperty(String title, String description, String location,
                                   double price, double size, Property.PropertyType type) {
        Property property = new Property(title, this.getUserID(), description, location, price, size, type);
        ownedProperties.add(property);
        return property;
    }

    public void publishProperty(Property property) {
        if (property == null) throw new IllegalArgumentException("property is required");
        
        // Ensure seller owns this property
        if (!property.getOwnerId().equals(this.getUserID())) {
            throw new IllegalArgumentException("Seller can only publish properties they own");
        }
        
        if (!ownedProperties.contains(property)) {
            ownedProperties.add(property);
        }
        property.publish();
    }

    public void respondToOffer(Offer offer, boolean accept) {
        if (offer == null) throw new IllegalArgumentException("offer is required");
        
        // Verify this offer is for one of the seller's properties
        boolean ownsProperty = ownedProperties.stream()
                .anyMatch(p -> p.getPropertyId().equals(offer.getPropertyId()));
        
        if (!ownsProperty) {
            throw new IllegalArgumentException("Seller can only respond to offers for their own properties");
        }
        
        if (!receivedOffers.contains(offer)) {
            receivedOffers.add(offer);
        }
        offer.setStatus(accept ? Offer.Status.ACCEPTED : Offer.Status.REJECTED);
    }

    @Override
    public String getRole() {
        return "Seller";
    }
}
