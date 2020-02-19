package a16team1.virtualwallet.controllers.mvc.util;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PreparePaymentInstrumentsModelAndView;
import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.dtos.ExistingCardDto;
import a16team1.virtualwallet.models.dtos.NewCardDto;
import a16team1.virtualwallet.models.dtos.NewPaymentInstrumentDto;
import a16team1.virtualwallet.models.dtos.NewWalletDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class PreparePaymentInstrumentsModelAndViewImpl implements PreparePaymentInstrumentsModelAndView {

    @Override
    public void forShowingAddCardForm(ModelAndView modelAndView) {
        NewPaymentInstrumentDto paymentInstrumentDto = new NewPaymentInstrumentDto();
        NewCardDto cardDto = new NewCardDto();
        cardDto.setPaymentInstrumentDto(paymentInstrumentDto);
        modelAndView.addObject("cardDto", cardDto);
    }

    @Override
    public void forAddingNewCardWithInvalidDetails(ModelAndView modelAndView, NewCardDto cardDto, String errorMessage) {
        modelAndView.addObject("duplicateCardError", errorMessage);
        modelAndView.addObject("cardDto", cardDto);
    }

    @Override
    public void forShowingAddWalletForm(ModelAndView modelAndView) {
        NewPaymentInstrumentDto paymentInstrumentDto = new NewPaymentInstrumentDto();
        NewWalletDto walletDto = new NewWalletDto();
        walletDto.setPaymentInstrumentDto(paymentInstrumentDto);
        modelAndView.addObject("walletDto", walletDto);
        boolean defaultWallet = false;
        modelAndView.addObject("default", defaultWallet);
    }

    @Override
    public void forShowingEditCardPage(ModelAndView modelAndView, Card card, ExistingCardDto existingCardDto) {
        modelAndView.addObject("cardId", card.getId());
        modelAndView.addObject("card", card);
        modelAndView.addObject("cardDto", existingCardDto);
    }

    @Override
    public void forShowingEditCardPage(ModelAndView modelAndView, Card card) {
        modelAndView.addObject("cardId", card.getId());
        modelAndView.addObject("card", card);
        modelAndView.addObject("cardDto", new ExistingCardDto());
    }
}
