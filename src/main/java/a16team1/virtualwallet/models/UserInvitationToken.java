package a16team1.virtualwallet.models;

import a16team1.virtualwallet.models.contracts.Token;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_invitation_tokens")
public class UserInvitationToken implements Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token")
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "owner_id")
    private User owner;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "email")
    private String invitedEmail;

    @Column(name = "used")
    private Boolean used;

    public UserInvitationToken() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}
