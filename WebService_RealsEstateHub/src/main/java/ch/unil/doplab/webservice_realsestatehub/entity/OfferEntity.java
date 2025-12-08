package ch.unil.doplab.webservice_realsestatehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "offers")
public class OfferEntity extends BaseEntity {

    @Id
    @Column(name = "offer_id", length = 36)
    private String offerId;

    /**
     * Property this offer is for.
     * Many offers can be made on one property.
     */
    @NotNull(message = "Offer must be for a property")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    /**
     * Buyer making this offer.
     * Many offers can be made by one buyer.
     */
    @NotNull(message = "Offer must have a buyer")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private BuyerEntity buyer;

    @NotNull(message = "Offer amount is required")
    @Min(value = 0, message = "Offer amount must be positive")
    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private OfferStatus status;

    public enum OfferStatus {
        PENDING, ACCEPTED, REJECTED, WITHDRAWN
    }

    public OfferEntity() {
        this.offerId = UUID.randomUUID().toString();
        this.status = OfferStatus.PENDING;
    }

    public OfferEntity(PropertyEntity property, BuyerEntity buyer, Double amount, String message) {
        this();
        this.property = property;
        this.buyer = buyer;
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

    public PropertyEntity getProperty() {
        return property;
    }

    public void setProperty(PropertyEntity property) {
        this.property = property;
    }

    public BuyerEntity getBuyer() {
        return buyer;
    }

    public void setBuyer(BuyerEntity buyer) {
        this.buyer = buyer;
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
}
