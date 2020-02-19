package a16team1.virtualwallet.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardWithdrawalDto {

    @Positive
    private int amount;

    @Pattern(regexp = "(USD|EUR|BGN)")
    private String currency;

    @NotEmpty
    private String description;

    @NotEmpty
    private String idempotencyKey;

    @Valid
    private CardDetailsDto cardDetails;

    public CardWithdrawalDto() { }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public CardDetailsDto getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetailsDto cardDetails) {
        this.cardDetails = cardDetails;
    }

    @Override
    public String toString() {
        return "{" +
                    "\"amount\": " + amount + ", " +
                    "\"currency\": \"" + currency + "\", " +
                    "\"description\": \"" + description + "\", " +
                    "\"idempotencyKey\": \"" + idempotencyKey + "\", " +
                    "\"cardDetails\": " + cardDetails +
                "}";
    }
}
