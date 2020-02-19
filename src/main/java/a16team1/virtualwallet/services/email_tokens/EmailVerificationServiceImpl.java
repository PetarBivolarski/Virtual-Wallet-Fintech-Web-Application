package a16team1.virtualwallet.services.email_tokens;

import a16team1.virtualwallet.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Value("${email.sender}")
    private String emailSender;

    private EmailSenderService emailSenderService;
    private TokenService tokenService;

    @Autowired
    public EmailVerificationServiceImpl(EmailSenderService emailSenderService,
                                        TokenService tokenService) {
        this.emailSenderService = emailSenderService;
        this.tokenService = tokenService;
    }

    @Override
    public void sendEmailVerification(Transaction transaction, String emailSubjectForLargeTransaction,
                                      String emailMessageForLargeTransaction) {
        User recipient = transaction.getSenderInstrument().getOwner();
        TransactionVerificationToken verificationToken = tokenService.createTransactionVerificationToken(transaction);
        sendEmailWithVerificationCode(recipient.getEmail(), new SimpleMailMessage(),
                emailSubjectForLargeTransaction, emailMessageForLargeTransaction, verificationToken.getToken());
    }

    @Override
    public void sendEmailVerification(User recipient, String emailSubject,
                                      String emailMessage, Optional<String> invitationToken) {

        UserVerificationToken verificationToken = tokenService.createUserVerificationToken(recipient);
        String invitationTokenQueryString = "";
        if (invitationToken.isPresent()) {
            invitationTokenQueryString = "&invitationToken=" + invitationToken.get();
        }
        sendEmailWithVerificationCode(recipient.getEmail(), new SimpleMailMessage(),
                emailSubject, emailMessage, verificationToken.getToken() + invitationTokenQueryString);
    }

    @Override
    public void sendEmailInvitation(User referrer, String recipientEmail, String emailSubject, String emailMessage) {
        UserInvitationToken userInvitationToken = tokenService.createUserInvitationToken(referrer, recipientEmail);
        sendEmailWithVerificationCode(recipientEmail, new SimpleMailMessage(),
                emailSubject, emailMessage, userInvitationToken.getToken());
    }

    private void sendEmailWithVerificationCode(String recipientEmail,
                                               SimpleMailMessage mailMessage,
                                               String emailSubject,
                                               String emailMessage,
                                               String token) {
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(emailSubject);
        mailMessage.setFrom(emailSender);
        mailMessage.setText(emailMessage + token);
        emailSenderService.sendEmail(mailMessage);
    }
}
