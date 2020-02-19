package a16team1.virtualwallet.services;

import a16team1.virtualwallet.PaymentInstrumentFactory;
import a16team1.virtualwallet.TransactionFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.dtos.*;
import a16team1.virtualwallet.utilities.Constants;
import a16team1.virtualwallet.utilities.InstrumentType;
import a16team1.virtualwallet.utilities.TransactionDirection;
import a16team1.virtualwallet.utilities.mappers.PaymentInstrumentDtoMapper;
import a16team1.virtualwallet.utilities.mappers.TransactionDtoMapper;
import a16team1.virtualwallet.utilities.mappers.UserDtoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class DtoListsMediatorServiceImplTests {

    @Mock
    private UserService userService;

    @Mock
    private TransactionDtoMapper transactionDtoMapper;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private PaymentInstrumentDtoMapper paymentInstrumentDtoMapper;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private CardService cardService;

    @InjectMocks
    DtoListsMediatorServiceImpl dtoListsMediatorService;

    @Test
    public void getPresentableTransactionsWithPagination_should_setStartAndEndDates_when_startAndEndDatesAreNotGiven() {
        // Arrange
        User user = UserFactory.createUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(transactionService.filterForUser(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                null,
                null,
                user.getUsername(),
                null,
                TransactionDirection.ALL,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterForUser(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_returnEmptyPage_when_directionIsNone() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        // Act
        PaginatedTransactionListDto transactions = dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                null,
                TransactionDirection.NONE,
                "", "",
                1, 5);
        // Assert
        Assert.assertTrue(transactions.getList().isEmpty());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_callFilterIncomingWithEmptySender_when_counterpartyIsNull_and_directionIsIncoming() {
        // Arrange
        User user = UserFactory.createUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(transactionService.filterIncoming(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                null,
                TransactionDirection.INCOMING,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterIncoming(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(user),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_callFilterOutgoingWithEmptyRecipient_when_counterpartyIsNull_and_directionIsOutgoing() {
        // Arrange
        User user = UserFactory.createUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(transactionService.filterOutgoing(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                null,
                TransactionDirection.OUTGOING,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterOutgoing(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(user),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_callFilterForUser_when_counterpartyIsNull_and_directionIsAll() {
        // Arrange
        User user = UserFactory.createUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(transactionService.filterForUser(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                null,
                TransactionDirection.ALL,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterForUser(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(user),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_callFilterIncomingWithSender_when_counterpartyIsNotNull_and_directionIsIncoming() {
        // Arrange
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(userService.getByUsername(otherUser.getUsername())).thenReturn(otherUser);
        Mockito.when(transactionService.filterIncoming(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                otherUser.getUsername(),
                TransactionDirection.INCOMING,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterIncoming(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(user),
                ArgumentMatchers.eq(Optional.of(otherUser)),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_callFilterOutgoingWithRecipient_when_counterpartyIsNotNull_and_directionIsOutgoing() {
        // Arrange
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(userService.getByUsername(otherUser.getUsername())).thenReturn(otherUser);
        Mockito.when(transactionService.filterOutgoing(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                otherUser.getUsername(),
                TransactionDirection.OUTGOING,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterOutgoing(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(user),
                ArgumentMatchers.eq(Optional.of(otherUser)),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_callFilterForUserWithCounterparty_when_counterpartyIsNotNull_and_directionIsAll() {
        // Arrange
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(userService.getByUsername(otherUser.getUsername())).thenReturn(otherUser);
        Mockito.when(transactionService.filterForUserWithCounterparty(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                otherUser.getUsername(),
                TransactionDirection.ALL,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filterForUserWithCounterparty(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(user),
                ArgumentMatchers.eq(otherUser),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    @Test
    public void getPresentableTransactionsWithPagination_should_createPaginatedTransactionListDto() {
        User user1 = UserFactory.createUser();
        User user2 = UserFactory.createOtherUser();
        PaymentInstrument user1Card = PaymentInstrumentFactory.createPaymentInstrument(1, user1, "Card 1", InstrumentType.CARD);
        PaymentInstrument user1Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user1, 2, "Wallet 1");
        PaymentInstrument user2Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user2, 3, "Wallet 2");
        Transaction transaction1 = TransactionFactory.createTransaction(user1Wallet, user2Wallet, BigDecimal.valueOf(20));
        Transaction transaction2 = TransactionFactory.createTransaction(user1Card, user1Wallet, BigDecimal.valueOf(100));
        Date startDate = Date.valueOf("2020-01-10");
        Date endDate = Date.valueOf("2020-01-15");
        String amount = "asc", date = "desc";
        TransactionDirection direction = TransactionDirection.OUTGOING;
        List<String> sortCriteria = Arrays.asList("amount.asc", "date.desc");
        Pageable pageable = PageRequest.of(0, 2);
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(transaction1, transaction2), pageable, 3);
        PresentableTransactionDto transactionDto1 = TransactionFactory.createPresentableTransactionDto(transaction1);
        PresentableTransactionDto transactionDto2 = TransactionFactory.createPresentableTransactionDto(transaction2);
        List<PresentableTransactionDto> transactionDtos = Arrays.asList(transactionDto1, transactionDto2);

        Mockito.when(userService.getByUsername(user1.getUsername())).thenReturn(user1);
        Mockito.when(transactionService.filterOutgoing(
                ArgumentMatchers.eq(startDate),
                ArgumentMatchers.eq(endDate),
                ArgumentMatchers.eq(user1),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.eq(sortCriteria),
                ArgumentMatchers.eq(pageable),
                ArgumentMatchers.eq(false))
        ).thenReturn(transactionPage);
        Mockito.when(transactionDtoMapper.toDto(transaction1)).thenReturn(transactionDto1);
        Mockito.when(transactionDtoMapper.toDto(transaction2)).thenReturn(transactionDto2);
        // Act
        PaginatedTransactionListDto transactionListDto =
                dtoListsMediatorService.getPresentableTransactionsWithPagination(startDate, endDate,
                        user1.getUsername(), null, direction, amount, date, 1, 2);
        // Assert
        Assert.assertTrue(transactionListDto.getList().equals(transactionDtos) &&
                transactionListDto.getSortCriteria().equals(sortCriteria) &&
                transactionListDto.getDirection().equals(direction));
    }

    @Test
    public void getPresentableTransactionsForAdminWithPagination_should_setStartAndEndDates_when_startAndEndDatesAreNotGiven() {
        // Arrange
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(transactionService.filter(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                null,
                null,
                null,
                null,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filter(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getPresentableTransactionsForAdminWithPagination_should_callFilterWithEmptySenderAndRecipient_when_senderNameIsNull_and_recipientNAmeIsNull() {
        // Arrange
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(transactionService.filter(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                null,
                null,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filter(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getPresentableTransactionsForAdminWithPagination_should_throw_when_recipientNameIsNull_and_senderDoesNotExist() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenThrow(EntityNotFoundException.class);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                null,
                "", "",
                1, 5);
    }

    @Test
    public void getPresentableTransactionsForAdminWithPagination_should_callFilterWithEmptyRecipient_when_recipientNameIsNull() {
        // Arrange
        User user = UserFactory.createUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(transactionService.filter(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                null,
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filter(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(Optional.of(user)),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getPresentableTransactionsForAdminWithPagination_should_throw_when_senderNameIsNull_and_recipientDoesNotExist() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenThrow(EntityNotFoundException.class);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                null,
                user.getUsername(),
                "", "",
                1, 5);
    }

    @Test
    public void getPresentableTransactionsForAdminWithPagination_should_callFilterWithEmptySender_when_senderUsernameIsNull() {
        // Arrange
        User user = UserFactory.createUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(transactionService.filter(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                null,
                user.getUsername(),
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filter(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(Optional.empty()),
                ArgumentMatchers.eq(Optional.of(user)),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getPresentableTransactionsForAdminWithPagination_should_throw_when_SenderDoesNotExist_and_recipientExists() {
        // Arrange
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenThrow(EntityNotFoundException.class);
        Mockito.when(userService.getByUsername(otherUser.getUsername())).thenReturn(otherUser);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                otherUser.getUsername(),
                "", "",
                1, 5);
    }


    @Test(expected = EntityNotFoundException.class) // Assert
    public void getPresentableTransactionsForAdminWithPagination_should_throw_when_SenderExists_and_RecipientDoesNotExist() {
        // Arrange
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenThrow(EntityNotFoundException.class);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                otherUser.getUsername(),
                user.getUsername(),
                "", "",
                1, 5);
    }

    @Test
    public void getPresentableTransactionsForAdminWithPagination_should_callFilterWithSenderAndRecipient_when_senderAndRecipientNamesAreNotNull() {
        // Arrange
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Page<Transaction> emptyPage = new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(userService.getByUsername(otherUser.getUsername())).thenReturn(otherUser);
        Mockito.when(transactionService.filter(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())
        ).thenReturn(emptyPage);
        // Act
        dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(
                Date.valueOf("2020-01-15"),
                Date.valueOf("2020-02-15"),
                user.getUsername(),
                otherUser.getUsername(),
                "", "",
                1, 5);
        // Assert
        Mockito.verify(transactionService).filter(
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.any(Date.class),
                ArgumentMatchers.eq(Optional.of(user)),
                ArgumentMatchers.eq(Optional.of(otherUser)),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getPresentableTransactionsForAdminWithPagination_should_createPaginatedTransactionListDto() {
        User user1 = UserFactory.createUser();
        User user2 = UserFactory.createOtherUser();
        PaymentInstrument user1Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user1, 2, "Wallet 1");
        PaymentInstrument user2Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user2, 3, "Wallet 2");
        Transaction transaction1 = TransactionFactory.createTransaction(user1Wallet, user2Wallet, BigDecimal.valueOf(20));
        Transaction transaction2 = TransactionFactory.createTransaction(user2Wallet, user1Wallet, BigDecimal.valueOf(100));
        Date startDate = Date.valueOf("2020-01-10");
        Date endDate = Date.valueOf("2020-01-15");
        String amount = "asc", date = "desc";
        List<String> sortCriteria = Arrays.asList("amount.asc", "date.desc");
        Pageable pageable = PageRequest.of(0, 2);
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(transaction1, transaction2), pageable, 3);
        PresentableTransactionDto transactionDto1 = TransactionFactory.createPresentableTransactionDto(transaction1);
        PresentableTransactionDto transactionDto2 = TransactionFactory.createPresentableTransactionDto(transaction2);
        List<PresentableTransactionDto> transactionDtos = Arrays.asList(transactionDto1, transactionDto2);

        Mockito.when(userService.getByUsername(user1.getUsername())).thenReturn(user1);
        Mockito.when(userService.getByUsername(user2.getUsername())).thenReturn(user2);
        Mockito.when(transactionService.filter(
                ArgumentMatchers.eq(startDate),
                ArgumentMatchers.eq(endDate),
                ArgumentMatchers.eq(Optional.of(user1)),
                ArgumentMatchers.eq(Optional.of(user2)),
                ArgumentMatchers.eq(sortCriteria),
                ArgumentMatchers.eq(pageable),
                ArgumentMatchers.eq(false))
        ).thenReturn(transactionPage);
        Mockito.when(transactionDtoMapper.toDto(transaction1)).thenReturn(transactionDto1);
        Mockito.when(transactionDtoMapper.toDto(transaction2)).thenReturn(transactionDto2);
        // Act
        PaginatedTransactionListDtoForAdmin transactionListDto =
                dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(startDate, endDate,
                        user1.getUsername(), user2.getUsername(), amount, date, 1, 2);
        // Assert
        Assert.assertTrue(transactionListDto.getList().equals(transactionDtos) &&
                transactionListDto.getSortCriteria().equals(sortCriteria));
    }

    @Test
    public void getRecipientsWithPagination_should_returnPaginatedRecipientListDto() {
        // Arrange
        initMockedFields();
        User user1 = UserFactory.createUser();
        User user2 = UserFactory.createOtherUser();
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2), pageable, 3);
        RecipientDto userDto1 = UserFactory.createRecipientDto(user1);
        RecipientDto userDto2 = UserFactory.createRecipientDto(user2);
        List<RecipientDto> userDtos = Arrays.asList(userDto1, userDto2);
        Mockito.when(userService.findUsersByContactType(pageable, "", "")).thenReturn(userPage);
        Mockito.when(userDtoMapper.toRecipientDto(user1)).thenReturn(userDto1);
        Mockito.when(userDtoMapper.toRecipientDto(user2)).thenReturn(userDto2);
        // Act
        PaginatedRecipientListDto recipientListDto = dtoListsMediatorService.getRecipientsWithPagination("", "", 1, 2);
        // Assert
        Assert.assertEquals(userDtos, recipientListDto.getList());
    }

    @Test
    public void getPresentableUsersWithPagination_should_returnPaginatedUserListDto() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user), pageable, 3);
        PresentableUserDto userDto = UserFactory.createPresentableUserDto(user);
        List<PresentableUserDto> userDtoList = Collections.singletonList(userDto);
        Mockito.when(userService.getAll(pageable)).thenReturn(userPage);
        Mockito.when(userDtoMapper.toDto(user)).thenReturn(userDto);
        // Act
        PaginatedUserListDto userListDto =
                dtoListsMediatorService.getPresentableUsersWithPagination(Constants.DEFAULT_EMPTY_VALUE, "", 1, 2);
        // Assert
        Assert.assertEquals(userDtoList, userListDto.getList());
    }

    @Test
    public void getPresentableUsersWithPagination_should_call_getAllWithContactTypeAndInfo_when_contactTypeIsNotNone() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user), pageable, 3);
        PresentableUserDto userDto = UserFactory.createPresentableUserDto(user);
        Mockito.when(userService.getAll(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(userPage);
        Mockito.when(userDtoMapper.toDto(user)).thenReturn(userDto);
        // Act
        dtoListsMediatorService.getPresentableUsersWithPagination("username", user.getUsername(), 1, 2);
        // Assert
        Mockito.verify(userService).getAll(pageable, "username", user.getUsername());
    }

    @Test
    public void getPresentableCardDtos_should_returnCardDtoList() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        PaymentInstrument cardInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user,"Card", InstrumentType.CARD);
        Card card = PaymentInstrumentFactory.createCard(cardInstrument);
        PresentableCardDto cardDto = PaymentInstrumentFactory.createPresentableCardDto(cardInstrument);
        Mockito.when(cardService.getAllByUser(user.getId())).thenReturn(Collections.singletonList(card));
        Mockito.when(paymentInstrumentDtoMapper.toDto(card)).thenReturn(cardDto);
        // Act
        List<PresentableCardDto> cardDtoList = dtoListsMediatorService.getPresentableCardDtos(user.getId());
        // Assert
        Assert.assertEquals(Collections.singletonList(cardDto), cardDtoList);
    }

    @Test
    public void getPresentableWalletDtos_should_returnWalletDtoList() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        PaymentInstrument walletInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user,"Wallet", InstrumentType.CARD);
        Wallet wallet = PaymentInstrumentFactory.createWallet(walletInstrument);
        PresentableWalletDto walletDto = PaymentInstrumentFactory.createPresentableWalletDto(walletInstrument);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(walletService.getAll(user.getId())).thenReturn(Collections.singletonList(wallet));
        Mockito.when(paymentInstrumentDtoMapper.toDto(wallet, user)).thenReturn(walletDto);
        // Act
        List<PresentableWalletDto> walletDtoList = dtoListsMediatorService.getPresentableWalletDtos(user.getUsername());
        // Assert
        Assert.assertEquals(Collections.singletonList(walletDto), walletDtoList);
    }

    private void initMockedFields() {
        dtoListsMediatorService = new DtoListsMediatorServiceImpl(
                Mockito.mock(UserService.class),
                Mockito.mock(TransactionDtoMapper.class),
                Mockito.mock(TransactionService.class));
        MockitoAnnotations.initMocks(this);
    }
}
