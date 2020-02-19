package a16team1.virtualwallet.controllers.mvc;

import a16team1.virtualwallet.controllers.mvc.util.contracts.PreparePaymentInstrumentsModelAndView;
import a16team1.virtualwallet.exceptions.AccessDeniedException;
import a16team1.virtualwallet.exceptions.DuplicateEntityException;
import a16team1.virtualwallet.exceptions.InvalidOperationException;
import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.dtos.*;
import a16team1.virtualwallet.services.CardService;
import a16team1.virtualwallet.services.DtoListsMediatorService;
import a16team1.virtualwallet.services.UserService;
import a16team1.virtualwallet.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class PaymentInstrumentsController {

    @Value("${error.pageDoesNotExist}")
    private String pageDoesNotExist;


    private CardService cardService;
    private UserService userService;
    private PreparePaymentInstrumentsModelAndView prepareModelAndViewService;
    private WalletService walletService;
    private DtoListsMediatorService dtoListsMediatorService;

    @Autowired
    public PaymentInstrumentsController(CardService cardService,
                                        PreparePaymentInstrumentsModelAndView prepareModelAndViewService,
                                        UserService userService,
                                        WalletService walletService) {
        this.cardService = cardService;
        this.prepareModelAndViewService = prepareModelAndViewService;
        this.userService = userService;
        this.walletService = walletService;
    }

    @ModelAttribute("user")
    public User loadUser(Principal principal) {
        return userService.getByUsername(principal.getName());
    }

    @GetMapping("/user/add-card")
    public ModelAndView showAddCardForm(ModelAndView modelAndView) {
        prepareModelAndViewService.forShowingAddCardForm(modelAndView);
        modelAndView.setViewName("card-add-new");
        return modelAndView;
    }

    @PostMapping("/user/add-card")
    public ModelAndView processAddCardFormData(ModelAndView modelAndView,
                                               @Valid @ModelAttribute("cardDto") NewCardDto cardDto,
                                               BindingResult result,
                                               Principal principal) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("cardDto", cardDto);
            modelAndView.setViewName("card-add-new");
        } else {
            try {
                cardService.create(principal.getName(), cardDto);
                modelAndView.setViewName("redirect:/user/cards");
            } catch (DuplicateEntityException e) {
                modelAndView.setStatus(HttpStatus.CONFLICT);
                prepareModelAndViewService.forAddingNewCardWithInvalidDetails(modelAndView, cardDto, e.getMessage());
                modelAndView.setViewName("card-add-new");
            }
        }
        return modelAndView;
    }

    @GetMapping("/user/add-wallet")
    public ModelAndView showAddWalletForm(ModelAndView modelAndView) {
        prepareModelAndViewService.forShowingAddWalletForm(modelAndView);
        modelAndView.setViewName("user-add-wallet");
        return modelAndView;
    }

    @PostMapping("/user/add-wallet")
    public ModelAndView addWallet(ModelAndView modelAndView,
                                  @Valid @ModelAttribute("walletDto") NewWalletDto walletDto,
                                  BindingResult result,
                                  Principal principal,
                                  @RequestParam(value = "default", required = false) Boolean defaultWallet) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.setViewName("user-add-wallet");
        } else {
            try {
                walletService.create(principal.getName(), walletDto, defaultWallet);
                modelAndView.setViewName("redirect:/user/wallets");
            } catch (DuplicateEntityException e) {
                modelAndView.setStatus(HttpStatus.CONFLICT);
                modelAndView.addObject("error", e.getMessage());
                modelAndView.setViewName("user-add-wallet");
            }
        }
        return modelAndView;
    }

    @GetMapping("/user/wallets")
    public ModelAndView getWallets(ModelAndView modelAndView, Principal principal) {
        List<PresentableWalletDto> list = dtoListsMediatorService.getPresentableWalletDtos(principal.getName());
        modelAndView.addObject("presentableWalletDtos", list);
        modelAndView.setViewName("user-wallets");
        return modelAndView;
    }

    @PostMapping("/user/wallets/{id}/make-default")
    public ModelAndView makeWalletDefault(ModelAndView modelAndView,
                                          @PathVariable("id") int walletId,
                                          Principal principal) {
        try {
            userService.updateDefaultWallet(principal.getName(), walletId);
        } catch (InvalidOperationException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("error", e.getMessage());
        }
        modelAndView.setViewName("redirect:/user/wallets");
        return modelAndView;
    }

    @PostMapping("/user/cards/{id}/delete")
    public ModelAndView deleteCard(ModelAndView modelAndView,
                                   @PathVariable("id") int cardId,
                                   Principal principal) {
        try {
            cardService.delete(cardId, principal.getName());
            modelAndView.setViewName("redirect:/user/cards");
        } catch (InvalidOperationException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("error", e.getMessage());
            getCards(modelAndView, principal);
        } catch (AccessDeniedException e) {
            modelAndView.setStatus(HttpStatus.FORBIDDEN);
            modelAndView.addObject("error", e.getMessage());
            getCards(modelAndView, principal);
        }
        return modelAndView;
    }

    @GetMapping("/user/cards")
    public ModelAndView getCards(ModelAndView modelAndView, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        List<Card> cardList = cardService.getAllByUser(user.getId());
        modelAndView.addObject("cards", cardList);
        modelAndView.setViewName("user-cards");
        return modelAndView;
    }

    @GetMapping("/user/cards/{id}/edit")
    public ModelAndView getEditCardDetailsPage(ModelAndView modelAndView,
                                               @PathVariable(value = "id") int cardId,
                                               Principal principal) {
        cardService.throwIfUserIsNotAllowedToViewCardPage(cardId, principal.getName(), pageDoesNotExist);
        Card card = cardService.getById(cardId);
        prepareModelAndViewService.forShowingEditCardPage(modelAndView, card);
        modelAndView.setViewName("user-edit-card");
        return modelAndView;
    }


    @PostMapping("/user/cards/{id}/edit")
    public ModelAndView editCardDetails(ModelAndView modelAndView,
                                        @PathVariable("id") int cardId,
                                        @Valid @ModelAttribute("cardDto") ExistingCardDto existingCardDto,
                                        BindingResult result,
                                        Principal principal) {
        if (result.hasErrors()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            Card card = cardService.getById(cardId);
            prepareModelAndViewService.forShowingEditCardPage(modelAndView, card, existingCardDto);
            modelAndView.setViewName("user-edit-card");
        } else {
            try {
                cardService.update(existingCardDto, cardId, principal.getName());
                modelAndView.setViewName("redirect:/user/cards");
            } catch (InvalidOperationException e) {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                modelAndView.addObject("error", e.getMessage());
                getCards(modelAndView, principal);
            } catch (DuplicateEntityException e) {
                modelAndView.setStatus(HttpStatus.CONFLICT);
                modelAndView.addObject("error", e.getMessage());
                getCards(modelAndView, principal);
            }

        }
        return modelAndView;
    }


    @Autowired
    public void setDtoListsMediatorService(DtoListsMediatorService dtoListsMediatorService) {
        this.dtoListsMediatorService = dtoListsMediatorService;
    }
}
