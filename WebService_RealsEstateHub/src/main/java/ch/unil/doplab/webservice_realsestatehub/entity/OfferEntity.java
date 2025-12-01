package ch.unil.doplab.webservice_realsestatehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "offers")
public class OfferEntity {

    @Id
    @Column(name = "offer_id", length = 36)
    private String offerId;

    @Column(name = "property_id", length = 36, nullable = false)
    private String propertyId;

    @Column(name = "buyer_id", length = 36, nullable = false)
    private String buyerId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private OfferStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum OfferStatus {
        PENDING, ACCEPTED, REJECTED, WITHDRAWN
    }

    public OfferEntity() {
        this.offerId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = OfferStatus.PENDING;
    }

    public OfferEntity(String propertyId, String buyerId, Double amount, String message) {
        this();
        this.propertyId = propertyId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.message = message;
    }

    // Getters and Setters
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
