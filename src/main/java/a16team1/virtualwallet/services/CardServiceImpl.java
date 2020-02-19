package a16team1.virtualwallet.services;

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
import a16team1.virtualwallet.utilities.BlockedUserForbiddenActions;
import a16team1.virtualwallet.utilities.ModelFactory;
import a16team1.virtualwallet.utilities.mappers.PaymentInstrumentDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@PropertySource("classpath:messages.properties")
public class CardServiceImpl implements CardService {

    private CardRepository cardRepository;
    private UserService userService;
    private PaymentInstrumentService paymentInstrumentService;
    private PaymentInstrumentDtoMapper paymentInstrumentDtoMapper;

    @Value("${error.cardNotFound}")
    private String cardNotFound;

    @Value("${error.duplicateCardName}")
    private String duplicateCardName;

    @Value("${error.duplicateCardNumber}")
    private String duplicateCardNumber;

    @Value("${error.editingCardWithBlockedAccount}")
    private String editingCardWithBlockedAccount;

    @Value("${error.deletingCardWithBlockedAccount}")
    private String deletingCardWithBlockedAccount;

    @Value("${access.denied}")
    private String accessDenied;

    @Value("${invalid.operation}")
    private String invalidOperation;


    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserService userService,
                           PaymentInstrumentService paymentInstrumentService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.paymentInstrumentService = paymentInstrumentService;
    }

    @Override
    public Card getById(int id) {
        Card card = cardRepository.getById(id);
        if (card == null) {
            throw new EntityNotFoundException(cardNotFound);
        }
        return card;
    }

    @Override
    public List<Card> getAllByUser(int userId) {
        User user = userService.getById(userId);
        return cardRepository.getAllByUser(user);
    }

    @Override
    public Card create(String cardOwnerUsername, NewCardDto cardDto) {
        User user = userService.getByUsername(cardOwnerUsername);
        PaymentInstrument paymentInstrument = ModelFactory.getPaymentInstrumentForCard(user);
        Card card = paymentInstrumentDtoMapper.fromDto(cardDto);
        throwIfCardWithSameNumberAlreadyExistsInSystem(card);
        setNameFromCardNumber(paymentInstrument, card);
        paymentInstrument = paymentInstrumentService.create(user, paymentInstrument);
        card.setId(paymentInstrument.getId());
        return cardRepository.create(card);
    }

    @Override
    public Card update(ExistingCardDto cardDto, int cardId, int userId) {
        User user = userService.getById(userId);
        return update(cardDto, cardId, user.getUsername());
    }

    @Override
    public Card update(ExistingCardDto cardDto, int cardId, String ownerUsername) {
        Card alreadyExistingCard = getById(cardId);
        Card updatedCardFromDto = paymentInstrumentDtoMapper.updateCardFromDto(alreadyExistingCard, cardDto);
        User user = userService.getByUsername(ownerUsername);
        throwIfUserIsBlocked(user, BlockedUserForbiddenActions.EDIT_CARD);
        throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(updatedCardFromDto);
        Card persistentCard = cardRepository.getById(updatedCardFromDto.getId());
        if (!persistentCard.getCardNumber().equals(updatedCardFromDto.getCardNumber())) {
            Card newCard = ModelFactory.getNewCardFromExistingCard(updatedCardFromDto);
            PaymentInstrument paymentInstrument = ModelFactory.getPaymentInstrumentForCard(user);
            setNameFromCardNumber(paymentInstrument, newCard);
            PaymentInstrument persistedPaymentInstrument = paymentInstrumentService.create(user, paymentInstrument);
            newCard.setId(persistedPaymentInstrument.getId());
            cardRepository.create(newCard);
            Card deleteOldCard = getById(updatedCardFromDto.getId());
            cardRepository.delete(deleteOldCard);
            return newCard;
        } else {
            return cardRepository.update(updatedCardFromDto);
        }
    }

    @Override
    public Card delete(int id, String ownerUsername) {
        User user = userService.getByUsername(ownerUsername);
        Card card = getById(id);
        throwIfUserIsBlocked(user, BlockedUserForbiddenActions.DELETE_CARD);
        throwIfUserDoesNotOwnCard(card, user);
        return cardRepository.delete(card);
    }

    @Override
    public void throwIfUserIsNotAllowedToViewCardPage(int cardId, String loggedUserUsername, String errorMessage) {
        User user = userService.getByUsername(loggedUserUsername);
        try {
            PaymentInstrument paymentInstrument = paymentInstrumentService.getById(cardId);
            if (paymentInstrument.getOwner().getId() != user.getId()) {
                throw new AccessDeniedException(accessDenied);
            }
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(errorMessage);
        }

    }


    @Autowired
    public void setPaymentInstrumentDtoMapper(PaymentInstrumentDtoMapper paymentInstrumentDtoMapper) {
        this.paymentInstrumentDtoMapper = paymentInstrumentDtoMapper;
    }

    private void throwIfUserDoesNotOwnCard(Card card, User user) {
        PaymentInstrument paymentInstrument = paymentInstrumentService.getById(card.getId());
        if (paymentInstrument.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException(invalidOperation);
        }
    }


    private void setNameFromCardNumber(PaymentInstrument paymentInstrument, Card card) {
        String cardNumber = card.getCardNumber();
        paymentInstrument.setName(cardNumber);
    }


    private void throwIfCardWithSameNumberAlreadyExistsInSystem(Card card) {
        if (cardRepository.getByCardNumber(card.getCardNumber()) != null) {
            throw new DuplicateEntityException(duplicateCardNumber);
        }
    }

    private void throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(Card card) {
        Card existingCard = cardRepository.getByCardNumber(card.getCardNumber());
        if (existingCard != null && existingCard.getId() != card.getId()) {
            throw new DuplicateEntityException(duplicateCardNumber);
        }
    }

    private void throwIfUserIsBlocked(User user, BlockedUserForbiddenActions action) {
        if (user.isBlocked()) {
            switch (action) {
                case EDIT_CARD:
                    throw new InvalidOperationException(editingCardWithBlockedAccount);
                case DELETE_CARD:
                    throw new InvalidOperationException(deletingCardWithBlockedAccount);
            }
        }
    }

}
