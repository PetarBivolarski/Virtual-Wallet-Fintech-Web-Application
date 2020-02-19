package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;

public interface PaymentInstrumentService {

    PaymentInstrument getById(int id);

    PaymentInstrument create(User owner, PaymentInstrument paymentInstrument);

    PaymentInstrument update(PaymentInstrument paymentInstrument);

    PaymentInstrument getWalletInstrumentByUserAndName(User user, String name);

    PaymentInstrument getCardInstrumentByUserAndName(User user, String name);
}
