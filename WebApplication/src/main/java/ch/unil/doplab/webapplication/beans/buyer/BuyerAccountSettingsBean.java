package ch.unil.doplab.webapplication.beans.buyer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import ch.unil.doplab.webapplication.beans.SessionBean;

import java.io.Serializable;
import java.util.Map;

/**
 * Managed bean for buyer account settings and deletion.
 */
@Named("buyerAccountSettings")
@RequestScoped
public class BuyerAccountSettingsBean implements Serializable {

    @Inject
    private SessionBean sessionBean;

    private static final String BUYERS_API_URL = "http://payara:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api/buyers";

    /**
     * Delete the current buyer's account.
     * This will cascade delete all offers they've made.
     */
    public String deleteAccount() {
        try {
            Map<String, Object> user = sessionBean.getUser();
            if (user == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No user session found");
                return null;
            }

            String userId = (String) user.get("userID");
            if (userId == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid user ID");
                return null;
            }

            // Call DELETE endpoint
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(BUYERS_API_URL + "/" + userId);
            Response response = target.request().delete();

            client.close();

            if (response.getStatus() == 200) {
                // Account deleted successfully
                // Clear session
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your account has been deleted");

                // Redirect to index page
                return "/index?faces-redirect=true";
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete account. Please try again.");
                return null;
            }

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred: " + e.getMessage());
            return null;
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}
