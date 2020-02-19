package a16team1.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Where;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

import static a16team1.virtualwallet.utilities.Constants.*;

@Entity
@Table(name = "users")
@PropertySource("classpath:messages.properties")
@Where(clause = "enabled = true")
@ApiModel(value = "User", description = "Details about a user returned in GET requests")
public class User {

    private static final String INVALID_PHONE_FORMAT = "Invalid phone number format.";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    @Size(min = 3, max = 30, message = INVALID_USERNAME_LENGTH)
    @Pattern(regexp = "[\\w]+", message = INVALID_USERNAME_FORMAT)
    private String username;

    @JsonIgnore
    @Column(name = "password")
    @Size(min = 6, max = 60, message = INVALID_PASSWORD_LENGTH)
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "phone_number")
    @Pattern(regexp = "\\(\\+[0-9]{1,5}\\)[0-9]{3,15}", message = INVALID_PHONE_FORMAT)
    private String phoneNumber;

    @Column(name = "email")
    @Size(max = 254, message = INVALID_EMAIL_LENGTH)
    @Pattern(regexp = "[^@]+@[^\\.]+\\..+", message = INVALID_EMAIL_FORMAT)
    private String email;

    @Column(name = "first_name")
    @Size(min = 2, max = 50, message = INVALID_FIRST_NAME_LENGTH)
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 2, max = 50, message = INVALID_LAST_NAME_LENGTH)
    private String lastName;

    @Column(name = "photo")
    @JsonIgnore
    private String photo;

    @JsonIgnore
    @Column(name = "joined_date")
    private Timestamp joinedDate;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "confirmed_registration")
    private boolean confirmedRegistration;

    @OneToOne
    @JoinColumn(name = "default_wallet_id", referencedColumnName = "id")
    private Wallet defaultWallet;

    @JsonIgnore
    @Column(name = "invited_users")
    private int invitedUsers;


    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Wallet getDefaultWallet() {
        return defaultWallet;
    }

    public void setDefaultWallet(Wallet defaultWallet) {
        this.defaultWallet = defaultWallet;
    }

    public Timestamp getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Timestamp joinedDate) {
        this.joinedDate = joinedDate;
    }

    public boolean getConfirmedRegistration() {
        return confirmedRegistration;
    }

    public void setConfirmedRegistration(boolean confirmedRegistration) {
        this.confirmedRegistration = confirmedRegistration;
    }

    public int getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(int invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return user.getUsername().equals(this.getUsername());
    }

    @Override
    public int hashCode() {
        // Overridden as in Effective Java https://medium.com/codelog/overriding-hashcode-method-effective-java-notes-723c1fedf51c
        int result = 17;
        result = 31 * result + Integer.hashCode(id);
        result = 31 * result + getUsername().hashCode();
        return result;
    }
}
