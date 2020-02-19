package a16team1.virtualwallet.controllers.mvc;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.misc.DonationProjectsFactory;
import a16team1.virtualwallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@PropertySource("classpath:messages.properties")
public class HomePageController {

    private UserService userService;
    private DonationProjectsFactory donationProjectsFactory;

    @Autowired
    public HomePageController(UserService userService,
                              DonationProjectsFactory donationProjectsFactory) {
        this.userService = userService;
        this.donationProjectsFactory = donationProjectsFactory;
    }

    @GetMapping("/")
    public ModelAndView showHomePage(ModelAndView modelAndView, Principal principal) {
        if (principal != null) {
            modelAndView.addObject("user", userService.getByUsername(principal.getName()));
        }
        User profileOfDonationProject = donationProjectsFactory.getCurrentActiveProject();
        modelAndView.addObject("currentDonatedAmount", profileOfDonationProject.getDefaultWallet().getSaldo());
        modelAndView.setViewName("index");
        return modelAndView;
    }
}
