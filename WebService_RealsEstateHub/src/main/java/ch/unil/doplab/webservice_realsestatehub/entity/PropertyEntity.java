package ch.unil.doplab.webservice_realsestatehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
public class PropertyEntity extends BaseEntity {

    @Id
    @Column(name = "property_id", length = 36)
    private String propertyId;

    /**
     * Owner of this property.
     * Many properties can belong to one seller.
     */
    @NotNull(message = "Property must have an owner")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private SellerEntity owner;

    @NotBlank(message = "Title is required")
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Location is required")
    @Column(name = "location", length = 255)
    private String location;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    @Column(name = "price")
    private Double price;

    @Min(value = 0, message = "Size must be positive")
    @Column(name = "size")
    private Double size;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 50)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PropertyStatus status;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "bathrooms")
    private Integer bathrooms;

    @Column(name = "has_garage")
    private Boolean hasGarage;

    @Column(name = "has_pool")
    private Boolean hasPool;

    @Column(name = "has_garden")
    private Boolean hasGarden;

    /**
     * Offers made on this property.
     * Cascade delete: when property is deleted, all offers are deleted.
     */
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OfferEntity> offers = new ArrayList<>();

    public enum PropertyType {
        APARTMENT, HOUSE, VILLA, STUDIO, LOFT, TOWNHOUSE, CONDO, COMMERCIAL, OFFICE, OTHER
    }

    public enum PropertyStatus {
        FOR_SALE, PENDING, SOLD, OFF_MARKET
    }

    public PropertyEntity() {
        this.propertyId = UUID.randomUUID().toString();
        this.status = PropertyStatus.FOR_SALE;
    }

    // Getters and Setters
    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public SellerEntity getOwner() {
        return owner;
    }

    public void setOwner(SellerEntity owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
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

    public Boolean getHasGarage() {
        return hasGarage;
    }

    public void setHasGarage(Boolean hasGarage) {
        this.hasGarage = hasGarage;
    }

    public Boolean getHasPool() {
        return hasPool;
    }

    public void setHasPool(Boolean hasPool) {
        this.hasPool = hasPool;
    }

    public Boolean getHasGarden() {
        return hasGarden;
    }

    public void setHasGarden(Boolean hasGarden) {
        this.hasGarden = hasGarden;
    }

    public List<OfferEntity> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferEntity> offers) {
        this.offers = offers;
    }
}
