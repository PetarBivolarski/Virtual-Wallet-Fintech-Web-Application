package a16team1.virtualwallet.models;

import a16team1.virtualwallet.utilities.TransactionType;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_time")
    private Timestamp dateTime;

    @Column(name = "transfer_amount")
    private BigDecimal transferAmount;

    @ManyToOne
    @JoinColumn(name = "sender_instrument_id")
    private PaymentInstrument senderInstrument;

    @ManyToOne
    @JoinColumn(name = "recipient_instrument_id")
    private PaymentInstrument recipientInstrument;

    @Column(name = "description")
    @Size(max = 255)
    private String description;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "with_donation")
    private boolean withDonation;

    public Transaction() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public PaymentInstrument getSenderInstrument() {
        return senderInstrument;
    }

    public void setSenderInstrument(PaymentInstrument senderInstrument) {
        this.senderInstrument = senderInstrument;
    }

    public PaymentInstrument getRecipientInstrument() {
        return recipientInstrument;
    }

    public void setRecipientInstrument(PaymentInstrument recipientInstrument) {
        this.recipientInstrument = recipientInstrument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public boolean getWithDonation() {
        return withDonation;
    }

    public void setWithDonation(boolean withDonation) {
        this.withDonation = withDonation;
    }
}
