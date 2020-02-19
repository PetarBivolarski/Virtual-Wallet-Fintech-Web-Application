package a16team1.virtualwallet.utilities;

import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.dtos.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static a16team1.virtualwallet.utilities.Constants.DEFAULT_EMPTY_VALUE;

public class ModelFactory {

    private static final int PAGE_WINDOW_SIZE = 5;

    public static Transaction getTransaction(BigDecimal amount,
                                             String description,
                                             PaymentInstrument senderInstrument,
                                             PaymentInstrument recipientInstrument) {
        Transaction transaction = new Transaction();
        transaction.setDateTime(Timestamp.from(ZonedDateTime.now().toInstant()));
        transaction.setDescription(description);
        transaction.setSenderInstrument(senderInstrument);
        transaction.setRecipientInstrument(recipientInstrument);
        transaction.setTransferAmount(amount);
        return transaction;
    }

    public static PaginatedTransactionListDto populatePaginatedTransactionListDto(Date startDate,
                                                                                  Date endDate,
                                                                                  String counterpartyUsername,
                                                                                  TransactionDirection direction,
                                                                                  List<PresentableTransactionDto> transactionDtos,
                                                                                  List<String> sortCriteria,
                                                                                  int page,
                                                                                  int pageSize,
                                                                                  int currentPage,
                                                                                  int lastPage) {
        PaginatedTransactionListDto paginatedTransactionListDto = new PaginatedTransactionListDto();
        paginatedTransactionListDto.setStartDate(startDate);
        paginatedTransactionListDto.setEndDate(endDate);
        paginatedTransactionListDto.setCounterPartyUsername(counterpartyUsername);
        paginatedTransactionListDto.setDirection(direction);
        paginatedTransactionListDto.setList(transactionDtos);
        paginatedTransactionListDto.setSortCriteria(sortCriteria);
        addPagination(paginatedTransactionListDto, page, pageSize, currentPage, lastPage);
        return paginatedTransactionListDto;
    }

    public static PaginatedTransactionListDtoForAdmin populatePaginatedTransactionListDtoForAdmins(Date startDate,
                                                                                                   Date endDate,
                                                                                                   List<PresentableTransactionDto> list,
                                                                                                   List<String> sortCriteria,
                                                                                                   int page,
                                                                                                   int pageSize,
                                                                                                   int currentPage,
                                                                                                   int lastPage) {
        PaginatedTransactionListDtoForAdmin dtoForAdmin = new PaginatedTransactionListDtoForAdmin();
        dtoForAdmin.setStartDate(startDate);
        dtoForAdmin.setEndDate(endDate);
        dtoForAdmin.setList(list);
        dtoForAdmin.setSortCriteria(sortCriteria);
        dtoForAdmin.setPage(page);
        dtoForAdmin.setPageSize(pageSize);
        dtoForAdmin.setTotalPages(lastPage);
        addPagination(dtoForAdmin, page, pageSize, currentPage, lastPage);
        return dtoForAdmin;
    }

    public static NewPaymentInstrumentDto getNewPaymentInstrumentDtoForCard(NewCardDto cardDto) {
        NewPaymentInstrumentDto paymentInstrumentDto = cardDto.getPaymentInstrumentDto();
        paymentInstrumentDto.setInstrumentType(InstrumentType.CARD);
        return paymentInstrumentDto;
    }

    public static NewPaymentInstrumentDto getNewPaymentInstrumentDtoForWallet(NewWalletDto walletDto) {
        NewPaymentInstrumentDto paymentInstrumentDto = walletDto.getPaymentInstrumentDto();
        paymentInstrumentDto.setName(walletDto.getName());
        paymentInstrumentDto.setInstrumentType(InstrumentType.WALLET);
        return paymentInstrumentDto;
    }

    public static PaginatedRecipientListDto getPaginatedRecipientListDto(List<RecipientDto> list,
                                                                         int page,
                                                                         int pageSize,
                                                                         int currentPage,
                                                                         int lastPage) {
        PaginatedRecipientListDto paginatedUserListDto = new PaginatedRecipientListDto();
        paginatedUserListDto.setList(list);
        addPagination(paginatedUserListDto, page, pageSize, currentPage, lastPage);
        return paginatedUserListDto;
    }

