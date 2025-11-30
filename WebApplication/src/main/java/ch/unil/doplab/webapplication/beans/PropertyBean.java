package ch.unil.doplab.webapplication.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Used AI to help write this code - REST client integration, Stream API filtering, and GenericType usage

@Named
@SessionScoped
public class PropertyBean implements Serializable {
    
    @Inject
    private SessionBean sessionBean;
    
    private static final String API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/properties";
    
    private List<Map<String, Object>> properties;
    private Map<String, Object> selectedProperty;
    
    // Filter properties
    private String searchLocation;
    private String filterType;
    private String filterStatus;
    private Integer minBedrooms;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy;
    
    // Form properties for add/edit
    private String address;
    private String propertyType;
    private Double price;
    private Integer squareFootage;
    private Integer bedrooms;
    private Integer bathrooms;
    private String description;
    private boolean hasGarage;
    private boolean hasPool;
    private boolean hasGarden;
    
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
    
    public void loadMyProperties() {
        try {
            if (sessionBean == null || sessionBean.getUserId() == null) {
                properties = new ArrayList<>();
                return;
            }
            
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL);
            List<Map<String, Object>> allProperties = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Map<String, Object>>>() {});
            
            // Filter to get only seller's properties (by ownerId)
            properties = new ArrayList<>();
            for (Map<String, Object> prop : allProperties) {
                Object ownerId = prop.get("ownerId");
                if (ownerId != null && ownerId.toString().equals(sessionBean.getUserId())) {
                    properties.add(prop);
                }
            }
            
