package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;

public interface PaymentInstrumentRepository {

    PaymentInstrument getById(int id);

    PaymentInstrument getCardInstrumentByUserAndName(User user, String name);

    PaymentInstrument getWalletInstrumentByUserAndName(User user, String name);

    PaymentInstrument create(PaymentInstrument paymentInstrument);

    PaymentInstrument update(PaymentInstrument paymentInstrument);
}
