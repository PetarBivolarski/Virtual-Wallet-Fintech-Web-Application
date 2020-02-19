package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.EditUserDto;
import a16team1.virtualwallet.models.dtos.NewUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Page<User> getAll(Pageable pageable);

    Page<User> getAll(Pageable pageable, String filterType, String filterValue);

    User getById(int id);

    User getByEmail(String email);

    User getByPhoneNumber(String phoneNumber);

    User getByUsername(String username);

    User create(NewUserDto userDto, Optional<String> invitationToken);

    Page<User> findUsersByContactType(Pageable pageable, String contactType, String contactInformation);

    User updatePassword(String username, String password, String newPassword);

    User updateDefaultWallet(String ownerUsername, int walletId);

    User block(String adminUsername, int userId);

    User unblock(String adminUsername, int userId);

    User confirmUserRegistration(String verificationTokenName, Optional<String> invitationTokenName);

    void sendReferralLinkForRegistration(String loggedUserUsername, String recipientEmail);

    User update(User user);

    User updateDetails(String username, EditUserDto editUserDto, Optional<MultipartFile> file);

    User delete(int userId);

    User authenticateRestCredentials(String encodedCredentials);

    void authorizeUserByIdOrRole(User user, int userId);

    void authorizeByAdminRole(User user);
}
