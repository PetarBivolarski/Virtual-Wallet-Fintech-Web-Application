package a16team1.virtualwallet.services;

import a16team1.virtualwallet.TransactionFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.VerificationTokenFactory;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.services.email_tokens.EmailSenderService;
import a16team1.virtualwallet.services.email_tokens.TokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import a16team1.virtualwallet.services.email_tokens.EmailVerificationServiceImpl;
import org.springframework.mail.SimpleMailMessage;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class EmailVerificationServiceImplTests {

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @Test
    public void sendEmailVerification_onLargeTransaction_should_sendVerificationTokenAlongWithMessage() {
        // Arrange
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        Transaction transaction = TransactionFactory.createTransaction(sender, recipient);
        TransactionVerificationToken verificationToken = VerificationTokenFactory.createTransactionVerificationToken(transaction);
        Mockito.when(tokenService.createTransactionVerificationToken(transaction)).thenReturn(verificationToken);
        // Act
        emailVerificationService.sendEmailVerification(transaction, "subject", "message");
        // Assert
        Mockito.verify(emailSenderService).sendEmail(
                ArgumentMatchers.argThat((SimpleMailMessage m) ->
                        Objects.requireNonNull(m.getText()).endsWith(verificationToken.getToken())));
    }

    @Test
    public void sendEmailVerification_onLargeTransaction_should_sendVerificationMessageToTransactionSender() {
        // Arrange
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        Transaction transaction = TransactionFactory.createTransaction(sender, recipient);
        TransactionVerificationToken verificationToken = VerificationTokenFactory.createTransactionVerificationToken(transaction);
        Mockito.when(tokenService.createTransactionVerificationToken(transaction)).thenReturn(verificationToken);
        // Act
        emailVerificationService.sendEmailVerification(transaction, "subject", "message");
        // Assert
        Mockito.verify(emailSenderService).sendEmail(
                ArgumentMatchers.argThat((SimpleMailMessage m) ->
                        Arrays.equals(m.getTo(), new String[] { sender.getEmail() })));
    }

    @Test
    public void sendEmailVerification_onUserRegistration_should_sendVerificationTokenAlongWithMessage() {
        // Arrange
        User user = UserFactory.createUser();
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(user);
        Mockito.when(tokenService.createUserVerificationToken(user)).thenReturn(verificationToken);
        // Act
        emailVerificationService.sendEmailVerification(user, "subject", "message", Optional.empty());
        // Assert
        Mockito.verify(emailSenderService).sendEmail(
                ArgumentMatchers.argThat((SimpleMailMessage m) ->
                        Objects.requireNonNull(m.getText()).endsWith(verificationToken.getToken())));
    }

    @Test
    public void sendEmailVerification_onUserRegistration_should_sendInvitationTokenAlongWithMessageIfPresent() {
        // Arrange
        User newUser = UserFactory.createUser();
        User referringUser = UserFactory.createOtherUser();
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(newUser);
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(referringUser);
        Mockito.when(tokenService.createUserVerificationToken(newUser)).thenReturn(verificationToken);
        // Act
        emailVerificationService.sendEmailVerification(newUser, "subject", "message",
                Optional.of(invitationToken.getToken()));
        // Assert
        Mockito.verify(emailSenderService).sendEmail(
                ArgumentMatchers.argThat((SimpleMailMessage m) ->
                        Objects.requireNonNull(m.getText()).endsWith("&invitationToken=" + invitationToken.getToken())));
    }

    @Test
    public void sendEmailInvitation_should_sendInvitationTokenAlongWithMessage() {
        // Arrange
        User newUser = UserFactory.createUser();
        User referringUser = UserFactory.createOtherUser();
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(referringUser);
        Mockito.when(tokenService.createUserInvitationToken(referringUser, newUser.getEmail())).thenReturn(invitationToken);
        // Act
        emailVerificationService.sendEmailInvitation(referringUser, newUser.getEmail(), "subject", "message");
        // Assert
        Mockito.verify(emailSenderService).sendEmail(
                ArgumentMatchers.argThat((SimpleMailMessage m) ->
                        Objects.requireNonNull(m.getText()).endsWith(invitationToken.getToken())));
    }
}
