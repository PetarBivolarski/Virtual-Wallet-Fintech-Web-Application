package a16team1.virtualwallet.models.misc;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DonationProjectsFactory {

    private static final String USERNAME_OF_DONATION_PROJECT_PROFILE = "SOSChildrenVillages";

    private UserService userService;

    @Autowired
    public DonationProjectsFactory(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentActiveProject() {
        return userService.getByUsername(USERNAME_OF_DONATION_PROJECT_PROFILE);
    }
}
