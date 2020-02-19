package a16team1.virtualwallet.services;

import a16team1.virtualwallet.PaymentInstrumentFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.repositories.contracts.PaymentInstrumentRepository;
import a16team1.virtualwallet.utilities.InstrumentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class PaymentInstrumentServiceImplTests {

    @Mock
    PaymentInstrumentRepository repository;

    @InjectMocks
    PaymentInstrumentServiceImpl paymentInstrumentService;

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getById_should_throw_when_paymentInstrumentDoesNotExist() {
        // Arrange
        Mockito.when(repository.getById(anyInt())).thenReturn(null);
        // Act
        paymentInstrumentService.getById(1);
    }

    @Test
    public void getById_should_returnPaymentInstrument_when_paymentInstrumentExists() {
        // Arrange
        User user = UserFactory.createUser();
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(
                1, user, "Payment Instrument 1", InstrumentType.WALLET);
        Mockito.when(repository.getById(anyInt())).thenReturn(paymentInstrument);
        // Act
        PaymentInstrument returnedPaymentInstrument = paymentInstrumentService.getById(1);
        // Assert
        Assert.assertSame(paymentInstrument, returnedPaymentInstrument);
    }
}
