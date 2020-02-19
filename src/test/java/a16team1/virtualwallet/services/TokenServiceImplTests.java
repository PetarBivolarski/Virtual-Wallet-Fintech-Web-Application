package a16team1.virtualwallet.services;

import a16team1.virtualwallet.TransactionFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.VerificationTokenFactory;
import a16team1.virtualwallet.exceptions.ExpiredVerificationTokenException;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.contracts.Token;
import a16team1.virtualwallet.repositories.contracts.TransactionVerificationTokenRepository;
import a16team1.virtualwallet.repositories.contracts.UserInvitationTokenRepository;
import a16team1.virtualwallet.repositories.contracts.UserVerificationTokenRepository;
import a16team1.virtualwallet.services.email_tokens.EmailSenderService;
import a16team1.virtualwallet.services.email_tokens.EmailVerificationServiceImpl;
import a16team1.virtualwallet.services.email_tokens.TokenService;
import a16team1.virtualwallet.services.email_tokens.TokenServiceImpl;
import a16team1.virtualwallet.utilities.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.invocation.ArgumentMatcherAction;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceImplTests {

    @Mock
    private UserVerificationTokenRepository userVerificationTokenRepository;

    @Mock
    private TransactionVerificationTokenRepository transactionVerificationTokenRepository;

    @Mock
    private UserInvitationTokenRepository userInvitationTokenRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    public void createUserVerificationToken_should_setTokenOwnerValueToGivenUser() {
        // Arrange
        User user = UserFactory.createUser();
        // Act
        tokenService.createUserVerificationToken(user);
        // Assert
        Mockito.verify(userVerificationTokenRepository)
                .createVerificationToken(ArgumentMatchers.eq(user),
                        ArgumentMatchers.argThat((UserVerificationToken t) -> t.getUser().equals(user)));
    }

    @Test
    public void createUserVerificationToken_should_createDistinctTokensOnMultipleInvocations() {
        // Arrange
        User user = UserFactory.createUser();
        ArgumentCaptor<UserVerificationToken> tokenCaptor = ArgumentCaptor.forClass(UserVerificationToken.class);
        // Act
        tokenService.createUserVerificationToken(user);
        tokenService.createUserVerificationToken(user);
        // Assert
        Mockito.verify(userVerificationTokenRepository, Mockito.times(2))
                .createVerificationToken(ArgumentMatchers.eq(user), tokenCaptor.capture());
        List<UserVerificationToken> tokens = tokenCaptor.getAllValues();
        Assert.assertNotEquals(tokens.get(0).getToken(), tokens.get(1).getToken());
    }

    @Test
    public void createTransactionVerificationToken_should_setTokenTransactionValueToGivenTransaction() {
        // Arrange
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        Transaction transaction = TransactionFactory.createTransaction(sender, recipient);
        // Act
        tokenService.createTransactionVerificationToken(transaction);
        // Assert
        Mockito.verify(transactionVerificationTokenRepository)
                .createVerificationToken(ArgumentMatchers.eq(transaction),
                        ArgumentMatchers.argThat((TransactionVerificationToken t) -> t.getTransaction().equals(transaction)));
    }

    @Test
    public void createTransactionVerificationToken_should_setTokenExpiryDateOneHourAfterCreatedDate() {
        // Arrange
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        Transaction transaction = TransactionFactory.createTransaction(sender, recipient);
        // Act
        tokenService.createTransactionVerificationToken(transaction);
        // Assert
        Mockito.verify(transactionVerificationTokenRepository)
                .createVerificationToken(ArgumentMatchers.eq(transaction),
                        ArgumentMatchers.argThat((TransactionVerificationToken t) ->
                                t.getExpiryDate().getTime() - t.getCreatedDate().getTime() == Constants.HOUR_IN_MILLISECONDS));
    }

    @Test
    public void createUserInvitationToken_should_setTokenOwnerAndEmailFromGivenParameters() {
        // Arrange
        User referringUser = UserFactory.createUser();
        User newUser = UserFactory.createOtherUser();
        // Act
        tokenService.createUserInvitationToken(referringUser, newUser.getEmail());
        // Assert
        Mockito.verify(userInvitationTokenRepository)
                .createVerificationToken(ArgumentMatchers.argThat((UserInvitationToken t) ->
                        t.getOwner().equals(referringUser) && t.getInvitedEmail().equals(newUser.getEmail())));
    }

    @Test
    public void createUserInvitationToken_should_setTokenExpiryDateOneDayAfterCreatedDate() {
        // Arrange
        User referringUser = UserFactory.createUser();
        User newUser = UserFactory.createOtherUser();
        // Act
        tokenService.createUserInvitationToken(referringUser, newUser.getEmail());
        // Assert
        Mockito.verify(userInvitationTokenRepository)
                .createVerificationToken(ArgumentMatchers.argThat((UserInvitationToken t) ->
                        t.getExpiryDate().getTime() - t.getCreatedDate().getTime() == Constants.DAY_IN_MILLISECONDS));
    }

    @Test(expected = ExpiredVerificationTokenException.class) // Assert
    public void throwIfTokenIsExpired_should_throw_ExpiredVerificationException_when_tokenExpiryDateIsAfterCurrentTme() {
        // Arrange
        User user = UserFactory.createUser();
        UserInvitationToken token = VerificationTokenFactory.createUserInvitationToken(user);
        token.setExpiryDate(new Date(System.currentTimeMillis() - 1));
        // Act
        tokenService.throwIfTokenIsExpired(token, "");
    }
}
