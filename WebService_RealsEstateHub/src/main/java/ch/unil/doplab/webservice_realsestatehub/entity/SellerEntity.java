package ch.unil.doplab.webservice_realsestatehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sellers")
@NamedQueries({
        @NamedQuery(name = "Seller.findByEmail", query = "SELECT s FROM SellerEntity s WHERE s.email = :email"),
        @NamedQuery(name = "Seller.findByUsername", query = "SELECT s FROM SellerEntity s WHERE s.username = :username")
})
public class SellerEntity extends BaseEntity {

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

    /**
     * Properties owned by this seller.
     * Cascade delete: when seller is deleted, all their properties are deleted.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PropertyEntity> properties = new ArrayList<>();

    public SellerEntity() {
        this.userId = UUID.randomUUID().toString();
    }

    public SellerEntity(String firstName, String lastName, String email, String username, String password) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
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

    public List<PropertyEntity> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyEntity> properties) {
        this.properties = properties;
    }
}
