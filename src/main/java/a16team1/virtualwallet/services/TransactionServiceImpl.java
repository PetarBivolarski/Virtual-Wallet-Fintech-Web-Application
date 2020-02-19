package a16team1.virtualwallet.services;

import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.exceptions.InsufficientFundsException;
import a16team1.virtualwallet.exceptions.InvalidOperationException;
import a16team1.virtualwallet.exceptions.LargeTransactionAmountException;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.dtos.ExternalTransactionDto;
import a16team1.virtualwallet.models.dtos.FundingTransactionDto;
import a16team1.virtualwallet.models.misc.DonationProjectsFactory;
import a16team1.virtualwallet.repositories.contracts.ExternalCardRepository;
import a16team1.virtualwallet.repositories.contracts.TransactionRepository;
import a16team1.virtualwallet.services.email_tokens.EmailVerificationService;
import a16team1.virtualwallet.services.email_tokens.TokenService;
import a16team1.virtualwallet.utilities.*;
import a16team1.virtualwallet.utilities.mappers.TransactionDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@PropertySource("classpath:messages.properties")
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;
    private ExternalCardRepository externalCardRepository;
    private PaymentInstrumentService paymentInstrumentService;
    private WalletService walletService;
    private UserService userService;
    private TokenService tokenService;
    private CardService cardService;
    private TransactionDtoMapper transactionDtoMapper;
    private EmailVerificationService emailVerificationService;
    private DonationProjectsFactory donationProjectsFactory;

    @Value("${error.invalidSortingArguments}")
    private String invalidSortingArguments;

    @Value("${error.invalidSortingCriterion}")
    private String invalidSortingCriterion;

    @Value("${error.invalidSortingDirection}")
    private String invalidSortingDirection;

    @Value("${error.transactionNotFound}")
    private String transactionNotFound;

    @Value("${error.userDoesNotOwnPaymentInstrument}")
    private String userDoesNotOwnPaymentInstrument;

    @Value("${error.insufficientWalletFunds}")
    private String insufficientWalletFunds;

    @Value("${error.insufficientCardFunds}")
    private String insufficientCardFunds;

    @Value("${error.recipientHasNoDefaultWallet}")
    private String recipientHasNoDefaultWallet;

    @Value("${error.senderHasNoDefaultWallet}")
    private String senderHasNoDefaultWallet;

    @Value("${error.csvMismatch}")
    private String csvMismatch;

    @Value("${error.transaction.unconfirmedUser}")
    private String unconfirmedUser;

    @Value("${error.makingTransactionWithBlockedAccount}")
    private String makingTransactionWithBlockedAccount;

    @Value("${email.sender}")
    private String emailSender;

    @Value("${email.largeTransaction.subject}")
    private String emailSubjectForLargeTransaction;

    @Value("${email.largeTransaction.message}")
    private String emailMessageForLargeTransaction;

    @Value("${error.transaction.largeAmount}")
    private String largeAmount;

    @Value("${error.transaction.expiredVerificationTokenText}")
    private String expiredTransactionVerificationToken;

    @Value("${transaction.already.completed}")
    private String transactionAlreadyCompleted;

    @Value("${transaction.emptyAmount}")
    private String emptyAmount;

    @Value("${transaction.invalidAmountFormat}")
    private String invalidAmountFormat;

    @Value("${error.senderAndReceiverWalletsAreTheSame}")
    private String senderAndReceiverAreTheSame;


    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  ExternalCardRepository externalCardRepository,
                                  PaymentInstrumentService paymentInstrumentService,
                                  WalletService walletService,
                                  UserService userService,
                                  TokenService tokenService) {
        this.transactionRepository = transactionRepository;
        this.externalCardRepository = externalCardRepository;
        this.paymentInstrumentService = paymentInstrumentService;
        this.walletService = walletService;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public Page<Transaction> getAll(Pageable pageable) {
        return transactionRepository.getAll(pageable);
    }

    @Override
    public Page<Transaction> filter(Date startDate, Date endDate, Optional<User> sender, Optional<User> recipient,
                                    List<String> sortCriteria, Pageable pageable, boolean includeUnverified) {
        Sort sortBy = parseSortCriteria(sortCriteria);
        pageable = addSortCriteria(sortBy, pageable);
        return transactionRepository.filter(startDate, endDate, sender, recipient, sortBy, pageable, includeUnverified);
    }

    @Override
    public Page<Transaction> filterForUser(Date startDate, Date endDate, User user,
                                           List<String> sortCriteria, Pageable pageable) {
        Sort sortBy = parseSortCriteria(sortCriteria);
        pageable = addSortCriteria(sortBy, pageable);
        return transactionRepository.filterForUser(startDate, endDate, user, sortBy, pageable);
    }

    @Override
    public Page<Transaction> filterForUserWithCounterparty(Date startDate, Date endDate, User user, User otherUser,
                                                           List<String> sortCriteria, Pageable pageable) {
        Sort sortBy = parseSortCriteria(sortCriteria);
        pageable = addSortCriteria(sortBy, pageable);
        return transactionRepository.filterForUserWithCounterparty(startDate, endDate, user, otherUser, sortBy, pageable);
    }

    @Override
    public Page<Transaction> filterOutgoing(Date startDate, Date endDate, User user, Optional<User> recipient,
                                            List<String> sortCriteria, Pageable pageable, boolean includeUnverified) {
        Sort sortBy = parseSortCriteria(sortCriteria);
        pageable = addSortCriteria(sortBy, pageable);
        return transactionRepository.filterOutgoing(startDate, endDate, user, recipient, sortBy, pageable, includeUnverified);
    }

    @Override
    public Page<Transaction> filterIncoming(Date startDate, Date endDate, User user, Optional<User> sender,
                                            List<String> sortCriteria, Pageable pageable, boolean includeUnverified) {
        return filter(startDate, endDate, sender, Optional.of(user), sortCriteria, pageable, includeUnverified);
    }

    @Override
    public Transaction getById(int id) {
        Transaction transaction = transactionRepository.getById(id);
        if (transaction == null) throw new EntityNotFoundException(transactionNotFound);
        return transaction;
    }

    @Override
    public Transaction createExternalTransaction(ExternalTransactionDto transactionDto) {
        User sender = userService.getByUsername(transactionDto.getSenderUsername());
        throwIfUserIsBlocked(sender, BlockedUserForbiddenActions.CREATE_TRANSACTION);
        throwIfUserHasNotConfirmedAccount(sender);
        BigDecimal amount = transactionDto.getTransferAmount();
        Wallet senderWallet = walletService.getById(transactionDto.getSenderWalletId());
        throwIfUserDoesNotOwnPaymentInstrument(sender, paymentInstrumentService.getById(senderWallet.getId()));
        throwIfThereIsNotEnoughSaldoInWallet(amount, senderWallet);
        User recipient = userService.getById(transactionDto.getRecipientId());
        throwIfRecipientHasNoDefaultWallet(recipient, recipientHasNoDefaultWallet);
        throwIfSenderAndReceiverWalletsAreTheSame(senderWallet, recipient.getDefaultWallet());
        if (transactionAmountIsTooLarge(amount)) {
            createLargeTransaction(amount, transactionDto.getDescription(), sender, senderWallet, recipient);
            throw new LargeTransactionAmountException(largeAmount);
        } else {
            return createSmallTransaction(amount, transactionDto.getDescription(), senderWallet, recipient);
        }
    }

    private void throwIfSenderAndReceiverWalletsAreTheSame(Wallet senderWallet, Wallet recipientWallet) {
        if (senderWallet.getId() == recipientWallet.getId()) {
            throw new InvalidOperationException(senderAndReceiverAreTheSame);
        }
    }

    @Override
    public Transaction createFundingTransaction(FundingTransactionDto fundingTransactionDto) {
        User user = userService.getById(fundingTransactionDto.getUserId());
        throwIfUserIsBlocked(user, BlockedUserForbiddenActions.CREATE_TRANSACTION);
        throwIfUserHasNotConfirmedAccount(user);
        Card card = cardService.getById(fundingTransactionDto.getCardId());
        PaymentInstrument senderPaymentInstrument = paymentInstrumentService.getById(card.getId());
        throwIfUserDoesNotOwnPaymentInstrument(user, senderPaymentInstrument);
        if (!card.getCsv().equals(fundingTransactionDto.getCsv())) {
            throw new InvalidOperationException(csvMismatch);
        }
        Wallet wallet = walletService.getById(fundingTransactionDto.getWalletId());
        PaymentInstrument recipientPaymentInstrument = paymentInstrumentService.getById(wallet.getId());
        Transaction transaction = transactionDtoMapper.fromDto(fundingTransactionDto, senderPaymentInstrument, recipientPaymentInstrument);
        BigDecimal amount = transaction.getTransferAmount();
        withdrawFromCard(card, amount, transaction.getDescription(), fundingTransactionDto.getCsv());
        transaction.setTransactionType(TransactionType.CARD_TO_WALLET);
        updateWallet(wallet, amount);
        transactionRepository.createFundingTransaction(transaction, wallet);
        if (transaction.getWithDonation()) {
            User userProfileOfDonationProject = donationProjectsFactory.getCurrentActiveProject();
            Wallet donationProjectWallet = userProfileOfDonationProject.getDefaultWallet();
            PaymentInstrument donationPaymentInstrument = paymentInstrumentService.getById(donationProjectWallet.getId());
            Transaction donationTransaction = ModelFactory.getDonationTransaction(recipientPaymentInstrument, donationPaymentInstrument);
            updateWallets(wallet, donationProjectWallet, donationTransaction.getTransferAmount());
            transactionRepository.createTransaction(donationTransaction, wallet, donationProjectWallet);
        }
        return transaction;
    }

    @Override
    public Transaction confirmLargeTransaction(String tokenName) {
        TransactionVerificationToken token = tokenService.getTransactionVerificationTokenByName(tokenName);
        tokenService.throwIfTokenIsExpired(token, expiredTransactionVerificationToken);
        Transaction transaction = token.getTransaction();
        if (transaction.getTransactionType().equals(TransactionType.LARGE_VERIFIED)) {
            throw new InvalidOperationException(transactionAlreadyCompleted);
        }
        Wallet senderWallet = walletService.getById(transaction.getSenderInstrument().getId());
        Wallet recipientWallet = walletService.getById(transaction.getRecipientInstrument().getId());
        BigDecimal amount = transaction.getTransferAmount();
        throwIfThereIsNotEnoughSaldoInWallet(amount, senderWallet);
        updateWallets(senderWallet, recipientWallet, amount);
        transaction.setTransactionType(TransactionType.LARGE_VERIFIED);
        transactionRepository.update(transaction, senderWallet, recipientWallet);
        return transaction;
    }


    public boolean transactionAmountIsTooLarge(BigDecimal bigDecimalAmount) {
        return bigDecimalAmount.compareTo(Constants.TRANSACTION_AMOUNT_LIMIT) > 0;
    }

    @Override
    public Transaction update(Transaction transaction, Wallet senderWallet, Wallet recipientWallet) {
        return transactionRepository.update(transaction, senderWallet, recipientWallet);
    }

    @Autowired
    public void setEmailVerificationService(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Autowired
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    @Autowired
    public void setTransactionDtoMapper(TransactionDtoMapper transactionDtoMapper) {
        this.transactionDtoMapper = transactionDtoMapper;
    }

    @Autowired
    public void setDonationProjectsFactory(DonationProjectsFactory donationProjectsFactory) {
        this.donationProjectsFactory = donationProjectsFactory;
    }

    private Transaction createSmallTransaction(BigDecimal amount, String description, Wallet senderWallet,
                                               User recipient) {
        PaymentInstrument senderInstrument = paymentInstrumentService.getById(senderWallet.getId());
        Wallet recipientWallet = recipient.getDefaultWallet();
        PaymentInstrument recipientPaymentInstrument = paymentInstrumentService.getById(recipientWallet.getId());
        Transaction transaction = ModelFactory.getTransaction(amount, description, senderInstrument, recipientPaymentInstrument);
        transaction.setTransactionType(TransactionType.SMALL_AMOUNT);
        updateWallets(senderWallet, recipientWallet, transaction.getTransferAmount());
        return transactionRepository.createTransaction(transaction, senderWallet, recipientWallet);
    }

    private Transaction createLargeTransaction(BigDecimal amount, String description, User sender, Wallet senderWallet,
                                               User recipient) {
        PaymentInstrument senderPaymentInstrument = paymentInstrumentService.getById(senderWallet.getId());
        PaymentInstrument recipientPaymentInstrument = paymentInstrumentService.getById(recipient.getDefaultWallet().getId());
        Transaction transaction = ModelFactory.getTransaction(amount, description, senderPaymentInstrument, recipientPaymentInstrument);
        transaction.setTransactionType(TransactionType.LARGE_UNVERIFIED);
        transactionRepository.createUnverifiedTransactionWithLargeAmount(transaction);
        emailVerificationService.sendEmailVerification(transaction, emailSubjectForLargeTransaction, emailMessageForLargeTransaction);
        return transaction;
    }

    private void throwIfUserDoesNotOwnPaymentInstrument(User user, PaymentInstrument paymentInstrument) {
        if (user.getId() != paymentInstrument.getOwner().getId()) {
            throw new InvalidOperationException(userDoesNotOwnPaymentInstrument);
        }
    }

    private void throwIfThereIsNotEnoughSaldoInWallet(BigDecimal transferAmount, Wallet wallet) {
        if (transferAmount.compareTo(wallet.getSaldo()) > 0) {
            throw new InvalidOperationException(insufficientWalletFunds);
        }
    }

    private void throwIfRecipientHasNoDefaultWallet(User user, String errorMessage) {
        if (user.getDefaultWallet() == null) {
            throw new InvalidOperationException(errorMessage);
        }
    }

    private void throwIfUserHasNotConfirmedAccount(User sender) {
        if (!sender.getConfirmedRegistration()) {
            throw new InvalidOperationException(unconfirmedUser);
        }
    }

    private void withdrawFromCard(Card card, BigDecimal transferAmount, String description, String csv) {
        String idempotencyKey = UUID.randomUUID().toString();
        if (!externalCardRepository.withdraw(transferAmount, description, card, csv, idempotencyKey)) {
            throw new InsufficientFundsException(insufficientCardFunds);
        }
    }

    private void throwIfUserIsBlocked(User user, BlockedUserForbiddenActions action) {
        if (user.isBlocked()) {
            if (action.equals(BlockedUserForbiddenActions.CREATE_TRANSACTION)) {
                throw new InvalidOperationException(makingTransactionWithBlockedAccount);
            }
        }
    }

    private static void updateWallets(Wallet senderWallet, Wallet recipientWallet, BigDecimal transferAmount) {
        senderWallet.setSaldo(senderWallet.getSaldo().subtract(transferAmount));
        recipientWallet.setSaldo(recipientWallet.getSaldo().add(transferAmount));
    }

    private static void updateWallet(Wallet recipientWallet, BigDecimal transferAmount) {
        recipientWallet.setSaldo(recipientWallet.getSaldo().add(transferAmount));
    }

    private Pageable addSortCriteria(Sort sortBy, Pageable pageable) {
        return pageable.isPaged() ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy) : pageable;
    }

    private Sort parseSortCriteria(List<String> sortCriteria) {
        Sort sortBy = Sort.unsorted();
        for (String sortCriterion : sortCriteria) {
            String[] criterionComponents = sortCriterion.toUpperCase().split("\\.");
            if (criterionComponents.length != 2) {
                throw new InvalidOperationException(invalidSortingArguments);
            }
            String attribute;
            try {
                attribute = SortAttribute.valueOf(criterionComponents[0]).toTransactionFieldName();
            } catch (IllegalArgumentException e) {
                throw new InvalidOperationException(invalidSortingCriterion);
            }
            Sort.Direction direction;
            try {
                direction = Sort.Direction.valueOf(criterionComponents[1]);
            } catch (IllegalArgumentException e) {
                throw new InvalidOperationException(invalidSortingDirection);
            }
            sortBy = sortBy.and(Sort.by(direction, attribute));
        }
        return sortBy;
    }


}
