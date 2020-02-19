package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private SessionFactory sessionFactory;
    private UserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory, UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.sessionFactory = sessionFactory;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> userCriteriaQuery = builder.createQuery(User.class);
            Root<User> users = userCriteriaQuery.from(User.class);
            Query<User> usersQuery = session.createQuery(userCriteriaQuery.select(users));
            addPaginationIfPageableRequest(pageable, usersQuery);
            CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
            Long count = session.createQuery(countQuery.select(builder.count(countQuery.from(User.class)))).uniqueResult();
            return new PageImpl<>(usersQuery.list(), pageable, count);
        }
    }

    @Override
    public Page<User> getAll(Pageable pageable, String filterType, String filterValue) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> usersQuery = session.createQuery("FROM User WHERE " + filterType + " LIKE :filterValue ORDER BY username", User.class);
            usersQuery.setParameter("filterValue", filterValue);
            Query<Long> countQuery = session.createQuery("SELECT COUNT (*) FROM User WHERE " + filterType + " LIKE :filterValue", Long.class);
            countQuery.setParameter("filterValue", filterValue);
            long count = countQuery.getSingleResult();
            addPaginationIfPageableRequest(pageable, usersQuery);
            return new PageImpl<>(usersQuery.list(), pageable, count);
        }
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT u FROM User AS u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT u FROM User AS u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("SELECT u FROM User AS u WHERE u.phoneNumber = :phoneNumber", User.class);
            query.setParameter("phoneNumber", phoneNumber);
            return query.uniqueResult();
        }
    }

    @Override
    public User create(User user) {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(user.getUsername(),
                        passwordEncoder.encode(user.getPassword()),
                        AuthorityUtils.createAuthorityList("ROLE_USER"));
        userDetailsManager.createUser(springUser);
        User createdUser = getByUsername(user.getUsername());
        createdUser.setPhoneNumber(user.getPhoneNumber());
        createdUser.setEmail(user.getEmail());
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        createdUser.setJoinedDate(currentTime);
        createdUser.setConfirmedRegistration(false);
        return update(createdUser);
    }

    @Override
    public User update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        }
        return user;
    }

    @Override
    @Transactional
    public User block(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    @Transactional
    public User unblock(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            return user;
        }
    }

    @Override
    public List<String> getUserRoles(User user) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<?> query = session.createNativeQuery
                    ("SELECT authority from authorities WHERE username = :username");
            query.setParameter("username", user.getUsername());
            List<String> roles = new ArrayList<>();
            for (Object string : query.list()) {
                roles.add((String) string);
            }
            return roles;
        }
    }

    @Transactional
    @Override
    public User delete(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            return user;
        }
    }

    private void addPaginationIfPageableRequest(Pageable pageable, Query<User> query) {
        if (pageable.isPaged()) {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            query.setFirstResult(pageNumber * pageSize).setMaxResults(pageSize);
        }
    }
}
