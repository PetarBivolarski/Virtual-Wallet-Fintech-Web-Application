package a16team1.virtualwallet.models;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Objects;

import static a16team1.virtualwallet.utilities.Constants.*;

@Entity
@Table(name = "cards")
@ApiModel(value = "Card", description = "Details about a card returned by GET requests")
public class Card {

    @Id
    @Column(name = "id")
    private int id;

    @Formula("(SELECT pi.name FROM payment_instruments AS pi WHERE pi.id = id)")
    private String name;

    @Column(name = "card_number")
    @Pattern(regexp = "([\\d]{4}-){3}[\\d]{4}", message = INVALID_CARD_NUMBER)
    private String cardNumber;

    @Column(name = "cardholder_name")
    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "[A-Za-z ]{2,40}", message = INVALID_CARDHOLDER_NAME)
    private String cardholderName;

    @Column(name = "expiration_date")
    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = INVALID_EXPIRATION_DATE)
    private String expirationDate;

    @Column(name = "card_csv")
    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "[\\d]{3}", message = INVALID_CSV_FORMAT)
    private String csv;

    @Column(name = "deleted")
    private boolean deleted;

    public Card() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass().equals(o.getClass())) {
            return false;
        } else {
            Card other = (Card) o;
            return Objects.equals(cardNumber, other.cardNumber);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cardNumber);
    }
}
