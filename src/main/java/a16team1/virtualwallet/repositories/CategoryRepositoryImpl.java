package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.Category;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.repositories.contracts.CategoryRepository;
import a16team1.virtualwallet.utilities.Pagination;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {


    private SessionFactory sessionFactory;

    @Autowired
    public CategoryRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Page<Category> getAll(Pageable pageable, User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Category> query = session.createQuery("FROM Category WHERE creator.id = :id",
                    Category.class);
            query.setParameter("id", user.getId());
            return Pagination.pagedList(pageable, query.list());
        }
    }

    @Override
    public Category getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Category category = session.get(Category.class, id);
            return category;
        }
    }

    @Override
    public Category create(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.save(category);
            return category;
        }
    }

    @Override
    public Category update(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(category);
            session.getTransaction().commit();
            return category;
        }
    }

    @Override
    public Category delete(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction();
            session.delete(category);
            session.getTransaction().commit();
            return category;
        }
    }

    @Override
    public boolean exists(int id) {
        return getById(id) != null;
    }
}
