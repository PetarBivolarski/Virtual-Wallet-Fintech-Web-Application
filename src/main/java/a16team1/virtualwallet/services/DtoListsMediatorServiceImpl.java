package a16team1.virtualwallet.services;

import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.*;
import a16team1.virtualwallet.utilities.ModelFactory;
import a16team1.virtualwallet.utilities.TransactionDirection;
import a16team1.virtualwallet.utilities.mappers.PaymentInstrumentDtoMapper;
import a16team1.virtualwallet.utilities.mappers.TransactionDtoMapper;
import a16team1.virtualwallet.utilities.mappers.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static a16team1.virtualwallet.utilities.Constants.DEFAULT_EMPTY_VALUE;

@Service
public class DtoListsMediatorServiceImpl implements DtoListsMediatorService {

    private static final Duration DAYS_IN_A_MONTH = Duration.ofDays(30);
    private static final String DEFAULT_COUNTERPARTY_NAME = "N/A";
    private static final String RECIPIENT_CANNOT_BE_EMPTY = "Recipient username cannot be empty";
    private static final String SENDER_CANNOT_BE_EMPTY = "Sender username cannot be empty";

    private UserService userService;
    private TransactionDtoMapper transactionDtoMapper;
    private UserDtoMapper userDtoMapper;
    private PaymentInstrumentDtoMapper paymentInstrumentDtoMapper;
    private TransactionService transactionService;
    private ModelFactory modelFactory;
    private WalletService walletService;
    private CardService cardService;

    @Value("${admin.transactions.recipientDoesNotExist}")
    private String recipientDoesNotExist;

    @Value("${admin.transactions.senderDoesNotExist}")
    private String senderDoesNotExist;

    @Value("${admin.transactions.recipientOrSenderDoesNotExist}")
    private String senderOrRecipientDoesNotExist;

    @Autowired
    public DtoListsMediatorServiceImpl(UserService userService,
                                       TransactionDtoMapper transactionDtoMapper,
                                       TransactionService transactionService) {
        this.userService = userService;
        this.transactionDtoMapper = transactionDtoMapper;
        this.transactionService = transactionService;
    }

