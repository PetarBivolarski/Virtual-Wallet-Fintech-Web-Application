package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.UserInvitationToken;

public interface UserInvitationTokenRepository {

    UserInvitationToken getUserInvitationTokenByName(String confirmationToken);

    UserInvitationToken createVerificationToken(UserInvitationToken userInvitationToken);

    UserInvitationToken update(UserInvitationToken userInvitationToken);

}
