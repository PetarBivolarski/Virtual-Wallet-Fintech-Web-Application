package a16team1.virtualwallet.controllers.mvc.util.contracts;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.ChangePasswordDto;
import org.springframework.web.servlet.ModelAndView;

public interface PrepareUsersModelAndView {
    void forCreatingUserWithValidDetails(ModelAndView modelAndView, User user);

    void forCreatingUserWithInvalidDetails(ModelAndView modelAndView, String errorMessage);

    void forShowingUserProfile(ModelAndView modelAndView, String loggedUserUsername);

    void forGettingEditProfilePage(ModelAndView modelAndView);

    void forEditingUserWithInvalidDetails(ModelAndView modelAndView);

    void forEditingUserWithInvalidDetails(ModelAndView modelAndView, String errorMessage);

    void forChangingPasswordWithInvalidDetails(ModelAndView modelAndView, ChangePasswordDto passwordDto);

    void forChangingPasswordWithInvalidDetails(ModelAndView modelAndView, ChangePasswordDto passwordDto, String errorMessage);

}
