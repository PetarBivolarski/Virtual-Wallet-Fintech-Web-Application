package a16team1.virtualwallet.controllers.mvc.util.contracts;

import a16team1.virtualwallet.models.dtos.PaginatedTransactionListDtoForAdmin;
import a16team1.virtualwallet.models.dtos.PaginatedUserListDto;
import org.springframework.web.servlet.ModelAndView;

public interface PrepareAdminModelAndView {

    void forGettingAdminUserList(ModelAndView modelAndView,
                                 PaginatedUserListDto paginatedUserListDto,
                                 String contactType,
                                 String contactInformation);

    void forBlockingUsers(ModelAndView modelAndView,
                          String filterType,
                          String contactInformation,
                          int currentPageNumber,
                          int currentPageSize);

    void forUnblockingUsers(ModelAndView modelAndView,
                            String filterType,
                            String contactInformation,
                            int currentPageNumber,
                            int currentPageSize);

    void forGettingAdminTransactionList(ModelAndView modelAndView,
                                   PaginatedTransactionListDtoForAdmin listDto,
                                   String senderUsername,
                                   String recipientUsername,
                                   String amount,
                                   String date);

    void forHandlingErrorWithPresentableTransactions(ModelAndView modelAndView,
                                                     String errorMessage);
}
