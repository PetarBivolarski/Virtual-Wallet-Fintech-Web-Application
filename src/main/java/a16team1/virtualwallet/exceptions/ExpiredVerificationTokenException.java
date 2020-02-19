package a16team1.virtualwallet.exceptions;

public class ExpiredVerificationTokenException extends RuntimeException {

    public ExpiredVerificationTokenException(String message) {
        super(message);
    }
}
