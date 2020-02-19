package a16team1.virtualwallet.models.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import static a16team1.virtualwallet.utilities.Constants.*;

public class ExternalTransactionDto {

    @Positive(message = AMOUNT_MUST_BE_POSITIVE)
    private BigDecimal transferAmount;

    private String senderUsername;

    private int recipientId;

    private int senderWalletId;

    @Size(max = 255, message = INVALID_DESCRIPTION_LENGTH)
    private String description;

    public ExternalTransactionDto() {
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(int senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