    public static PaginatedUserListDto getPaginatedUserListDto(List<PresentableUserDto> list,
                                                               int page,
                                                               int pageSize,
                                                               int currentPage,
                                                               int lastPage) {
        PaginatedUserListDto paginatedUserListDto = new PaginatedUserListDto();
        paginatedUserListDto.setList(list);
        addPagination(paginatedUserListDto, page, pageSize, currentPage, lastPage);
        return paginatedUserListDto;
    }

    public static List<String> getSortingCriteriaList(String amount, String date) {
        List<String> sortCriteria = new ArrayList<>();
        if (!amount.equals(DEFAULT_EMPTY_VALUE)) {
            sortCriteria.add("amount." + amount);
        }
        if (!date.equals(DEFAULT_EMPTY_VALUE)) {
            sortCriteria.add("date." + date);
        }
        if (sortCriteria.isEmpty()) {
            sortCriteria = Collections.singletonList("date.desc");
        }
        return sortCriteria;
    }

    public static Wallet getWalletWithZeroSaldo() {
        Wallet wallet = new Wallet();
        wallet.setSaldo(BigDecimal.ZERO);
        return wallet;
    }

    public static Transaction getDonationTransaction(PaymentInstrument senderInstrument, PaymentInstrument recipientInstrument) {
        Transaction transaction = new Transaction();
        transaction.setWithDonation(true);
        transaction.setTransferAmount(Constants.DONATION_AMOUNT);
        transaction.setTransactionType(TransactionType.DONATION);
        transaction.setSenderInstrument(senderInstrument);
        transaction.setRecipientInstrument(recipientInstrument);
        transaction.setDescription("Donation");
        transaction.setDateTime(Timestamp.from(ZonedDateTime.now().toInstant()));
        return transaction;
    }

    public static NewWalletDto getNewWalletDto(String name, int ownerId) {
        NewWalletDto walletDto = new NewWalletDto();
        walletDto.setName(name);
        walletDto.setOwnerId(ownerId);
        return walletDto;
    }

    public static NewPaymentInstrumentDto getNewPaymentInstrumentDto(String name,
                                                                     InstrumentType instrumentType,
                                                                     int ownerId) {
        NewPaymentInstrumentDto paymentInstrument = new NewPaymentInstrumentDto();
        paymentInstrument.setName(name);
        paymentInstrument.setInstrumentType(instrumentType);
        paymentInstrument.setOwnerId(ownerId);
        return paymentInstrument;
    }

    public static PaymentInstrument getPaymentInstrumentForCard(User user) {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setInstrumentType(InstrumentType.CARD);
        paymentInstrument.setOwner(user);
        return paymentInstrument;
    }

    public static PaymentInstrument getPaymentInstrumentForWallet(User user, NewWalletDto walletDto) {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setInstrumentType(InstrumentType.WALLET);
        paymentInstrument.setOwner(user);
        paymentInstrument.setName(walletDto.getName());
        return paymentInstrument;
    }

    public static Card getNewCardFromExistingCard(Card card) {
        Card newCard = new Card();
        newCard.setCardholderName(card.getCardholderName());
        newCard.setCardNumber(card.getCardNumber());
        newCard.setExpirationDate(card.getExpirationDate());
        newCard.setCsv(card.getCsv());
        return newCard;
    }

    private static <T extends PaginatedList> void addPagination(T t,
                                                                int page,
                                                                int pageSize,
                                                                int currentPage,
                                                                int lastPage) {
        t.setPage(page);
        t.setPageSize(pageSize);
        t.setTotalPages(lastPage);
        if (lastPage > 0) {
            int beginIndex;
            int endIndex;
            if (currentPage + PAGE_WINDOW_SIZE <= lastPage) {
                beginIndex = Math.max(1, currentPage);
                endIndex = beginIndex + PAGE_WINDOW_SIZE - 1;
            } else {
                endIndex = lastPage;
                beginIndex = Math.max(1, endIndex - PAGE_WINDOW_SIZE + 1);
            }
            t.setBeginIndex(beginIndex);
            t.setEndIndex(endIndex);
        }
    }


}
