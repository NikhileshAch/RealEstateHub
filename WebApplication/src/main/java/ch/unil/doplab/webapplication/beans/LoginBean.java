package ch.unil.doplab.webapplication.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// Used AI to help write this code - REST client authentication flow

@Named
@RequestScoped
public class LoginBean implements Serializable {
    
    @Inject
    private SessionBean sessionBean;
    
    private String email;
    private String password;
    private String selectedRole; // "BUYER" or "SELLER"
    private String errorMessage;
    
    private static final String BUYERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/buyers";
    private static final String SELLERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/sellers";
    
    @jakarta.annotation.PostConstruct
    public void init() {
        // Check if there's an error message in flash scope
        Object flashError = FacesContext.getCurrentInstance().getExternalContext().getFlash().get("loginError");
        if (flashError != null) {
            this.errorMessage = flashError.toString();
        }
    }
    
    public String login() {
        try {
            errorMessage = null; // Clear previous error
            
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                setError("Email and password are required");
                return "/login?faces-redirect=true";
            }
            
            if (selectedRole == null || selectedRole.trim().isEmpty()) {
                setError("Please select a role");
                return "/login?faces-redirect=true";
            }
            
            // Choose API based on role
            String apiUrl = "BUYER".equals(selectedRole) ? BUYERS_API_URL : SELLERS_API_URL;
            
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(apiUrl);
            List<Map<String, Object>> users = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Map<String, Object>>>() {});
            
            // Find user by email and password
            Map<String, Object> foundUser = null;
            boolean emailFound = false;
            
            for (Map<String, Object> user : users) {
                String userEmail = user.get("email") != null ? user.get("email").toString() : "";
                
                if (email.equals(userEmail)) {
                    emailFound = true;
                    String userPassword = user.get("password") != null ? user.get("password").toString() : "";
                    
                    if (password.equals(userPassword)) {
                        foundUser = user;
                        break;
                    }
                }
            }
            
            client.close();
            
            if (foundUser != null) {
                // Login successful
                sessionBean.setUser(foundUser);
                sessionBean.setUserRole(selectedRole);
                
                // Also store in HTTP session for AuthenticationFilter
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", foundUser);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userRole", selectedRole);
                
                // Redirect based on role
                if ("BUYER".equals(selectedRole)) {
                    return "/buyer/properties?faces-redirect=true";
                } else {
                    return "/seller/my-properties?faces-redirect=true";
                }
            } else if (emailFound) {
                setError("Incorrect password. Please try again.");
                return "/login?faces-redirect=true";
            } else {
                setError("No account found with this email address.");
                return "/login?faces-redirect=true";
            }
            
        } catch (Exception e) {
            setError("Login failed: " + e.getMessage());
            return "/login?faces-redirect=true";
        }
    }
    
    private void setError(String message) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("loginError", message);
    }
    
    public String logout() {
        sessionBean.logout();
        // Clear HTTP session
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("user");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("userRole");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSelectedRole() {
        return selectedRole;
    }
    
    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
