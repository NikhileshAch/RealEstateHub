package ch.unil.doplab.webservice_realsestatehub;

import ch.unil.doplab.Seller;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Path("/sellers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SellerResource {

    @Inject
    private ApplicationState state;

    /**
     * Create a new seller
     * POST /api/sellers
     */
    @POST
    public Response createSeller(SellerDTO dto) {
        try {
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Email is required"))
                        .build();
            }
            
            Seller seller = new Seller(
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getEmail(),
                    dto.getUsername(),
                    dto.getPassword()
            );
            
            state.getSellers().put(seller.getUserID(), seller);
            
            return Response.status(Response.Status.CREATED)
                    .entity(seller)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller data: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all sellers
     * GET /api/sellers
     */
    @GET
    public Response getAllSellers() {
        return Response.ok(new ArrayList<>(state.getSellers().values())).build();
    }

    /**
     * Get seller by ID
     * GET /api/sellers/{id}
     */
    @GET
    @Path("/{id}")
    public Response getSellerById(@PathParam("id") String id) {
        try {
            UUID sellerId = UUID.fromString(id);
            Seller seller = state.getSellers().get(sellerId);
            
            if (seller == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }
            
            return Response.ok(seller).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID"))
                    .build();
        }
    }

    /**
     * Update seller
     * PUT /api/sellers/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateSeller(@PathParam("id") String id, SellerDTO dto) {
        try {
            UUID sellerId = UUID.fromString(id);
            Seller seller = state.getSellers().get(sellerId);
            
            if (seller == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }
            
            // Update fields
            if (dto.getFirstName() != null) {
                seller.updateProfile(dto.getFirstName(), seller.getLastName(), seller.getEmail());
            }
            if (dto.getLastName() != null) {
                seller.updateProfile(seller.getFirstName(), dto.getLastName(), seller.getEmail());
            }
            if (dto.getEmail() != null) {
                seller.updateProfile(seller.getFirstName(), seller.getLastName(), dto.getEmail());
            }
            
            return Response.ok(seller).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID"))
                    .build();
        }
    }

    /**
     * Delete seller
     * DELETE /api/sellers/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteSeller(@PathParam("id") String id) {
        try {
            UUID sellerId = UUID.fromString(id);
            Seller removed = state.getSellers().remove(sellerId);
            
            if (removed == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }
            
            return Response.ok()
                    .entity(Map.of("message", "Seller deleted successfully", "seller", removed))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID"))
                    .build();
        }
    }

    /**
     * Get properties owned by seller
     * GET /api/sellers/{id}/properties
     */
    @GET
    @Path("/{id}/properties")
    public Response getSellerProperties(@PathParam("id") String id) {
        try {
            UUID sellerId = UUID.fromString(id);
            Seller seller = state.getSellers().get(sellerId);
            
            if (seller == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Seller not found"))
                        .build();
            }
            
            return Response.ok(seller.getOwnedProperties()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid seller ID"))
                    .build();
        }
    }

    // DTO for Seller requests
    public static class SellerDTO {
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String password;

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
    }

    // Error response class
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
