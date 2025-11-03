package ch.unil.doplab;

import java.time.LocalDateTime;
import java.util.*;

public class Property {
    // iu
    public enum PropertyType {
        APARTMENT,
        HOUSE,
        VILLA,
        STUDIO,
        LOFT,
        TOWNHOUSE,
        LAND,
        COMMERCIAL,
        OFFICE,
        OTHER
    }
    
    public enum PropertyStatus {
        FOR_SALE,
        PENDING,
        SOLD,
        OFF_MARKET
    }

    private final UUID propertyId;
    private UUID ownerId;
    private String title;
    private String description;
    private String location;
    private double price;
    private double size; // in square meters
    private PropertyType type;
    private final Map<String, Object> features;
    private final List<String> images;
    private PropertyStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Property() {
        this.propertyId = UUID.randomUUID();
        this.features = new LinkedHashMap<>();
        this.images = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PropertyStatus.OFF_MARKET;
    }

    public Property(String title, UUID ownerId, String description, String location,
                   double price, double size, PropertyType type) {
        this();
        this.title = title;
        this.ownerId = ownerId;
        this.description = description;
        this.location = location;
        this.price = price;
        this.size = size;
        this.type = type;
    }

    // Getters and Setters
    public UUID getPropertyId() {
        return propertyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
        this.updatedAt = LocalDateTime.now();
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    public Map<String, Object> getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    public void addFeature(String key, Object value) {
        features.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeFeature(String key) {
        features.remove(key);
        this.updatedAt = LocalDateTime.now();
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void addImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            this.images.add(imageUrl);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeImage(String imageUrl) {
        if (this.images.remove(imageUrl)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Property Actions
    public void publish() {
        this.status = PropertyStatus.FOR_SALE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = PropertyStatus.OFF_MARKET;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = PropertyStatus.SOLD;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePropertyDetails(String title, String description, String location,
                                    double price, double size, PropertyType type) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (location != null) this.location = location;
        if (price >= 0) this.price = price;
        if (size >= 0) this.size = size;
        if (type != null) this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    // Derived Statistics
    public double computePricePerSquareMeter() {
        return size > 0 ? price / size : 0.0;
    }

    public boolean isOwnedBy(UUID userId) {
        return Objects.equals(this.ownerId, userId);
    }

    public boolean isAvailableForSale() {
        return status == PropertyStatus.FOR_SALE;
    }

    public int getBedroomCount() {
        Object bedrooms = features.get("bedrooms");
        return bedrooms instanceof Integer ? (Integer) bedrooms : 0;
    }

    public int getBathroomCount() {
        Object bathrooms = features.get("bathrooms");
        return bathrooms instanceof Integer ? (Integer) bathrooms : 0;
    }

    @Override
    public String toString() {
        return "Property{" +
                "propertyId=" + propertyId +
                ", title='" + title + '\'' +
                ", ownerId=" + ownerId +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", price=" + price +
                ", size=" + size +
                ", type=" + type +
                ", features=" + features +
                ", status=" + status +
                ", images=" + images.size() + " items" +
                ", pricePerM2=" + computePricePerSquareMeter() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property property)) return false;
        return Objects.equals(propertyId, property.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId);
    }
}
