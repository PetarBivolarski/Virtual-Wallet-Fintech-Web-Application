package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.dtos.ExistingCardDto;
import a16team1.virtualwallet.models.dtos.NewCardDto;

import java.util.List;

public interface CardService {

    Card getById(int id);

    List<Card> getAllByUser(int userId);

    Card create(String cardOwnerUsername, NewCardDto cardDto);

    Card update(ExistingCardDto cardDto, int cardId, String ownerUsername);

    Card update(ExistingCardDto card, int cardId, int userId);

    Card delete(int id, String ownerUsername);

   void throwIfUserIsNotAllowedToViewCardPage(int cardId, String loggedUserUsername, String errorMessage);
}
