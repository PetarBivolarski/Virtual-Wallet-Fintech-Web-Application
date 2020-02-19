package a16team1.virtualwallet.controllers.mvc.util;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PrepareAdminModelAndView;
import a16team1.virtualwallet.models.dtos.PaginatedTransactionListDtoForAdmin;
import a16team1.virtualwallet.models.dtos.PaginatedUserListDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;

import static a16team1.virtualwallet.utilities.Constants.DEFAULT_EMPTY_VALUE;

@Service
public class PrepareAdminModelAndViewImpl implements PrepareAdminModelAndView {

    private static final Duration DAYS_IN_A_MONTH = Duration.ofDays(30);

    @Value("${admin.users.emptyTitle}")
    private String noRegisteredUsers;

    @Value("${error.userNotFound}")
    private String userNotFound;

    public void forGettingAdminUserList(ModelAndView modelAndView, PaginatedUserListDto paginatedUserListDto,
                                        String contactType, String contactInformation) {
        if (paginatedUserListDto.getList().isEmpty() && contactType.equals(DEFAULT_EMPTY_VALUE)) {
            modelAndView.addObject("noUsers", noRegisteredUsers);
        } else if (paginatedUserListDto.getList().isEmpty() && !contactType.equals(DEFAULT_EMPTY_VALUE)) {
            modelAndView.addObject("userNotFound", userNotFound);
        }
        modelAndView.addObject("paginatedUserListDto", paginatedUserListDto);
        modelAndView.addObject("filterType", contactType);
        modelAndView.addObject("contact", contactInformation);
    }

    public void forBlockingUsers(ModelAndView modelAndView, String filterType,
                                 String contactInformation, int currentPageNumber,
                                 int currentPageSize) {
        if (filterType.equals(DEFAULT_EMPTY_VALUE) || contactInformation.equals(DEFAULT_EMPTY_VALUE)) {
            modelAndView.setViewName("redirect:/admin/users/?size=" + currentPageSize + "&page=" + currentPageNumber);
        } else {
            modelAndView.setViewName("redirect:/admin/users/?size=" + currentPageSize + "&page=" + currentPageNumber +
                    "&filterType=" + filterType + "&contact=" + contactInformation);
        }
    }

    @Override
    public void forUnblockingUsers(ModelAndView modelAndView, String filterType,
                                   String contactInformation, int currentPageNumber,
                                   int currentPageSize) {
        if (filterType.equals(DEFAULT_EMPTY_VALUE) || contactInformation.equals(DEFAULT_EMPTY_VALUE)) {
            modelAndView.setViewName("redirect:/admin/users/?size=" + currentPageSize + "&page=" + currentPageNumber);
        } else {
            modelAndView.setViewName("redirect:/admin/users/?size=" + currentPageSize + "&page=" + currentPageNumber +
                    "&filterType=" + filterType + "&contact=" + contactInformation);
        }
    }

    @Override
    public void forGettingAdminTransactionList(ModelAndView modelAndView, PaginatedTransactionListDtoForAdmin listDto,
                                               String senderUsername, String recipientUsername, String amount, String date) {
        modelAndView.addObject("startDate", listDto.getStartDate());
        modelAndView.addObject("endDate", listDto.getEndDate());
        modelAndView.addObject("sender", senderUsername);
        modelAndView.addObject("recipient", recipientUsername);
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("date", date);
        modelAndView.addObject("transactions", listDto.getList());
        if (listDto.getTotalPages() > 1) {
            modelAndView.addObject("pageSize", listDto.getPageSize());
            modelAndView.addObject("pageNumber", listDto.getPage());
            modelAndView.addObject("beginIndex", listDto.getBeginIndex());
            modelAndView.addObject("endIndex", listDto.getEndIndex());
            modelAndView.addObject("lastPage", listDto.getTotalPages());
        }
    }

    @Override
    public void forHandlingErrorWithPresentableTransactions(ModelAndView modelAndView, String errorMessage) {
        modelAndView.addObject("transactions", Collections.emptyList());
        modelAndView.addObject("error", errorMessage);
        Instant currentTimeInstant = ZonedDateTime.now().toInstant();
        Date startDate = Date.valueOf((currentTimeInstant.minus(DAYS_IN_A_MONTH)).toString().substring(0, 10));
        Date endDate = Date.valueOf(currentTimeInstant.toString().substring(0, 10));
        modelAndView.addObject("startDate", startDate);
        modelAndView.addObject("endDate", endDate);
    }


}
