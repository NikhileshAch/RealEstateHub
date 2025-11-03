package ch.unil.doplab;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class User {

    // Core User Data
    private UUID userID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    
    // Real Estate Specific Data
    private final List<String> preferredLocations;
    private final List<String> savedProperties; // List of property IDs
    private final List<Message> messages;

    public User() {
        this(null, null, null, null, null);
    }

    public User(String firstName, String lastName, String email, String username, String password) {
        this(null, firstName, lastName, email, username, password);
    }

    public User(UUID userID, String firstName, String lastName, String email, String username, String password) {
        this.userID = userID != null ? userID : UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.preferredLocations = new ArrayList<>();
        this.savedProperties = new ArrayList<>();
    this.messages = new ArrayList<>();
    }

    public void replaceWith(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        this.userID = user.userID;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.username = user.username;
        this.password = user.password;
        this.preferredLocations.clear();
        this.preferredLocations.addAll(user.preferredLocations);
        this.savedProperties.clear();
        this.savedProperties.addAll(user.savedProperties);
    this.messages.clear();
    user.messages.forEach(message -> this.messages.add(new Message(message)));
    }

    public void mergeWith(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.userID != null) {
            this.userID = user.userID;
        }
        if (user.firstName != null) {
            this.firstName = user.firstName;
        }
        if (user.lastName != null) {
            this.lastName = user.lastName;
        }
        if (user.email != null) {
            this.email = user.email;
        }
        if (user.username != null) {
            this.username = user.username;
        }
        if (user.password != null) {
            this.password = user.password;
        }
        if (!user.preferredLocations.isEmpty()) {
            this.preferredLocations.addAll(user.preferredLocations);
        }
        if (!user.savedProperties.isEmpty()) {
            this.savedProperties.addAll(user.savedProperties);
        }
        if (!user.messages.isEmpty()) {
            user.messages.forEach(message -> this.messages.add(new Message(message)));
        }
        // identity documents and preferences removed in simplified model
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPreferredLocations() {
        return Collections.unmodifiableList(preferredLocations);
    }

    public boolean addPreferredLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location must not be blank");
        }
        return preferredLocations.add(location.trim());
    }

    public boolean removePreferredLocation(String location) {
        if (location == null || location.isBlank()) {
            return false;
        }
        return preferredLocations.remove(location.trim());
    }

    public List<String> getSavedProperties() {
        return Collections.unmodifiableList(savedProperties);
    }

    public boolean saveProperty(String propertyId) {
        if (propertyId == null || propertyId.isBlank()) {
            throw new IllegalArgumentException("Property ID must not be blank");
        }
        return savedProperties.add(propertyId.trim());
    }

    public boolean removeSavedProperty(String propertyId) {
        if (propertyId == null || propertyId.isBlank()) {
            return false;
        }
        return savedProperties.remove(propertyId.trim());
    }

    public List<Property> searchProperties(Collection<Property> availableProperties, PropertySearchCriteria criteria) {
        Objects.requireNonNull(availableProperties, "Available properties must not be null");
        Predicate<Property> predicate = property -> true;
        if (criteria != null) {
            predicate = predicate.and(criteria.toPredicate());
        }
        return availableProperties.stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Property::getPrice))
                .collect(Collectors.toList());
    }

    public List<Property> displayAvailableProperties(Collection<Property> availableProperties) {
        Objects.requireNonNull(availableProperties, "Available properties must not be null");
        return availableProperties.stream()
                .filter(property -> property.getStatus() == Property.PropertyStatus.FOR_SALE)
                .sorted(Comparator.comparing(property -> property.getPropertyId().toString()))
                .collect(Collectors.toList());
    }

    public Message sendMessage(User recipient, String subject, String content) {
        Objects.requireNonNull(recipient, "Recipient must not be null");
        Message outbound = Message.outbound(this.userID, recipient.userID, subject, content);
        this.messages.add(outbound);
        recipient.receiveMessage(Message.inbound(this.userID, recipient.userID, subject, content));
        return outbound;
    }

    private void receiveMessage(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List<Message> getMessages(Message.MessageDirection direction) {
        Objects.requireNonNull(direction, "Direction must not be null");
        List<Message> filtered = messages.stream()
                .filter(message -> message.getDirection() == direction)
                .map(Message::new)
                .collect(Collectors.toList());
        return Collections.unmodifiableList(filtered);
    }

    public void updateProfile(String firstName, String lastName, String email) {
        if (firstName != null && !firstName.isBlank()) {
            this.firstName = firstName;
        }
        if (lastName != null && !lastName.isBlank()) {
            this.lastName = lastName;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!Objects.equals(this.password, currentPassword)) {
            throw new IllegalArgumentException("Current password does not match");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password must not be blank");
        }
        this.password = newPassword;
    }

    public String describe() {
    return "userID=" + this.userID +
                ", firstName='" + this.firstName + "'" +
                ", lastName='" + this.lastName + "'" +
                ", username='" + this.username + "'" +
                ", email='" + this.email + "'" +
                ", preferredLocations=" + preferredLocations +
        ", savedProperties=" + savedProperties;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.describe() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(userID, user.userID) && Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, username, email);
    }

    // abstract role accessor for subclasses
    public abstract String getRole();
}
