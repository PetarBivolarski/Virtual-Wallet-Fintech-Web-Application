package a16team1.virtualwallet.models;

import a16team1.virtualwallet.models.contracts.Token;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction_verification_tokens")
public class TransactionVerificationToken implements Token {

    public TransactionVerificationToken() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token")
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "transaction_id")
    private Transaction transaction;

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

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
