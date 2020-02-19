package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.repositories.contracts.PaymentInstrumentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentInstrumentRepositoryImpl implements PaymentInstrumentRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public PaymentInstrumentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PaymentInstrument getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(PaymentInstrument.class, id);
        }
    }

    @Override
    public PaymentInstrument getCardInstrumentByUserAndName(User user, String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<PaymentInstrument> query = session.createQuery("SELECT pi FROM PaymentInstrument AS pi" +
                    " INNER JOIN Card c on pi.id = c.id WHERE pi.owner.id = :userId AND c.deleted = false AND pi.name = :name", PaymentInstrument.class);
            query.setParameter("userId", user.getId());
            query.setParameter("name", name);
            return query.uniqueResult();
        }
    }

    @Override
    public PaymentInstrument getWalletInstrumentByUserAndName(User user, String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<PaymentInstrument> query = session.createQuery("SELECT pi FROM PaymentInstrument AS pi" +
                    " INNER JOIN Wallet w ON pi.id = w.id WHERE pi.owner.id = :userId AND w.deleted = false AND pi.name = :name", PaymentInstrument.class);
            query.setParameter("userId", user.getId());
            query.setParameter("name", name);
            return query.uniqueResult();
        }
    }

    @Override
    public PaymentInstrument create(PaymentInstrument paymentInstrument) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(paymentInstrument);
            session.getTransaction().commit();
        }
        return paymentInstrument;
    }

    @Override
    public PaymentInstrument update(PaymentInstrument paymentInstrument) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(paymentInstrument);
            session.getTransaction().commit();
        }
        return paymentInstrument;
    }
}
