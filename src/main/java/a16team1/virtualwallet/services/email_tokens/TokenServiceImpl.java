package a16team1.virtualwallet.services.email_tokens;

import a16team1.virtualwallet.exceptions.ExpiredVerificationTokenException;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.contracts.Token;
import a16team1.virtualwallet.repositories.contracts.TransactionVerificationTokenRepository;
import a16team1.virtualwallet.repositories.contracts.UserInvitationTokenRepository;
import a16team1.virtualwallet.repositories.contracts.UserVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private static final int HOUR_IN_MILLISECONDS = 1000 * 60 * 60;
    private static final int DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    private UserVerificationTokenRepository userVerificationTokenRepository;
    private TransactionVerificationTokenRepository transactionVerificationTokenRepository;
    private UserInvitationTokenRepository userInvitationTokenRepository;

    @Autowired
    public TokenServiceImpl(UserVerificationTokenRepository userVerificationTokenRepository,
                            TransactionVerificationTokenRepository transactionVerificationTokenRepository,
                            UserInvitationTokenRepository userInvitationTokenRepository) {
        this.userVerificationTokenRepository = userVerificationTokenRepository;
        this.transactionVerificationTokenRepository = transactionVerificationTokenRepository;
        this.userInvitationTokenRepository = userInvitationTokenRepository;
    }

    @Override
    public UserVerificationToken createUserVerificationToken(User user) {
        UserVerificationToken verificationToken = new UserVerificationToken();
        Date createdDate = new Date(System.currentTimeMillis());
        verificationToken.setCreatedDate(createdDate);
        verificationToken.setUser(user);
        String verificationTokenName = UUID.randomUUID().toString();
        verificationToken.setToken(verificationTokenName);
        return userVerificationTokenRepository.createVerificationToken(user, verificationToken);
    }

    @Override
    public TransactionVerificationToken createTransactionVerificationToken(Transaction transaction) {
        TransactionVerificationToken verificationToken = new TransactionVerificationToken();
        long currentTime = System.currentTimeMillis();
        java.sql.Date createdDate = new Date(currentTime);
        verificationToken.setCreatedDate(createdDate);
        Date expiryDate = new Date(currentTime + HOUR_IN_MILLISECONDS);
        verificationToken.setExpiryDate(expiryDate);
        verificationToken.setTransaction(transaction);
        String verificationTokenName = UUID.randomUUID().toString();
        verificationToken.setToken(verificationTokenName);
        return transactionVerificationTokenRepository.createVerificationToken(transaction, verificationToken);
    }

    @Override
    public UserInvitationToken createUserInvitationToken(User user, String email) {
        UserInvitationToken userInvitationToken = new UserInvitationToken();
        userInvitationToken.setOwner(user);
        userInvitationToken.setInvitedEmail(email);
        long currentTime = System.currentTimeMillis();
        java.sql.Date createdDate = new Date(currentTime);
        userInvitationToken.setCreatedDate(createdDate);
        Date expiryDate = new Date(currentTime + DAY_IN_MILLISECONDS);
        userInvitationToken.setExpiryDate(expiryDate);
        String verificationTokenName = UUID.randomUUID().toString();
        userInvitationToken.setToken(verificationTokenName);
        userInvitationToken.setUsed(false);
        return userInvitationTokenRepository.createVerificationToken(userInvitationToken);
    }


    @Override
    public void throwIfTokenIsExpired(Token token, String errorMessage) {
        if (isExpired(token)) {
            throw new ExpiredVerificationTokenException(errorMessage);
        }
    }

    @Override
    public boolean isExpired(Token token) {
        java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
        return currentDate.after(token.getExpiryDate());
    }

    @Override
    public UserVerificationToken getUserVerificationTokenByName(String confirmationToken) {
        return userVerificationTokenRepository.getVerificationTokenByName(confirmationToken);
    }

    @Override
    public TransactionVerificationToken getTransactionVerificationTokenByName(String confirmationToken) {
        return transactionVerificationTokenRepository.getVerificationTokenByName(confirmationToken);
    }

    @Override
    public UserInvitationToken getUserInvitationTokenByName(String invitationTokenName) {
        return userInvitationTokenRepository.getUserInvitationTokenByName(invitationTokenName);
    }

    @Override
    public UserInvitationToken update(UserInvitationToken userInvitationToken) {
        return userInvitationTokenRepository.update(userInvitationToken);
    }

}
