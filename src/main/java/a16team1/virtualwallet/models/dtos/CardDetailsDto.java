package a16team1.virtualwallet.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDetailsDto {

    @Pattern(regexp = "([\\d]{4}-){3}[\\d]{4}")
    @NotNull
    private String cardNumber;

    @Pattern(regexp = "[A-Za-z ]{2,40}")
    private String cardholderName;

    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}")
    private String expirationDate;

    @Pattern(regexp = "[\\d]{3}")
    private String csv;

    public CardDetailsDto() { }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

    @Override
    public String toString() {
        return "{" +
                    "\"cardNumber\": \"" + cardNumber + "\", " +
                    "\"expirationDate\": \"" + expirationDate + "\", " +
                    "\"cardholderName\": \"" + cardholderName + "\", " +
                    "\"csv\": \"" + csv + "\"" +
                "}";
    }
}
