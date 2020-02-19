package a16team1.virtualwallet.services;

import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.NewWalletDto;
import a16team1.virtualwallet.repositories.contracts.WalletRepository;
import a16team1.virtualwallet.utilities.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@PropertySource("classpath:messages.properties")
public class WalletServiceImpl implements WalletService {

    private WalletRepository walletRepository;
    private PaymentInstrumentService paymentInstrumentService;
    private UserService userService;

    @Value("${error.walletNotFound}")
    private String walletNotFound;

    @Value("${error.duplicateWallet}")
    private String duplicateWallet;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, PaymentInstrumentService paymentInstrumentService,
                             UserService userService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.paymentInstrumentService = paymentInstrumentService;

    }

    @Override
    public Wallet getById(int id) {
        Wallet wallet = walletRepository.getById(id);
        if (wallet == null) {
            throw new EntityNotFoundException(walletNotFound);
        }
        return wallet;
    }

    @Override
    public List<Wallet> getAll(int userId) {
        User user = userService.getById(userId);
        return walletRepository.getAllByUser(user);
    }

    @Override
    public List<Wallet> getAll(String ownerUsername) {
        User user = userService.getByUsername(ownerUsername);
        return walletRepository.getAllByUser(user);
    }

    public BigDecimal getTotalUserSaldo(int userId) {
        return walletRepository.getTotalUserSaldo(userId);
    }

    @Override
    public Wallet create(String loggedUserUsername, NewWalletDto walletDto, Boolean defaultWallet) {
        User user = userService.getByUsername(loggedUserUsername);
        throwIfWalletWithSameNameAlreadyExistsForUser(walletDto.getName(), user);
        PaymentInstrument paymentInstrument = ModelFactory.getPaymentInstrumentForWallet(user, walletDto);
        Wallet wallet = ModelFactory.getWalletWithZeroSaldo();
        paymentInstrument = paymentInstrumentService.create(user, paymentInstrument);
        wallet.setId(paymentInstrument.getId());
        wallet = walletRepository.create(wallet);
        setDefaultForOwnerIfNoneSelected(wallet, user);
        setDefaultForOwnerIfDefaultIsSelected(wallet, user, defaultWallet);
        return wallet;
    }

    @Override
    public Wallet updateName(PaymentInstrument paymentInstrument, Wallet wallet) {
        User owner = paymentInstrument.getOwner();
        throwIfWalletWithSameNameAlreadyExistsForUser(wallet.getName(), owner);
        paymentInstrumentService.update(paymentInstrument);
        return walletRepository.update(wallet);
    }

    @Override
    public Wallet updateSaldo(Wallet wallet) {
        return walletRepository.update(wallet);
    }


    private void throwIfWalletWithSameNameAlreadyExistsForUser(String walletName, User owner) {
        PaymentInstrument existingWallet = paymentInstrumentService.getWalletInstrumentByUserAndName(owner, walletName);
        if (existingWallet != null) {
            throw new DuplicateEntityException(duplicateWallet);
        }

    }

    private void setDefaultForOwnerIfNoneSelected(Wallet wallet, User owner) {
        if (owner.getDefaultWallet() == null) {
            owner.setDefaultWallet(wallet);
            userService.update(owner);
        }
    }


    private void setDefaultForOwnerIfDefaultIsSelected(Wallet wallet, User owner, Boolean defaultWallet) {
        if (defaultWallet != null && defaultWallet) {
            owner.setDefaultWallet(wallet);
            userService.update(owner);
        }
    }


}
