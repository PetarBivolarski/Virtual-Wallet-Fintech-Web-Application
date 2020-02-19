package a16team1.virtualwallet.models.dtos;

import a16team1.virtualwallet.models.PaymentInstrument;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PresentableTransactionDto {

    private Timestamp dateTime;

    private BigDecimal transferAmount;

    private PaymentInstrument senderInstrument;

    private PaymentInstrument recipientInstrument;

    private String description;

    public PresentableTransactionDto() {
    }

    public PresentableTransactionDto(Timestamp dateTime, BigDecimal transferAmount,
                                     PaymentInstrument senderInstrument, PaymentInstrument recipientInstrument,
                                     String description) {
        this.dateTime = dateTime;
        this.transferAmount = transferAmount;
        this.senderInstrument = senderInstrument;
        this.recipientInstrument = recipientInstrument;
        this.description = description;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public PaymentInstrument getSenderInstrument() {
        return senderInstrument;
    }

    public PaymentInstrument getRecipientInstrument() {
        return recipientInstrument;
    }

    public String getDescription() {
        return description;
    }
}
