package ch.unil.doplab.webservice_realsestatehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "buyers")
@NamedQueries({
        @NamedQuery(name = "Buyer.findByEmail", query = "SELECT b FROM BuyerEntity b WHERE b.email = :email"),
        @NamedQuery(name = "Buyer.findByUsername", query = "SELECT b FROM BuyerEntity b WHERE b.username = :username")
})
public class BuyerEntity extends BaseEntity {

    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", length = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", length = 255, unique = true)
    private String email;

    @NotBlank(message = "Username is required")
    @Column(name = "username", length = 100, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(name = "password", length = 255)
    private String password;

    @Min(value = 0, message = "Budget must be positive")
    @Column(name = "budget")
    private Double budget;

    /**
     * Offers made by this buyer.
     * Cascade delete: when buyer is deleted, all their offers are deleted.
     */
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OfferEntity> offers = new ArrayList<>();

    public BuyerEntity() {
        this.userId = UUID.randomUUID().toString();
    }

    public BuyerEntity(String firstName, String lastName, String email, String username, String password,
            Double budget) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.budget = budget;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public List<OfferEntity> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferEntity> offers) {
        this.offers = offers;
    }
}
