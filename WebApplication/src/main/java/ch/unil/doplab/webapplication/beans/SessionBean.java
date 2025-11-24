package ch.unil.doplab.webapplication.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Map;

@Named
@SessionScoped
public class SessionBean implements Serializable {
    
    private Map<String, Object> user;
    private String userRole; // "BUYER" or "SELLER"
    
    public boolean isLoggedIn() {
        return user != null;
    }
    
    public boolean isBuyer() {
        ensureUserRoleLoaded();
        return "BUYER".equals(userRole);
    }
    
    public boolean isSeller() {
        ensureUserRoleLoaded();
        return "SELLER".equals(userRole);
    }
    
    public String getUserRole() {
        ensureUserRoleLoaded();
        return userRole;
    }
    
    private void ensureUserRoleLoaded() {
        if (userRole == null) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                if (context != null && context.getExternalContext() != null) {
                    Object roleFromSession = context.getExternalContext().getSessionMap().get("userRole");
                    if (roleFromSession != null) {
                        this.userRole = roleFromSession.toString();
                    }
                }
            } catch (Exception e) {
                // Ignore if FacesContext not available
            }
        }
    }
    
    public String getUserId() {
        if (user != null && user.get("userID") != null) {
            return user.get("userID").toString();
        }
        return null;
    }
    
    public String getUserEmail() {
        if (user != null && user.get("email") != null) {
            return user.get("email").toString();
        }
        return null;
    }
    
    public String getUserFullName() {
        if (user != null) {
            String firstName = user.get("firstName") != null ? user.get("firstName").toString() : "";
            String lastName = user.get("lastName") != null ? user.get("lastName").toString() : "";
            return firstName + " " + lastName;
        }
        return "Guest";
    }
    
    public void logout() {
        this.user = null;
        this.userRole = null;
        // Also clear from HTTP session
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null && context.getExternalContext() != null) {
                context.getExternalContext().getSessionMap().remove("user");
                context.getExternalContext().getSessionMap().remove("userRole");
            }
        } catch (Exception e) {
            // Ignore if FacesContext not available
        }
    }
    
    // Getters and Setters
    public Map<String, Object> getUser() {
        return user;
    }
    
    public void setUser(Map<String, Object> user) {
        this.user = user;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
