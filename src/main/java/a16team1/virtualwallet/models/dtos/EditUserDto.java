package a16team1.virtualwallet.models.dtos;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static a16team1.virtualwallet.utilities.Constants.*;

@ApiModel(value = "EditUserDto" , description = "Details about payload for editing user information")
public class EditUserDto {

    private static final String INVALID_COUNTRY_CODE_FORMAT = "Invalid country code format - please use between 1 and 5 digits.";
    private static final String INVALID_PHONE_FORMAT = "Invalid phone number format - please use between 3 and 15 digits.";

    @Pattern(regexp = "[0-9]{1,5}", message = INVALID_COUNTRY_CODE_FORMAT)
    private String countryCode;

    @Pattern(regexp = "[0-9]{3,15}", message = INVALID_PHONE_FORMAT)
    private String localPhoneNumber;

    @Size(max = 254, message = INVALID_EMAIL_LENGTH)
    @Pattern(regexp = "[^@]+@[^\\.]+\\..+", message = INVALID_EMAIL_FORMAT)
    private String email;

    @Size(min = 2, max = 50, message = INVALID_FIRST_NAME_LENGTH)
    private String firstName;

    @Size(min = 2, max = 50, message = INVALID_LAST_NAME_LENGTH)
    private String lastName;

    public EditUserDto() {
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


}
