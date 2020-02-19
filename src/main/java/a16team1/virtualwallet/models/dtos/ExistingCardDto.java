package a16team1.virtualwallet.models.dtos;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static a16team1.virtualwallet.utilities.Constants.*;

@ApiModel(value = "ExistingCardDto", description = "Details about payload for editing card information")
public class ExistingCardDto {

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "([\\d]{4}-){3}[\\d]{4}", message = INVALID_CARD_NUMBER)
    private String cardNumber;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "[A-Za-z ]{2,40}", message = INVALID_CARDHOLDER_NAME)
    private String cardholderName;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = INVALID_EXPIRATION_DATE)
    private String expirationDate;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "[\\d]{3}", message = INVALID_CSV_FORMAT)
    private String csv;

    public ExistingCardDto() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

}
