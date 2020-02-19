package a16team1.virtualwallet.services;

import a16team1.virtualwallet.PaymentInstrumentFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.VerificationTokenFactory;
import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.exceptions.InvalidOperationException;
import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import a16team1.virtualwallet.repositories.contracts.UserRepository;
import a16team1.virtualwallet.services.email_tokens.EmailVerificationService;
import a16team1.virtualwallet.services.email_tokens.TokenService;
import a16team1.virtualwallet.utilities.Constants;
import a16team1.virtualwallet.utilities.InstrumentType;
import a16team1.virtualwallet.utilities.UserAttribute;
import a16team1.virtualwallet.utilities.mappers.UserDtoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDtoMapper dtoMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserServiceImpl userService;

    @Test(expected = IllegalArgumentException.class) // Assert
    public void getAll_should_throw_when_givenEmailIsInvalid() {
        // Arrange, Act
        userService.getAll(Pageable.unpaged(), "email", "petbivgmail.com");
    }

    @Test(expected = IllegalArgumentException.class) // Assert
    public void getAll_should_throw_when_givenPhoneNumberIsInvalid() {
        // Arrange, Act
        userService.getAll(Pageable.unpaged(), "phone", "(+2a)2345789");
    }

    @Test
    public void getAll_should_returnUserPage_when_givenUsernamePrefixIsValid() {
        // Arrange
        User user = UserFactory.createUser();
        Pageable pageable = Pageable.unpaged();
        String filterType = "username";
        String filterValue = user.getUsername().substring(0, 1);
        Page<User> expectedUsers = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Mockito.when(userRepository.getAll(pageable, filterType, filterValue + "%")).thenReturn(expectedUsers);
        // Act
        Page<User> actualUsers = userService.getAll(pageable, filterType, filterValue);
        // Assert
        Assert.assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void getAll_should_returnUserPage_when_givenEmailIsValid() {
        // Arrange
        User user = UserFactory.createUser();
        Pageable pageable = Pageable.unpaged();
        String filterType = "email";
        String filterValue = user.getEmail();
        Page<User> expectedUsers = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Mockito.when(userRepository.getAll(pageable, filterType, filterValue + "%")).thenReturn(expectedUsers);
        // Act
        Page<User> actualUsers = userService.getAll(pageable, filterType, filterValue);
        // Assert
        Assert.assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void getAll_should_returnUserPage_when_givenPhoneNumberIsValid() {
        // Arrange
        User user = UserFactory.createUser();
        Pageable pageable = Pageable.unpaged();
        String filterType = "phone";
        String fieldName = UserAttribute.valueOf(filterType.toUpperCase()).toFieldName();
        String filterValue = user.getPhoneNumber();
        Page<User> expectedUsers = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Mockito.when(userRepository.getAll(pageable, fieldName, filterValue + "%")).thenReturn(expectedUsers);
        // Act
        Page<User> actualUsers = userService.getAll(pageable, filterType, filterValue);
        // Assert
        Assert.assertEquals(expectedUsers, actualUsers);
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getById_should_throw_when_userDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.getById(0)).thenReturn(null);
        // Act
        userService.getById(0);
    }

    @Test
    public void getById_should_returnUser_when_userExists() {
        // Arrange
        User expectedUser = UserFactory.createUser();
        Mockito.when(userRepository.getById(expectedUser.getId())).thenReturn(expectedUser);
        // Act
        User actualUser = userService.getById(expectedUser.getId());
        // Assert
        Assert.assertSame(expectedUser, actualUser);
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getByUsername_should_throw_when_userDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.getByUsername("")).thenReturn(null);
        // Act
        userService.getByUsername("");
    }

    @Test
    public void getByUsername_should_returnUser_when_userExists() {
        // Arrange
        User expectedUser = UserFactory.createUser();
        Mockito.when(userRepository.getByUsername(expectedUser.getUsername())).thenReturn(expectedUser);
        // Act
        User actualUser = userService.getByUsername(expectedUser.getUsername());
        // Assert
        Assert.assertSame(expectedUser, actualUser);
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getByEmail_should_throw_when_userDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.getByEmail("")).thenReturn(null);
        // Act
        userService.getByEmail("");
    }

    @Test
    public void getByEmail_should_returnUser_when_userExists() {
        // Arrange
        User expectedUser = UserFactory.createUser();
        Mockito.when(userRepository.getByEmail(expectedUser.getEmail())).thenReturn(expectedUser);
        // Act
        User actualUser = userService.getByEmail(expectedUser.getEmail());
        // Assert
        Assert.assertSame(expectedUser, actualUser);
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getByPhoneNumber_should_throw_when_userDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.getByPhoneNumber("")).thenReturn(null);
        // Act
        userService.getByPhoneNumber("");
    }

    @Test
    public void getByPhoneNumber_should_returnUser_when_userExists() {
        // Arrange
        User expectedUser = UserFactory.createUser();
        Mockito.when(userRepository.getByPhoneNumber(expectedUser.getPhoneNumber())).thenReturn(expectedUser);
        // Act
        User actualUser = userService.getByPhoneNumber(expectedUser.getPhoneNumber());
        // Assert
        Assert.assertSame(expectedUser, actualUser);
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void create_should_throw_when_userWithSameUsernameAlreadyExists() {
        // Arrange
        User existingUser = UserFactory.createUser();
        NewUserDto userDto = UserFactory.createNewUserDto(existingUser.getUsername(), "", "", "");
        Mockito.when(userRepository.getByUsername(existingUser.getUsername())).thenReturn(existingUser);
        // Act
        userService.create(userDto, Optional.empty());
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void create_should_throw_when_userWithSameEmailAlreadyExists() {
        // Arrange
        User existingUser = UserFactory.createUser();
        NewUserDto userDto = UserFactory.createNewUserDto("", existingUser.getEmail(), "", "");
        Mockito.when(userRepository.getByUsername(userDto.getUsername())).thenReturn(null);
        Mockito.when(userRepository.getByEmail(existingUser.getEmail())).thenReturn(existingUser);
        // Act
        userService.create(userDto, Optional.empty());
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void create_should_throw_when_userWithSamePhoneNumberAlreadyExists() {
        // Arrange
        User existingUser = UserFactory.createUser();
        String[] countryCodeAndLocalPhoneNumber = getCountryCodeAndLocalPhoneNumber(existingUser.getPhoneNumber());
        NewUserDto userDto = UserFactory.createNewUserDto("", "", countryCodeAndLocalPhoneNumber[0], countryCodeAndLocalPhoneNumber[1]);
        Mockito.when(userRepository.getByUsername(userDto.getUsername())).thenReturn(null);
        Mockito.when(userRepository.getByEmail(userDto.getEmail())).thenReturn(null);
        Mockito.when(userRepository.getByPhoneNumber(existingUser.getPhoneNumber())).thenReturn(existingUser);
        // Act
        userService.create(userDto, Optional.empty());
    }

    @Test
    public void create_should_returnCreatedUser_when_noUserWithSameDataExistsYet() {
        // Arrange
        initMockedFields();
        User expectedUser = UserFactory.createUser();
        User userFromDto = UserFactory.createUserWithoutId();
        String[] countryCodeAndLocalPhoneNumber = getCountryCodeAndLocalPhoneNumber(expectedUser.getPhoneNumber());
        NewUserDto userDto = UserFactory.createNewUserDto(expectedUser.getUsername(), expectedUser.getEmail(),
                countryCodeAndLocalPhoneNumber[0], countryCodeAndLocalPhoneNumber[1]);
        Mockito.when(userRepository.getByUsername(expectedUser.getUsername())).thenReturn(null);
        Mockito.when(userRepository.getByEmail(expectedUser.getEmail())).thenReturn(null);
        Mockito.when(userRepository.getByPhoneNumber(expectedUser.getPhoneNumber())).thenReturn(null);
        Mockito.when(dtoMapper.fromDtoWithoutPhoneNumber(userDto)).thenReturn(userFromDto);
        Mockito.when(userRepository.create(userFromDto)).thenReturn(expectedUser);
        // Act
        User actualUser = userService.create(userDto, Optional.empty());
        // Assert
        Assert.assertSame(actualUser, expectedUser);
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void block_should_throw_when_userWithGivenUsernameIsNotAdmin() {
        // Arrange
        User regularUser = UserFactory.createUser();
        Mockito.when(userRepository.getByUsername(regularUser.getUsername())).thenReturn(regularUser);
        Mockito.when(userRepository.getUserRoles(regularUser)).thenReturn(Collections.singletonList("ROLE_USER"));
        // Act
        userService.block(regularUser.getUsername(), 2);
    }

    @Test
    public void block_should_blockUserWithGivenId_when_userWithGivenUsernameIsAdmin() {
        // Arrange
        User admin = UserFactory.createUser();
        User userToBlock = UserFactory.createUserToBlock();
        Mockito.when(userRepository.getByUsername(admin.getUsername())).thenReturn(admin);
        Mockito.when(userRepository.getUserRoles(admin)).thenReturn(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        Mockito.when(userRepository.getById(userToBlock.getId())).thenReturn(userToBlock);
        // Act
        User blockedUser = userService.block(admin.getUsername(), userToBlock.getId());
        // Assert
        Mockito.verify(userRepository).block(ArgumentMatchers.argThat(User::isBlocked));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void unblock_should_throw_when_userWithGivenUsernameIsNotAdmin() {
        // Arrange
        User regularUser = UserFactory.createUser();
        Mockito.when(userRepository.getByUsername(regularUser.getUsername())).thenReturn(regularUser);
        Mockito.when(userRepository.getUserRoles(regularUser)).thenReturn(Collections.singletonList("ROLE_USER"));
        // Act
        userService.unblock(regularUser.getUsername(), 2);
    }

    @Test
    public void unblock_should_unblockUserWithGivenId_when_userWithGivenUsernameIsAdmin() {
        // Arrange
        User admin = UserFactory.createUser();
        User userToUnBlock = UserFactory.createUserToUnBlock();
        Mockito.when(userRepository.getByUsername(admin.getUsername())).thenReturn(admin);
        Mockito.when(userRepository.getUserRoles(admin)).thenReturn(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        Mockito.when(userRepository.getById(userToUnBlock.getId())).thenReturn(userToUnBlock);
        // Act
        userService.unblock(admin.getUsername(), userToUnBlock.getId());
        // Assert
        Mockito.verify(userRepository).unblock(ArgumentMatchers.argThat((User u) -> !u.isBlocked()));
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void update_should_throw_when_userDoesNotExist() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userRepository.getById(user.getId())).thenReturn(null);
        // Act
        userService.update(user);
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void update_should_throw_when_anotherUserWithSameEmailAlreadyExists() {
        // Arrange
        User conflictingUser = UserFactory.createUser();
        User userToUpdate = UserFactory.createUserWithEmail(conflictingUser.getEmail());
        Mockito.when(userRepository.getById(userToUpdate.getId())).thenReturn(userToUpdate);
        Mockito.when(userRepository.getByEmail(conflictingUser.getEmail())).thenReturn(conflictingUser);
        // Act
        userService.update(userToUpdate);
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void update_should_throw_when_anotherUserWithSamePoneNumberAlreadyExists() {
        // Arrange
        User conflictingUser = UserFactory.createUser();
        User userToUpdate = UserFactory.createUserWithPhoneNumber(conflictingUser.getPhoneNumber());
        Mockito.when(userRepository.getById(userToUpdate.getId())).thenReturn(userToUpdate);
        Mockito.when(userRepository.getByEmail(userToUpdate.getEmail())).thenReturn(null);
        Mockito.when(userRepository.getByPhoneNumber(conflictingUser.getPhoneNumber())).thenReturn(conflictingUser);
        // Act
        userService.update(userToUpdate);
    }

    @Test
    public void update_should_returnUserToUpdate_when_noConflictWithOtherUserDataExists() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userRepository.getById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.getByEmail(user.getEmail())).thenReturn(null);
        Mockito.when(userRepository.getByPhoneNumber(user.getPhoneNumber())).thenReturn(null);
        Mockito.when(userRepository.update(user)).thenReturn(user);
        // Act
        User updatedUser = userService.update(user);
        // Assert
        Assert.assertSame(user, updatedUser);
    }

    @Test(expected = EntityNotFoundException.class) // Assert
    public void delete_should_throw_when_userDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.getById(0)).thenReturn(null);
        // Act
        userService.delete(0);
    }

    @Test
    public void delete_should_disableUser_when_userExists() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userRepository.getById(user.getId())).thenReturn(user);
        // Act
        User deletedUser = userService.delete(user.getId());
        // Assert
        Mockito.verify(userRepository).delete(ArgumentMatchers.argThat((User u) -> !u.isEnabled()));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void updateDefaultWallet_should_throw_when_userDoesNotOwnWalletWithGivenId() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        PaymentInstrument oldPaymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Old Wallet", InstrumentType.WALLET);
        Wallet oldWallet = PaymentInstrumentFactory.createWallet(oldPaymentInstrument);
        Mockito.when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(walletService.getAll(user.getId())).thenReturn(Collections.singletonList(oldWallet));
        // Act
        userService.updateDefaultWallet(user.getUsername(), 2);
    }

    @Test
    public void updateDefaultWallet_should_changeDefaultWallet_when_userOwnsWalletWithGivenId() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        PaymentInstrument oldPaymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Old Wallet", InstrumentType.WALLET);
        Wallet oldWallet = PaymentInstrumentFactory.createWallet(oldPaymentInstrument);
        user.setDefaultWallet(oldWallet);
        PaymentInstrument newPaymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(2, user, "New Wallet", InstrumentType.WALLET);
        Wallet newWallet = PaymentInstrumentFactory.createWallet(newPaymentInstrument);
        Mockito.when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(walletService.getAll(user.getId())).thenReturn(Arrays.asList(oldWallet, newWallet));
        Mockito.when(walletService.getById(newWallet.getId())).thenReturn(newWallet);
        Mockito.when(userRepository.getById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.getByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(userRepository.getByPhoneNumber(user.getPhoneNumber())).thenReturn(user);
        Mockito.when(userRepository.update(user)).thenReturn(user);
        // Act
        User updatedUser = userService.updateDefaultWallet(user.getUsername(), newWallet.getId());
        // Assert
        Assert.assertTrue(updatedUser.equals(user) && updatedUser.getDefaultWallet().equals(newWallet));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void updatePassword_should_throw_when_passwordsDontMatch() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        Mockito.when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
        // Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(false);
        // Act
        userService.updatePassword(user.getUsername(), "a", "b");
    }

    @Test
    public void updatePassword_shouldChangePassword_when_passwordsMatch() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        String oldPassword = "abcdef";
        String newPassword = "123456";
        user.setPassword(oldPassword);
        Mockito.when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(passwordEncoder.matches(oldPassword, oldPassword)).thenReturn(true);
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);
        Mockito.when(userRepository.update(user)).thenReturn(user);
        // Act
        User updatedUser = userService.updatePassword(user.getUsername(), oldPassword, newPassword);
        // Assert
        Assert.assertTrue(user.equals(updatedUser) && updatedUser.getPassword().equals(newPassword));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void findUsersByContactType_should_throw_when_contactTypeIsInvalid() {
        // Arrange, Act
        userService.findUsersByContactType(Pageable.unpaged(), "", "");
    }

    @Test
    public void findUsersByContactType_should_returnPageOfUsersWhen_when_contactTypeIsUsername() {
        // Arrange
        User user = UserFactory.createUser();
        Pageable pageable = Pageable.unpaged();
        String contactType = "username";
        String contactInfo = user.getUsername().substring(0, 3);
        Page<User> expectedUsers = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Mockito.when(userRepository.getAll(pageable, contactType, contactInfo + "%")).thenReturn(expectedUsers);
        // Act
        Page<User> actualUsers = userService.findUsersByContactType(pageable, contactType, contactInfo);
        // Assert
        Assert.assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void findUsersByContactType_should_returnPageOfUsers_when_contactTypeIsEmail() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        Pageable pageable = Pageable.unpaged();
        String contactType = "email";
        String contactInfo = user.getEmail();
        Page<User> expectedUsers = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Mockito.when(userRepository.getByEmail(contactInfo)).thenReturn(user);
        Mockito.when(validator.validateValue(NewUserDto.class, contactType, contactInfo)).thenReturn(Collections.emptySet());
        // Act
        Page<User> actualUsers = userService.findUsersByContactType(pageable, contactType, contactInfo);
        // Assert
        Assert.assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void findUsersByContactType_should_returnPageOfUsers_when_contactTypeIsPhoneNumber() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        Pageable pageable = Pageable.unpaged();
        String contactType = "phone";
        String contactInfo = user.getPhoneNumber();
        Page<User> expectedUsers = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Mockito.when(userRepository.getByPhoneNumber(contactInfo)).thenReturn(user);
//        Mockito.when(validator.validateValue(NewUserDto.class, contactType, contactInfo)).thenReturn(Collections.emptySet());
        // Act
        Page<User> actualUsers = userService.findUsersByContactType(pageable, contactType, contactInfo);
        // Assert
        Assert.assertEquals(expectedUsers.getContent(), actualUsers.getContent());
    }

    @Test(expected = IllegalArgumentException.class) // Assert
    public void confirmUserRegistration_should_throw_when_verificationTokenIsNull() {
        // Arrange
        initMockedFields();
        Mockito.when(tokenService.getUserVerificationTokenByName("")).thenReturn(null);
        // Act
        userService.confirmUserRegistration("", Optional.empty());
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void confirmUserRegistration_should_throw_when_userAccountIsAlreadyConfirmed() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        user.setConfirmedRegistration(true);
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(user);
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        // Act
        userService.confirmUserRegistration(verificationToken.getToken(), Optional.empty());
    }

    @Test
    public void confirmUserRegistration_should_changeRegistrationStatus_whenUserAccountIsNotYetConfirmed() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(user);
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        Mockito.when(userRepository.getByEmail(user.getEmail())).thenReturn(user);
        // Act
        User updatedUser = userService.confirmUserRegistration(verificationToken.getToken(), Optional.empty());
        // Assert
        Assert.assertTrue(updatedUser.equals(user) && updatedUser.getConfirmedRegistration());
    }

    @Test
    public void confirmUserRegistration_should_notProcessInvitationTokenStatus_when_invitationTokenIsExpired() {
        initMockedFields();
        User newUser = UserFactory.createUser();
        User invitingUser = UserFactory.createOtherUser();
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(newUser);
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(invitingUser);
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        Mockito.when(userRepository.getByEmail(newUser.getEmail())).thenReturn(newUser);
        Mockito.when(tokenService.getUserInvitationTokenByName(invitationToken.getToken())).thenReturn(invitationToken);
        Mockito.when(tokenService.isExpired(invitationToken)).thenReturn(true);
        // Act
        userService.confirmUserRegistration(verificationToken.getToken(), Optional.of(invitationToken.getToken()));
        // Assert
        Mockito.verify(tokenService, Mockito.never()).update(invitationToken);
    }

    @Test
    public void confirmUserRegistration_should_updateInvitationTokenStatus_when_invitationTokenIsUnusedAndNotExpired() {
        initMockedFields();
        User newUser = UserFactory.createUser();
        User invitingUser = UserFactory.createOtherUser();
        invitingUser.setInvitedUsers(Constants.MAXIMUM_ALLOWED_REFERRAL_INVITATIONS);
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(newUser);
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(invitingUser);
        PaymentInstrumentFactory.createDefaultWalletFor(invitingUser, 1, "Wallet 1");
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        Mockito.when(userRepository.getByEmail(newUser.getEmail())).thenReturn(newUser);
        Mockito.when(tokenService.getUserInvitationTokenByName(invitationToken.getToken())).thenReturn(invitationToken);
        // Act
        userService.confirmUserRegistration(verificationToken.getToken(), Optional.of(invitationToken.getToken()));
        // Assert
        Mockito.verify(tokenService).update(ArgumentMatchers.argThat((UserInvitationToken::getUsed)));
    }

    @Test
    public void confirmUserRegistration_should_notUpdateWallets_when_invitingUserHasReachedTheMaximumNumberOfReferrals() {
        initMockedFields();
        User newUser = UserFactory.createUser();
        User invitingUser = UserFactory.createOtherUser();
        invitingUser.setInvitedUsers(Constants.MAXIMUM_ALLOWED_REFERRAL_INVITATIONS);
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(newUser);
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(invitingUser);
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        Mockito.when(userRepository.getByEmail(newUser.getEmail())).thenReturn(newUser);
        Mockito.when(tokenService.getUserInvitationTokenByName(invitationToken.getToken())).thenReturn(invitationToken);
        Mockito.when(tokenService.isExpired(invitationToken)).thenReturn(true);
        // Act
        userService.confirmUserRegistration(verificationToken.getToken(), Optional.of(invitationToken.getToken()));
        // Assert
        Mockito.verify(walletService, Mockito.never()).updateSaldo(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    public void confirmUserRegistration_should_giveBonusesToBothUsers_when_invitingUserHasNotYetReachedMaximumNumberOfReferrals() {
        initMockedFields();
        User newUser = UserFactory.createUser();
        User invitingUser = UserFactory.createOtherUser();
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(newUser);
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(invitingUser);
        Wallet wallet1 = PaymentInstrumentFactory.createWallet(
                PaymentInstrumentFactory.createPaymentInstrument(1, invitingUser, "Wallet 1", InstrumentType.WALLET));
        Wallet wallet2 = PaymentInstrumentFactory.createWallet(
                PaymentInstrumentFactory.createPaymentInstrument(2, newUser, "Wallet 2", InstrumentType.WALLET));
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        Mockito.when(userRepository.getByEmail(newUser.getEmail())).thenReturn(newUser);
        Mockito.when(tokenService.getUserInvitationTokenByName(invitationToken.getToken())).thenReturn(invitationToken);
        Mockito.when(walletService.create(
                ArgumentMatchers.eq(invitingUser.getUsername()),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean())
        ).thenReturn(wallet1);
        Mockito.when(walletService.create(
                ArgumentMatchers.eq(newUser.getUsername()),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean())
        ).thenReturn(wallet2);
        // Act
        userService.confirmUserRegistration(verificationToken.getToken(), Optional.of(invitationToken.getToken()));
        // Assert
        Assert.assertTrue(wallet1.getSaldo().equals(Constants.REFERRAL_BONUS_IN_EURO) &&
                wallet2.getSaldo().equals(Constants.REFERRAL_BONUS_IN_EURO));
    }

    @Test
    public void confirmUserRegistration_should_giveBonusToInvitingUsersDefaultWallet_when_invitingUserHasDefaultWallet() {
        initMockedFields();
        User newUser = UserFactory.createUser();
        User invitingUser = UserFactory.createOtherUser();
        UserVerificationToken verificationToken = VerificationTokenFactory.createUserVerificationToken(newUser);
        UserInvitationToken invitationToken = VerificationTokenFactory.createUserInvitationToken(invitingUser);
        PaymentInstrumentFactory.createDefaultWalletFor(invitingUser, 1, "Wallet 1");
        Wallet wallet1 = invitingUser.getDefaultWallet();
        wallet1.setSaldo(BigDecimal.TEN);
        Wallet wallet2 = PaymentInstrumentFactory.createWallet(
                PaymentInstrumentFactory.createPaymentInstrument(2, newUser, "Wallet 2", InstrumentType.WALLET));
        Mockito.when(tokenService.getUserVerificationTokenByName(verificationToken.getToken())).thenReturn(verificationToken);
        Mockito.when(userRepository.getByEmail(newUser.getEmail())).thenReturn(newUser);
        Mockito.when(tokenService.getUserInvitationTokenByName(invitationToken.getToken())).thenReturn(invitationToken);
        Mockito.when(walletService.create(
                ArgumentMatchers.eq(newUser.getUsername()),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean())
        ).thenReturn(wallet2);
        // Act
        userService.confirmUserRegistration(verificationToken.getToken(), Optional.of(invitationToken.getToken()));
        // Assert
        Assert.assertTrue(wallet1.getSaldo().equals(BigDecimal.TEN.add(Constants.REFERRAL_BONUS_IN_EURO)) &&
                wallet2.getSaldo().equals(Constants.REFERRAL_BONUS_IN_EURO));
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void updateDetails_should_throw_when_MultipartFileContentTypeIsNotImage() {
        // Arrange
        User user = UserFactory.createUser();
        MultipartFile file = new MockMultipartFile("name", "filename", "imag", new byte[1024]);
        String[] countryCodeAndLocalPhoneNumber = getCountryCodeAndLocalPhoneNumber(user.getPhoneNumber());
        EditUserDto userDto = UserFactory.createEditUserDto(user.getEmail(),
                countryCodeAndLocalPhoneNumber[0], countryCodeAndLocalPhoneNumber[1]);
        Mockito.when(userRepository.getByUsername(user.getUsername())).thenReturn(user);
        // Act
        userService.updateDetails(user.getUsername(), userDto, Optional.of(file));
    }

    private void initMockedFields() {
        userService = new UserServiceImpl(Mockito.mock(UserRepository.class));
        MockitoAnnotations.initMocks(this);
    }

    private static String[] getCountryCodeAndLocalPhoneNumber(String phoneNumber) {
        int localPhoneNumberStartIndex = phoneNumber.indexOf(')') + 1;
        return new String[]{
                phoneNumber.substring(2, localPhoneNumberStartIndex - 1),
                phoneNumber.substring(localPhoneNumberStartIndex)
        };
    }
}
