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
        // Flash scope not needed in init
    }
    
    public String login() {
        try {
            errorMessage = null; // Clear previous error
            
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
                FacesContext context = FacesContext.getCurrentInstance();
                jakarta.servlet.http.HttpSession httpSession = 
                    (jakarta.servlet.http.HttpSession) context.getExternalContext().getSession(false);
                httpSession.setAttribute("user", foundUser);
                httpSession.setAttribute("userRole", selectedRole);
                
                // Use sendRedirect directly on HttpServletResponse to bypass JSF entirely
                try {
                    jakarta.servlet.http.HttpServletResponse response = 
                        (jakarta.servlet.http.HttpServletResponse) context.getExternalContext().getResponse();
                    String contextPath = context.getExternalContext().getRequestContextPath();
                    String redirectUrl = "BUYER".equals(selectedRole) 
                        ? contextPath + "/buyer/properties.xhtml" 
                        : contextPath + "/seller/my-properties.xhtml";
                    response.sendRedirect(redirectUrl);
                    context.responseComplete();
                } catch (java.io.IOException e) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Redirect failed: " + e.getMessage());
                }
                return null;
            } else if (emailFound) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect password. Please try again.");
                return null;
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No account found with this email address.");
                return null;
            }
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Login failed: " + e.getMessage());
            return null;
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
    
    public String logout() {
        sessionBean.logout();
        FacesContext context = FacesContext.getCurrentInstance();
        
        // Invalidate session
        context.getExternalContext().invalidateSession();
        
        // Use sendRedirect directly on HttpServletResponse
        try {
            jakarta.servlet.http.HttpServletResponse response = 
                (jakarta.servlet.http.HttpServletResponse) context.getExternalContext().getResponse();
            response.sendRedirect(context.getExternalContext().getRequestContextPath() + "/index.xhtml");
            context.responseComplete();
        } catch (java.io.IOException e) {
            return "/index";
        }
        return null;
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
