package a16team1.virtualwallet.controllers.mvc.util.contracts;

import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.dtos.ExistingCardDto;
import a16team1.virtualwallet.models.dtos.NewCardDto;
import org.springframework.web.servlet.ModelAndView;

public interface PreparePaymentInstrumentsModelAndView {

    void forShowingAddCardForm(ModelAndView modelAndView);

    void forAddingNewCardWithInvalidDetails(ModelAndView modelAndView, NewCardDto cardDto, String errorMessage);

    void forShowingAddWalletForm(ModelAndView modelAndView);

    void forShowingEditCardPage(ModelAndView modelAndView, Card card, ExistingCardDto existingCardDto);

    void forShowingEditCardPage(ModelAndView modelAndView, Card card);
}
