package a16team1.virtualwallet.services.email_tokens;

import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.contracts.Token;

public interface TokenService {

    UserVerificationToken createUserVerificationToken(User user);

    UserVerificationToken getUserVerificationTokenByName(String confirmationToken);

    void throwIfTokenIsExpired(Token token, String errorMessage);

    boolean isExpired(Token token);

    TransactionVerificationToken getTransactionVerificationTokenByName(String confirmationToken);

    TransactionVerificationToken createTransactionVerificationToken(Transaction transaction);

    UserInvitationToken createUserInvitationToken(User user, String email);

    UserInvitationToken getUserInvitationTokenByName(String invitationToken);

    UserInvitationToken update(UserInvitationToken userInvitationToken);

}
