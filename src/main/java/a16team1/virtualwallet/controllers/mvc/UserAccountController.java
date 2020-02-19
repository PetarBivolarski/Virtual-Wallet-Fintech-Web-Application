package a16team1.virtualwallet.controllers.mvc;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PrepareUsersModelAndView;
import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.InvalidOperationException;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.ChangePasswordDto;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import a16team1.virtualwallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.UncheckedIOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@PropertySource("classpath:messages.properties")
public class UserAccountController {

    private UserService userService;
    private PrepareUsersModelAndView prepareModelAndViewService;

    @Autowired
    public UserAccountController(UserService userService,
                                 PrepareUsersModelAndView prepareModelAndViewService) {
        this.userService = userService;
        this.prepareModelAndViewService = prepareModelAndViewService;
    }

    @ModelAttribute("user")
    public void populateUserModel(Principal principal, ModelAndView modelAndView) {
        if (principal != null) {
            User user = userService.getByUsername(principal.getName());
            modelAndView.addObject("user", user);
        }
    }


    @GetMapping("/registration")
    public ModelAndView displayRegistration(ModelAndView modelAndView, NewUserDto newUserDto,
                                            @RequestParam(name = "invitationToken", required = false) String invitationToken) {
        modelAndView.addObject("invitationToken", invitationToken);
        modelAndView.addObject("user", newUserDto);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView createUser(ModelAndView modelAndView,
                                   @Valid @ModelAttribute("user") NewUserDto newUserDto,
                                   BindingResult result,
                                   @RequestParam(name = "invitationToken", required = false) String invitationToken) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("invitationToken", invitationToken);
            modelAndView.setViewName("register");
        } else {
            try {
                User user = userService.create(newUserDto, Optional.ofNullable(invitationToken));
                prepareModelAndViewService.forCreatingUserWithValidDetails(modelAndView, user);
            } catch (DuplicateEntityException e) {
                modelAndView.setStatus(HttpStatus.CONFLICT);
                prepareModelAndViewService.forCreatingUserWithInvalidDetails(modelAndView, e.getMessage());
            }
        }
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView showLoginPage(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/user/profile")
    public ModelAndView showMyProfile(ModelAndView modelAndView, Principal principal) {
        prepareModelAndViewService.forShowingUserProfile(modelAndView, principal.getName());
        return modelAndView;
    }

    @GetMapping("/user/profile/edit")
    public ModelAndView getEditProfilePage(ModelAndView modelAndView) {
        prepareModelAndViewService.forGettingEditProfilePage(modelAndView);
        return modelAndView;
    }

    @PostMapping("/user/profile/edit")
    public ModelAndView editProfile(ModelAndView modelAndView, @Valid @ModelAttribute("editUserDto") EditUserDto userDto, BindingResult result,
                                    Principal principal, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            prepareModelAndViewService.forEditingUserWithInvalidDetails(modelAndView);
        } else {
            try {
                userService.updateDetails(principal.getName(), userDto, Optional.of(file));
                modelAndView.setViewName("redirect:/user/profile");
            } catch (InvalidOperationException e) {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                prepareModelAndViewService.forEditingUserWithInvalidDetails(modelAndView, e.getMessage());
            } catch (DuplicateEntityException e) {
                modelAndView.setStatus(HttpStatus.CONFLICT);
                prepareModelAndViewService.forEditingUserWithInvalidDetails(modelAndView, e.getMessage());
            } catch (UncheckedIOException e) {
                modelAndView.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
                prepareModelAndViewService.forEditingUserWithInvalidDetails(modelAndView, e.getMessage());
            }
        }
        return modelAndView;
    }

    @PostMapping("/user/change-password")
    public ModelAndView changePassword(ModelAndView modelAndView,
                                       @Valid @ModelAttribute("changePasswordDto") ChangePasswordDto passwordDto,
                                       BindingResult result,
                                       Principal principal) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            prepareModelAndViewService.forChangingPasswordWithInvalidDetails(modelAndView, passwordDto);
        } else {
            try {
                userService.updatePassword(principal.getName(), passwordDto.getCurrentPassword(), passwordDto.getNewPassword());
                modelAndView.setViewName("redirect:/user/profile");
            } catch (InvalidOperationException e) {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                prepareModelAndViewService.forChangingPasswordWithInvalidDetails(modelAndView, passwordDto, e.getMessage());
            }
        }
        return modelAndView;
    }

    @GetMapping("/confirm-account")
    public ModelAndView confirmUserAccount(ModelAndView modelAndView,
                                           @RequestParam("token") String verificationTokenName,
                                           @RequestParam(value = "invitationToken", required = false) String invitationTokenName) {
        try {
            userService.confirmUserRegistration(verificationTokenName, Optional.ofNullable(invitationTokenName));
            modelAndView.setViewName("confirmed-registration");
        } catch (InvalidOperationException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.setViewName("already-confirmed-registration");
        } catch (IllegalArgumentException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("message", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @PostMapping("/referral-invitation")
    public ModelAndView sendReferralInvitation(ModelAndView modelAndView,
                                               Principal principal,
                                               @RequestParam("email") String email) {
        try {
            userService.sendReferralLinkForRegistration(principal.getName(), email);
        } catch (IllegalArgumentException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("error", e.getMessage());
        } catch (DuplicateEntityException e) {
            modelAndView.setStatus(HttpStatus.CONFLICT);
            modelAndView.addObject("error", e.getMessage());
        }
        modelAndView.setViewName("referral-invitation");
        return modelAndView;
    }


}
