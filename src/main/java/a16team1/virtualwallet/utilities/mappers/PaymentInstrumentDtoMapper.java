package a16team1.virtualwallet.utilities.mappers;

import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentInstrumentDtoMapper {

    public PresentableCardDto toDto(Card card) {
        PresentableCardDto cardDto = new PresentableCardDto();
        cardDto.setId(card.getId());
        cardDto.setName(card.getName());
        return cardDto;
    }

    public PresentableWalletDto toDto(Wallet wallet, User user) {
        PresentableWalletDto walletDto = new PresentableWalletDto();
        walletDto.setId(wallet.getId());
        walletDto.setName(wallet.getName());
        walletDto.setAmount(wallet.getSaldo());
        if (wallet.getId() == user.getDefaultWallet().getId()) {
            walletDto.setDefaultWallet(true);
        } else {
            walletDto.setDefaultWallet(false);
        }
        return walletDto;
    }


    public Card fromDto(NewCardDto cardDto) {
        Card card = new Card();
        card.setCardNumber(cardDto.getCardNumber());
        card.setCardholderName(cardDto.getCardholderName());
        card.setExpirationDate(cardDto.getExpirationDate());
        card.setCsv(cardDto.getCsv());
        return card;
    }

    public CardWithdrawalDto fromCard(BigDecimal amount, String description, String idempotencyKey, Card card) {
        CardWithdrawalDto paymentRequest = new CardWithdrawalDto();
        paymentRequest.setAmount(amount.scaleByPowerOfTen(2).intValue());
        paymentRequest.setCurrency("EUR");
        paymentRequest.setDescription(description);
        paymentRequest.setIdempotencyKey(idempotencyKey);
        CardDetailsDto cardDetails = new CardDetailsDto();
        cardDetails.setCardholderName(card.getCardholderName());
        cardDetails.setCardNumber(card.getCardNumber());
        cardDetails.setCsv(card.getCsv());
        cardDetails.setExpirationDate(card.getExpirationDate());
        paymentRequest.setCardDetails(cardDetails);
        return paymentRequest;
    }

    public Card updateCardFromDto(Card card, ExistingCardDto cardDto) {
        card.setCardNumber(cardDto.getCardNumber());
        card.setCardholderName(cardDto.getCardholderName());
        card.setExpirationDate(cardDto.getExpirationDate());
        card.setCsv(cardDto.getCsv());
        return card;
    }

}
