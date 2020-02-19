package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.ExternalTransactionDto;
import a16team1.virtualwallet.models.dtos.FundingTransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Page<Transaction> getAll(Pageable pageable);

    Page<Transaction> filter(Date startDate, Date endDate, Optional<User> sender, Optional<User> recipient,
                             List<String> sortCriteria, Pageable pageable, boolean includeUnverified);

    Page<Transaction> filterForUser(Date startDate, Date endDate, User user,
                                    List<String> sortCriteria, Pageable pageable);

    Page<Transaction> filterForUserWithCounterparty(Date startDate, Date endDate, User user, User otherUser,
                                                    List<String> sortCriteria, Pageable pageable);

    Page<Transaction> filterOutgoing(Date startDate, Date endDate, User user, Optional<User> recipient,
                                     List<String> sortCriteria, Pageable pageable, boolean includeUnverified);

    Page<Transaction> filterIncoming(Date startDate, Date endDate, User user, Optional<User> sender,
                                     List<String> sortCriteria, Pageable pageable, boolean includeUnverified);


    boolean transactionAmountIsTooLarge(BigDecimal bigDecimalAmount);

    Transaction update(Transaction transaction, Wallet senderWallet, Wallet recipientWallet);

    Transaction getById(int id);

    Transaction createExternalTransaction(ExternalTransactionDto transactionDto);

    Transaction confirmLargeTransaction(String tokenName);

    Transaction createFundingTransaction(FundingTransactionDto fundingTransactionDto);

}
