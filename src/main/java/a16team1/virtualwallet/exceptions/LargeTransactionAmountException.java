package a16team1.virtualwallet.exceptions;

public class LargeTransactionAmountException extends RuntimeException {
    public LargeTransactionAmountException(String message) {
        super(message);
    }
}
