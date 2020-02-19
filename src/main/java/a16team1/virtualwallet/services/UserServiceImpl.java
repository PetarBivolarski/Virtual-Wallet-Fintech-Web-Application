package a16team1.virtualwallet.services;

import a16team1.virtualwallet.exceptions.AccessDeniedException;
import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.exceptions.InvalidOperationException;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.UserInvitationToken;
import a16team1.virtualwallet.models.UserVerificationToken;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.dtos.NewPaymentInstrumentDto;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import a16team1.virtualwallet.models.dtos.NewWalletDto;
import a16team1.virtualwallet.repositories.contracts.UserRepository;
import a16team1.virtualwallet.services.email_tokens.EmailVerificationService;
import a16team1.virtualwallet.services.email_tokens.TokenService;
import a16team1.virtualwallet.utilities.Constants;
import a16team1.virtualwallet.utilities.InstrumentType;
import a16team1.virtualwallet.utilities.ModelFactory;
import a16team1.virtualwallet.utilities.UserAttribute;
import a16team1.virtualwallet.utilities.mappers.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static a16team1.virtualwallet.utilities.Constants.DEFAULT_EMPTY_VALUE;
import static a16team1.virtualwallet.utilities.Constants.VALUE_CANNOT_BE_EMPTY;

@Service
@PropertySource("classpath:messages.properties")
public class UserServiceImpl implements UserService {

    private static final String VALID_EMAIL_FORMAT = "[^@]+@[^\\.]+\\..+";
    private static final String VALID_PHONE_NUMBER_FORMAT = "\\(\\+[0-9]{1,5}\\)[0-9]{3,15}";
    private static final String DEFAULT_NAME_FOR_REFERRAL_BONUS_DEFAULT_WALLET = "Referral Bonus Wallet";


    @Value("${error.userNotFound}")
    private String userNotFound;

    @Value("${error.duplicateUsername}")
    private String duplicateUsername;

    @Value("${error.duplicateEmail}")
    private String duplicateEmail;

    @Value("${error.duplicatePhoneNumber}")
    private String duplicatePhoneNumber;

    @Value("${admin.users.illegalPhoneValue}")
    private String invalidPhoneValue;

    @Value("${admin.users.illegalEmailValue}")
    private String invalidEmailValue;

    @Value("${user.isNotAdmin}")
    private String userIsNotAdmin;

    @Value("${error.invalidRecipientContactType}")
    private String invalidRecipientContactType;

    @Value("${email.subject}")
    private String emailSubject;

    @Value("${email.sender}")
    private String emailSender;

    @Value("${email.message}")
    private String emailMessage;

    @Value("${error.edit.wrong.password}")
    private String wrongPassword;

    @Value("${error.linkInvalidOrBroken}")
    private String invalidLink;

    @Value("${registration.already.confirmed.title}")
    private String accountAlreadyConfirmed;

    @Value("${user.doesNotOwnWallet}")
    private String userDoesNotOwnWallet;

    @Value("${referral.invitation.emailSubject}")
    private String referralInvitationEmailSubject;

    @Value("${referral.invitation.emailMessage}")
    private String referralInvitationEmailMessage;

    @Value("${referral.alreadyExistingUserText}")
    private String referredUserAlreadyExists;

    @Value("${referral.invalidEmailFormat}")
    private String invalidEmailFormatOfReferredUser;

    @Value("${error.invalidImageFormat}")
    private String invalidImageFormat;

    @Value("${error.duringImageUpload}")
    private String errorDuringImageUpload;

    @Value("${access.denied}")
    private String accessDenied;

    @Value("${error.invalidCredentials}")
    private String invalidCredentials;

