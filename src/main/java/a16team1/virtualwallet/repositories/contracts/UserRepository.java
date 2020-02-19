package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepository {
    Page<User> getAll(Pageable pageable);

    Page<User> getAll(Pageable pageable, String filterType, String filterValue);

    User getById(int id);

    User getByUsername(String username);

    User getByEmail(String email);

    User getByPhoneNumber(String phoneNumber);

    User create(User user);

    User update(User user);

    User block(User user);

    User unblock(User user);

    User delete(User user);

    List<String> getUserRoles(User user);
}