    @Override
    public PaginatedTransactionListDto getPresentableTransactionsWithPagination(Date startDate,
                                                                                Date endDate,
                                                                                String loggedUserUsername,
                                                                                String counterpartyUsername,
                                                                                TransactionDirection direction,
                                                                                String amount,
                                                                                String date,
                                                                                int page,
                                                                                int pageSize) {
        User user = userService.getByUsername(loggedUserUsername);
        Instant currentTimeInstant = ZonedDateTime.now().toInstant();
        if (startDate == null) {
            startDate = Date.valueOf((currentTimeInstant.minus(DAYS_IN_A_MONTH)).toString().substring(0, 10));
        }
        if (endDate == null) {
            endDate = Date.valueOf(currentTimeInstant.toString().substring(0, 10));
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<String> sortCriteria = ModelFactory.getSortingCriteriaList(amount, date);
        Page<Transaction> transactions = getTransactionsByCriteria(startDate, endDate, counterpartyUsername, direction, user, sortCriteria, pageable);
        List<PresentableTransactionDto> transactionListDto = new ArrayList<>();
        for (Transaction transaction : transactions) {
            PresentableTransactionDto presentableTransactionDto = transactionDtoMapper.toDto(transaction);
            transactionListDto.add(presentableTransactionDto);
        }
        return ModelFactory.populatePaginatedTransactionListDto(
                startDate, endDate, counterpartyUsername, direction, transactionListDto, sortCriteria, page,
                pageSize, transactions.getNumber(), transactions.getTotalPages());
    }

    @Override
    public PaginatedTransactionListDtoForAdmin getPresentableTransactionsForAdminWithPagination(Date startDate,
                                                                                                Date endDate,
                                                                                                String senderUsername,
                                                                                                String recipientUsername,
                                                                                                String amount,
                                                                                                String date,
                                                                                                int page,
                                                                                                int pageSize) {
        Instant currentTimeInstant = ZonedDateTime.now().toInstant();
        if (startDate == null) {
            startDate = Date.valueOf((currentTimeInstant.minus(DAYS_IN_A_MONTH)).toString().substring(0, 10));
        }
        if (endDate == null) {
            endDate = Date.valueOf(currentTimeInstant.toString().substring(0, 10));
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<String> sortCriteria = ModelFactory.getSortingCriteriaList(amount, date);
        Page<Transaction> transactions = getAdminTransactionsByCriteria(startDate, endDate, senderUsername, recipientUsername, sortCriteria, pageable);
        List<PresentableTransactionDto> transactionListDto = new ArrayList<>();
        for (Transaction transaction : transactions) {
            PresentableTransactionDto presentableTransactionDto = transactionDtoMapper.toDto(transaction);
            transactionListDto.add(presentableTransactionDto);
        }
        return ModelFactory.populatePaginatedTransactionListDtoForAdmins(
                startDate, endDate, transactionListDto, sortCriteria, page,
                pageSize, transactions.getNumber(), transactions.getTotalPages());
    }

    @Override
    public PaginatedRecipientListDto getRecipientsWithPagination(String contactType, String contactInformation, int page, int pageSize) {
        Page<User> recipients = userService.findUsersByContactType(PageRequest.of(page - 1, pageSize), contactType, contactInformation);
        List<RecipientDto> listOfUserDtos = new ArrayList<>();
        for (User user : recipients) {
            listOfUserDtos.add(userDtoMapper.toRecipientDto(user));
        }
        return ModelFactory.getPaginatedRecipientListDto(listOfUserDtos, page, pageSize, recipients.getNumber() + 1, recipients.getTotalPages());
    }

    @Override
    public PaginatedUserListDto getPresentableUsersWithPagination(String contactType, String contactInformation, int page, int pageSize) {
        Page<User> userPage;
        if (!contactType.equals(DEFAULT_EMPTY_VALUE)) {
            userPage = userService.getAll(PageRequest.of(page - 1, pageSize), contactType, contactInformation);
        } else {
            userPage = userService.getAll(PageRequest.of(page - 1, pageSize));
        }
        List<PresentableUserDto> listOfUserDtos = new ArrayList<>();
        for (User user : userPage) {
            PresentableUserDto presentableUserDto = userDtoMapper.toDto(user);
            listOfUserDtos.add(presentableUserDto);
        }
        return ModelFactory.getPaginatedUserListDto(
                listOfUserDtos, page, pageSize,
                userPage.getNumber(), userPage.getTotalPages());
    }

    @Override
    public List<PresentableCardDto> getPresentableCardDtos(int userId) {
        List<Card> list = cardService.getAllByUser(userId);
        List<PresentableCardDto> presentableCardDtos = new ArrayList<>();
        for (Card card : list) {
            PresentableCardDto cardDto = paymentInstrumentDtoMapper.toDto(card);
            presentableCardDtos.add(cardDto);
        }
        return presentableCardDtos;
    }

    @Override
    public List<PresentableWalletDto> getPresentableWalletDtos(String ownerUsername) {
        User user = userService.getByUsername(ownerUsername);
        List<Wallet> userWallets = walletService.getAll(user.getId());
        List<PresentableWalletDto> presentableWalletDtoList = new ArrayList<>();
        for (Wallet wallet : userWallets) {
            PresentableWalletDto walletDto = paymentInstrumentDtoMapper.toDto(wallet, user);
            presentableWalletDtoList.add(walletDto);
        }
        return presentableWalletDtoList;
    }

    @Autowired
    public void setUserDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    @Autowired
    public void setPaymentInstrumentDtoMapper(PaymentInstrumentDtoMapper paymentInstrumentDtoMapper) {
        this.paymentInstrumentDtoMapper = paymentInstrumentDtoMapper;
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    private Page<Transaction> getTransactionsByCriteria(Date startDate, Date endDate, String counterparty,
                                                        TransactionDirection direction, User user,
                                                        List<String> sortBy, Pageable pageable) {
        Page<Transaction> transactions = new PageImpl<>(new ArrayList<>(), pageable, 0);
        if (counterparty == null) {
            switch (direction) {
                case INCOMING:
                    transactions = transactionService.filterIncoming(startDate, endDate, user, Optional.empty(), sortBy, pageable, false);
                    break;
                case OUTGOING:
                    transactions = transactionService.filterOutgoing(startDate, endDate, user, Optional.empty(), sortBy, pageable, false);
                    break;
                case ALL:
                    transactions = transactionService.filterForUser(startDate, endDate, user, sortBy, pageable);
            }
        } else {
            User otherUser = userService.getByUsername(counterparty);
            switch (direction) {
                case INCOMING:
                    transactions = transactionService.filterIncoming(startDate, endDate, user, Optional.of(otherUser), sortBy, pageable, false);
                    break;
                case OUTGOING:
                    transactions = transactionService.filterOutgoing(startDate, endDate, user, Optional.of(otherUser), sortBy, pageable, false);
                    break;
                case ALL:
                    transactions = transactionService.filterForUserWithCounterparty(startDate, endDate, user, otherUser, sortBy, pageable);
            }
        }
        return transactions;
    }

    private Page<Transaction> getAdminTransactionsByCriteria(Date startDate, Date endDate, String senderUsername, String recipientUsername,
                                                             List<String> sortCriteria, Pageable pageable) {
        Page<Transaction> transactions;
        if (recipientUsername == null) {
            if (senderUsername == null) {
                transactions = transactionService.filter(startDate, endDate, Optional.empty(), Optional.empty(), sortCriteria, pageable, false);
            } else {
                try {
                    User sender = userService.getByUsername(senderUsername);
                    transactions = transactionService.filter(startDate, endDate, Optional.of(sender), Optional.empty(), sortCriteria, pageable, false);
                } catch (EntityNotFoundException e) {
                    throw new EntityNotFoundException(senderDoesNotExist);
                }
            }
        } else {
            if (senderUsername == null) {
                try {
                    User recipient = userService.getByUsername(recipientUsername);
                    transactions = transactionService.filter(startDate, endDate, Optional.empty(), Optional.of(recipient), sortCriteria, pageable, false);
                } catch (EntityNotFoundException e) {
                    throw new EntityNotFoundException(recipientDoesNotExist);
                }
            } else {
                try {
                    User recipient = userService.getByUsername(recipientUsername);
                    User sender = userService.getByUsername(senderUsername);
                    transactions = transactionService.filter(startDate, endDate, Optional.of(sender), Optional.of(recipient), sortCriteria, pageable, false);
                } catch (EntityNotFoundException e) {
                    throw new EntityNotFoundException(senderOrRecipientDoesNotExist);
                }
            }
        }
        return transactions;
    }


}
