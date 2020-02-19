package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.TransactionVerificationToken;

public interface TransactionVerificationTokenRepository {
    TransactionVerificationToken createVerificationToken(Transaction transaction, TransactionVerificationToken transactionVerificationToken);

    TransactionVerificationToken getVerificationTokenByName(String confirmationToken);
}
