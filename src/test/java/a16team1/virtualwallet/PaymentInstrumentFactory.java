package a16team1.virtualwallet;

import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.*;
import a16team1.virtualwallet.utilities.InstrumentType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentInstrumentFactory {
    public static PaymentInstrument createPaymentInstrument(int id, User owner, String name, InstrumentType type) {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setId(id);
        paymentInstrument.setOwner(owner);
        paymentInstrument.setName(name);
        paymentInstrument.setInstrumentType(type);
        return paymentInstrument;
    }

    public static Wallet createWallet(PaymentInstrument paymentInstrument) {
        Wallet wallet = new Wallet();
        wallet.setId(paymentInstrument.getId());
        wallet.setSaldo(BigDecimal.ZERO);
        return wallet;
    }

    public static Wallet createWallet(int id) {
        Wallet wallet = new Wallet();
        wallet.setSaldo(BigDecimal.ZERO);
        wallet.setId(id);
        return wallet;
    }

    public static NewWalletDto createNewWalletDto(User user, String name) {
        NewWalletDto walletDto = new NewWalletDto();
        NewPaymentInstrumentDto paymentInstrumentDto = new NewPaymentInstrumentDto();
        paymentInstrumentDto.setName(name);
        paymentInstrumentDto.setInstrumentType(InstrumentType.WALLET);
        paymentInstrumentDto.setOwnerId(user.getId());
        walletDto.setPaymentInstrumentDto(paymentInstrumentDto);
        walletDto.setOwnerId(user.getId());
        walletDto.setName(name);
        return walletDto;
    }

    public static Card createCard(PaymentInstrument paymentInstrument, String cardholderName, String cardNumber, String expirationDate, String csv) {
        Card card = new Card();
        card.setId(paymentInstrument.getId());
        card.setCardholderName(cardholderName);
        card.setCardNumber(cardNumber);
        card.setExpirationDate(expirationDate);
        card.setCsv(csv);
        return card;
    }

    public static Card createCard() {
        User user = UserFactory.createUser();
        PaymentInstrument paymentInstrument = createPaymentInstrument(1, user, "Test card ", InstrumentType.CARD);
        return createCard(paymentInstrument);
    }

    public static Card createAnotherCard() {
        User user = UserFactory.createUser();
        PaymentInstrument paymentInstrument = createPaymentInstrument(0, user, "Test card 2", InstrumentType.CARD);
        return createCard(paymentInstrument, paymentInstrument.getOwner().getUsername(),
                "8948-5951-9012-3456", "12/25", "343");
    }

    public static Card createCard(PaymentInstrument paymentInstrument) {
        return createCard(paymentInstrument, paymentInstrument.getOwner().getUsername(),
                "1234-5678-9012-3456", "12/20", "123");
    }

    public static NewCardDto createNewCardDto() {
        NewCardDto newCardDto = new NewCardDto();
        newCardDto.setCardholderName("Petar Bivolarski");
        newCardDto.setCardNumber("1234-4321-4512-1551");
        newCardDto.setCsv("151");
        newCardDto.setExpirationDate("05/29");
        NewPaymentInstrumentDto newPaymentInstrumentDto = new NewPaymentInstrumentDto();
        User user = UserFactory.createUser();
        newPaymentInstrumentDto.setOwnerId(user.getId());
        newPaymentInstrumentDto.setInstrumentType(InstrumentType.CARD);
        newPaymentInstrumentDto.setName("Test NewCardDto");
        newCardDto.setPaymentInstrumentDto(newPaymentInstrumentDto);
        return newCardDto;
    }

    public static ExistingCardDto createExistingCardDto() {
        ExistingCardDto cardDto = new ExistingCardDto();
        cardDto.setCardholderName("Petar Bivolarski");
        cardDto.setCardNumber("9845-4515-4554-5154");
        cardDto.setCsv("151");
        cardDto.setExpirationDate("07/29");
        return cardDto;
    }

    public static Card createCardFromNewCardDto(NewCardDto cardDto) {
        Card card = new Card();
        card.setCardholderName(cardDto.getCardholderName());
        card.setCardNumber(cardDto.getCardNumber());
        card.setExpirationDate(cardDto.getExpirationDate());
        card.setCsv(cardDto.getCsv());
//        card.setId(1);
        card.setDeleted(false);
        return card;
    }

    public static PaymentInstrument createDefaultWalletFor(User user, int id, String walletName) {
        PaymentInstrument paymentInstrument = PaymentInstrumentFactory.createPaymentInstrument(id, user, walletName, InstrumentType.WALLET);
        Wallet wallet = PaymentInstrumentFactory.createWallet(paymentInstrument);
        user.setDefaultWallet(wallet);
        return paymentInstrument;
    }

    public static PresentableCardDto createPresentableCardDto(PaymentInstrument cardInstrument) {
        PresentableCardDto cardDto = new PresentableCardDto();
        cardDto.setId(cardInstrument.getId());
        cardDto.setName(cardInstrument.getName());
        return cardDto;
    }

    public static PresentableWalletDto createPresentableWalletDto(PaymentInstrument walletInstrument) {
        PresentableWalletDto walletDto = new PresentableWalletDto();
        walletDto.setId(walletInstrument.getId());
        walletDto.setName(walletInstrument.getName());
        walletDto.setAmount(BigDecimal.ZERO);
        return walletDto;
    }

    public static List<PaymentInstrument> createPaymentInstrumentList(User user1, User user2, User user3) {
        List<PaymentInstrument> instruments = new ArrayList<>();
        PaymentInstrument user1Card = PaymentInstrumentFactory.createPaymentInstrument(1, user1, "Card 1", InstrumentType.CARD);
        instruments.add(user1Card);
        PaymentInstrument user1Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user1, 2, "Wallet 1");
        instruments.add(user1Wallet);
        PaymentInstrument user2Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user2, 3, "Wallet 2");
        instruments.add(user2Wallet);
        PaymentInstrument user3Wallet = PaymentInstrumentFactory.createDefaultWalletFor(user3, 4, "Wallet 3");
        instruments.add(user3Wallet);
        return instruments;
    }
}
