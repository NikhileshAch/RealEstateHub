package ch.unil.doplab.webservice_realsestatehub;

import ch.unil.doplab.webservice_realsestatehub.entity.BuyerEntity;
import ch.unil.doplab.webservice_realsestatehub.entity.OfferEntity;
import ch.unil.doplab.webservice_realsestatehub.entity.PropertyEntity;
import ch.unil.doplab.webservice_realsestatehub.repository.BuyerRepository;
import ch.unil.doplab.webservice_realsestatehub.repository.OfferRepository;
import ch.unil.doplab.webservice_realsestatehub.repository.PropertyRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

// Used AI to help write this code - JAX-RS REST endpoints and offer status management with JPA

@Path("/offers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OfferResource {

    @Inject
    private OfferRepository offerRepository;
    
    @Inject
    private PropertyRepository propertyRepository;
    
    @Inject
    private BuyerRepository buyerRepository;

    /**
     * Create a new offer
     * POST /api/offers
     */
    @POST
    public Response createOffer(OfferDTO dto) {
        try {
            if (dto.getAmount() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Amount must be positive"))
                        .build();
            }
            
            OfferEntity offer = new OfferEntity(
                    dto.getPropertyId() != null ? dto.getPropertyId().toString() : null,
                    dto.getBuyerId() != null ? dto.getBuyerId().toString() : null,
                    dto.getAmount(),
                    dto.getMessage()
            );
            
            OfferEntity saved = offerRepository.save(offer);
            
            return Response.status(Response.Status.CREATED)
                    .entity(toDTO(saved))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid offer data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all offers
     * GET /api/offers
     */
    @GET
    public Response getAllOffers() {
        List<OfferEntity> offers = offerRepository.findAll();
        List<Map<String, Object>> result = offers.stream().map(this::toDTO).toList();
        return Response.ok(result).build();
    }

    /**
     * Get offer by ID
     * GET /api/offers/{id}
     */
    @GET
    @Path("/{id}")
    public Response getOfferById(@PathParam("id") String id) {
        try {
            Optional<OfferEntity> offer = offerRepository.findById(id);
            
            if (offer.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Offer not found"))
                        .build();
            }
            
            return Response.ok(toDTO(offer.get())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid offer ID"))
                    .build();
        }
    }

    /**
     * Update offer status (accept/reject)
     * PUT /api/offers/{id}/status
     */
    @PUT
    @Path("/{id}/status")
    public Response updateOfferStatus(@PathParam("id") String id, StatusDTO statusDto) {
        try {
            Optional<OfferEntity> optOffer = offerRepository.findById(id);
            
            if (optOffer.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Offer not found"))
                        .build();
            }
            
            OfferEntity offer = optOffer.get();
            OfferEntity.OfferStatus oldStatus = offer.getStatus();
            OfferEntity.OfferStatus newStatus = OfferEntity.OfferStatus.valueOf(statusDto.getStatus());
            offer.setStatus(newStatus);
            
            // If offer is ACCEPTED, update property status to SOLD
            if (newStatus == OfferEntity.OfferStatus.ACCEPTED) {
                try {
                    Optional<PropertyEntity> property = propertyRepository.findById(offer.getPropertyId());
                    if (property.isPresent()) {
                        PropertyEntity p = property.get();
                        p.setStatus(PropertyEntity.PropertyStatus.SOLD);
                        propertyRepository.update(p);
                        System.out.println("Property " + offer.getPropertyId() + " marked as SOLD");
                    }
                } catch (Exception e) {
                    System.err.println("Error updating property status: " + e.getMessage());
                }
            }
            
            OfferEntity updated = offerRepository.update(offer);
            
            // Get buyer's email for notification
            String buyerEmail = "nikhilesh.acharya@unil.ch";
            try {
                Optional<BuyerEntity> buyer = buyerRepository.findById(offer.getBuyerId());
                if (buyer.isPresent() && buyer.get().getEmail() != null) {
                    buyerEmail = buyer.get().getEmail();
                }
            } catch (Exception e) {
                System.out.println("Could not fetch buyer email, using default");
            }
            
            boolean emailSent = EmailNotificationService.sendOfferStatusNotification(
                id,
                offer.getPropertyId(),
                oldStatus.toString(),
                newStatus.toString(),
                buyerEmail,
                "seller@realestatehub.com"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("offer", toDTO(updated));
            response.put("emailNotificationSent", emailSent);
            response.put("message", emailSent ? 
                "Offer status updated and email notifications sent via external API" : 
                "Offer status updated but email notification failed");
            
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid status. Use: PENDING, ACCEPTED, REJECTED, WITHDRAWN"))
                    .build();
        }
    }

    /**
     * Delete/cancel offer
     * DELETE /api/offers/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteOffer(@PathParam("id") String id) {
        try {
            Optional<OfferEntity> offer = offerRepository.findById(id);
            
            if (offer.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Offer not found"))
                        .build();
            }
            
            offerRepository.delete(id);
            
            return Response.ok()
                    .entity(new SuccessResponse("Offer deleted successfully"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid offer ID"))
                    .build();
        }
    }

    /**
     * Get offers by property
     * GET /api/offers/property/{propertyId}
     */
    @GET
    @Path("/property/{propertyId}")
    public Response getOffersByProperty(@PathParam("propertyId") String propertyId) {
        try {
            List<OfferEntity> propertyOffers = offerRepository.findByPropertyId(propertyId);
            List<Map<String, Object>> result = propertyOffers.stream().map(this::toDTO).toList();
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid property ID"))
                    .build();
        }
    }

    // Convert entity to DTO map
    private Map<String, Object> toDTO(OfferEntity o) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("offerId", o.getOfferId());
        dto.put("propertyId", o.getPropertyId());
        dto.put("buyerId", o.getBuyerId());
        dto.put("amount", o.getAmount());
        dto.put("message", o.getMessage());
        dto.put("status", o.getStatus() != null ? o.getStatus().name() : null);
        dto.put("createdAt", o.getCreatedAt());
        return dto;
    }

    // DTOs
    public static class OfferDTO {
        private UUID propertyId;
        private UUID buyerId;
        private double amount;
        private String message;

        public UUID getPropertyId() { return propertyId; }
        public void setPropertyId(UUID propertyId) { this.propertyId = propertyId; }
        
        public UUID getBuyerId() { return buyerId; }
        public void setBuyerId(UUID buyerId) { this.buyerId = buyerId; }
        
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class StatusDTO {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

    public static class SuccessResponse {
        private String message;
        public SuccessResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
