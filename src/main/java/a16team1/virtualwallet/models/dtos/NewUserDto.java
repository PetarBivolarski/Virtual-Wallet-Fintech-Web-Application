package a16team1.virtualwallet.models.dtos;

import a16team1.virtualwallet.utilities.FieldMatch;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static a16team1.virtualwallet.utilities.Constants.*;

@FieldMatch(first = "password", second = "confirmedPassword", message = "Passwords do not match")
@ApiModel(value = "NewUserDto", description = "Details about payload for creating new user")
public class NewUserDto {

    private static final String INVALID_COUNTRY_CODE_FORMAT = "Invalid country code format - please use between 1 and 5 digits.";
    private static final String INVALID_PHONE_FORMAT = "Invalid phone number format - please use between 3 and 15 digits.";

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Size(min = 3, max = 30, message = INVALID_USERNAME_LENGTH)
    @Pattern(regexp = "[\\w]+", message = INVALID_USERNAME_FORMAT)
    private String username;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Size(min = 6, max = 60, message = INVALID_PASSWORD_LENGTH)
    private String password;

    private String confirmedPassword;

    @Pattern(regexp = "[0-9]{1,5}", message = INVALID_COUNTRY_CODE_FORMAT)
    private String countryCode;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Pattern(regexp = "[0-9]{3,15}", message = INVALID_PHONE_FORMAT)
    private String localPhoneNumber;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Size(max = 254, message = INVALID_EMAIL_LENGTH)
    @Pattern(regexp = "[^@]+@[^\\.]+\\..+", message = INVALID_EMAIL_FORMAT)
    private String email;

    public NewUserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLocalPhoneNumber() {
        return localPhoneNumber;
    }

    public void setLocalPhoneNumber(String localPhoneNumber) {
        this.localPhoneNumber = localPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }
}
