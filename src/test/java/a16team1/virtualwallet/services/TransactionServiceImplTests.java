package a16team1.virtualwallet.services;

import a16team1.virtualwallet.PaymentInstrumentFactory;
import a16team1.virtualwallet.TransactionFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.VerificationTokenFactory;
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
import a16team1.virtualwallet.utilities.Constants;
import a16team1.virtualwallet.utilities.InstrumentType;
import a16team1.virtualwallet.utilities.TransactionType;
import a16team1.virtualwallet.utilities.mappers.TransactionDtoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplTests {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExternalCardRepository externalCardRepository;

    @Mock
    private PaymentInstrumentService paymentInstrumentService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private CardService cardService;

    @Mock
    private TransactionDtoMapper transactionDtoMapper;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private DonationProjectsFactory donationProjectsFactory;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test(expected = InvalidOperationException.class) // Assert
    public void filter_should_throw_when_sortArgumentIsNotInExpectedFormat() {
        // Arrange
        List<String> sortCriteria = Arrays.asList("amount.asc", "date");
        Pageable pageable = PageRequest.of(0, 10);
        // Act
        transactionService.filter(Date.valueOf("2020-02-13"),
                Date.valueOf("2020-02-14"),
                Optional.empty(),
                Optional.empty(),
                sortCriteria,
                pageable,
                false);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void filter_should_throw_when_sortCriterionIsUnsupported() {
        // Arrange
        List<String> sortCriteria = Arrays.asList("amount.asc", "dat.desc");
        Pageable pageable = PageRequest.of(0, 10);
        // Act
        transactionService.filter(Date.valueOf("2020-02-13"),
                Date.valueOf("2020-02-14"),
                Optional.empty(),
                Optional.empty(),
                sortCriteria,
                pageable,
                false);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void filter_should_throw_when_sortDirectionIsInvalid() {
        // Arrange
        List<String> sortCriteria = Arrays.asList("amount.asc", "date.des");
        Pageable pageable = PageRequest.of(0, 10);
        // Act
        transactionService.filter(Date.valueOf("2020-02-13"),
                Date.valueOf("2020-02-14"),
                Optional.empty(),
                Optional.empty(),
                sortCriteria,
                pageable,
                false);
    }

    @Test
    public void filter_shouldParseSortCriteria_when_argumentsAreInCorrectFormat() {
        // Arrange
        List<String> sortCriteria = Arrays.asList("amount.asc", "date.desc");
        Pageable pageable = PageRequest.of(0, 10);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "transferAmount")
                .and(Sort.by(Sort.Direction.DESC, "dateTime"));
        Pageable pageableWithSort = PageRequest.of(0, 10, sortBy);
        // Act
        transactionService.filter(Date.valueOf("2020-02-13"),
                Date.valueOf("2020-02-14"),
                Optional.empty(),
                Optional.empty(),
                sortCriteria,
                pageable,
                false);
        // Assert
        Mockito.verify(transactionRepository)
                .filter(ArgumentMatchers.any(Date.class),
                        ArgumentMatchers.any(Date.class),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(sortBy),
                        ArgumentMatchers.eq(pageableWithSort),
                        ArgumentMatchers.anyBoolean());
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getById_should_throw_when_transactionDoesNotExist() {
        // Arrange
        Mockito.when(transactionRepository.getById(0)).thenReturn(null);
        // Act
        transactionService.getById(0);
    }

    @Test
    public void getById_should_returnTransaction_when_transactionExists() {
        // Arrange
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        Transaction expectedTransaction = TransactionFactory.createTransaction(sender, recipient);
        Mockito.when(transactionRepository.getById(expectedTransaction.getId())).thenReturn(expectedTransaction);
        // Act
        Transaction actualTransaction = transactionService.getById(expectedTransaction.getId());
        // Assert
        Assert.assertSame(expectedTransaction, actualTransaction);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createExternalTransaction_should_throw_when_senderIsBlocked() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setBlocked(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Default wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, BigDecimal.TEN);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createExternalTransaction_should_throw_when_senderHasNotConfirmedAccount() {
        // Arrange
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Default wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, BigDecimal.TEN);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createExternalTransaction_should_throw_when_senderDoesNotOwnSenderWallet() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 1, "Wrong wallet");
        Wallet senderWallet = recipient.getDefaultWallet();
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, BigDecimal.TEN);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createExternalTransaction_should_throw_when_thereIsNotEnoughSaldoInSenderWallet() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Default wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, BigDecimal.valueOf(0.25));

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createExternalTransaction_should_throw_when_recipientHasNoDefaultWallet() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Default wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        senderWallet.setSaldo(BigDecimal.TEN);
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, BigDecimal.TEN);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createExternalTransaction_should_throw_when_senderAndRecipientWalletsAreTheSame() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrumentFactory.createDefaultWalletFor(recipient, 1, "Default Wallet");
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Default wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        senderWallet.setSaldo(BigDecimal.TEN);
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, BigDecimal.TEN);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test
    public void createExternalTransaction_should_createSmallTransaction_when_transferAmountIsSmall() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Sender wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Recipient wallet");
        Wallet recipientWallet = recipient.getDefaultWallet();
        senderWallet.setSaldo(BigDecimal.ONE);
        BigDecimal amount = BigDecimal.valueOf(0.75);
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, amount);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        Mockito.when(paymentInstrumentService.getById(recipientWallet.getId())).thenReturn(recipientInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
        // Assert
        Mockito.verify(transactionRepository).createTransaction(
                ArgumentMatchers.argThat((Transaction t) ->
                        t.getTransactionType().equals(TransactionType.SMALL_AMOUNT) &&
                                t.getSenderInstrument().equals(senderInstrument) &&
                                t.getRecipientInstrument().equals(recipientInstrument)),
                ArgumentMatchers.eq(senderWallet),
                ArgumentMatchers.eq(recipientWallet)
        );
    }

    @Test
    public void createExternalTransaction_should_updateWallets_when_transferAmountIsSmall() {
        // Arrange
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Sender wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Recipient wallet");
        Wallet recipientWallet = recipient.getDefaultWallet();
        senderWallet.setSaldo(BigDecimal.ONE);
        BigDecimal amount = BigDecimal.valueOf(0.75);
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, amount);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        Mockito.when(paymentInstrumentService.getById(recipientWallet.getId())).thenReturn(recipientInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
        // Assert
        Assert.assertTrue(senderWallet.getSaldo().equals(BigDecimal.valueOf(0.25)) &&
                recipientWallet.getSaldo().equals(BigDecimal.valueOf(0.75)));
    }

    @Test(expected = LargeTransactionAmountException.class) // Assert
    public void createExternalTransaction_should_throw_when_transferAmountIsLarge() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Sender wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Recipient wallet");
        BigDecimal senderWalletSaldo = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.ONE);
        senderWallet.setSaldo(senderWalletSaldo);
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, amount);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        Mockito.when(paymentInstrumentService.getById(recipientInstrument.getId())).thenReturn(recipientInstrument);
        // Act
        transactionService.createExternalTransaction(transactionDto);
    }

    @Test
    public void createExternalTransaction_should_createUnverifiedLargeTransaction_when_transferAmountIsLarge() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Sender wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Recipient wallet");
        Wallet recipientWallet = recipient.getDefaultWallet();
        BigDecimal senderWalletSaldo = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.ONE);
        senderWallet.setSaldo(senderWalletSaldo);
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, amount);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        Mockito.when(paymentInstrumentService.getById(recipientWallet.getId())).thenReturn(recipientInstrument);        // Act
        try {
            transactionService.createExternalTransaction(transactionDto);
        } catch (LargeTransactionAmountException e) {
        }
        // Assert
        Mockito.verify(transactionRepository).createUnverifiedTransactionWithLargeAmount(
                ArgumentMatchers.argThat((Transaction t) ->
                        t.getTransactionType().equals(TransactionType.LARGE_UNVERIFIED) &&
                                t.getSenderInstrument().equals(senderInstrument) &&
                                t.getRecipientInstrument().equals(recipientInstrument))
        );
    }

    @Test
    public void createExternalTransaction_should_notUpdateWallets_when_transferAmountIsLarge() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        sender.setConfirmedRegistration(true);
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Sender wallet");
        Wallet senderWallet = sender.getDefaultWallet();
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Recipient wallet");
        Wallet recipientWallet = recipient.getDefaultWallet();
        BigDecimal senderWalletSaldo = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.ONE);
        senderWallet.setSaldo(senderWalletSaldo);
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        ExternalTransactionDto transactionDto = TransactionFactory.createExternalTransactionDto(sender, recipient, senderWallet, amount);

        Mockito.when(userService.getByUsername(transactionDto.getSenderUsername())).thenReturn(sender);
        Mockito.when(userService.getById(transactionDto.getRecipientId())).thenReturn(recipient);
        Mockito.when(walletService.getById(transactionDto.getSenderWalletId())).thenReturn(senderWallet);
        Mockito.when(paymentInstrumentService.getById(senderWallet.getId())).thenReturn(senderInstrument);
        Mockito.when(paymentInstrumentService.getById(recipientInstrument.getId())).thenReturn(recipientInstrument);
        // Act
        try {
            transactionService.createExternalTransaction(transactionDto);
        } catch (LargeTransactionAmountException e) {
        }
        // Assert
        Assert.assertTrue(senderWallet.getSaldo().equals(senderWalletSaldo) &&
                recipientWallet.getSaldo().equals(BigDecimal.ZERO));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createFundingTransaction_should_throw_when_userIsBlocked() {
        // Arrange
        User user = UserFactory.createUser();
        user.setBlocked(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, "123");
        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        // Act
        transactionService.createFundingTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createFundingTransaction_should_throw_when_userHasNotConfirmedAccount() {
        // Arrange
        User user = UserFactory.createUser();
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());
        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        // Act
        transactionService.createFundingTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createFundingTransaction_should_throw_when_userDoesNotOwnCard() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        User otherUser = UserFactory.createOtherUser();
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, otherUser, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        // Act
        transactionService.createFundingTransaction(transactionDto);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void createFundingTransaction_should_throw_when_givenCsvDoesNotMatchCardCsv() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, "122");

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        // Act
        transactionService.createFundingTransaction(transactionDto);
    }

    @Test(expected = InsufficientFundsException.class) // Assert
    public void createFundingTransaction_should_throw_when_cardWithdrawalFails() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrument walletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());
        Transaction transaction = TransactionFactory.createTransaction(cardInstrument, walletInstrument, amount);

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        Mockito.when(walletService.getById(transactionDto.getWalletId())).thenReturn(wallet);
        Mockito.when(paymentInstrumentService.getById(wallet.getId())).thenReturn(walletInstrument);
        Mockito.when(transactionDtoMapper.fromDto(transactionDto, cardInstrument, walletInstrument)).thenReturn(transaction);
        // Act
        transactionService.createFundingTransaction(transactionDto);
    }

    @Test
    public void createFundingTransaction_should_createCardToWalletTransaction_when_cardWithdrawalSucceeds() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrument walletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        wallet.setSaldo(BigDecimal.TEN);
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());
        Transaction transaction = TransactionFactory.createTransaction(cardInstrument, walletInstrument, amount);
        transaction.setDescription("");

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        Mockito.when(walletService.getById(transactionDto.getWalletId())).thenReturn(wallet);
        Mockito.when(paymentInstrumentService.getById(wallet.getId())).thenReturn(walletInstrument);
        Mockito.when(transactionDtoMapper.fromDto(transactionDto, cardInstrument, walletInstrument)).thenReturn(transaction);
        Mockito.when(externalCardRepository.withdraw(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Card.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())
        ).thenReturn(true);
        // Act
        transactionService.createFundingTransaction(transactionDto);

        // Assert
        Mockito.verify(transactionRepository).createFundingTransaction(
                ArgumentMatchers.argThat((Transaction t) ->
                        t.getTransactionType().equals(TransactionType.CARD_TO_WALLET) &&
                                t.getSenderInstrument().equals(cardInstrument) &&
                                t.getRecipientInstrument().equals(walletInstrument)),
                ArgumentMatchers.eq(wallet)
        );
    }

    @Test
    public void createFundingTransaction_should_addFundsToWallet_when_cardWithdrawalSucceeds() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrument walletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        wallet.setSaldo(BigDecimal.TEN);
        BigDecimal amount = BigDecimal.valueOf(100);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());
        Transaction transaction = TransactionFactory.createTransaction(cardInstrument, walletInstrument, amount);
        transaction.setDescription("");

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        Mockito.when(walletService.getById(transactionDto.getWalletId())).thenReturn(wallet);
        Mockito.when(paymentInstrumentService.getById(wallet.getId())).thenReturn(walletInstrument);
        Mockito.when(transactionDtoMapper.fromDto(transactionDto, cardInstrument, walletInstrument)).thenReturn(transaction);
        Mockito.when(externalCardRepository.withdraw(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Card.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())
        ).thenReturn(true);
        // Act
        transactionService.createFundingTransaction(transactionDto);

        Assert.assertEquals(wallet.getSaldo(), BigDecimal.valueOf(110));
    }

    @Test
    public void createFundingTransaction_should_createDonationTransaction_when_cardWithdrawalSucceedsAndDonationIsSelected() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrument walletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        wallet.setSaldo(BigDecimal.TEN);
        User donationProjectProfile = UserFactory.createDonationProjectProfile();
        PaymentInstrument donationProjectWalletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(donationProjectProfile, 3, "Donation wallet");
        Wallet donationProjectWallet = donationProjectProfile.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100).add(Constants.DONATION_AMOUNT);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());
        Transaction transaction = TransactionFactory.createTransaction(cardInstrument, walletInstrument, amount);
        transaction.setDescription("");
        transaction.setWithDonation(true);

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        Mockito.when(walletService.getById(transactionDto.getWalletId())).thenReturn(wallet);
        Mockito.when(paymentInstrumentService.getById(wallet.getId())).thenReturn(walletInstrument);
        Mockito.when(transactionDtoMapper.fromDto(transactionDto, cardInstrument, walletInstrument)).thenReturn(transaction);
        Mockito.when(externalCardRepository.withdraw(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Card.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())
        ).thenReturn(true);
        Mockito.when(donationProjectsFactory.getCurrentActiveProject()).thenReturn(donationProjectProfile);
        Mockito.when(paymentInstrumentService.getById(donationProjectWallet.getId())).thenReturn(donationProjectWalletInstrument);
        // Act
        transactionService.createFundingTransaction(transactionDto);

        // Assert
        Mockito.verify(transactionRepository).createTransaction(
                ArgumentMatchers.argThat((Transaction dt) ->
                        dt.getTransactionType().equals(TransactionType.DONATION) &&
                                dt.getSenderInstrument().equals(walletInstrument) &&
                                dt.getRecipientInstrument().equals(donationProjectWalletInstrument)),
                ArgumentMatchers.eq(wallet),
                ArgumentMatchers.eq(donationProjectWallet)
        );
    }

    @Test
    public void createFundingTransaction_should_updateWallets_when_cardWithdrawalSucceedsAndDonationIsSelected() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PaymentInstrument walletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(user, 2, "Wallet");
        Wallet wallet = user.getDefaultWallet();
        wallet.setSaldo(BigDecimal.TEN);
        User donationProjectProfile = UserFactory.createDonationProjectProfile();
        PaymentInstrument donationProjectWalletInstrument = PaymentInstrumentFactory.createDefaultWalletFor(donationProjectProfile, 3, "Donation wallet");
        Wallet donationProjectWallet = donationProjectProfile.getDefaultWallet();
        BigDecimal amount = BigDecimal.valueOf(100).add(Constants.DONATION_AMOUNT);
        FundingTransactionDto transactionDto = TransactionFactory.createFundingTransactionDto(user, card, wallet, amount, card.getCsv());
        Transaction transaction = TransactionFactory.createTransaction(cardInstrument, walletInstrument, amount);
        transaction.setDescription("");
        transaction.setWithDonation(true);

        Mockito.when(userService.getById(transactionDto.getUserId())).thenReturn(user);
        Mockito.when(cardService.getById(transactionDto.getCardId())).thenReturn(card);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(cardInstrument);
        Mockito.when(walletService.getById(transactionDto.getWalletId())).thenReturn(wallet);
        Mockito.when(paymentInstrumentService.getById(wallet.getId())).thenReturn(walletInstrument);
        Mockito.when(transactionDtoMapper.fromDto(transactionDto, cardInstrument, walletInstrument)).thenReturn(transaction);
        Mockito.when(externalCardRepository.withdraw(
                ArgumentMatchers.any(BigDecimal.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Card.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())
        ).thenReturn(true);
        Mockito.when(donationProjectsFactory.getCurrentActiveProject()).thenReturn(donationProjectProfile);
        Mockito.when(paymentInstrumentService.getById(donationProjectWallet.getId())).thenReturn(donationProjectWalletInstrument);
        // Act
        transactionService.createFundingTransaction(transactionDto);

        // Assert
        Assert.assertTrue(wallet.getSaldo().equals(BigDecimal.valueOf(110)) &&
                donationProjectWallet.getSaldo().equals(Constants.DONATION_AMOUNT));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void confirmLargeTransaction_should_throw_when_transactionHasAlreadyBeenVerified() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Wallet 1");
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Wallet 2");
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        Transaction transaction = TransactionFactory.createTransaction(senderInstrument, recipientInstrument, amount);
        transaction.setTransactionType(TransactionType.LARGE_VERIFIED);
        TransactionVerificationToken verificationToken = VerificationTokenFactory.createTransactionVerificationToken(transaction);
        String tokenName = verificationToken.getToken();
        Mockito.when(tokenService.getTransactionVerificationTokenByName(tokenName)).thenReturn(verificationToken);
        // Act
        transactionService.confirmLargeTransaction(tokenName);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void confirmLargeTransaction_should_throw_when_senderWalletHasNotEnoughMoney() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Wallet 1");
        Wallet senderWallet = sender.getDefaultWallet();
        senderWallet.setSaldo(Constants.TRANSACTION_AMOUNT_LIMIT);
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Wallet 2");
        Wallet recipientWallet = recipient.getDefaultWallet();
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        Transaction transaction = TransactionFactory.createTransaction(senderInstrument, recipientInstrument, amount);
        transaction.setTransactionType(TransactionType.LARGE_UNVERIFIED);
        TransactionVerificationToken verificationToken = VerificationTokenFactory.createTransactionVerificationToken(transaction);
        String tokenName = verificationToken.getToken();

        Mockito.when(tokenService.getTransactionVerificationTokenByName(tokenName)).thenReturn(verificationToken);
        Mockito.when(walletService.getById(transaction.getSenderInstrument().getId())).thenReturn(senderWallet);
        Mockito.when(walletService.getById(transaction.getRecipientInstrument().getId())).thenReturn(recipientWallet);
        // Act
        transactionService.confirmLargeTransaction(tokenName);
    }

    @Test
    public void confirmLargeTransaction_should_updateTransaction_when_transactionHasNotYetBeenVerified() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Wallet 1");
        Wallet senderWallet = sender.getDefaultWallet();
        senderWallet.setSaldo(Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.ONE));
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Wallet 2");
        Wallet recipientWallet = recipient.getDefaultWallet();
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        Transaction transaction = TransactionFactory.createTransaction(senderInstrument, recipientInstrument, amount);
        transaction.setTransactionType(TransactionType.LARGE_UNVERIFIED);
        TransactionVerificationToken verificationToken = VerificationTokenFactory.createTransactionVerificationToken(transaction);
        String tokenName = verificationToken.getToken();

        Mockito.when(tokenService.getTransactionVerificationTokenByName(tokenName)).thenReturn(verificationToken);
        Mockito.when(walletService.getById(transaction.getSenderInstrument().getId())).thenReturn(senderWallet);
        Mockito.when(walletService.getById(transaction.getRecipientInstrument().getId())).thenReturn(recipientWallet);
        // Act
        Transaction confirmedTransaction = transactionService.confirmLargeTransaction(tokenName);
        // Assert
        Mockito.verify(transactionRepository).update(
                ArgumentMatchers.argThat((Transaction t) -> t.getTransactionType().equals(TransactionType.LARGE_VERIFIED)),
                ArgumentMatchers.eq(senderWallet),
                ArgumentMatchers.eq(recipientWallet)
        );
    }

    @Test
    public void confirmLargeTransaction_should_updateUserWallets_when_transactionHasNotYetBeenVerified() {
        // Arrange
        initMockedFields();
        User sender = UserFactory.createUser();
        User recipient = UserFactory.createOtherUser();
        PaymentInstrument senderInstrument = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Wallet 1");
        Wallet senderWallet = sender.getDefaultWallet();
        senderWallet.setSaldo(Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.ONE));
        PaymentInstrument recipientInstrument = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Wallet 2");
        Wallet recipientWallet = recipient.getDefaultWallet();
        BigDecimal amount = Constants.TRANSACTION_AMOUNT_LIMIT.add(BigDecimal.valueOf(0.25));
        Transaction transaction = TransactionFactory.createTransaction(senderInstrument, recipientInstrument, amount);
        transaction.setTransactionType(TransactionType.LARGE_UNVERIFIED);
        TransactionVerificationToken verificationToken = VerificationTokenFactory.createTransactionVerificationToken(transaction);
        String tokenName = verificationToken.getToken();

        Mockito.when(tokenService.getTransactionVerificationTokenByName(tokenName)).thenReturn(verificationToken);
        Mockito.when(walletService.getById(transaction.getSenderInstrument().getId())).thenReturn(senderWallet);
        Mockito.when(walletService.getById(transaction.getRecipientInstrument().getId())).thenReturn(recipientWallet);
        // Act
        transactionService.confirmLargeTransaction(tokenName);
        // Assert
        Assert.assertTrue(senderWallet.getSaldo().equals(BigDecimal.valueOf(0.75)) &&
                recipientWallet.getSaldo().equals(amount));
    }

    private void initMockedFields() {
        transactionService = new TransactionServiceImpl(
                Mockito.mock(TransactionRepository.class),
                Mockito.mock(ExternalCardRepository.class),
                Mockito.mock(PaymentInstrumentService.class),
                Mockito.mock(WalletService.class),
                Mockito.mock(UserService.class),
                Mockito.mock(TokenService.class));
        MockitoAnnotations.initMocks(this);
    }
}
