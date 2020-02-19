package a16team1.virtualwallet.utilities;

public enum SortAttribute {
    DATE,
    AMOUNT;

    public String toTransactionFieldName() {
        switch (this) {
            case DATE: return "dateTime";
            case AMOUNT: return "transferAmount";
            default: return "";
        }
    }
}
