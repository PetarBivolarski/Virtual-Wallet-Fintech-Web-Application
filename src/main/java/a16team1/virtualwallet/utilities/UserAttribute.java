package a16team1.virtualwallet.utilities;

public enum UserAttribute {
    USERNAME,
    EMAIL,
    PHONE;

    public String toFieldName() {
        switch (this) {
            case USERNAME: return "username";
            case EMAIL: return "email";
            case PHONE: return "phoneNumber";
            default: return "";
        }
    }
}
