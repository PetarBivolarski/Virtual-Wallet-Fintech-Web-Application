package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.repositories.contracts.WalletRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public WalletRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Wallet> getAllByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("SELECT w FROM Wallet AS w JOIN PaymentInstrument AS pi" +
                    " ON w.id = pi.id WHERE pi.owner.id = :userId", Wallet.class);
            query.setParameter("userId", user.getId());
            return query.list();
        }
    }

    public BigDecimal getTotalUserSaldo(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery("SELECT SUM (w.saldo) FROM Wallet as w JOIN PaymentInstrument as pi " +
                    " ON w.id = pi.id WHERE pi.owner.id = :userId", BigDecimal.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
    }

    @Override
    public Wallet getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Wallet.class, id);
        }
    }

    @Override
    public Wallet create(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(wallet);
            session.getTransaction().commit();
        }
        return wallet;
    }

    @Override
    public Wallet update(Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(wallet);
            session.getTransaction().commit();
        }
        return wallet;
    }
}
