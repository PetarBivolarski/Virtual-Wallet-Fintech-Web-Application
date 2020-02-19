package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.UserVerificationToken;
import a16team1.virtualwallet.repositories.contracts.UserVerificationTokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserVerificationTokenRepositoryImpl implements UserVerificationTokenRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public UserVerificationTokenRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserVerificationToken createVerificationToken(User user, UserVerificationToken userVerificationToken) {
        try (Session session = sessionFactory.openSession()) {
            userVerificationToken.setUser(user);
            session.save(userVerificationToken);
            return userVerificationToken;
        }
    }

    @Override
    public UserVerificationToken getVerificationTokenByName(String confirmationToken) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserVerificationToken> query = session.createQuery("FROM UserVerificationToken where token = :name", UserVerificationToken.class);
            query.setParameter("name", confirmationToken);
            return query.uniqueResult();
        }
    }
}
