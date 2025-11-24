package ch.unil.doplab.webapplication.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Named
@RequestScoped
public class RegisterBean implements Serializable {
    
    @Inject
    private SessionBean sessionBean;
    
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String selectedRole; // "BUYER" or "SELLER"
    private Double budget; // Only for buyers
    
    private static final String BUYERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/buyers";
    private static final String SELLERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/sellers";
    
    public String register() {
        try {
            // Validation
            if (!validateInputs()) {
                return null;
            }
            
            // Check password match
            if (!password.equals(confirmPassword)) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords do not match");
                return null;
            }
            
            // Choose API based on role
            String apiUrl = "BUYER".equals(selectedRole) ? BUYERS_API_URL : SELLERS_API_URL;
            
            // Prepare data
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", firstName);
            userData.put("lastName", lastName);
            userData.put("email", email);
            userData.put("username", username);
            userData.put("password", password);
            
            if ("BUYER".equals(selectedRole) && budget != null) {
                userData.put("budget", budget);
            }
            
            // Send POST request
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(apiUrl);
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(userData, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 201) {
                // Registration successful
                Map<String, Object> createdUser = response.readEntity(Map.class);
                sessionBean.setUser(createdUser);
                sessionBean.setUserRole(selectedRole);
                
                // Also store in HTTP session for AuthenticationFilter
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", createdUser);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userRole", selectedRole);
                
                client.close();
                
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Registration successful! Welcome " + firstName);
                
                // Redirect based on role
                if ("BUYER".equals(selectedRole)) {
                    return "/buyer/properties?faces-redirect=true";
                } else {
                    return "/seller/my-properties?faces-redirect=true";
                }
            } else {
                client.close();
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Registration failed. Please try again.");
                return null;
            }
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Registration failed: " + e.getMessage());
            return null;
        }
    }
    
    private boolean validateInputs() {
        if (firstName == null || firstName.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "First name is required");
            return false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Last name is required");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email is required");
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password is required");
            return false;
        }
        if (selectedRole == null || selectedRole.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a role");
            return false;
        }
        if ("BUYER".equals(selectedRole) && (budget == null || budget <= 0)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Budget is required for buyers and must be positive");
            return false;
        }
        return true;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getSelectedRole() {
        return selectedRole;
    }
    
    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
    }
    
    public Double getBudget() {
        return budget;
    }
    
    public void setBudget(Double budget) {
        this.budget = budget;
    }
}
