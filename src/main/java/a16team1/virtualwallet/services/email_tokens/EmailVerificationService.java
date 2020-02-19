package a16team1.virtualwallet.services.email_tokens;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;

import java.util.Optional;

public interface EmailVerificationService {

    void sendEmailVerification(Transaction transaction,
                               String emailSubjectForLargeTransaction,
                               String emailMessageForLargeTransaction);

    void sendEmailVerification(User emailRecipient, String emailSubject,
                               String emailMessage, Optional<String> invitationToken);

    void sendEmailInvitation (User referrer, String recipientEmail,
                              String emailSubject, String emailMessage);

}
