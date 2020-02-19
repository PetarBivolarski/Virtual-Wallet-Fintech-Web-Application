package a16team1.virtualwallet.models;

import a16team1.virtualwallet.models.contracts.Token;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_verification_tokens")
public class UserVerificationToken implements Token {

    public UserVerificationToken() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token")
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
