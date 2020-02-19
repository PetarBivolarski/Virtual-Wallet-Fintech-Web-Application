package a16team1.virtualwallet.utilities;

import java.math.BigDecimal;

public class Constants {

    public static final BigDecimal TRANSACTION_AMOUNT_LIMIT = BigDecimal.valueOf(1000);
    public static final BigDecimal DONATION_AMOUNT = BigDecimal.ONE;
    public static final String VALUE_CANNOT_BE_EMPTY = "Value cannot be empty";
    public static final String DEFAULT_EMPTY_VALUE = "none";
    public static final String INVALID_DESCRIPTION_LENGTH = "Description cannot exceed 255 characters.";
    public static final String INVALID_CSV_FORMAT = "Invalid CSV format - please use 3 digits only.";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be a positive number.";
    public static final String INVALID_CARD_NUMBER = "Card number must consist of three groups of four digits separated by dashes.";
    public static final String INVALID_CARDHOLDER_NAME = "Cardholder name must contain between 2 and 40 characters which are capital or small Latin letters, or spaces.";
    public static final String INVALID_EXPIRATION_DATE = "Expiration date must be in the format MM/YY.";
    public static final String INVALID_USERNAME_LENGTH = "Username must be between 3 and 30 characters.";
    public static final String INVALID_USERNAME_FORMAT = "Username may only include small and capital letters, underscore and numbers.";
    public static final String INVALID_PASSWORD_LENGTH = "Password must be between 6 and 60 characters.";
    public static final String INVALID_EMAIL_FORMAT = "Invalid e-mail format.";
    public static final String INVALID_EMAIL_LENGTH = "Email must be up to 254 characters long.";
    public static final String INVALID_FIRST_NAME_LENGTH = "First name must be between 2 and 50 characters long.";
    public static final String INVALID_LAST_NAME_LENGTH = "Last name must be between 2 and 50 characters long.";
    public static final int MAXIMUM_ALLOWED_REFERRAL_INVITATIONS = 3;
    public static final BigDecimal REFERRAL_BONUS_IN_EURO = BigDecimal.valueOf(20);
    public static final int HOUR_IN_MILLISECONDS = 1000 * 60 * 60;
    public static final int DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;
}