    private UserRepository userRepository;
    private WalletService walletService;
    private UserDtoMapper dtoMapper;
    private EmailVerificationService emailVerificationService;
    private TokenService tokenService;
    private PasswordEncoder passwordEncoder;
    private Validator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.getAll(pageable);
    }

    @Override
    public Page<User> getAll(Pageable pageable, String filterType, String filterValue) {
        UserAttribute validFilterType = UserAttribute.valueOf(filterType.toUpperCase());
        String validFieldTypeAsString = validFilterType.toFieldName();
        throwIfInvalidFilterValue(validFieldTypeAsString, filterValue);
        return userRepository.getAll(pageable, validFieldTypeAsString, getSearchPattern(filterValue));
    }


    @Override
    public User getById(int id) {
        User user = userRepository.getById(id);
        throwIfUserDoesNotYetExist(user);
        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepository.getByEmail(email);
        throwIfUserDoesNotYetExist(user);
        return user;
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        User user = userRepository.getByPhoneNumber(phoneNumber);
        throwIfUserDoesNotYetExist(user);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userRepository.getByUsername(username);
        throwIfUserDoesNotYetExist(user);
        return user;
    }

    @Override
    public User create(NewUserDto userDto, Optional<String> invitationToken) {
        throwIfUserAlreadyExists(userRepository.getByUsername(userDto.getUsername()), duplicateUsername);
        throwIfUserAlreadyExists(userRepository.getByEmail(userDto.getEmail()), duplicateEmail);
        String phoneNumber = formatPhoneNumber(userDto.getCountryCode(), userDto.getLocalPhoneNumber());
        throwIfUserAlreadyExists(userRepository.getByPhoneNumber(phoneNumber), duplicatePhoneNumber);
        User user = dtoMapper.fromDtoWithoutPhoneNumber(userDto);
        user.setPhoneNumber(formatPhoneNumber(userDto.getCountryCode(), userDto.getLocalPhoneNumber()));
        User persistedUser = userRepository.create(user);
        emailVerificationService.sendEmailVerification(persistedUser, emailSubject, emailMessage, invitationToken);
        return persistedUser;
    }

    @Override
    public void sendReferralLinkForRegistration(String loggedUserUsername, String recipientEmail) {
        User referrer = getByUsername(loggedUserUsername);
        throwIfEmailIsNotValid(recipientEmail, invalidEmailFormatOfReferredUser);
        throwIfUserAlreadyExists(userRepository.getByEmail(recipientEmail), referredUserAlreadyExists);
        emailVerificationService.sendEmailInvitation(referrer, recipientEmail,
                referralInvitationEmailSubject, referralInvitationEmailMessage);
    }


    @Override
    public User block(String adminUsername, int userId) {
        User admin = getByUsername(adminUsername);
        throwIfUserIsNotAdmin(admin);
        User userToBlock = getById(userId);
        userToBlock.setBlocked(true);
        return userRepository.block(userToBlock);
    }

    @Override
    public User unblock(String adminUsername, int userId) {
        User admin = getByUsername(adminUsername);
        throwIfUserIsNotAdmin(admin);
        User userToUnblock = getById(userId);
        userToUnblock.setBlocked(false);
        return userRepository.unblock(userToUnblock);
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        throwIfUserDoesNotYetExist(userRepository.getById(userId));
        throwIfUserWithSameUniqueAttributeAlreadyExists(userRepository.getByEmail(user.getEmail()), userId, duplicateEmail);
        throwIfUserWithSameUniqueAttributeAlreadyExists(userRepository.getByPhoneNumber(user.getPhoneNumber()), userId, duplicatePhoneNumber);
        return userRepository.update(user);
    }

    public User updateDetails(String username, EditUserDto editUserDto, Optional<MultipartFile> file) {
        User userToBeUpdated = getByUsername(username);
        if (editUserDto.getEmail() != null) {
            userToBeUpdated.setEmail(editUserDto.getEmail());
        }
        String countryCode = editUserDto.getCountryCode();
        String localPhoneNumber = editUserDto.getLocalPhoneNumber();
        if (countryCode != null && localPhoneNumber != null) {
            userToBeUpdated.setPhoneNumber(formatPhoneNumber(countryCode, localPhoneNumber));
        }
        if (editUserDto.getFirstName() != null) {
            userToBeUpdated.setFirstName(editUserDto.getFirstName());
        }
        if (editUserDto.getLastName() != null) {
            userToBeUpdated.setLastName(editUserDto.getLastName());
        }
        file.ifPresent(multipartFile -> updateUserPictureIfProvided(userToBeUpdated, multipartFile));
        return update(userToBeUpdated);
    }

    @Override
    public User delete(int userId) {
        User user = getById(userId);
        user.setEnabled(false);
        return userRepository.delete(user);
    }

    @Override
    public User updateDefaultWallet(String ownerUsername, int walletId) {
        User user = getByUsername(ownerUsername);
        List<Wallet> wallets = walletService.getAll(user.getId());
        throwIfUserDoesNotOwnWallet(walletId, wallets);
        Wallet wallet = walletService.getById(walletId);
        user.setDefaultWallet(wallet);
        User updatedUser = update(user);
        return updatedUser;
    }

    @Override
    public User updatePassword(String username, String currentPassword, String newPassword) {
        User user = getByUsername(username);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidOperationException(wrongPassword);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.update(user);
    }

    @Override
    public Page<User> findUsersByContactType(Pageable pageable, String contactType, String contactInformation) {
        switch (contactType) {
            case "username":
                throwIfNullInput(contactInformation);
                return userRepository.getAll(pageable, contactType, getSearchPattern(contactInformation));
            case "email":
                throwIfInputViolatesConstraints(contactType, contactInformation);
                return getPageOfSingleResult(userRepository.getByEmail(contactInformation), pageable);
            case "phone":
                throwIfInputViolatesConstraints("localPhoneNumber", contactInformation);
                return getPageOfSingleResult(userRepository.getByPhoneNumber(contactInformation), pageable);
            default:
                throw new InvalidOperationException(invalidRecipientContactType);
        }
    }

    @Override
    public User confirmUserRegistration(String tokenName, Optional<String> invitationTokenName) {
        UserVerificationToken verificationToken = tokenService.getUserVerificationTokenByName(tokenName);
        if (verificationToken != null) {
            if (verificationToken.getUser().getConfirmedRegistration()) {
                throw new InvalidOperationException(accountAlreadyConfirmed);
            } else {
                User user = getByEmail(verificationToken.getUser().getEmail());
                user.setConfirmedRegistration(true);
                userRepository.update(user);
                if (invitationTokenName.isPresent()) {
                    UserInvitationToken userInvitationToken = tokenService.getUserInvitationTokenByName(invitationTokenName.get());
                    if (!userInvitationToken.getUsed() && !tokenService.isExpired(userInvitationToken)) {
                        userInvitationToken.setUsed(true);
                        tokenService.update(userInvitationToken);
                        User owner = userInvitationToken.getOwner();
                        // Give sender of referral link a bonus
                        if (owner.getInvitedUsers() < Constants.MAXIMUM_ALLOWED_REFERRAL_INVITATIONS) {
                            if (owner.getDefaultWallet() == null) {
                                giveBonusToSpeciallyCreatedWallet(owner);
                            } else {
                                Wallet wallet = owner.getDefaultWallet();
                                wallet.setSaldo(wallet.getSaldo().add(Constants.REFERRAL_BONUS_IN_EURO));
                                walletService.updateSaldo(wallet);
                            }
                            owner.setInvitedUsers(owner.getInvitedUsers() + 1);
                            userRepository.update(owner);
                            giveBonusToSpeciallyCreatedWallet(user);
                        }
                    }
                }
                return user;
            }
        } else {
            throw new IllegalArgumentException(invalidLink);
        }
    }

    @Override
    public User authenticateRestCredentials(String encodedCredentials) {
        // Remove initial string "Basic Auth " from encodedCredentials:
        int lastSpace = encodedCredentials.lastIndexOf(" ");
        if (lastSpace >= 0) {
            encodedCredentials = encodedCredentials.substring(lastSpace + 1);
        }
        String[] credentials = (new String(Base64.getDecoder().decode(encodedCredentials))).split(":");
        String username = credentials[0];
        if (credentials.length < 2) {
            throw new AccessDeniedException(invalidCredentials);
        }
        String password = credentials[1];
        User user = getByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AccessDeniedException(invalidCredentials);
        }
        return user;
    }

    @Override
    public void authorizeUserByIdOrRole(User user, int userId) {
        if (user.getId() != userId && !userRepository.getUserRoles(user).contains("ROLE_ADMIN")) {
            throw new AccessDeniedException(accessDenied);
        }
    }

    @Override
    public void authorizeByAdminRole(User user) {
        if (!userRepository.getUserRoles(user).contains("ROLE_ADMIN")) {
            throw new AccessDeniedException(accessDenied);
        }
    }

    @Autowired
    public void setDtoMapper(UserDtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Autowired
    public void setEmailVerificationService(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Autowired
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    private void throwIfInputViolatesConstraints(String contactType, String contactInformation) {
        Set<ConstraintViolation<NewUserDto>> usernameConstraintViolations = validator.validateValue(NewUserDto.class, contactType, contactInformation);
        if (!usernameConstraintViolations.isEmpty()) {
            throw new ConstraintViolationException(usernameConstraintViolations);
        }
    }

    private String formatPhoneNumber(String countryCode, String localPhoneNumber) {
        // Remove a single leading zero from the local phone number if the user has added it in error.
        if (localPhoneNumber.startsWith("0")) {
            localPhoneNumber = localPhoneNumber.substring(1);
        }
        return String.format("(+%s)%s", countryCode, localPhoneNumber);
    }

    private void updateUserPictureIfProvided(User userToBeUpdated, MultipartFile file) {
        try {
            if (file != null && file.getSize() > 0) {
                if (file.getContentType() == null || !file.getContentType().contains("image")) {
                    throw new InvalidOperationException(invalidImageFormat);
                } else {
                    userToBeUpdated.setPhoto(Base64.getEncoder().encodeToString(file.getBytes()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(errorDuringImageUpload, e);
        }
    }

    private void throwIfUserDoesNotOwnWallet(int walletId, List<Wallet> wallets) {
        if (wallets.stream().map(Wallet::getId).noneMatch(w -> w.equals(walletId))) {
            throw new InvalidOperationException(userDoesNotOwnWallet);
        }
    }

    private void throwIfEmailIsNotValid(String email, String errorMessage) {
        Pattern pattern = Pattern.compile(VALID_EMAIL_FORMAT);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void throwIfNullInput(String input) {
        if (input == null) {
            throw new IllegalArgumentException(VALUE_CANNOT_BE_EMPTY);
        }
    }

    private void throwIfUserDoesNotYetExist(User user) {
        if (user == null) {
            throw new EntityNotFoundException(userNotFound);
        }
    }

    private void throwIfUserAlreadyExists(User user, String errorMessage) {
        if (user != null) {
            throw new DuplicateEntityException(errorMessage);
        }
    }

    private void throwIfUserWithSameUniqueAttributeAlreadyExists(User user, int id, String errorMessage) {
        if (user != null && user.getId() != id) {
            throw new DuplicateEntityException(errorMessage);
        }
    }

    private void throwIfUserIsNotAdmin(User user) {
        List<String> roles = userRepository.getUserRoles(user);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new InvalidOperationException(userIsNotAdmin);
        }
    }

    private void throwIfInvalidFilterValue(String validFieldTypeAsString, String filterValue) {
        throwIfNullInput(filterValue);
        if (validFieldTypeAsString.equals("email")) {
            if (!filterValue.contains("@") && !filterValue.equals(DEFAULT_EMPTY_VALUE)) {
                throw new IllegalArgumentException(invalidEmailValue);
            }
        }
        if (validFieldTypeAsString.equals("phoneNumber")) {
            if (!filterValue.matches(VALID_PHONE_NUMBER_FORMAT) && !filterValue.equals(DEFAULT_EMPTY_VALUE)) {
                throw new IllegalArgumentException(invalidPhoneValue);
            }
        }
    }

    private void giveBonusToSpeciallyCreatedWallet(User user) {
        NewWalletDto walletDto = ModelFactory.getNewWalletDto(
                DEFAULT_NAME_FOR_REFERRAL_BONUS_DEFAULT_WALLET, user.getId());
        NewPaymentInstrumentDto paymentInstrument = ModelFactory.getNewPaymentInstrumentDto
                (DEFAULT_NAME_FOR_REFERRAL_BONUS_DEFAULT_WALLET, InstrumentType.WALLET, user.getId());
        walletDto.setPaymentInstrumentDto(paymentInstrument);
        Wallet wallet = walletService.create(user.getUsername(), walletDto, true);
        wallet.setSaldo(Constants.REFERRAL_BONUS_IN_EURO);
        walletService.updateSaldo(wallet);
    }

    private static Page<User> getPageOfSingleResult(User user, Pageable pageable) {
        List<User> userList = (user == null) ? new ArrayList<>() : Collections.singletonList(user);
        return new PageImpl<>(userList, pageable, userList.size());
    }

    private static String getSearchPattern(String filterValue) {
        filterValue = filterValue.replace("%", "");
        // "\\s*" matches all strings consisting of zero or more whitespace characters.
        if (!filterValue.matches("\\s*")) {
            filterValue += "%";
        }
        return filterValue;
    }
}
