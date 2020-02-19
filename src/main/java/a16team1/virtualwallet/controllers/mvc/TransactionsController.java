package a16team1.virtualwallet.controllers.mvc;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PrepareTransactionsModelAndView;
import a16team1.virtualwallet.exceptions.*;
import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.TransactionVerificationToken;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.ExternalTransactionDto;
import a16team1.virtualwallet.models.dtos.FundingTransactionDto;
import a16team1.virtualwallet.models.dtos.PaginatedRecipientListDto;
import a16team1.virtualwallet.models.dtos.PaginatedTransactionListDto;
import a16team1.virtualwallet.services.DtoListsMediatorService;
import a16team1.virtualwallet.services.TransactionService;
import a16team1.virtualwallet.services.UserService;
import a16team1.virtualwallet.services.email_tokens.TokenService;
import a16team1.virtualwallet.utilities.TransactionDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;

import static a16team1.virtualwallet.utilities.Constants.DEFAULT_EMPTY_VALUE;

@Controller
public class TransactionsController {

    private static final String DEFAULT_TRANSACTION_DIRECTION = "ALL";

    private UserService userService;
    private TransactionService transactionService;
    private PrepareTransactionsModelAndView prepareModelAndViewService;
    private TokenService tokenService;
    private DtoListsMediatorService dtoListsMediatorService;

    @Autowired
    public TransactionsController(UserService userService,
                                  TransactionService transactionService,
                                  PrepareTransactionsModelAndView prepareModelAndViewService,
                                  DtoListsMediatorService dtoListsMediatorService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.prepareModelAndViewService = prepareModelAndViewService;
        this.dtoListsMediatorService = dtoListsMediatorService;
    }

    @ModelAttribute("user")
    public User loadUser(Principal principal) {
        return userService.getByUsername(principal.getName());
    }

