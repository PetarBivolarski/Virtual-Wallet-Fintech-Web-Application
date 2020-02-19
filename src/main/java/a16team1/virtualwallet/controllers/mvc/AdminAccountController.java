package a16team1.virtualwallet.controllers.mvc;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PrepareAdminModelAndView;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.PaginatedTransactionListDtoForAdmin;
import a16team1.virtualwallet.models.dtos.PaginatedUserListDto;
import a16team1.virtualwallet.services.DtoListsMediatorService;
import a16team1.virtualwallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.sql.Date;

import static a16team1.virtualwallet.utilities.Constants.DEFAULT_EMPTY_VALUE;

@Controller
@PropertySource("classpath:messages.properties")
public class AdminAccountController {

    private UserService userService;
    private DtoListsMediatorService dtoListsMediatorService;
    private PrepareAdminModelAndView prepareModelAndViewService;

    @Autowired
    public AdminAccountController(UserService userService,
                                  DtoListsMediatorService dtoListsMediatorService,
                                  PrepareAdminModelAndView prepareModelAndViewService) {
        this.userService = userService;
        this.dtoListsMediatorService = dtoListsMediatorService;
        this.prepareModelAndViewService = prepareModelAndViewService;
    }

    @ModelAttribute("user")
    public User loadUser(Principal principal) {
        return userService.getByUsername(principal.getName());
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage(ModelAndView modelAndView) {
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @GetMapping("/admin/users")
    public ModelAndView showListOfUsers(ModelAndView modelAndView,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "5") int pageSize,
                                        @RequestParam(name = "filterType", defaultValue = DEFAULT_EMPTY_VALUE, required = false) String contactType,
                                        @RequestParam(name = "contact", defaultValue = DEFAULT_EMPTY_VALUE, required = false) String contactInformation) {
        try {
            PaginatedUserListDto paginatedUserListDto =
                    dtoListsMediatorService.getPresentableUsersWithPagination(contactType, contactInformation, page, pageSize);
            prepareModelAndViewService.forGettingAdminUserList(modelAndView, paginatedUserListDto, contactType, contactInformation);
        } catch (IllegalArgumentException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("invalidValue", e.getMessage());
        }
        modelAndView.setViewName("admin-users");
        return modelAndView;
    }

    @GetMapping("/admin/transactions")
    public ModelAndView showListOfTransactions(@RequestParam(required = false) Date startDate,
                                               @RequestParam(required = false) Date endDate,
                                               @RequestParam(name = "sender", required = false) String senderUsername,
                                               @RequestParam(name = "recipient", required = false) String recipientUsername,
                                               @RequestParam(name = "amount", defaultValue = DEFAULT_EMPTY_VALUE) String amount,
                                               @RequestParam(name = "date", defaultValue = DEFAULT_EMPTY_VALUE) String date,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               ModelAndView modelAndView) {
        try {
            PaginatedTransactionListDtoForAdmin list = dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(startDate,
                    endDate, senderUsername, recipientUsername, amount, date, page, pageSize);
            prepareModelAndViewService.forGettingAdminTransactionList(modelAndView, list, senderUsername, recipientUsername, amount, date);
        } catch (IllegalArgumentException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            prepareModelAndViewService.forHandlingErrorWithPresentableTransactions(modelAndView, e.getMessage());
        } catch (EntityNotFoundException e) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            prepareModelAndViewService.forHandlingErrorWithPresentableTransactions(modelAndView, e.getMessage());
        }
        modelAndView.setViewName("admin-transactions");
        return modelAndView;
    }


    @PostMapping("/admin/users/{id}/block")
    public ModelAndView blockUser(ModelAndView modelAndView, Principal principal,
                                  @PathVariable(name = "id") int userId,
                                  @RequestParam(name = "currentPageNumber", defaultValue = "1") int currentPageNumber,
                                  @RequestParam(name = "currentPageSize", defaultValue = "5") int currentPageSize,
                                  @RequestParam(name = "filterType", defaultValue = DEFAULT_EMPTY_VALUE) String filterType,
                                  @RequestParam(name = "contact", defaultValue = DEFAULT_EMPTY_VALUE) String contactInformation) {
        userService.block(principal.getName(), userId);
        prepareModelAndViewService.forBlockingUsers(modelAndView, filterType, contactInformation, currentPageNumber, currentPageSize);
        return modelAndView;
    }

    @PostMapping("admin/users/{id}/unblock")
    public ModelAndView unblockUser(ModelAndView modelAndView, Principal principal,
                                    @PathVariable(name = "id") int userId,
                                    @RequestParam(name = "currentPageNumber", defaultValue = "1") int currentPageNumber,
                                    @RequestParam(name = "currentPageSize", defaultValue = "5") int currentPageSize,
                                    @RequestParam(name = "filterType", defaultValue = DEFAULT_EMPTY_VALUE) String filterType,
                                    @RequestParam(name = "contact", defaultValue = DEFAULT_EMPTY_VALUE) String contactInformation) {
        userService.unblock(principal.getName(), userId);
        prepareModelAndViewService.forUnblockingUsers(modelAndView, filterType, contactInformation, currentPageNumber, currentPageSize);
        return modelAndView;
    }

}
