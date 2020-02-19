package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.repositories.contracts.CardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CardRepositoryImpl implements CardRepository {


    private SessionFactory sessionFactory;

    @Autowired
    public CardRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Card> getAllByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("SELECT c FROM Card AS c JOIN PaymentInstrument AS pi" +
                    " ON c.id = pi.id WHERE pi.owner.id = :userId AND c.deleted = false", Card.class);
            query.setParameter("userId", user.getId());
            return query.list();
        }
    }

    @Override
    public List<Card> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("SELECT c FROM Card AS c", Card.class);
            return query.list();
        }
    }

    @Override
    public Card getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("SELECT c from Card as c WHERE c.id = :id AND c.deleted = false", Card.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        }
    }

    @Override
    public Card getByCardNumber(String cardNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("SELECT c FROM Card AS c WHERE c.cardNumber = :cardNumber", Card.class);
            query.setParameter("cardNumber", cardNumber);
            return query.uniqueResult();
        }
    }

    @Override
    public Card create(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(card);
            session.getTransaction().commit();
            return card;
        }
    }

    @Override
    @Transactional
    public Card update(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(card);
            session.getTransaction().commit();
            return card;
        }
    }

    @Override
    @Transactional
    public Card delete(Card card) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            card.setDeleted(true);
            card.setCardNumber(null);
            session.update(card);
            session.getTransaction().commit();
            return card;
        }
    }

}
