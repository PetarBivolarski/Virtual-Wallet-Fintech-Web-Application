package a16team1.virtualwallet.controllers.mvc.util;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PrepareTransactionsModelAndView;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.dtos.*;
import a16team1.virtualwallet.services.*;
import a16team1.virtualwallet.utilities.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PrepareTransactionsModelAndViewImpl implements PrepareTransactionsModelAndView {

    private static final Duration DAYS_IN_A_MONTH = Duration.ofDays(30);

    private UserService userService;
    private WalletService walletService;
    private DtoListsMediatorService dtoListsMediatorService;

    @Value("${error.noUserMatchesSearchCriteria}")
    private String noUserMatchesSearchCriteria;

    @Value("${default.currency}")
    private String defaultCurrency;


    @Autowired
    public PrepareTransactionsModelAndViewImpl(UserService userService,
                                               WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @Override
    public void forGettingCreateExternalTransactionPage(ModelAndView modelAndView, String loggedUserUsername) {
        List<Wallet> wallets = walletService.getAll(loggedUserUsername);
        modelAndView.addObject("wallets", wallets);
        ExternalTransactionDto transactionDto = new ExternalTransactionDto();
        transactionDto.setSenderUsername(loggedUserUsername);
        modelAndView.addObject("externalTransactionDto", transactionDto);
    }

    @Override
    public void forChoosingRecipientFromPaginatedList(ModelAndView modelAndView,
                                                      PaginatedRecipientListDto recipientListDto,
                                                      String recipientContactType,
                                                      String recipientContactInfo) {
        if (recipientListDto.getList().isEmpty()) {
            modelAndView.addObject("noSearchResultsError", noUserMatchesSearchCriteria);
        } else {
            modelAndView.addObject("contactType", recipientContactType);
            modelAndView.addObject("contact", recipientContactInfo);
            modelAndView.addObject("recipientListDto", recipientListDto);
        }
    }

    @Override
    public void forCreatingTransactionWithValidDetails(ModelAndView modelAndView,
                                                       String senderUsername,
                                                       int recipientId,
                                                       BigDecimal transferAmount) {
        modelAndView.addObject("amount", transferAmount);
        modelAndView.addObject("currency", defaultCurrency);
        User recipient = userService.getById(recipientId);
        modelAndView.addObject("contactField", "username");
        modelAndView.addObject("recipient", recipient.getUsername());
        modelAndView.setViewName("transaction-completed");
    }

    @Override
    public void forMakingTransactionWithLargeAmount(ModelAndView modelAndView, String loggedUserUsername) {
        User user = userService.getByUsername(loggedUserUsername);
        modelAndView.addObject("email", user.getEmail());
        modelAndView.setViewName("transaction-confirmation-required");
    }

    @Override
    public void forCreatingTransactionWithInvalidDetails(ModelAndView modelAndView, String senderUsername, String errorMessage) {
        modelAndView.addObject("error", errorMessage);
        modelAndView.setViewName("transaction-create-external");
    }

    @Override
    public void forConfirmingTransactionWithValidToken(ModelAndView modelAndView, Transaction transaction) {
        if (transaction.getTransactionType().equals(TransactionType.LARGE_VERIFIED)) {
            modelAndView.addObject("verifiedTransaction", transaction);
        }
        modelAndView.addObject("amount", transaction.getTransferAmount().setScale(2, RoundingMode.HALF_UP));
        PaymentInstrument senderPaymentRecipient = transaction.getSenderInstrument();
        modelAndView.addObject("currency", defaultCurrency);
        User user = transaction.getRecipientInstrument().getOwner();
        modelAndView.addObject("contactField", "username");
        modelAndView.addObject("recipient", user.getUsername());
        modelAndView.setViewName("transaction-completed");
    }

    @Override
    public void forExpiredVerificationToken(ModelAndView modelAndView, String errorMessage) {
        modelAndView.addObject("verificationExpired", errorMessage);
        modelAndView.setViewName("transaction-confirmation-expired");
    }

    @Override
    public void forAlreadyConfirmedTransaction(ModelAndView modelAndView,
                                               String errorMessage,
                                               TransactionVerificationToken verificationToken) {
        modelAndView.addObject("contactField", "username");
        modelAndView.addObject("recipient", verificationToken.getTransaction().getRecipientInstrument().getOwner().getUsername());
        modelAndView.addObject("transactionCompleted", errorMessage);
        modelAndView.setViewName("transaction-completed");
    }

    @Override
    public void forGettingPresentableTransactionsWithPagination(ModelAndView modelAndView,
                                                                PaginatedTransactionListDto listDto,
                                                                String amount,
                                                                String date) {
        modelAndView.addObject("startDate", listDto.getStartDate());
        modelAndView.addObject("endDate", listDto.getEndDate());
        modelAndView.addObject("counterparty", listDto.getCounterPartyUsername());
        modelAndView.addObject("direction", listDto.getDirection());
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("date", date);
        modelAndView.addObject("currency", defaultCurrency);
        modelAndView.addObject("transactions", listDto.getList());
        if (listDto.getTotalPages() > 1) {
            modelAndView.addObject("pageSize", listDto.getPageSize());
            modelAndView.addObject("pageNumber", listDto.getPage());
            modelAndView.addObject("beginIndex", listDto.getBeginIndex());
            modelAndView.addObject("endIndex", listDto.getEndIndex());
            modelAndView.addObject("lastPage", listDto.getTotalPages());
        }
    }

    public void forHandlingErrorWithPresentableTransactions(ModelAndView modelAndView, String errorMessage) {
        modelAndView.addObject("transactions", Collections.emptyList());
        modelAndView.addObject("error", errorMessage);
        Instant currentTimeInstant = ZonedDateTime.now().toInstant();
        Date startDate = Date.valueOf((currentTimeInstant.minus(DAYS_IN_A_MONTH)).toString().substring(0, 10));
        Date endDate = Date.valueOf(currentTimeInstant.toString().substring(0, 10));
        modelAndView.addObject("startDate", startDate);
        modelAndView.addObject("endDate", endDate);

    }

    @Override
    public void forGettingFundingTransactionPage(ModelAndView modelAndView, String ownerUsername) {
        User user = userService.getByUsername(ownerUsername);
        List<PresentableCardDto> presentableCardDtos = dtoListsMediatorService.getPresentableCardDtos(user.getId());
        modelAndView.addObject("cardList", presentableCardDtos);
        List<Wallet> wallets = walletService.getAll(user.getId());
        modelAndView.addObject("walletList", wallets);
        FundingTransactionDto transactionDto = new FundingTransactionDto();
        transactionDto.setUserId(user.getId());
        modelAndView.addObject("fundingTransactionDto", transactionDto);
    }

    @Override
    public void forMakingFundingTransactionWithInvalidDetails(ModelAndView modelAndView, String ownerUsername, FundingTransactionDto fundingTransactionDto) {
        User user = userService.getByUsername(ownerUsername);
        List<PresentableCardDto> presentableCardDtos = dtoListsMediatorService.getPresentableCardDtos(user.getId());
        modelAndView.addObject("cardList", presentableCardDtos);
        List<Wallet> wallets = walletService.getAll(user.getId());
        modelAndView.addObject("walletList", wallets);
        fundingTransactionDto.setUserId(user.getId());
        modelAndView.addObject("fundingTransactionDto", fundingTransactionDto);
    }

    @Autowired
    public void setDtoListsMediatorService(DtoListsMediatorService dtoListsMediatorService) {
        this.dtoListsMediatorService = dtoListsMediatorService;
    }
}
