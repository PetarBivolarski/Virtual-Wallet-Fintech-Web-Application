package a16team1.virtualwallet;

import a16team1.virtualwallet.models.*;

import java.util.UUID;

public class VerificationTokenFactory {
    public static UserVerificationToken createUserVerificationToken(User user) {
        UserVerificationToken verificationToken = new UserVerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        return verificationToken;
    }

    public static TransactionVerificationToken createTransactionVerificationToken(Transaction transaction) {
        TransactionVerificationToken verificationToken = new TransactionVerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setTransaction(transaction);
        return verificationToken;
    }

    public static UserInvitationToken createUserInvitationToken(User user) {
        UserInvitationToken invitationToken = new UserInvitationToken();
        invitationToken.setToken(UUID.randomUUID().toString());
        invitationToken.setOwner(user);
        invitationToken.setUsed(false);
        return invitationToken;
    }
}