    @GetMapping("/transaction/create-external")
    public ModelAndView getExternalTransactionForm(@RequestParam(name = "contactType", defaultValue = "") String recipientContactType,
                                                   @RequestParam(name = "contact", defaultValue = "") String recipientContactInfo,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "5") int pageSize,
                                                   ModelAndView modelAndView,
                                                   Principal principal) {
        prepareModelAndViewService.forGettingCreateExternalTransactionPage(modelAndView, principal.getName());
        if (recipientContactType != null) {
            try {
                PaginatedRecipientListDto recipientListDto = dtoListsMediatorService.getRecipientsWithPagination(recipientContactType, recipientContactInfo, page, pageSize);
                prepareModelAndViewService.forChoosingRecipientFromPaginatedList(modelAndView, recipientListDto, recipientContactType, recipientContactInfo);
            } catch (IllegalArgumentException | ConstraintViolationException e) {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                modelAndView.addObject("error", e.getMessage());
            }
        }
        modelAndView.setViewName("transaction-create-external");
        return modelAndView;
    }

    @PostMapping("/transaction/create-external")
    public ModelAndView createExternalTransaction(@Valid @ModelAttribute("externalTransactionDto") ExternalTransactionDto transactionDto,
                                                  BindingResult result,
                                                  ModelAndView modelAndView,
                                                  Principal principal) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("externalTransactionDto", transactionDto);
            modelAndView.setViewName("transaction-create-external");
        } else {
            try {
                transactionService.createExternalTransaction(transactionDto);
                prepareModelAndViewService.forCreatingTransactionWithValidDetails(modelAndView, principal.getName(), transactionDto.getRecipientId(), transactionDto.getTransferAmount());
            } catch (LargeTransactionAmountException e) {
                modelAndView.setStatus(HttpStatus.CONFLICT);
                prepareModelAndViewService.forMakingTransactionWithLargeAmount(modelAndView, principal.getName());
            } catch (InvalidOperationException e) {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                prepareModelAndViewService.forCreatingTransactionWithInvalidDetails(modelAndView, principal.getName(), e.getMessage());
            } catch (EntityNotFoundException e) {
                modelAndView.setStatus(HttpStatus.NOT_FOUND);
                prepareModelAndViewService.forCreatingTransactionWithInvalidDetails(modelAndView, principal.getName(), e.getMessage());
            }
        }
        return modelAndView;
    }


    @GetMapping("/transaction/create-funding")
    public ModelAndView getFundingTransactionPage(ModelAndView modelAndView, Principal principal) {
        prepareModelAndViewService.forGettingFundingTransactionPage(modelAndView, principal.getName());
        modelAndView.setViewName("transaction-create-funding");
        return modelAndView;
    }

    @PostMapping("/transaction/create-funding")
    public ModelAndView createFundingTransaction(ModelAndView modelAndView, Principal principal,
                                                 @Valid @ModelAttribute("fundingTransactionDto") FundingTransactionDto fundingTransactionDto,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            prepareModelAndViewService.forMakingFundingTransactionWithInvalidDetails(modelAndView, principal.getName(), fundingTransactionDto);
            modelAndView.setViewName("transaction-create-funding");
        } else {
            try {
                transactionService.createFundingTransaction(fundingTransactionDto);
                modelAndView.addObject("amount", fundingTransactionDto.getTransferAmount());
                modelAndView.setViewName("transaction-funding-completed");
            } catch (InvalidOperationException e) {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                modelAndView.addObject("error", e.getMessage());
                getFundingTransactionPage(modelAndView, principal);
            } catch (InsufficientFundsException e) {
                modelAndView.setStatus(HttpStatus.FORBIDDEN);
                modelAndView.addObject("error", e.getMessage());
                getFundingTransactionPage(modelAndView, principal);
            }
        }
        return modelAndView;
    }

    @GetMapping("/confirm-transaction")
    public ModelAndView confirmTransaction(ModelAndView modelAndView,
                                           @RequestParam("token") String name) {
        try {
            Transaction transaction = transactionService.confirmLargeTransaction(name);
            prepareModelAndViewService.forConfirmingTransactionWithValidToken(modelAndView, transaction);
        } catch (InvalidOperationException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            TransactionVerificationToken verificationToken = tokenService.getTransactionVerificationTokenByName(name);
            prepareModelAndViewService.forAlreadyConfirmedTransaction(modelAndView, e.getMessage(), verificationToken);
        } catch (ExpiredVerificationTokenException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            prepareModelAndViewService.forExpiredVerificationToken(modelAndView, e.getMessage());
        }
        return modelAndView;
    }

    @GetMapping("/user/transaction-history")
    public ModelAndView getTransactionHistory(@RequestParam(required = false) Date startDate,
                                              @RequestParam(required = false) Date endDate,
                                              @RequestParam(required = false) String counterparty,
                                              @RequestParam(defaultValue = DEFAULT_TRANSACTION_DIRECTION) TransactionDirection direction,
                                              @RequestParam(name = "amount", defaultValue = DEFAULT_EMPTY_VALUE) String amount,
                                              @RequestParam(name = "date", defaultValue = DEFAULT_EMPTY_VALUE) String date,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "5") int pageSize,
                                              ModelAndView modelAndView,
                                              Principal principal) {
        try {
            PaginatedTransactionListDto list = dtoListsMediatorService.getPresentableTransactionsWithPagination(startDate,
                    endDate, principal.getName(), counterparty, direction, amount, date, page, pageSize);
            prepareModelAndViewService.forGettingPresentableTransactionsWithPagination(modelAndView, list, amount, date);
        } catch (IllegalArgumentException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            prepareModelAndViewService.forHandlingErrorWithPresentableTransactions(modelAndView, e.getMessage());
        } catch (EntityNotFoundException e) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            prepareModelAndViewService.forHandlingErrorWithPresentableTransactions(modelAndView, e.getMessage());
        }
        modelAndView.setViewName("transaction-history");
        return modelAndView;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

}
