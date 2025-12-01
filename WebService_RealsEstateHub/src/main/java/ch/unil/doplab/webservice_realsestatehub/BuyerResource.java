package ch.unil.doplab.webservice_realsestatehub;

import ch.unil.doplab.webservice_realsestatehub.entity.BuyerEntity;
import ch.unil.doplab.webservice_realsestatehub.repository.BuyerRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Path("/buyers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BuyerResource {

    @Inject
    private BuyerRepository buyerRepository;

    /**
     * Create a new buyer
     * POST /api/buyers
     */
    @POST
    public Response createBuyer(BuyerDTO dto) {
        try {
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Email is required"))
                        .build();
            }

            BuyerEntity buyer = new BuyerEntity(
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getEmail(),
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getBudget() > 0 ? dto.getBudget() : 0.0
            );

            BuyerEntity saved = buyerRepository.save(buyer);

            return Response.status(Response.Status.CREATED)
                    .entity(toDTO(saved))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid buyer data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all buyers
     * GET /api/buyers
     */
    @GET
    public Response getAllBuyers() {
        List<BuyerEntity> buyers = buyerRepository.findAll();
        List<Map<String, Object>> result = buyers.stream().map(this::toDTO).toList();
        return Response.ok(result).build();
    }

    /**
     * Get buyer by ID
     * GET /api/buyers/{id}
     */
    @GET
    @Path("/{id}")
    public Response getBuyerById(@PathParam("id") String id) {
        try {
            Optional<BuyerEntity> buyer = buyerRepository.findById(id);

            if (buyer.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Buyer not found"))
                        .build();
            }

            return Response.ok(toDTO(buyer.get())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid buyer ID"))
                    .build();
        }
    }

    /**
     * Update buyer budget
     * PUT /api/buyers/{id}/budget
     */
    @PUT
    @Path("/{id}/budget")
    public Response updateBuyerBudget(@PathParam("id") String id, BudgetDTO dto) {
        try {
            Optional<BuyerEntity> optBuyer = buyerRepository.findById(id);

            if (optBuyer.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Buyer not found"))
                        .build();
            }

            if (dto.getBudget() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Budget must be positive"))
                        .build();
            }

            BuyerEntity buyer = optBuyer.get();
            buyer.setBudget(dto.getBudget());
            BuyerEntity updated = buyerRepository.update(buyer);

            return Response.ok(toDTO(updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid buyer ID"))
                    .build();
        }
    }

    /**
     * Delete buyer
     * DELETE /api/buyers/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteBuyer(@PathParam("id") String id) {
        try {
            Optional<BuyerEntity> buyer = buyerRepository.findById(id);

            if (buyer.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Buyer not found"))
                        .build();
            }

            buyerRepository.delete(id);

            return Response.ok()
                    .entity(new SuccessResponse("Buyer deleted successfully"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid buyer ID"))
                    .build();
        }
    }

    // Convert entity to DTO map
    private Map<String, Object> toDTO(BuyerEntity b) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("userID", b.getUserId());
        dto.put("firstName", b.getFirstName());
        dto.put("lastName", b.getLastName());
        dto.put("email", b.getEmail());
        dto.put("username", b.getUsername());
        dto.put("password", b.getPassword());
        dto.put("budget", b.getBudget());
        dto.put("role", "Buyer");
        return dto;
    }

    // DTOs
    public static class BuyerDTO {
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private double budget;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public double getBudget() { return budget; }
        public void setBudget(double budget) { this.budget = budget; }
    }

    public static class BudgetDTO {
        private double budget;
        public double getBudget() { return budget; }
        public void setBudget(double budget) { this.budget = budget; }
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