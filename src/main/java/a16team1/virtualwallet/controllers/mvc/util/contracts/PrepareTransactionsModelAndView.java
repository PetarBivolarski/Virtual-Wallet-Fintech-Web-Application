package a16team1.virtualwallet.controllers.mvc.util.contracts;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.TransactionVerificationToken;
import a16team1.virtualwallet.models.dtos.FundingTransactionDto;
import a16team1.virtualwallet.models.dtos.PaginatedRecipientListDto;
import a16team1.virtualwallet.models.dtos.PaginatedTransactionListDto;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

public interface PrepareTransactionsModelAndView {

    void forGettingCreateExternalTransactionPage(ModelAndView modelAndView,
                                                 String loggedUserUsername);

    void forChoosingRecipientFromPaginatedList(ModelAndView modelAndView,
                                               PaginatedRecipientListDto recipientListDto,
                                               String recipientContactType,
                                               String recipientContactInfo);

    void forCreatingTransactionWithValidDetails(ModelAndView modelAndView,
                                                String senderUsername,
                                                int recipientId,
                                                BigDecimal transferAmount);

    void forMakingTransactionWithLargeAmount(ModelAndView modelAndView, String loggedUserUsername);


    void forCreatingTransactionWithInvalidDetails(ModelAndView modelAndView,
                                                  String senderUsername,
                                                  String errorMessage);

    void forConfirmingTransactionWithValidToken(ModelAndView modelAndView,
                                                Transaction transaction);

    void forAlreadyConfirmedTransaction(ModelAndView modelAndView,
                                        String errorMessage,
                                        TransactionVerificationToken verificationToken);

    void forExpiredVerificationToken(ModelAndView modelAndView,
                                     String errorMessage);

    void forGettingPresentableTransactionsWithPagination(ModelAndView modelAndView,
                                                         PaginatedTransactionListDto listDto,
                                                         String amount,
                                                         String date);

    void forHandlingErrorWithPresentableTransactions(ModelAndView modelAndView, String errorMessage);

    void forGettingFundingTransactionPage(ModelAndView modelAndView, String ownerUsername);

    void forMakingFundingTransactionWithInvalidDetails(ModelAndView modelAndView, String ownerUsername, FundingTransactionDto fundingTransactionDto);
}
