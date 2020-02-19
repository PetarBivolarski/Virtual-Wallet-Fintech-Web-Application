package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.Date;
import java.util.Optional;

public interface TransactionRepository {

    Page<Transaction> getAll(Pageable pageable);

    Page<Transaction> filter(Date startDate, Date endDate, Optional<User> sender, Optional<User> recipient,
                             Sort sortBy, Pageable pageable, boolean includeUnverified);

    Page<Transaction> filterOutgoing(Date startDate, Date endDate, User sender, Optional<User> recipient,
                                     Sort sortBy, Pageable pageable, boolean includeUnverified);

    Page<Transaction> filterForUser(Date startDate, Date endDate, User loggedUser,
                                    Sort sortBy, Pageable pageable);

    Page<Transaction> filterForUserWithCounterparty(Date startDate, Date endDate, User user, User otherUser,
                                                    Sort sortBy, Pageable pageable);

    Transaction getById(int id);

    Transaction createTransaction(Transaction transaction, Wallet senderWallet, Wallet recipientWallet);

    Transaction createFundingTransaction(Transaction transaction, Wallet wallet);

    Transaction createUnverifiedTransactionWithLargeAmount(Transaction transaction);

    Transaction update(Transaction transaction, Wallet senderWallet, Wallet recipientWallet);

    Transaction update(Transaction transaction);

    boolean exists(int id);
}
