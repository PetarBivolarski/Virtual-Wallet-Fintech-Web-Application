package a16team1.virtualwallet.models.dtos;

import javax.validation.constraints.*;

import java.math.BigDecimal;

import static a16team1.virtualwallet.utilities.Constants.*;

public class FundingTransactionDto {

    @Positive (message = AMOUNT_MUST_BE_POSITIVE)
    @DecimalMax("21474836.47")
    private BigDecimal transferAmount;

    private int userId;

    private int cardId;

    private int walletId;

    @Size(max = 255, message = INVALID_DESCRIPTION_LENGTH)
    private String description;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "[\\d]{3}", message = INVALID_CSV_FORMAT)
    private String csv;

    private boolean withDonation;

    public FundingTransactionDto() {
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

    public boolean getWithDonation() {
        return withDonation;
    }

    public void setWithDonation(boolean withDonation) {
        this.withDonation = withDonation;
    }
}
