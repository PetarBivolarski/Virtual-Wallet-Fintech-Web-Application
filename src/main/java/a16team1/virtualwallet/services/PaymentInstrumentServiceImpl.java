package a16team1.virtualwallet.services;

import a16team1.virtualwallet.exceptions.EntityNotFoundException;
import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.repositories.contracts.PaymentInstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


@Service
@PropertySource("classpath:messages.properties")
public class PaymentInstrumentServiceImpl implements PaymentInstrumentService {

    private PaymentInstrumentRepository paymentInstrumentRepository;

    @Value("${error.paymentInstrumentNotFound}")
    private String paymentInstrumentNotFound;

    @Autowired
    public PaymentInstrumentServiceImpl(PaymentInstrumentRepository paymentInstrumentRepository, UserService userService) {
        this.paymentInstrumentRepository = paymentInstrumentRepository;
    }

    @Override
    public PaymentInstrument getById(int id) {
        PaymentInstrument paymentInstrument = paymentInstrumentRepository.getById(id);
        if (paymentInstrument == null) {
            throw new EntityNotFoundException(paymentInstrumentNotFound);
        }
        return paymentInstrument;
    }

    @Override
    public PaymentInstrument create(User owner, PaymentInstrument paymentInstrument) {
        return paymentInstrumentRepository.create(paymentInstrument);
    }

    @Override
    public PaymentInstrument update(PaymentInstrument paymentInstrument) {
        return paymentInstrumentRepository.update(paymentInstrument);
    }

    @Override
    public PaymentInstrument getWalletInstrumentByUserAndName(User user, String name) {
        return paymentInstrumentRepository.getWalletInstrumentByUserAndName(user, name);
    }

    @Override
    public PaymentInstrument getCardInstrumentByUserAndName(User user, String name) {
        return paymentInstrumentRepository.getCardInstrumentByUserAndName(user, name);
    }


}
