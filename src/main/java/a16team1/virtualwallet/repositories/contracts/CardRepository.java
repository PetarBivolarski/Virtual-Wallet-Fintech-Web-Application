package a16team1.virtualwallet.repositories.contracts;


import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.User;

import java.util.List;

public interface CardRepository {

    List<Card> getAllByUser(User user);

    List<Card> getAll();

    Card getById(int id);

    Card getByCardNumber(String cardNumber);

    Card create(Card card);

    Card update(Card card);

    Card delete(Card card);

}