            client.close();
        } catch (Exception e) {
            System.err.println("Error loading my properties: " + e.getMessage());
            e.printStackTrace();
            properties = new ArrayList<>();
        }
    }
    
    public void viewProperty(Map<String, Object> property) {
        this.selectedProperty = property;
    }
    
    public String editProperty(Map<String, Object> property) {
        this.selectedProperty = property;
        
        // Populate form fields from property - API returns title, location, size, type, features
        this.address = (String) property.get("location");
        
        Object typeObj = property.get("type");
        this.propertyType = typeObj != null ? typeObj.toString() : null;
        
        Object priceObj = property.get("price");
        this.price = priceObj instanceof Number ? ((Number) priceObj).doubleValue() : null;
        
        Object sizeObj = property.get("size");
        this.squareFootage = sizeObj instanceof Number ? ((Number) sizeObj).intValue() : null;
        
        this.description = (String) property.get("description");
        
        // Extract from features map
        Object featuresObj = property.get("features");
        if (featuresObj instanceof Map) {
            Map<String, Object> features = (Map<String, Object>) featuresObj;
            
            Object bedroomsObj = features.get("bedrooms");
            this.bedrooms = bedroomsObj instanceof Number ? ((Number) bedroomsObj).intValue() : null;
            
            Object bathroomsObj = features.get("bathrooms");
            this.bathrooms = bathroomsObj instanceof Number ? ((Number) bathroomsObj).intValue() : null;
            
            Object garageObj = features.get("hasGarage");
            this.hasGarage = garageObj instanceof Boolean ? (Boolean) garageObj : false;
            
            Object poolObj = features.get("hasPool");
            this.hasPool = poolObj instanceof Boolean ? (Boolean) poolObj : false;
            
            Object gardenObj = features.get("hasGarden");
            this.hasGarden = gardenObj instanceof Boolean ? (Boolean) gardenObj : false;
        }
        
        return "/seller/edit-property?faces-redirect=true";
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
    
    // Filter methods
    public List<Map<String, Object>> getFilteredProperties() {
        if (properties == null) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> filtered = new ArrayList<>(properties);
        
        // Apply location filter
        if (searchLocation != null && !searchLocation.trim().isEmpty()) {
            String search = searchLocation.toLowerCase();
            filtered.removeIf(p -> {
                Object location = p.get("location");
                return location == null || !location.toString().toLowerCase().contains(search);
            });
        }
        
        // Apply type filter
        if (filterType != null && !filterType.isEmpty()) {
            filtered.removeIf(p -> {
                Object type = p.get("type");
                return type == null || !type.toString().equals(filterType);
            });
        }
        
        // Apply status filter
        if (filterStatus != null && !filterStatus.isEmpty()) {
            filtered.removeIf(p -> {
                Object status = p.get("status");
                return status == null || !status.toString().equals(filterStatus);
            });
        }
        
        // Apply min bedrooms filter - get from features map
        if (minBedrooms != null && minBedrooms > 0) {
            filtered.removeIf(p -> {
                Object featuresObj = p.get("features");
                if (featuresObj instanceof Map) {
                    Map<String, Object> features = (Map<String, Object>) featuresObj;
                    Object bedrooms = features.get("bedrooms");
                    if (bedrooms instanceof Number) {
                        return ((Number) bedrooms).intValue() < minBedrooms;
                    }
                }
                return true;
            });
        }
        
        // Apply price filters
        if (minPrice != null && minPrice > 0) {
            filtered.removeIf(p -> {
                Object price = p.get("price");
                if (price instanceof Number) {
                    return ((Number) price).doubleValue() < minPrice;
                }
                return true;
            });
        }
        
        if (maxPrice != null && maxPrice > 0) {
            filtered.removeIf(p -> {
                Object price = p.get("price");
                if (price instanceof Number) {
                    return ((Number) price).doubleValue() > maxPrice;
                }
                return true;
            });
        }
        
        // Apply sorting (descending - highest/largest first)
        if (sortBy != null && !sortBy.isEmpty()) {
            filtered.sort((p1, p2) -> {
                Object val1 = p1.get(sortBy);
                Object val2 = p2.get(sortBy);
                
                // Handle null values - put nulls at the end
                if (val1 == null && val2 == null) return 0;
                if (val1 == null) return 1;
                if (val2 == null) return -1;
                
                if (val1 instanceof Number && val2 instanceof Number) {
                    // Descending order (highest first)
                    return Double.compare(((Number) val2).doubleValue(), ((Number) val1).doubleValue());
                }
                
                // Fallback to string comparison (descending)
                return val2.toString().compareTo(val1.toString());
            });
        }
        
        return filtered;
    }
    
    public void clearFilters() {
        searchLocation = null;
        filterType = null;
        filterStatus = null;
        minBedrooms = null;
        minPrice = null;
        maxPrice = null;
        sortBy = null;
    }
    
    // Filter property getters and setters
    public String getSearchLocation() {
        return searchLocation;
    }
    
    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }
    
    public String getFilterType() {
        return filterType;
    }
    
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
    
    public String getFilterStatus() {
        return filterStatus;
    }
    
    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }
    
    public Integer getMinBedrooms() {
        return minBedrooms;
    }
    
    public void setMinBedrooms(Integer minBedrooms) {
        this.minBedrooms = minBedrooms;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    // Form property getters and setters
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getSquareFootage() {
        return squareFootage;
    }
    
    public void setSquareFootage(Integer squareFootage) {
        this.squareFootage = squareFootage;
    }
    
    public Integer getBedrooms() {
        return bedrooms;
    }
    
    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }
    
    public Integer getBathrooms() {
        return bathrooms;
    }
    
    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isHasGarage() {
        return hasGarage;
    }
    
    public void setHasGarage(boolean hasGarage) {
        this.hasGarage = hasGarage;
    }
    
    public boolean isHasPool() {
        return hasPool;
    }
    
    public void setHasPool(boolean hasPool) {
        this.hasPool = hasPool;
    }
    
    public boolean isHasGarden() {
        return hasGarden;
    }
    
    public void setHasGarden(boolean hasGarden) {
        this.hasGarden = hasGarden;
    }
    
    // Add property method
    public String addProperty() {
        try {
            // Create property DTO matching the REST API format
            Map<String, Object> propertyDTO = new HashMap<>();
            propertyDTO.put("title", address); // Using address as title
            propertyDTO.put("ownerId", sessionBean.getUserId()); // UUID string
            propertyDTO.put("description", description != null ? description : "");
            propertyDTO.put("location", address);
            propertyDTO.put("price", price != null ? price : 0.0);
            propertyDTO.put("size", squareFootage != null ? squareFootage.doubleValue() : 0.0);
            propertyDTO.put("type", propertyType);
            propertyDTO.put("status", "FOR_SALE");
            
            // Create features map
            Map<String, Object> features = new HashMap<>();
            features.put("bedrooms", bedrooms != null ? bedrooms : 0);
            features.put("bathrooms", bathrooms != null ? bathrooms : 0);
            features.put("hasGarage", hasGarage);
            features.put("hasPool", hasPool);
            features.put("hasGarden", hasGarden);
            propertyDTO.put("features", features);
            
            // POST to API
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL);
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(propertyDTO));
            
            if (response.getStatus() == 201 || response.getStatus() == 200) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Property listed successfully!"));
                
                // Clear form
                clearForm();
                
                // Reload properties
                loadMyProperties();
                
                client.close();
                return "/seller/my-properties?faces-redirect=true";
            } else {
                String errorMsg = response.readEntity(String.class);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to list property: " + errorMsg));
                client.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage()));
            return null;
        }
    }
    
    public String updateProperty() {
        try {
            if (selectedProperty == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No property selected."));
                return null;
            }
            
            // Create property DTO matching the REST API format
            Map<String, Object> propertyDTO = new HashMap<>();
            propertyDTO.put("title", address);
            propertyDTO.put("ownerId", sessionBean.getUserId());
            propertyDTO.put("description", description != null ? description : "");
            propertyDTO.put("location", address);
            propertyDTO.put("price", price != null ? price : 0.0);
            propertyDTO.put("size", squareFootage != null ? squareFootage.doubleValue() : 0.0);
            propertyDTO.put("type", propertyType);
            
            // Preserve status from selected property
            Object currentStatus = selectedProperty.get("status");
            propertyDTO.put("status", currentStatus != null ? currentStatus.toString() : "FOR_SALE");
            
            // Create features map
            Map<String, Object> features = new HashMap<>();
            features.put("bedrooms", bedrooms != null ? bedrooms : 0);
            features.put("bathrooms", bathrooms != null ? bathrooms : 0);
            features.put("hasGarage", hasGarage);
            features.put("hasPool", hasPool);
            features.put("hasGarden", hasGarden);
            propertyDTO.put("features", features);
            
            // PUT to API
            Object propertyId = selectedProperty.get("propertyId");
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL + "/" + propertyId);
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(propertyDTO));
            
            if (response.getStatus() == 200 || response.getStatus() == 204) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Property updated successfully!"));
                
                // Clear form
                clearForm();
                selectedProperty = null;
                
                // Reload properties
                loadMyProperties();
                
                client.close();
                return "/seller/my-properties?faces-redirect=true";
            } else {
                String errorMsg = response.readEntity(String.class);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update property: " + errorMsg));
                client.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage()));
            return null;
        }
    }
    
    public void deleteProperty(Map<String, Object> property) {
        try {
            Object propertyId = property.get("propertyId");
            
            // DELETE from API
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(API_URL + "/" + propertyId);
            Response response = target.request(MediaType.APPLICATION_JSON).delete();
            
            if (response.getStatus() == 200 || response.getStatus() == 204) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Property deleted successfully!"));
                
                // Reload properties
                loadMyProperties();
            } else {
                String errorMsg = response.readEntity(String.class);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete property: " + errorMsg));
            }
            
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage()));
        }
    }
    
    private void clearForm() {
        address = null;
        propertyType = null;
        price = null;
        squareFootage = null;
        bedrooms = null;
        bathrooms = null;
        description = null;
        hasGarage = false;
        hasPool = false;
        hasGarden = false;
    }
}
