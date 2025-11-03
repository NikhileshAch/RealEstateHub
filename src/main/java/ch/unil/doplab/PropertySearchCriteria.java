package ch.unil.doplab;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class PropertySearchCriteria {
    private final Set<String> locations;
    private final Double minPrice;
    private final Double maxPrice;
    private final Set<String> propertyTypes;

    private PropertySearchCriteria(Builder builder) {
        this.locations = builder.locations.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(builder.locations));
        this.minPrice = builder.minPrice;
        this.maxPrice = builder.maxPrice;
        this.propertyTypes = builder.propertyTypes.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(builder.propertyTypes));
    }

    public Predicate<Property> toPredicate() {
        return property -> matchesLocation(property) && matchesPrice(property) && matchesType(property);
    }

    private boolean matchesLocation(Property property) {
        return locations.isEmpty() || (property.getLocation() != null && locations.contains(property.getLocation()));
    }

    private boolean matchesPrice(Property property) {
        boolean minOk = minPrice == null || property.getPrice() >= minPrice;
        boolean maxOk = maxPrice == null || property.getPrice() <= maxPrice;
        return minOk && maxOk;
    }

    private boolean matchesType(Property property) {
        return propertyTypes.isEmpty() || (property.getType() != null && propertyTypes.contains(property.getType().toString()));
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Set<String> locations = new LinkedHashSet<>();
        private Double minPrice;
        private Double maxPrice;
        private final Set<String> propertyTypes = new LinkedHashSet<>();

        public Builder addLocation(String location) {
            if (location != null && !location.isBlank()) {
                locations.add(location.trim());
            }
            return this;
        }

        public Builder minPrice(double minPrice) { this.minPrice = minPrice; return this; }
        public Builder maxPrice(double maxPrice) { this.maxPrice = maxPrice; return this; }
        public Builder addPropertyType(String propertyType) {
            if (propertyType != null && !propertyType.isBlank()) {
                propertyTypes.add(Objects.requireNonNull(propertyType).trim());
            }
            return this;
        }

        public PropertySearchCriteria build() {
            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                throw new IllegalArgumentException("Min price cannot exceed max price");
            }
            return new PropertySearchCriteria(this);
        }
    }
}
