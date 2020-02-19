package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.NewWalletDto;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    Wallet getById(int id);

    List<Wallet> getAll(int userId);

    List<Wallet> getAll(String ownerUsername);

    BigDecimal getTotalUserSaldo(int userId);

    Wallet create(String loggedUserUsername, NewWalletDto walletDto, Boolean defaultWallet);

    Wallet updateName(PaymentInstrument paymentInstrument, Wallet wallet);

    Wallet updateSaldo(Wallet wallet);
}


