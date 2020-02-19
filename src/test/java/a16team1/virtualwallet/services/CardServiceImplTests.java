package a16team1.virtualwallet.services;

import a16team1.virtualwallet.PaymentInstrumentFactory;
import a16team1.virtualwallet.UserFactory;
import a16team1.virtualwallet.exceptions.AccessDeniedException;
import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.exceptions.InvalidOperationException;
import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.ExistingCardDto;
import a16team1.virtualwallet.models.dtos.NewCardDto;
import a16team1.virtualwallet.repositories.contracts.CardRepository;
import a16team1.virtualwallet.utilities.InstrumentType;
import a16team1.virtualwallet.utilities.mappers.PaymentInstrumentDtoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;


@RunWith(MockitoJUnitRunner.class)
public class CardServiceImplTests {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private PaymentInstrumentService paymentInstrumentService;

    @Mock
    private PaymentInstrumentDtoMapper paymentInstrumentDtoMapper;


    @InjectMocks
    CardServiceImpl cardService;

    @Test(expected = EntityNotFoundException.class) // Assert
    public void getById_should_throw_when_cardDoesNotExist() {
        // Arrange
        Mockito.when(cardRepository.getById(anyInt())).thenReturn(null);
        // Act
        cardService.getById(1);
    }

    @Test
    public void getById_should_returnCard_when_cardExists() {
        // Arrange
        Card card = PaymentInstrumentFactory.createCard();
        Mockito.when(cardRepository.getById(anyInt())).thenReturn(card);
        // Act
        Card returnedCard = cardService.getById(1);
        // Assert
        Assert.assertSame(card, returnedCard);
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void create_should_throw_when_cardWithSameNumberAlreadyExists() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        NewCardDto cardDto = PaymentInstrumentFactory.createNewCardDto();
        Card card = PaymentInstrumentFactory.createCardFromNewCardDto(cardDto);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(paymentInstrumentDtoMapper.fromDto(cardDto)).thenReturn(card);
        Mockito.when(cardRepository.getByCardNumber(card.getCardNumber())).thenReturn(card);
        // Act
        cardService.create(user.getUsername(), cardDto);
    }

    @Test
    public void create_should_changeCardIdToEqualPaymentInstrumentId_when_validDetailsArePassed() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        NewCardDto cardDto = PaymentInstrumentFactory.createNewCardDto();
        Card card = PaymentInstrumentFactory.createCardFromNewCardDto(cardDto);
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(
                1, user, "Test Instrument", InstrumentType.CARD);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(paymentInstrumentDtoMapper.fromDto(cardDto)).thenReturn(card);
        Mockito.when(paymentInstrumentService.create(any(), any())).thenReturn(paymentInstrument);
        Mockito.when(cardRepository.create(card)).thenReturn(card);
        // Act
        Card returnedCard = cardService.create(user.getUsername(), cardDto);
        // Assert
        Assert.assertEquals(paymentInstrument.getId(), returnedCard.getId());
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void update_should_throw_when_userIsBlocked() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createBlockedUser();
        ExistingCardDto cardDto = PaymentInstrumentFactory.createExistingCardDto();
        Card card = PaymentInstrumentFactory.createCard();
        Mockito.when(cardRepository.getById(anyInt())).thenReturn(card);
        Mockito.when(paymentInstrumentDtoMapper.updateCardFromDto(card, cardDto)).thenReturn(card);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        // Act
        cardService.update(cardDto, 1, user.getUsername());
    }

    @Test(expected = DuplicateEntityException.class) // Assert
    public void update_should_throw_when_cardWithSameNumberAlreadyExists() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        ExistingCardDto cardDto = PaymentInstrumentFactory.createExistingCardDto();
        Card card = PaymentInstrumentFactory.createCard();
        Card anotherCard = PaymentInstrumentFactory.createAnotherCard();
        Mockito.when(cardRepository.getById(anyInt())).thenReturn(card);
        Mockito.when(paymentInstrumentDtoMapper.updateCardFromDto(card, cardDto)).thenReturn(card);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(cardRepository.getByCardNumber(card.getCardNumber())).thenReturn(anotherCard);
        // Act
        cardService.update(cardDto, 1, user.getUsername());
    }

    @Test
    public void update_should_createCardWithDifferentId_when_cardNumberIsChanged() {
        // Arrange
        initMockedFields();
        User user = UserFactory.createUser();
        ExistingCardDto cardDto = PaymentInstrumentFactory.createExistingCardDto();
        Card updatedCard = PaymentInstrumentFactory.createCard();
        Card alreadyExistingCard = PaymentInstrumentFactory.createAnotherCard();
        Mockito.when(cardRepository.getById(updatedCard.getId())).thenReturn(alreadyExistingCard);
        Mockito.when(paymentInstrumentDtoMapper.updateCardFromDto(alreadyExistingCard, cardDto)).thenReturn(updatedCard);
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(cardRepository.getByCardNumber(updatedCard.getCardNumber())).thenReturn(updatedCard);
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(
                2, user, "Test Card 1", InstrumentType.CARD);
        Mockito.when(paymentInstrumentService.create(any(), any())).thenReturn(paymentInstrument);
        // Act
        Card newCard = cardService.update(cardDto, updatedCard.getId(), user.getUsername());
        // Assert
        Assert.assertNotSame(newCard.getId(), alreadyExistingCard.getId());
    }

    @Test(expected = InvalidOperationException.class) // Assert
    public void deleteCard_should_throw_when_userIsBlocked() {
        // Arrange
        Card card = PaymentInstrumentFactory.createCard();
        User user = UserFactory.createBlockedUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);
        // Act
        cardService.delete(card.getId(), user.getUsername());
    }

    @Test(expected = AccessDeniedException.class) // Assert
    public void deleteCard_should_throw_when_userDoesNotOwnCard() {
        // Arrange
        Card card = PaymentInstrumentFactory.createCard();
        User user = UserFactory.createUser();
        User otherUser = UserFactory.createOtherUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(
                1, otherUser, "Test Card", InstrumentType.CARD);
        Mockito.when(paymentInstrumentService.getById(card.getId())).thenReturn(paymentInstrument);
        // Act
        cardService.delete(card.getId(), user.getUsername());
    }

    @Test(expected = AccessDeniedException.class) // Assert
    public void throwIfUserIsNotAllowedToViewCardPage_should_throw_when_userIsNotOwnerOfCard() {
        // Arrange
        User user = UserFactory.createUser();
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);
        User otherUser = UserFactory.createOtherUser();
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(
                1, otherUser, "Test Card 2", InstrumentType.CARD);
        Mockito.when(paymentInstrumentService.getById(1)).thenReturn(paymentInstrument);
        // Act
        cardService.throwIfUserIsNotAllowedToViewCardPage(1, user.getUsername(), "Error message");
    }


    private void initMockedFields() {
        cardService = new CardServiceImpl(
                Mockito.mock(CardRepository.class),
                Mockito.mock(UserService.class),
                Mockito.mock(PaymentInstrumentService.class));
        MockitoAnnotations.initMocks(this);
    }
}
