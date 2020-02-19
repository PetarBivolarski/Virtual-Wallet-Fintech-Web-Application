package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.UserVerificationToken;

public interface UserVerificationTokenRepository {

    UserVerificationToken createVerificationToken(User user, UserVerificationToken userVerificationToken);

    UserVerificationToken getVerificationTokenByName(String confirmationToken);
}
