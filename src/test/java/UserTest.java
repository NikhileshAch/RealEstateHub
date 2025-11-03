import ch.unil.doplab.User;
import ch.unil.doplab.Message;
import ch.unil.doplab.Buyer;
import ch.unil.doplab.Seller;
import ch.unil.doplab.Property;
import ch.unil.doplab.PropertySearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String USERNAME = "johndoe";
    private static final String PASSWORD = "password123";
    private static final double BUDGET = 500000.0;

    @BeforeEach
    void setUp() {
        user = new Buyer(FIRST_NAME, LAST_NAME, EMAIL, USERNAME, PASSWORD, BUDGET); // Using a concrete subclass
    }

@Test
    void testConstructorAndGetters() {
        assertNotNull(user.getUserID());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        assertTrue(user.getPreferredLocations().isEmpty());
        assertTrue(user.getSavedProperties().isEmpty());
        assertTrue(user.getMessages().isEmpty());
    }

    @Test
    void testAddAndRemovePreferredLocation() {
        assertTrue(user.addPreferredLocation("Lausanne"));
        assertEquals(1, user.getPreferredLocations().size());
        assertTrue(user.getPreferredLocations().contains("Lausanne"));

        assertTrue(user.removePreferredLocation("Lausanne"));
        assertTrue(user.getPreferredLocations().isEmpty());

        assertFalse(user.removePreferredLocation("")); // Invalid input
        assertThrows(IllegalArgumentException.class, () -> user.addPreferredLocation(null));
    }

    @Test
    void testSaveAndRemoveSavedProperty() {
        assertTrue(user.saveProperty("PROP123"));
        assertEquals(1, user.getSavedProperties().size());
        assertTrue(user.getSavedProperties().contains("PROP123"));

        assertTrue(user.removeSavedProperty("PROP123"));
        assertTrue(user.getSavedProperties().isEmpty());

        assertFalse(user.removeSavedProperty("")); // Invalid input
        assertThrows(IllegalArgumentException.class, () -> user.saveProperty(null));
    }

    @Test
    void testSendAndReceiveMessage() {
        User recipient = new Buyer("Jane", "Doe", "jane.doe@example.com", "janedoe", "pass123", 600000.0);
        String subject = "Viewing";
        String content = "Let's view the property at 10 AM.";

        assertEquals(0, user.getMessages().size());
        assertEquals(0, recipient.getMessages().size());

        user.sendMessage(recipient, subject, content);

        assertEquals(1, user.getMessages().size());
        assertEquals(1, recipient.getMessages().size());
        assertEquals(subject, user.getMessages().get(0).getSubject());
        assertEquals(content, recipient.getMessages().get(0).getContent());
    }

    @Test
    void testUpdateProfile() {
        user.updateProfile("Johnny", "Doe", "johnny.doe@example.com");
        assertEquals("Johnny", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johnny.doe@example.com", user.getEmail());

        user.updateProfile(null, null, null); // No changes
        assertEquals("Johnny", user.getFirstName());
    }

    @Test
    void testChangePassword() {
        user.changePassword(PASSWORD, "newpass123");
        assertEquals("newpass123", user.getPassword());

        assertThrows(IllegalArgumentException.class, () -> user.changePassword("wrongpass", "newpass456"));
        assertThrows(IllegalArgumentException.class, () -> user.changePassword(PASSWORD, ""));
    }

    @Test
    void testSearchProperties() {
        user.addPreferredLocation("Lausanne");

        // Create real Property objects
        Seller seller = new Seller("Seller", "One", "seller1@example.com", "seller1", "pass123");
        Property property1 = new Property("Apartment 1", seller.getUserID(),
                "Nice place", "Lausanne", 500000, 75, Property.PropertyType.APARTMENT);
        property1.publish();
        
        Property property2 = new Property("Apartment 2", seller.getUserID(),
                "Cozy place", "Lausanne", 550000, 80, Property.PropertyType.APARTMENT);
        property2.publish();

        // Create search criteria
        PropertySearchCriteria criteria = PropertySearchCriteria.builder()
                .addLocation("Lausanne")
                .maxPrice(600000)
                .build();

        List<Property> properties = new ArrayList<>();
        properties.add(property1);
        properties.add(property2);

        List<Property> results = user.searchProperties(properties, criteria);
        assertEquals(2, results.size());
        assertEquals("Lausanne", results.get(0).getLocation());
    }

    @Test
    void testDisplayAvailableProperties() {
        Seller seller = new Seller("Seller", "Two", "seller2@example.com", "seller2", "pass123");
        Property property1 = new Property("House 1", seller.getUserID(),
                "Nice house", "Geneva", 600000, 120, Property.PropertyType.HOUSE);
        property1.publish();
        
        Property property2 = new Property("House 2", seller.getUserID(),
                "Cozy house", "Geneva", 700000, 150, Property.PropertyType.HOUSE);
        property2.publish();

        List<Property> properties = new ArrayList<>();
        properties.add(property1);
        properties.add(property2);

        List<Property> displayed = user.displayAvailableProperties(properties);
        assertEquals(2, displayed.size());
        assertEquals(Property.PropertyStatus.FOR_SALE, displayed.get(0).getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        // Two separate instances with same data will NOT be equal (different UUIDs)
        User anotherUser = new Buyer(FIRST_NAME, LAST_NAME, EMAIL, USERNAME, PASSWORD, BUDGET);
        User differentUser = new Buyer("Jane", "Doe", "jane@example.com", "janedoe", "pass123", 600000.0);

        // Different instances have different UUIDs, so they're not equal
        assertNotEquals(user, anotherUser);
        assertNotEquals(user, differentUser);
        
        // Same instance should equal itself
        assertEquals(user, user);
        assertEquals(user.hashCode(), user.hashCode());
        
        // Null check
        assertNotEquals(user, null);
    }
}

