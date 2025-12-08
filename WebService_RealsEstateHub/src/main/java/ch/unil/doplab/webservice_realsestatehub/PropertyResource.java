package ch.unil.doplab.webservice_realsestatehub;

import ch.unil.doplab.webservice_realsestatehub.entity.PropertyEntity;
import ch.unil.doplab.webservice_realsestatehub.entity.SellerEntity;
import ch.unil.doplab.webservice_realsestatehub.repository.PropertyRepository;
import ch.unil.doplab.webservice_realsestatehub.repository.SellerRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

// Used AI to help write this code - JAX-RS REST endpoints for property CRUD operations with JPA

@Path("/properties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropertyResource {

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private SellerRepository sellerRepository;

    /**
     * Create a new property
     * POST /api/properties
     */
    @POST
    public Response createProperty(PropertyDTO dto) {
        try {
            // Fetch the owner (seller) entity
            if (dto.getOwnerId() == null || dto.getOwnerId().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Owner ID is required"))
                        .build();
            }

            Optional<SellerEntity> owner = sellerRepository.findById(dto.getOwnerId());
            if (owner.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid owner ID"))
                        .build();
            }

            PropertyEntity property = new PropertyEntity();
            property.setOwner(owner.get());
            property.setTitle(dto.getTitle());
            property.setDescription(dto.getDescription());
            property.setLocation(dto.getLocation());
            property.setPrice(dto.getPrice());
            property.setSize(dto.getSize());

            if (dto.getType() != null && !dto.getType().isEmpty()) {
                property.setType(PropertyEntity.PropertyType.valueOf(dto.getType()));
            }

            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                property.setStatus(PropertyEntity.PropertyStatus.valueOf(dto.getStatus()));
            }

            // Set features if provided
            if (dto.getFeatures() != null) {
                if (dto.getFeatures().get("bedrooms") != null) {
                    property.setBedrooms(((Number) dto.getFeatures().get("bedrooms")).intValue());
                }
                if (dto.getFeatures().get("bathrooms") != null) {
                    property.setBathrooms(((Number) dto.getFeatures().get("bathrooms")).intValue());
                }
                if (dto.getFeatures().get("hasGarage") != null) {
                    property.setHasGarage((Boolean) dto.getFeatures().get("hasGarage"));
                }
                if (dto.getFeatures().get("hasPool") != null) {
                    property.setHasPool((Boolean) dto.getFeatures().get("hasPool"));
                }
                if (dto.getFeatures().get("hasGarden") != null) {
                    property.setHasGarden((Boolean) dto.getFeatures().get("hasGarden"));
                }
            }

            PropertyEntity saved = propertyRepository.save(property);

            return Response.status(Response.Status.CREATED)
                    .entity(toDTO(saved))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid property data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all properties
     * GET /api/properties
     */
    @GET
    public Response getAllProperties() {
        List<PropertyEntity> properties = propertyRepository.findAll();
        List<Map<String, Object>> result = properties.stream().map(this::toDTO).toList();
        return Response.ok(result).build();
    }

    /**
     * Get property by ID
     * GET /api/properties/{id}
     */
    @GET
    @Path("/{id}")
    public Response getPropertyById(@PathParam("id") String id) {
        try {
            Optional<PropertyEntity> property = propertyRepository.findById(id);

            if (property.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Property not found"))
                        .build();
            }

            return Response.ok(toDTO(property.get())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid property ID: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update property
     * PUT /api/properties/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateProperty(@PathParam("id") String id, PropertyDTO dto) {
        try {
            Optional<PropertyEntity> optProperty = propertyRepository.findById(id);

            if (optProperty.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Property not found"))
                        .build();
            }

            PropertyEntity property = optProperty.get();

            if (dto.getTitle() != null)
                property.setTitle(dto.getTitle());
            if (dto.getDescription() != null)
                property.setDescription(dto.getDescription());
            if (dto.getLocation() != null)
                property.setLocation(dto.getLocation());
            if (dto.getPrice() > 0)
                property.setPrice(dto.getPrice());
            if (dto.getSize() > 0)
                property.setSize(dto.getSize());
            if (dto.getType() != null && !dto.getType().isEmpty()) {
                property.setType(PropertyEntity.PropertyType.valueOf(dto.getType()));
            }
            if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                property.setStatus(PropertyEntity.PropertyStatus.valueOf(dto.getStatus()));
            }

            if (dto.getFeatures() != null) {
                if (dto.getFeatures().get("bedrooms") != null) {
                    property.setBedrooms(((Number) dto.getFeatures().get("bedrooms")).intValue());
                }
                if (dto.getFeatures().get("bathrooms") != null) {
                    property.setBathrooms(((Number) dto.getFeatures().get("bathrooms")).intValue());
                }
            }

            PropertyEntity updated = propertyRepository.update(property);
            return Response.ok(toDTO(updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete property
     * DELETE /api/properties/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteProperty(@PathParam("id") String id) {
        try {
            Optional<PropertyEntity> property = propertyRepository.findById(id);

            if (property.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Property not found"))
                        .build();
            }

            propertyRepository.delete(id);

            return Response.ok()
                    .entity(new SuccessResponse("Property deleted successfully"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid property ID: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Search properties by location
     * GET /api/properties/search?location=Zurich
     */
    @GET
    @Path("/search")
    public Response searchProperties(@QueryParam("location") String location) {
        List<PropertyEntity> properties = propertyRepository.findAll();
        List<Map<String, Object>> results = properties.stream()
                .filter(p -> location == null ||
                        (p.getLocation() != null && p.getLocation().toLowerCase().contains(location.toLowerCase())))
                .map(this::toDTO)
                .toList();

        return Response.ok(results).build();
    }

    // Convert entity to DTO map for JSON response
    private Map<String, Object> toDTO(PropertyEntity p) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("propertyId", p.getPropertyId());
        dto.put("ownerId", p.getOwner() != null ? p.getOwner().getUserId() : null);
        dto.put("title", p.getTitle());
        dto.put("description", p.getDescription());
        dto.put("location", p.getLocation());
        dto.put("price", p.getPrice());
        dto.put("size", p.getSize());
        dto.put("type", p.getType() != null ? p.getType().name() : null);
        dto.put("status", p.getStatus() != null ? p.getStatus().name() : null);
        dto.put("createdAt", p.getCreatedAt());
        dto.put("updatedAt", p.getUpdatedAt());

        Map<String, Object> features = new HashMap<>();
        features.put("bedrooms", p.getBedrooms());
        features.put("bathrooms", p.getBathrooms());
        features.put("hasGarage", p.getHasGarage());
        features.put("hasPool", p.getHasPool());
        features.put("hasGarden", p.getHasGarden());
        dto.put("features", features);

        return dto;
    }

    // DTO for creating/updating properties
    public static class PropertyDTO {
        private String title;
        private String ownerId;
        private String description;
        private String location;
        private double price;
        private double size;
        private String type;
        private String status;
        private Map<String, Object> features;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
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

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Map<String, Object> getFeatures() {
            return features;
        }

        public void setFeatures(Map<String, Object> features) {
            this.features = features;
        }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
