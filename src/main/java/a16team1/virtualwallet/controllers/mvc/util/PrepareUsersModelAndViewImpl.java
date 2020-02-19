package a16team1.virtualwallet.controllers.mvc.util;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PrepareUsersModelAndView;
import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.ChangePasswordDto;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.misc.InspirationalQuote;
import a16team1.virtualwallet.services.InspirationalQuoteService;
import a16team1.virtualwallet.services.TransactionService;
import a16team1.virtualwallet.services.UserService;
import a16team1.virtualwallet.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class PrepareUsersModelAndViewImpl implements PrepareUsersModelAndView {

    private UserService userService;
    private TransactionService transactionService;
    private WalletService walletService;
    private InspirationalQuoteService inspirationalQuoteService;

    @Autowired
    public PrepareUsersModelAndViewImpl(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Override
    public void forCreatingUserWithValidDetails(ModelAndView modelAndView, User user) {
        modelAndView.addObject("email", user.getEmail());
        modelAndView.setViewName("successful-registration");
    }

    @Override
    public void forCreatingUserWithInvalidDetails(ModelAndView modelAndView, String errorMessage) {
        modelAndView.addObject("duplicateUser", errorMessage);
        modelAndView.setViewName("register");
    }

    @Override
    public void forShowingUserProfile(ModelAndView modelAndView, String loggedUserUsername) {
        User user = userService.getByUsername(loggedUserUsername);
        String joinedDate = new SimpleDateFormat("dd/MM/yyyy").format(user.getJoinedDate());
        modelAndView.addObject("joinedDate", joinedDate);
        Date currentDate = new Date(System.currentTimeMillis());
        Date dateJoined = new Date(user.getJoinedDate().getTime());
        List<String> sortCriteria = Arrays.asList("date.desc");
        Page<Transaction> transactionsByUser = transactionService.filterForUser(dateJoined, currentDate, user, sortCriteria, PageRequest.of(0, 3));
        List<Transaction> latestThreeTransactions = transactionsByUser.getContent();
        modelAndView.addObject("latestThreeTransactions", latestThreeTransactions);
        BigDecimal currentSaldo = walletService.getTotalUserSaldo(user.getId());
        modelAndView.addObject("currentSaldo", currentSaldo);
        InspirationalQuote inspirationalQuote = inspirationalQuoteService.getRandom();
        modelAndView.addObject("quote", inspirationalQuote);
        modelAndView.setViewName("user-profile");
    }

    @Override
    public void forGettingEditProfilePage(ModelAndView modelAndView) {
        modelAndView.addObject("editUserDto", new EditUserDto());
        modelAndView.addObject("changePasswordDto", new ChangePasswordDto());
        modelAndView.setViewName("user-profile-edit");
    }

    @Override
    public void forEditingUserWithInvalidDetails(ModelAndView modelAndView) {
        modelAndView.addObject("changePasswordDto", new ChangePasswordDto());
        modelAndView.setViewName("user-profile-edit");
    }

    @Override
    public void forEditingUserWithInvalidDetails(ModelAndView modelAndView,
                                                 String errorMessage) {
        modelAndView.addObject("errorDetails", errorMessage);
        modelAndView.addObject("changePasswordDto", new ChangePasswordDto());
        modelAndView.setViewName("user-profile-edit");
    }

    @Override
    public void forChangingPasswordWithInvalidDetails(ModelAndView modelAndView,
                                                      ChangePasswordDto passwordDto) {
        modelAndView.addObject("editUserDto", new EditUserDto());
        modelAndView.addObject("changePasswordDto", passwordDto);
        modelAndView.setViewName("user-profile-edit");
    }

    @Override
    public void forChangingPasswordWithInvalidDetails(ModelAndView modelAndView,
                                                      ChangePasswordDto passwordDto,
                                                      String errorMessage) {
        modelAndView.addObject("editUserDto", new EditUserDto());
        modelAndView.addObject("changePasswordDto", passwordDto);
        modelAndView.addObject("errorPassword", errorMessage);
        modelAndView.setViewName("user-profile-edit");
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setInspirationalQuoteService(InspirationalQuoteService inspirationalQuoteService) {
        this.inspirationalQuoteService = inspirationalQuoteService;
    }
}
