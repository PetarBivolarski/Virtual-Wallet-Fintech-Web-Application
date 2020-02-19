package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.repositories.contracts.TransactionRepository;
import a16team1.virtualwallet.utilities.Pagination;
import a16team1.virtualwallet.utilities.TransactionType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final int MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;

    private SessionFactory sessionFactory;


    @Autowired
    public TransactionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Page<Transaction> getAll(Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery("SELECT t FROM Transaction AS t", Transaction.class);
            return Pagination.pagedList(pageable, query.list());
        }
    }

    @Override
    @Transactional
    public Transaction update(Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(transaction);
            session.getTransaction().commit();
            return transaction;
        }
    }

    @Override
    public Page<Transaction> filter(Date startDate, Date endDate, Optional<User> sender, Optional<User> recipient,
                                    Sort sortBy, Pageable pageable, boolean includeUnverified) {
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
            List<Predicate> predicateList = new ArrayList<>();
            sender.ifPresent((User actualSender) ->
                    predicateList.add(criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), actualSender)));
            recipient.ifPresent((User actualRecipient) ->
                    predicateList.add(criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), actualRecipient)));
            if (!includeUnverified) {
                predicateList.add(criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED));
            }
            Predicate[] predicates = new Predicate[]{
                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
                    criteriaBuilder.and(predicateList.toArray(new Predicate[0]))
            };
            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
            Query<Transaction> query = session.createQuery(criteriaQuery);
            Pagination.addPagination(pageable, query);
            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
        }
    }

    @Override
    public Page<Transaction> filterOutgoing(Date startDate, Date endDate, User sender, Optional<User> recipient,
                                            Sort sortBy, Pageable pageable, boolean includeUnverified) {
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
            List<Predicate> predicateList = new ArrayList<>();
            recipient.ifPresent((User actualRecipient) ->
                    predicateList.add(criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), actualRecipient)));
            if (!includeUnverified) {
                predicateList.add(criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED));
            }
            Predicate[] predicates = new Predicate[]{
                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
                    criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.CARD_TO_WALLET),
                    criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), sender),
                    criteriaBuilder.and(predicateList.toArray(new Predicate[0]))
            };
            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
            Query<Transaction> query = session.createQuery(criteriaQuery);
            Pagination.addPagination(pageable, query);
            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
        }
    }

    @Override
    public Page<Transaction> filterForUser(Date startDate, Date endDate, User user, Sort sortBy, Pageable pageable) {
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
            Predicate userIsSender = criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), user);
            Predicate userIsRecipient = criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), user);
            Predicate[] predicates = new Predicate[] {
                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
                    criteriaBuilder.or(userIsSender, userIsRecipient),
                    criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED)
            };
            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
            Query<Transaction> query = session.createQuery(criteriaQuery);
            Pagination.addPagination(pageable, query);
            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
        }
    }

    @Override
    public Page<Transaction> filterForUserWithCounterparty(Date startDate, Date endDate, User user, User otherUser, Sort sortBy, Pageable pageable) {
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime() + MILLISECONDS_IN_A_DAY - 1);
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
            Root<Transaction> transactions = criteriaQuery.from(Transaction.class);
            Predicate userIsSender = criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), user);
            Predicate userIsRecipient = criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), user);
            Predicate otherUserIsSender = criteriaBuilder.equal(transactions.get("senderInstrument").<User>get("owner"), otherUser);
            Predicate otherUserIsRecipient = criteriaBuilder.equal(transactions.get("recipientInstrument").<User>get("owner"), otherUser);
            Predicate[] predicates = new Predicate[]{
                    criteriaBuilder.between(transactions.get("dateTime"), startTimestamp, endTimestamp),
                    criteriaBuilder.or(criteriaBuilder.and(userIsSender, otherUserIsRecipient),
                            criteriaBuilder.and(otherUserIsSender, userIsRecipient)),
                    criteriaBuilder.notEqual(transactions.get("transactionType"), TransactionType.LARGE_UNVERIFIED)
            };
            List<Order> orders = QueryUtils.toOrders(sortBy, transactions, criteriaBuilder);
            criteriaQuery.select(transactions).where(predicates).orderBy(orders);
            Query<Transaction> query = session.createQuery(criteriaQuery);
            Pagination.addPagination(pageable, query);
            Query<Long> countQuery = session.createQuery(getCountQuery(criteriaBuilder, Transaction.class, predicates));
            return new PageImpl<>(query.list(), pageable, countQuery.uniqueResult());
        }
    }

    @Override
    public Transaction getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Transaction.class, id);
        }
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction, Wallet senderWallet, Wallet recipientWallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(senderWallet);
            session.update(recipientWallet);
            transaction.setDateTime(getCurrentTimestamp());
            session.save(transaction);
            session.getTransaction().commit();
            return transaction;
        }
    }

    @Override
    @Transactional
    public Transaction update(Transaction transaction, Wallet senderWallet, Wallet recipientWallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(senderWallet);
            session.update(recipientWallet);
            transaction.setDateTime(getCurrentTimestamp());
            session.update(transaction);
            session.getTransaction().commit();
            return transaction;
        }
    }

    @Override
    public Transaction createUnverifiedTransactionWithLargeAmount(Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            session.save(transaction);
            return transaction;
        }
    }

    @Override
    @Transactional
    public Transaction createFundingTransaction(Transaction transaction, Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(wallet);
            transaction.setDateTime(getCurrentTimestamp());
            session.save(transaction);
            session.getTransaction().commit();
            return transaction;
        }
    }

    @Override
    public boolean exists(int id) {
        return getById(id) != null;
    }


    private static <E> CriteriaQuery<Long> getCountQuery(CriteriaBuilder criteriaBuilder,
                                                         Class<E> entityClass,
                                                         Predicate[] predicates) {
        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> entityRoot = countCriteriaQuery.from(entityClass);
        return countCriteriaQuery.select(criteriaBuilder.count(entityRoot)).where(predicates);
    }

    private static Timestamp getCurrentTimestamp() {
        return Timestamp.from(ZonedDateTime.now().toInstant());
    }
}
