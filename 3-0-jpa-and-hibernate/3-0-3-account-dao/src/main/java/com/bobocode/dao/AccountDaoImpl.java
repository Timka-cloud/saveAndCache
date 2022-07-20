package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;
import com.bobocode.util.ExerciseNotCompletedException;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
       performWithinPersistenceContext(c -> c.persist(account));
    }

    @Override
    public Account findById(Long id) {
       return performReturningWithinPersistenceContext(c -> c.find(Account.class, id));
    }

    @Override
    public Account findByEmail(String email) {
        return performReturningWithinPersistenceContext(em ->
                em.createQuery("select a from Account a where a.email = :email", Account.class)
                        .setParameter("email", email)
                        .getSingleResult()
        );
    }

    @Override
    public List<Account> findAll() {
        return performReturningWithinPersistenceContext(em ->
                em.createQuery("select a from Account a", Account.class).getResultList()
        );
    }

    @Override
    public void update(Account account) {
        performWithinPersistenceContext(entityManager -> entityManager.merge(account));
    }

    @Override
    public void remove(Account account) {
        performWithinPersistenceContext(em -> {
            Account managedAccount = em.merge(account); // only managed entities can be removed
            em.remove(managedAccount);
        });
    }

    public  void performWithinPersistenceContext(Consumer<EntityManager> operation) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            operation.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Error performing JPA operation. Transaction is rolled back", e);
        } finally {
            entityManager.close();
        }
    }

    public  <T> T performReturningWithinPersistenceContext(Function<EntityManager, T> entityManagerFunction) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            T result = entityManagerFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Error performing JPA operation. Transaction is rolled back", e);
        } finally {
            entityManager.close();
        }
    }
}

