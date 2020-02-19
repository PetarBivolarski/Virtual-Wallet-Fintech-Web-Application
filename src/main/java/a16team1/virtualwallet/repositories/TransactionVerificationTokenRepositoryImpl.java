package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.TransactionVerificationToken;
import a16team1.virtualwallet.repositories.contracts.TransactionVerificationTokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionVerificationTokenRepositoryImpl implements TransactionVerificationTokenRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public TransactionVerificationTokenRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public TransactionVerificationToken createVerificationToken(Transaction transaction, TransactionVerificationToken transactionVerificationToken) {
        try (Session session = sessionFactory.openSession()) {
            transactionVerificationToken.setTransaction(transaction);
            session.save(transactionVerificationToken);
            return transactionVerificationToken;
        }
    }

    @Override
    public TransactionVerificationToken getVerificationTokenByName(String confirmationToken) {
        try (Session session = sessionFactory.openSession()) {
            Query<TransactionVerificationToken> query = session.createQuery("FROM TransactionVerificationToken where token = :name", TransactionVerificationToken.class);
            query.setParameter("name", confirmationToken);
            return query.uniqueResult();
        }
    }
}
