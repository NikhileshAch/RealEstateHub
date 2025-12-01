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

/**
 * SellerResource - REST API for Seller CRUD operations with JPA
 */
@Path("/sellers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SellerResource {

    @Inject
    private SellerRepository sellerRepository;
    
    @Inject
    private PropertyRepository propertyRepository;

    // ===== CREATE =====
    @POST
    public Response createSeller(SellerDTO sellerDTO) {
        if (sellerDTO.firstName == null || sellerDTO.lastName == null || sellerDTO.email == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("First name, last name, and email are required"))
                    .build();
        }

        SellerEntity seller = new SellerEntity(
                sellerDTO.firstName,
                sellerDTO.lastName,
                sellerDTO.email,
                sellerDTO.username,
                sellerDTO.password
        );

        SellerEntity saved = sellerRepository.save(seller);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(saved))
                .build();
    }

    // ===== READ ALL =====
    @GET
    public Response getAllSellers() {
        List<SellerEntity> sellers = sellerRepository.findAll();
        List<Map<String, Object>> result = sellers.stream().map(this::toDTO).toList();
        return Response.ok(result).build();
    }

    // ===== READ ONE =====
    @GET
    @Path("/{id}")
    public Response getSellerById(@PathParam("id") String id) {
        try {
            Optional<SellerEntity> seller = sellerRepository.findById(id);

            if (seller.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }

            return Response.ok(toDTO(seller.get())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID format"))
                    .build();
        }
    }

    // ===== UPDATE =====
    @PUT
    @Path("/{id}")
    public Response updateSeller(@PathParam("id") String id, SellerDTO sellerDTO) {
        try {
            Optional<SellerEntity> optSeller = sellerRepository.findById(id);

            if (optSeller.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }

            SellerEntity seller = optSeller.get();
            if (sellerDTO.firstName != null) seller.setFirstName(sellerDTO.firstName);
            if (sellerDTO.lastName != null) seller.setLastName(sellerDTO.lastName);
            if (sellerDTO.email != null) seller.setEmail(sellerDTO.email);
            if (sellerDTO.username != null) seller.setUsername(sellerDTO.username);
            if (sellerDTO.password != null) seller.setPassword(sellerDTO.password);

            SellerEntity updated = sellerRepository.update(seller);
            return Response.ok(toDTO(updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID format"))
                    .build();
        }
    }

    // ===== DELETE =====
    @DELETE
    @Path("/{id}")
    public Response deleteSeller(@PathParam("id") String id) {
        try {
            Optional<SellerEntity> seller = sellerRepository.findById(id);

            if (seller.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }

            sellerRepository.delete(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID format"))
                    .build();
        }
    }

    // ===== GET SELLER'S PROPERTIES =====
    @GET
    @Path("/{id}/properties")
    public Response getSellerProperties(@PathParam("id") String id) {
        try {
            Optional<SellerEntity> seller = sellerRepository.findById(id);

            if (seller.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }

            List<PropertyEntity> ownedProperties = propertyRepository.findByOwnerId(id);
            List<Map<String, Object>> result = ownedProperties.stream()
                    .map(this::propertyToDTO)
                    .toList();

            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID format"))
                    .build();
        }
    }

    // Convert seller entity to DTO map
    private Map<String, Object> toDTO(SellerEntity s) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("userID", s.getUserId());
        dto.put("firstName", s.getFirstName());
        dto.put("lastName", s.getLastName());
        dto.put("email", s.getEmail());
        dto.put("username", s.getUsername());
        dto.put("password", s.getPassword());
        dto.put("role", "Seller");
        return dto;
    }
    
    // Convert property entity to DTO map
    private Map<String, Object> propertyToDTO(PropertyEntity p) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("propertyId", p.getPropertyId());
        dto.put("ownerId", p.getOwnerId());
        dto.put("title", p.getTitle());
        dto.put("description", p.getDescription());
        dto.put("location", p.getLocation());
        dto.put("price", p.getPrice());
        dto.put("size", p.getSize());
        dto.put("type", p.getType() != null ? p.getType().name() : null);
        dto.put("status", p.getStatus() != null ? p.getStatus().name() : null);
        
        Map<String, Object> features = new HashMap<>();
        features.put("bedrooms", p.getBedrooms());
        features.put("bathrooms", p.getBathrooms());
        dto.put("features", features);
        
        return dto;
    }

    // ===== DTOs =====
    public static class SellerDTO {
        public String firstName;
        public String lastName;
        public String email;
        public String username;
        public String password;
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
