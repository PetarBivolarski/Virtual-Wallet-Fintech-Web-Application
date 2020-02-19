package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletRepository {
    List<Wallet> getAllByUser(User user);

    BigDecimal getTotalUserSaldo(int userId);

    Wallet getById(int id);

    Wallet create(Wallet wallet);

    Wallet update(Wallet wallet);
}
