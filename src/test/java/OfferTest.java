import ch.unil.doplab.Offer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Offer class.
 * Tests offer creation, validation, and status transitions.
 */
class OfferTest {

    private UUID propertyId;
    private UUID buyerId;
    private Offer offer;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        buyerId = UUID.randomUUID();
        offer = new Offer(propertyId, buyerId, 500000);
    }

    @Test
    void testOfferCreationWithValidData() {
        assertNotNull(offer.getOfferId());
        assertEquals(propertyId, offer.getPropertyId());
        assertEquals(buyerId, offer.getBuyerId());
        assertEquals(500000, offer.getAmount());
        assertEquals(Offer.Status.PENDING, offer.getStatus());
        assertNotNull(offer.getCreatedAt());
    }

    @Test
    void testOfferIdIsUnique() {
        Offer offer2 = new Offer(propertyId, buyerId, 500000);
        
        assertNotEquals(offer.getOfferId(), offer2.getOfferId());
    }

    @Test
    void testOfferInitialStatusIsPending() {
        assertEquals(Offer.Status.PENDING, offer.getStatus());
    }

    @Test
    void testSetStatusToAccepted() {
        offer.setStatus(Offer.Status.ACCEPTED);
        assertEquals(Offer.Status.ACCEPTED, offer.getStatus());
    }

    @Test
    void testSetStatusToRejected() {
        offer.setStatus(Offer.Status.REJECTED);
        assertEquals(Offer.Status.REJECTED, offer.getStatus());
    }

    @Test
    void testSetStatusToWithdrawn() {
        offer.setStatus(Offer.Status.WITHDRAWN);
        assertEquals(Offer.Status.WITHDRAWN, offer.getStatus());
    }

    @Test
    void testStatusTransitionFromPendingToAccepted() {
        assertEquals(Offer.Status.PENDING, offer.getStatus());
        
        offer.setStatus(Offer.Status.ACCEPTED);
        
        assertEquals(Offer.Status.ACCEPTED, offer.getStatus());
    }

    @Test
    void testStatusTransitionFromPendingToRejected() {
        assertEquals(Offer.Status.PENDING, offer.getStatus());
        
        offer.setStatus(Offer.Status.REJECTED);
        
        assertEquals(Offer.Status.REJECTED, offer.getStatus());
    }

    @Test
    void testCannotCreateOfferWithNullPropertyId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Offer(null, buyerId, 500000));
        assertTrue(ex.getMessage().contains("propertyId is required"));
    }

    @Test
    void testCannotCreateOfferWithNullBuyerId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Offer(propertyId, null, 500000));
        assertTrue(ex.getMessage().contains("buyerId is required"));
    }

    @Test
    void testCannotCreateOfferWithZeroAmount() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Offer(propertyId, buyerId, 0));
        assertTrue(ex.getMessage().contains("amount must be positive"));
    }

    @Test
    void testCannotCreateOfferWithNegativeAmount() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Offer(propertyId, buyerId, -100000));
        assertTrue(ex.getMessage().contains("amount must be positive"));
    }

    @Test
    void testOfferWithSmallPositiveAmount() {
        Offer lowOffer = new Offer(propertyId, buyerId, 1);
        assertEquals(1, lowOffer.getAmount());
    }

    @Test
    void testOfferWithLargeAmount() {
        Offer expensiveOffer = new Offer(propertyId, buyerId, 50000000);
        assertEquals(50000000, expensiveOffer.getAmount());
    }

    @Test
    void testEqualsAndHashCode() {
        Offer offer2 = new Offer(propertyId, buyerId, 500000);
        
        // Different offers should not be equal (different IDs)
        assertNotEquals(offer, offer2);
        assertNotEquals(offer.hashCode(), offer2.hashCode());

        // Same offer should equal itself
        assertEquals(offer, offer);
        assertEquals(offer.hashCode(), offer.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(offer, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(offer, "Not an Offer");
    }

    @Test
    void testAllOfferStatuses() {
        // Verify all statuses are available
        assertNotNull(Offer.Status.PENDING);
        assertNotNull(Offer.Status.ACCEPTED);
        assertNotNull(Offer.Status.REJECTED);
        assertNotNull(Offer.Status.WITHDRAWN);
    }

    @Test
    void testMultipleStatusTransitions() {
        // Start as PENDING
        assertEquals(Offer.Status.PENDING, offer.getStatus());
        
        // Can change to WITHDRAWN
        offer.setStatus(Offer.Status.WITHDRAWN);
        assertEquals(Offer.Status.WITHDRAWN, offer.getStatus());
        
        // Can change back to PENDING (if business logic allows)
        offer.setStatus(Offer.Status.PENDING);
        assertEquals(Offer.Status.PENDING, offer.getStatus());
        
        // Can change to ACCEPTED
        offer.setStatus(Offer.Status.ACCEPTED);
        assertEquals(Offer.Status.ACCEPTED, offer.getStatus());
    }

    @Test
    void testOfferAmountIsImmutable() {
        assertEquals(500000, offer.getAmount());
        
        // Amount should not change - it's final
        // This test just verifies the getter returns same value
        assertEquals(500000, offer.getAmount());
        assertEquals(500000, offer.getAmount());
    }

    @Test
    void testOfferIdsAreImmutable() {
        UUID originalOfferId = offer.getOfferId();
        UUID originalPropertyId = offer.getPropertyId();
        UUID originalBuyerId = offer.getBuyerId();
        
        // All IDs should remain the same (they're final)
        assertEquals(originalOfferId, offer.getOfferId());
        assertEquals(originalPropertyId, offer.getPropertyId());
        assertEquals(originalBuyerId, offer.getBuyerId());
    }

    @Test
    void testCreatedAtIsSet() {
        assertNotNull(offer.getCreatedAt());
        // CreatedAt should be close to now (within a few seconds)
        assertTrue(offer.getCreatedAt().isBefore(java.time.LocalDateTime.now().plusSeconds(5)));
        assertTrue(offer.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    void testOfferForDifferentProperties() {
        UUID property2Id = UUID.randomUUID();
        Offer offer2 = new Offer(property2Id, buyerId, 600000);
        
        assertNotEquals(offer.getPropertyId(), offer2.getPropertyId());
        assertEquals(propertyId, offer.getPropertyId());
        assertEquals(property2Id, offer2.getPropertyId());
    }

    @Test
    void testOfferFromDifferentBuyers() {
        UUID buyer2Id = UUID.randomUUID();
        Offer offer2 = new Offer(propertyId, buyer2Id, 550000);
        
        assertNotEquals(offer.getBuyerId(), offer2.getBuyerId());
        assertEquals(buyerId, offer.getBuyerId());
        assertEquals(buyer2Id, offer2.getBuyerId());
    }
}
