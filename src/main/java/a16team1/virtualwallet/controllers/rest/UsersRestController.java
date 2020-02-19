package a16team1.virtualwallet.controllers.rest;


import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.ChangePasswordRestDto;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.dtos.ExistingCardDto;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import a16team1.virtualwallet.services.CardService;
import a16team1.virtualwallet.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UsersRestController {

    private UserService userService;
    private CardService cardService;

    @Autowired
    public UsersRestController(UserService userService,
                               CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping
    @ApiOperation(value = "Get all users", notes = "The API returns paginated list with default page size of 5, unless changed.")
    public List<User> getUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeByAdminRole(requestAuthor);
        return userService.getAll(PageRequest.of(page, size)).getContent();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get user by id")
    public User getUserById(@PathVariable int id,
                            @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, id);
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Edit user")
    public User updateUser(@PathVariable int id,
                           @Valid @RequestBody EditUserDto editUserDto,
                           @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, id);
        User user = userService.getById(id);
        return userService.updateDetails(user.getUsername(), editUserDto, Optional.empty());
    }

    @PutMapping("/{id}/change-password")
    @ApiOperation(value = "Change user password")
    public void updatePassword(@PathVariable int id,
                               @Valid @RequestBody ChangePasswordRestDto changePasswordDto,
                               @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, id);
        User user = userService.getById(id);
        userService.updatePassword(user.getUsername(), changePasswordDto.getCurrentPassword(), changePasswordDto.getNewPassword());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete user")
    public User deleteUser(@PathVariable int id,
                           @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, id);
        return userService.delete(id);
    }

    @PostMapping("/register")
    @ApiOperation(value = "Create user")
    public User createUser(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.create(newUserDto, Optional.empty());
    }

    @GetMapping("/{userId}/cards")
    @ApiOperation(value = "Get all cards", notes = "The API returns a list of a specific user`s cards.")
    public List<Card> getCards(@PathVariable int userId,
                               @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, userId);
        return cardService.getAllByUser(userId);
    }

    @GetMapping("/{userId}/cards/{cardId}")
    @ApiOperation(value = "Get a card")
    public Card getCardById(@PathVariable int userId, @PathVariable int cardId,
                            @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, userId);
        return cardService.getById(cardId);
    }

    @PutMapping("/{userId}/cards/{cardId}")
    @ApiOperation(value = "Update a card")
    public Card updateCardDetails(@PathVariable int userId, @PathVariable int cardId,
                                  @Valid @RequestBody ExistingCardDto existingCardDto,
                                  @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, userId);
        return cardService.update(existingCardDto, cardId, userId);
    }

    @DeleteMapping("/{userId}/cards/{cardId}")
    @ApiOperation(value = "Delete a card")
    public Card deleteCard(@PathVariable int userId, @PathVariable int cardId,
                           @RequestHeader(name = "Authorization") String authorization) {
        User requestAuthor = userService.authenticateRestCredentials(authorization);
        userService.authorizeUserByIdOrRole(requestAuthor, userId);
        User user = userService.getById(userId);
        return cardService.delete(cardId, user.getUsername());
    }

}
