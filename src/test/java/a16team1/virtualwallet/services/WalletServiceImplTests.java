package a16team1.virtualwallet.services;

import a16team1.virtualwallet.PaymentInstrumentFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.NewWalletDto;
import a16team1.virtualwallet.repositories.contracts.WalletRepository;
import a16team1.virtualwallet.utilities.InstrumentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceImplTests {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PaymentInstrumentService paymentInstrumentService;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletServiceImpl walletService;


    @Test(expected = EntityNotFoundException.class) // Assert
    public void getById_should_throw_when_walletDoesNotExist() {
        // Arrange
        Mockito.when(walletRepository.getById(anyInt())).thenReturn(null);
        // Act
        walletService.getById(1);
    }

    @Test
    public void getById_should_returnWallet_when_walletExists() {
        // Arrange
        Wallet wallet = PaymentInstrumentFactory.createWallet(1);
        Mockito.when(walletRepository.getById(anyInt())).thenReturn(wallet);
        // Act
        Wallet returnedWallet = walletService.getById(1);
        // Assert
        Assert.assertSame(wallet, returnedWallet);
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void create_should_throw_when_walletWithSameNameAlreadyExistsForUser() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        NewWalletDto walletDto = PaymentInstrumentFactory.createNewWalletDto(user, "Wallet 1");
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Wallet 1", InstrumentType.WALLET);
        Mockito.when(paymentInstrumentService.getWalletInstrumentByUserAndName(user, walletDto.getName())).thenReturn(paymentInstrument);
        // Act
        walletService.create(user.getUsername(), walletDto, false);
    }

    @Test
    public void create_should_changeWalletIdToEqualPaymentInstrumentId_when_validDetailsArePassed() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Wallet wallet = PaymentInstrumentFactory.createWallet(0);
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Wallet 1", InstrumentType.WALLET);
        NewWalletDto walletDto = PaymentInstrumentFactory.createNewWalletDto(user, "Wallet 1");
        Mockito.when(paymentInstrumentService.getWalletInstrumentByUserAndName(user, walletDto.getName())).thenReturn(null);
        Mockito.when(paymentInstrumentService.create(eq(user), any(PaymentInstrument.class))).thenReturn(paymentInstrument);
        Mockito.when(walletRepository.create(any())).thenReturn(wallet);
        // Act
        walletService.create(user.getUsername(), walletDto, true);
        // Assert
        Mockito.verify(walletRepository).create(ArgumentMatchers.argThat((Wallet w) -> w.getId() == paymentInstrument.getId()));
    }

    @Test
    public void create_should_setDefaultWalletForOwner_when_ownerDoesNotHaveDefaultWallet() {
        // Arrange
        User user = UserFactory.createUser();
        user.setDefaultWallet(null);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Wallet wallet = PaymentInstrumentFactory.createWallet(0);
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Wallet 1", InstrumentType.WALLET);
        NewWalletDto walletDto = PaymentInstrumentFactory.createNewWalletDto(user, "Wallet 1");
        Mockito.when(paymentInstrumentService.getWalletInstrumentByUserAndName(user, walletDto.getName())).thenReturn(null);
        Mockito.when(paymentInstrumentService.create(eq(user), any(PaymentInstrument.class))).thenReturn(paymentInstrument);
        Mockito.when(walletRepository.create(any())).thenReturn(wallet);
        // Act
        walletService.create(user.getUsername(), walletDto, false);
        // Assert
        Mockito.verify(userService).update(ArgumentMatchers.argThat((User u) -> u.getDefaultWallet() == wallet));
    }

    @Test
    public void create_should_updateDefaultWalletForOwner_when_newWalletIsSelectedAsDefault() {
        // Arrange
        User user = UserFactory.createUser();
        Wallet wallet = PaymentInstrumentFactory.createWallet(0);
        user.setDefaultWallet(wallet);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Wallet 1", InstrumentType.WALLET);
        NewWalletDto walletDto = PaymentInstrumentFactory.createNewWalletDto(user, "Wallet 1");
        Wallet anotherWallet = PaymentInstrumentFactory.createWallet(1);
        Mockito.when(paymentInstrumentService.getWalletInstrumentByUserAndName(user, walletDto.getName())).thenReturn(null);
        Mockito.when(paymentInstrumentService.create(eq(user), any(PaymentInstrument.class))).thenReturn(paymentInstrument);
        Mockito.when(walletRepository.create(any())).thenReturn(anotherWallet);
        // Act
        walletService.create(user.getUsername(), walletDto, true);
        // Assert
        Mockito.verify(userService).update(ArgumentMatchers.argThat((User u) -> u.getDefaultWallet().getId() == anotherWallet.getId()));
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void updateName_should_throw_when_walletWithSameNameAlreadyExistsForUser() {
        // Arrange
        User user = UserFactory.createUser();
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Wallet 1", InstrumentType.WALLET);
        Wallet wallet = PaymentInstrumentFactory.createWallet(paymentInstrument);
        Mockito.when(paymentInstrumentService.getWalletInstrumentByUserAndName(eq(user), any())).thenReturn(paymentInstrument);
        // Act
        walletService.updateName(paymentInstrument, wallet);
    }

    @Test
    public void updateName_should_returnWallet_when_validDetailsArePassed() {
        // Arrange
        User user = UserFactory.createUser();
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(1, user, "Wallet 1", InstrumentType.WALLET);
        Wallet wallet = PaymentInstrumentFactory.createWallet(paymentInstrument);
        Mockito.when(paymentInstrumentService.getWalletInstrumentByUserAndName(eq(user), any())).thenReturn(null);
        Mockito.when(walletRepository.update(wallet)).thenReturn(wallet);
        // Act
        Wallet updatedWallet = walletService.updateName(paymentInstrument, wallet);
        // Assert
        Assert.assertSame(wallet, updatedWallet);
    }

}
