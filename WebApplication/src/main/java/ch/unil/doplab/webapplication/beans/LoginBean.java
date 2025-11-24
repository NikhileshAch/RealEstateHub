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

@Named
@RequestScoped
public class LoginBean implements Serializable {
    
    @Inject
    private SessionBean sessionBean;
    
    private String email;
    private String password;
    private String selectedRole; // "BUYER" or "SELLER"
    
    private static final String BUYERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/buyers";
    private static final String SELLERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/sellers";
    
    public String login() {
        try {
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email and password are required");
                return null;
            }
            
            if (selectedRole == null || selectedRole.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a role");
                return null;
            }
            
            // Choose API based on role
            String apiUrl = "BUYER".equals(selectedRole) ? BUYERS_API_URL : SELLERS_API_URL;
            
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(apiUrl);
            List<Map<String, Object>> users = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Map<String, Object>>>() {});
            
            // Find user by email and password
            for (Map<String, Object> user : users) {
                String userEmail = user.get("email") != null ? user.get("email").toString() : "";
                String userPassword = user.get("password") != null ? user.get("password").toString() : "";
                
                if (email.equals(userEmail) && password.equals(userPassword)) {
                    // Login successful
                    sessionBean.setUser(user);
                    sessionBean.setUserRole(selectedRole);
                    
                    // Also store in HTTP session for AuthenticationFilter
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userRole", selectedRole);
                    
                    client.close();
                    
                    addMessage(FacesMessage.SEVERITY_INFO, "Success", "Welcome " + sessionBean.getUserFullName());
                    
                    // Redirect based on role
                    if ("BUYER".equals(selectedRole)) {
                        return "/buyer/properties?faces-redirect=true";
                    } else {
                        return "/seller/my-properties?faces-redirect=true";
                    }
                }
            }
            
            client.close();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid email or password");
            return null;
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Login failed: " + e.getMessage());
            return null;
        }
    }
    
    public String logout() {
        sessionBean.logout();
        // Clear HTTP session
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("user");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("userRole");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        addMessage(FacesMessage.SEVERITY_INFO, "Success", "Logged out successfully");
        return "/index?faces-redirect=true";
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
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
}
