package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.UserInvitationToken;
import a16team1.virtualwallet.repositories.contracts.UserInvitationTokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserInvitationTokenRepositoryImpl implements UserInvitationTokenRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public UserInvitationTokenRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserInvitationToken createVerificationToken(UserInvitationToken userInvitationToken) {
        try (Session session = sessionFactory.openSession()) {
            session.save(userInvitationToken);
            return userInvitationToken;
        }
    }

    @Override
    public UserInvitationToken getUserInvitationTokenByName(String confirmationToken) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserInvitationToken> query = session.createQuery("FROM UserInvitationToken where token = :name", UserInvitationToken.class);
            query.setParameter("name", confirmationToken);
            return query.uniqueResult();
        }
    }

    @Transactional
    @Override
    public UserInvitationToken update(UserInvitationToken userInvitationToken) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(userInvitationToken);
            session.getTransaction().commit();
            return userInvitationToken;
        }
    }
}
