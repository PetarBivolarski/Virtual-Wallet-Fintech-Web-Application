package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.misc.InspirationalQuote;
import a16team1.virtualwallet.repositories.contracts.InspirationalQuoteRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InspirationalQuoteRepositoryImpl implements InspirationalQuoteRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public InspirationalQuoteRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public InspirationalQuote getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            InspirationalQuote inspirationalQuote = session.get(InspirationalQuote.class, id);
            return inspirationalQuote;
        }
    }

    @Override
    public List<InspirationalQuote> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<InspirationalQuote> query = session.createQuery("FROM InspirationalQuote ", InspirationalQuote.class);
            return query.list();
        }
    }
}
