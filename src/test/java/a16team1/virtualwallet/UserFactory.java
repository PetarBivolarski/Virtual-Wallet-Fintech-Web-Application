package a16team1.virtualwallet;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import a16team1.virtualwallet.models.dtos.PresentableUserDto;
import a16team1.virtualwallet.models.dtos.RecipientDto;

import javax.naming.event.EventDirContext;

public class UserFactory {

    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("petbiv");
        user.setPhoneNumber("(+359)888333444");
        user.setEmail("petbiv@gmail.com");
        return user;
    }

    public static User createBlockedUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("petbiv");
        user.setPhoneNumber("(+359)888333444");
        user.setEmail("petbiv@gmail.com");
        user.setBlocked(true);
        return user;
    }

    public static User createOtherUser() {
        User user = new User();
        user.setId(2);
        user.setUsername("inpenev");
        user.setPhoneNumber("(+1)123456789");
        user.setEmail("inpenev@gmail.com");
        return user;
    }

    public static User createUserWithoutId() {
        User user = createUser();
        user.setId(0);
        return user;
    }

    public static User createUserToBlock() {
        User user = new User();
        user.setId(2);
        user.setUsername("inpenev");
        user.setPhoneNumber("(+1)123456789");
        user.setEmail("inpenev@gmail.com");
        return user;
    }

    public static User createUserToUnBlock() {
        User user = createUserToBlock();
        user.setBlocked(true);
        return user;
    }

    public static User createUserWithEmail(String email) {
        User user = new User();
        user.setId(3);
        user.setUsername("penka");
        user.setEmail(email);
        user.setPhoneNumber("(+44)987654321");
        return user;
    }

    public static User createUserWithPhoneNumber(String phoneNumber) {
        User user = new User();
        user.setId(4);
        user.setUsername("donka");
        user.setEmail("donka@example.com");
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    public static User createDonationProjectProfile() {
        User user = new User();
        user.setId(5);
        user.setUsername("SOS");
        user.setEmail("sos@example.com");
        user.setPhoneNumber("(+359)89955566");
        return user;
    }

    public static User createThirdUser() {
        User user = new User();
        user.setId(6);
        user.setUsername("stefan");
        user.setEmail("stefan@example.com");
        user.setPhoneNumber("(+49)58755658");
        return user;
    }

    public static NewUserDto createNewUserDto(String username, String email, String countryCode, String localPhoneNumber) {
        NewUserDto userDto = new NewUserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setCountryCode(countryCode);
        userDto.setLocalPhoneNumber(localPhoneNumber);
        return userDto;
    }

    public static RecipientDto createRecipientDto(User user) {
        RecipientDto recipientDto = new RecipientDto();
        recipientDto.setId(user.getId());
        recipientDto.setUsername(user.getUsername());
        return recipientDto;
    }

    public static PresentableUserDto createPresentableUserDto(User user) {
        return new PresentableUserDto(user.getId(), user.getUsername(), user.getPhoneNumber(), user.getEmail(), user.isBlocked());
    }

    public static EditUserDto createEditUserDto(String email, String countryCode, String localPhoneNumber) {
        EditUserDto userDto = new EditUserDto();
        userDto.setEmail(email);
        userDto.setCountryCode(countryCode);
        userDto.setLocalPhoneNumber(localPhoneNumber);
        return userDto;
    }

}
