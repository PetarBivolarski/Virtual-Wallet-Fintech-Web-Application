package a16team1.virtualwallet.utilities.mappers;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import a16team1.virtualwallet.models.dtos.PresentableUserDto;
import a16team1.virtualwallet.models.dtos.RecipientDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public User fromDtoWithoutPhoneNumber(NewUserDto newUserDto) {
        User user = new User();
        user.setUsername(newUserDto.getUsername());
        user.setEmail(newUserDto.getEmail());
        user.setPassword(newUserDto.getPassword());
        return user;
    }

    public PresentableUserDto toDto(User user) {
        return new PresentableUserDto(
                user.getId(),
                user.getUsername(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.isBlocked());
    }

    public RecipientDto toRecipientDto(User user) {
        RecipientDto userDto = new RecipientDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPhoto(user.getPhoto());
        return userDto;
    }

}
