import ch.unil.doplab.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Property class.
 * Tests property lifecycle, validation, and business logic.
 */
class PropertyTest {

    private Property property;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        property = new Property(
                "Luxury Apartment",
                ownerId,
                "Spacious 3BR apartment with panoramic views",
                "Lausanne",
                850000,
                120.5,
                Property.PropertyType.APARTMENT
        );
    }

    @Test
    void testPropertyCreationWithAllFields() {
        assertNotNull(property.getPropertyId());
        assertEquals("Luxury Apartment", property.getTitle());
        assertEquals(ownerId, property.getOwnerId());
        assertEquals("Spacious 3BR apartment with panoramic views", property.getDescription());
        assertEquals("Lausanne", property.getLocation());
        assertEquals(850000, property.getPrice());
        assertEquals(120.5, property.getSize());
        assertEquals(Property.PropertyType.APARTMENT, property.getType());
        assertEquals(Property.PropertyStatus.OFF_MARKET, property.getStatus());
        assertNotNull(property.getCreatedAt());
        assertNotNull(property.getUpdatedAt());
    }

    @Test
    void testPropertyCreationWithDefaultConstructor() {
        Property emptyProperty = new Property();
        
        assertNotNull(emptyProperty.getPropertyId());
        assertEquals(Property.PropertyStatus.OFF_MARKET, emptyProperty.getStatus());
        assertNotNull(emptyProperty.getCreatedAt());
        assertTrue(emptyProperty.getFeatures().isEmpty());
        assertTrue(emptyProperty.getImages().isEmpty());
    }

    @Test
    void testPublishProperty() {
        assertEquals(Property.PropertyStatus.OFF_MARKET, property.getStatus());
        
        property.publish();
        
        assertEquals(Property.PropertyStatus.FOR_SALE, property.getStatus());
    }

    @Test
    void testSuspendProperty() {
        property.publish();
        assertEquals(Property.PropertyStatus.FOR_SALE, property.getStatus());
        
        property.suspend();
        
        assertEquals(Property.PropertyStatus.OFF_MARKET, property.getStatus());
    }

    @Test
    void testCloseProperty() {
        property.publish();
        
        property.close();
        
        assertEquals(Property.PropertyStatus.SOLD, property.getStatus());
    }

    @Test
    void testSetStatus() {
        property.setStatus(Property.PropertyStatus.PENDING);
        assertEquals(Property.PropertyStatus.PENDING, property.getStatus());
        
        property.setStatus(Property.PropertyStatus.SOLD);
        assertEquals(Property.PropertyStatus.SOLD, property.getStatus());
    }

    @Test
    void testUpdatePropertyDetails() {
        property.updatePropertyDetails(
                "Updated Title",
                "Updated description",
                "Geneva",
                900000,
                130,
                Property.PropertyType.HOUSE
        );

        assertEquals("Updated Title", property.getTitle());
        assertEquals("Updated description", property.getDescription());
        assertEquals("Geneva", property.getLocation());
        assertEquals(900000, property.getPrice());
        assertEquals(130, property.getSize());
        assertEquals(Property.PropertyType.HOUSE, property.getType());
    }

    @Test
    void testUpdatePropertyDetailsWithNullValuesIgnored() {
        String originalTitle = property.getTitle();
        double originalPrice = property.getPrice();

        property.updatePropertyDetails(null, null, null, -1, -1, null);

        // Original values preserved
        assertEquals(originalTitle, property.getTitle());
        assertEquals(originalPrice, property.getPrice());
    }

    @Test
    void testAddAndRemoveFeatures() {
        assertTrue(property.getFeatures().isEmpty());

        property.addFeature("bedrooms", 3);
        property.addFeature("bathrooms", 2);
        property.addFeature("parking", true);
        property.addFeature("balcony", true);

        assertEquals(4, property.getFeatures().size());
        assertEquals(3, property.getFeatures().get("bedrooms"));
        assertEquals(2, property.getFeatures().get("bathrooms"));
        assertTrue((Boolean) property.getFeatures().get("parking"));

        property.removeFeature("parking");
        assertEquals(3, property.getFeatures().size());
        assertFalse(property.getFeatures().containsKey("parking"));
    }

    @Test
    void testGetBedroomCount() {
        assertEquals(0, property.getBedroomCount());

        property.addFeature("bedrooms", 3);
        assertEquals(3, property.getBedroomCount());
    }

    @Test
    void testGetBathroomCount() {
        assertEquals(0, property.getBathroomCount());

        property.addFeature("bathrooms", 2);
        assertEquals(2, property.getBathroomCount());
    }

    @Test
    void testAddAndRemoveImages() {
        assertTrue(property.getImages().isEmpty());

        property.addImage("https://example.com/image1.jpg");
        property.addImage("https://example.com/image2.jpg");
        property.addImage("https://example.com/image3.jpg");

        assertEquals(3, property.getImages().size());
        assertTrue(property.getImages().contains("https://example.com/image1.jpg"));

        property.removeImage("https://example.com/image2.jpg");
        assertEquals(2, property.getImages().size());
        assertFalse(property.getImages().contains("https://example.com/image2.jpg"));
    }

    @Test
    void testAddImageIgnoresNullOrBlank() {
        property.addImage(null);
        property.addImage("");
        property.addImage("   ");

        assertTrue(property.getImages().isEmpty());
    }

    @Test
    void testComputePricePerSquareMeter() {
        double expected = 850000 / 120.5;
        assertEquals(expected, property.computePricePerSquareMeter(), 0.01);
    }

    @Test
    void testComputePricePerSquareMeterWithZeroSize() {
        property.setSize(0);
        assertEquals(0.0, property.computePricePerSquareMeter());
    }

    @Test
    void testIsOwnedBy() {
        assertTrue(property.isOwnedBy(ownerId));
        assertFalse(property.isOwnedBy(UUID.randomUUID()));
    }

    @Test
    void testIsAvailableForSale() {
        assertFalse(property.isAvailableForSale());

        property.publish();
        assertTrue(property.isAvailableForSale());

        property.setStatus(Property.PropertyStatus.PENDING);
        assertFalse(property.isAvailableForSale());

        property.setStatus(Property.PropertyStatus.SOLD);
        assertFalse(property.isAvailableForSale());
    }

    @Test
    void testSettersUpdateTimestamp() throws InterruptedException {
        var originalUpdatedAt = property.getUpdatedAt();
        
        Thread.sleep(10); // Small delay to ensure timestamp changes
        property.setTitle("New Title");
        
        assertTrue(property.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void testEqualsAndHashCode() {
        Property property2 = new Property();
        
        // Different properties with different IDs should not be equal
        assertNotEquals(property, property2);
        assertNotEquals(property.hashCode(), property2.hashCode());

        // Same property should be equal to itself
        assertEquals(property, property);
        assertEquals(property.hashCode(), property.hashCode());
    }

    @Test
    void testToString() {
        String str = property.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("Property"));
        assertTrue(str.contains("Luxury Apartment"));
        assertTrue(str.contains("850000"));
        assertTrue(str.contains("Lausanne"));
    }

    @Test
    void testPropertyTypes() {
        // Verify all property types are available
        assertNotNull(Property.PropertyType.APARTMENT);
        assertNotNull(Property.PropertyType.HOUSE);
        assertNotNull(Property.PropertyType.VILLA);
        assertNotNull(Property.PropertyType.STUDIO);
        assertNotNull(Property.PropertyType.LOFT);
        assertNotNull(Property.PropertyType.TOWNHOUSE);
        assertNotNull(Property.PropertyType.CONDO);
        assertNotNull(Property.PropertyType.COMMERCIAL);
        assertNotNull(Property.PropertyType.OFFICE);
        assertNotNull(Property.PropertyType.OTHER);
    }

    @Test
    void testPropertyStatuses() {
        // Verify all statuses are available
        assertNotNull(Property.PropertyStatus.FOR_SALE);
        assertNotNull(Property.PropertyStatus.PENDING);
        assertNotNull(Property.PropertyStatus.SOLD);
        assertNotNull(Property.PropertyStatus.OFF_MARKET);
    }

    @Test
    void testFeaturesAreImmutable() {
        property.addFeature("bedrooms", 3);
        var features = property.getFeatures();
        
        // Should throw exception when trying to modify returned map
        assertThrows(UnsupportedOperationException.class, 
                () -> features.put("bathrooms", 2));
    }

    @Test
    void testImagesAreImmutable() {
        property.addImage("image1.jpg");
        var images = property.getImages();
        
        // Should throw exception when trying to modify returned list
        assertThrows(UnsupportedOperationException.class,
                () -> images.add("image2.jpg"));
    }
}
