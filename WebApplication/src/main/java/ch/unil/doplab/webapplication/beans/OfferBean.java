package ch.unil.doplab.webapplication.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Used AI to help write this code.

@Named
@SessionScoped
public class OfferBean implements Serializable {
    private static final String OFFERS_API = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/offers";
    private static final String PROPERTIES_API = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/properties";
    
    @Inject
    private SessionBean sessionBean;
    
    private List<Map<String, Object>> offers;
    private Map<String, Object> selectedOffer;
    
    // Form fields for creating offer
    private String propertyId;
    private BigDecimal offerAmount;
    private String message;
    
    // Filter fields
    private String filterStatus;
    
    @PostConstruct
    public void init() {
        offers = new ArrayList<>();
    }
    
    public void loadMyOffers() {
        if (!sessionBean.isLoggedIn()) {
            return;
        }
        
        Client client = ClientBuilder.newClient();
        try {
            // Get all offers and filter by buyerId
            Response response = client.target(OFFERS_API)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                List<Map<String, Object>> allOffers = response.readEntity(new GenericType<List<Map<String, Object>>>() {});
                // Filter for current buyer
                offers = new ArrayList<>();
                for (Map<String, Object> offer : allOffers) {
                    Object buyerId = offer.get("buyerId");
                    if (buyerId != null && buyerId.toString().equals(sessionBean.getUserId())) {
                        offers.add(offer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Failed to load offers: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    
    public void loadPropertyOffers(String propId) {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(OFFERS_API)
                    .path("property/" + propId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                offers = response.readEntity(new GenericType<List<Map<String, Object>>>() {});
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Failed to load offers: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    
    public void loadAllOffersForSeller() {
        if (!sessionBean.isLoggedIn() || !sessionBean.isSeller()) {
            return;
        }
        
        Client client = ClientBuilder.newClient();
        try {
            // First, get all properties owned by this seller
            Response propertiesResponse = client.target(PROPERTIES_API)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (propertiesResponse.getStatus() == 200) {
                List<Map<String, Object>> allProperties = propertiesResponse.readEntity(new GenericType<List<Map<String, Object>>>() {});
                
                // Filter for seller's properties
                List<String> sellerPropertyIds = allProperties.stream()
                        .filter(prop -> {
                            Object ownerId = prop.get("ownerId");
                            return ownerId != null && ownerId.toString().equals(sessionBean.getUserId());
                        })
                        .map(prop -> prop.get("propertyId").toString())
                        .collect(Collectors.toList());
                
                // Now get all offers
                Response offersResponse = client.target(OFFERS_API)
                        .request(MediaType.APPLICATION_JSON)
                        .get();
                
                if (offersResponse.getStatus() == 200) {
                    List<Map<String, Object>> allOffers = offersResponse.readEntity(new GenericType<List<Map<String, Object>>>() {});
                    
                    // Filter offers for seller's properties
                    offers = allOffers.stream()
                            .filter(offer -> {
                                Object propertyId = offer.get("propertyId");
                                return propertyId != null && sellerPropertyIds.contains(propertyId.toString());
                            })
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Failed to load offers: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    
    public String createOffer() {
        System.out.println("=== createOffer called ===");
        System.out.println("sessionBean: " + sessionBean);
        System.out.println("sessionBean.isLoggedIn(): " + (sessionBean != null ? sessionBean.isLoggedIn() : "null"));
        System.out.println("sessionBean.isBuyer(): " + (sessionBean != null ? sessionBean.isBuyer() : "null"));
        System.out.println("propertyId: " + propertyId);
        System.out.println("offerAmount: " + offerAmount);
        
        if (!sessionBean.isLoggedIn() || !sessionBean.isBuyer()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Only buyers can create offers");
            return null;
        }
        
        if (!validateOfferInput()) {
            return null;
        }
        
        Client client = ClientBuilder.newClient();
        try {
            Map<String, Object> offerData = new HashMap<>();
            offerData.put("buyerId", sessionBean.getUserId());
            offerData.put("propertyId", propertyId);
            offerData.put("amount", offerAmount.doubleValue());
            offerData.put("message", message); // Include buyer's message to seller
            
            System.out.println("Sending offer data: " + offerData);
            System.out.println("API URL: " + OFFERS_API);
            
            Response response = client.target(OFFERS_API)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(offerData));
            
            System.out.println("Response status: " + response.getStatus());
            
            if (response.getStatus() == 201) {
                addMessage(FacesMessage.SEVERITY_INFO, "Offer created successfully!");
                clearOfferForm();
                loadMyOffers();
                return "/buyer/my-offers?faces-redirect=true";
            } else {
                String errorMsg = response.readEntity(String.class);
                System.out.println("Error response: " + errorMsg);
                addMessage(FacesMessage.SEVERITY_ERROR, "Failed to create offer: " + errorMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in createOffer: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Error creating offer: " + e.getMessage());
        } finally {
            client.close();
        }
        return null;
    }
    
    public void acceptOffer(String offerId) {
        updateOfferStatus(offerId, "ACCEPTED");
    }
    
    public void rejectOffer(String offerId) {
        updateOfferStatus(offerId, "REJECTED");
    }
    
    private void updateOfferStatus(String offerId, String newStatus) {
        if (!sessionBean.isLoggedIn() || !sessionBean.isSeller()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Only sellers can manage offers");
            return;
        }
        
        Client client = ClientBuilder.newClient();
        try {
            Map<String, String> statusUpdate = new HashMap<>();
            statusUpdate.put("status", newStatus);
            
            Response response = client.target(OFFERS_API)
                    .path(offerId + "/status")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(statusUpdate));
            
            if (response.getStatus() == 200) {
                // Parse response to get email notification status
                Map<String, Object> responseData = response.readEntity(new GenericType<Map<String, Object>>() {});
                boolean emailSent = responseData.get("emailNotificationSent") != null && 
                                   (Boolean) responseData.get("emailNotificationSent");
                
                String message = "Offer " + newStatus.toLowerCase() + "!";
                if (emailSent) {
                    message += " Email notification sent to buyer.";
                } else {
                    message += " (Email notification simulated - check console logs)";
                }
                
                addMessage(FacesMessage.SEVERITY_INFO, message);
                
                // Reload all offers for seller
                loadAllOffersForSeller();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Failed to update offer status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error updating offer: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    
    public List<Map<String, Object>> getFilteredOffers() {
        if (filterStatus == null || filterStatus.isEmpty() || filterStatus.equals("ALL")) {
            return offers;
        }
        return offers.stream()
                .filter(offer -> filterStatus.equals(offer.get("status")))
                .collect(Collectors.toList());
    }
    
    public String getPropertyAddress(String propId) {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(PROPERTIES_API)
                    .path(propId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                Map<String, Object> property = response.readEntity(new GenericType<Map<String, Object>>() {});
                Object location = property.get("location");
                return location != null ? location.toString() : "Unknown";
            }
        } catch (Exception e) {
            // Ignore
        } finally {
            client.close();
        }
        return "Unknown";
    }
    
    public String getBuyerName(Object buyerIdObj) {
        if (buyerIdObj == null) {
            return "Unknown Buyer";
        }
        
        String buyerId = buyerIdObj.toString();
        Client client = ClientBuilder.newClient();
        try {
            // Call the buyers API to get buyer information
            Response response = client.target("http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/buyers")
                    .path(buyerId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                Map<String, Object> user = response.readEntity(new GenericType<Map<String, Object>>() {});
                Object firstName = user.get("firstName");
                Object lastName = user.get("lastName");
                
                if (firstName != null && lastName != null) {
                    return firstName + " " + lastName;
                } else if (firstName != null) {
                    return firstName.toString();
                }
                
                // Fallback to email if name not available
                Object email = user.get("email");
                if (email != null) {
                    return email.toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching buyer name: " + e.getMessage());
        } finally {
            client.close();
        }
        return "Buyer #" + buyerId.substring(0, Math.min(8, buyerId.length()));
    }
    
    public String formatAmount(Object amount) {
        if (amount == null) return "CHF 0.00";
        try {
            if (amount instanceof Number) {
                return String.format("CHF %,.2f", ((Number) amount).doubleValue());
            }
            return String.format("CHF %,.2f", Double.parseDouble(amount.toString()));
        } catch (Exception e) {
            return "CHF 0.00";
        }
    }
    
    private boolean validateOfferInput() {
        if (propertyId == null || propertyId.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Property ID is required");
            return false;
        }
        
        if (offerAmount == null || offerAmount.compareTo(BigDecimal.ZERO) <= 0) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Offer amount must be greater than 0");
            return false;
        }
        
        return true;
    }
    
    private void clearOfferForm() {
        propertyId = null;
        offerAmount = null;
        message = null;
    }
    
    private void addMessage(FacesMessage.Severity severity, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, "", detail));
    }
    
    // Getters and Setters
    public List<Map<String, Object>> getOffers() {
        return offers;
    }
    
    public void setOffers(List<Map<String, Object>> offers) {
        this.offers = offers;
    }
    
    public Map<String, Object> getSelectedOffer() {
        return selectedOffer;
    }
    
    public void setSelectedOffer(Map<String, Object> selectedOffer) {
        this.selectedOffer = selectedOffer;
    }
    
    public String getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
    
    public BigDecimal getOfferAmount() {
        return offerAmount;
    }
    
    public void setOfferAmount(BigDecimal offerAmount) {
        this.offerAmount = offerAmount;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getFilterStatus() {
        return filterStatus;
    }
    
    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }
}
