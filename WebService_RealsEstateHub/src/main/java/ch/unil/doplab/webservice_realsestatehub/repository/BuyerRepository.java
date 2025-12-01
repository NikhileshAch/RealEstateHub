package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.BuyerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BuyerRepository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RealEstateHubPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<BuyerEntity> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT b FROM BuyerEntity b", BuyerEntity.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<BuyerEntity> findById(String id) {
        EntityManager em = getEntityManager();
        try {
            BuyerEntity buyer = em.find(BuyerEntity.class, id);
            return Optional.ofNullable(buyer);
        } finally {
            em.close();
        }
    }

    public Optional<BuyerEntity> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            List<BuyerEntity> results = em.createQuery("SELECT b FROM BuyerEntity b WHERE b.email = :email", BuyerEntity.class)
                    .setParameter("email", email)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<BuyerEntity> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            List<BuyerEntity> results = em.createQuery("SELECT b FROM BuyerEntity b WHERE b.username = :username", BuyerEntity.class)
                    .setParameter("username", username)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public BuyerEntity save(BuyerEntity buyer) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (em.find(BuyerEntity.class, buyer.getUserId()) != null) {
                buyer = em.merge(buyer);
            } else {
                em.persist(buyer);
            }
            em.getTransaction().commit();
            return buyer;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public BuyerEntity update(BuyerEntity buyer) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            buyer = em.merge(buyer);
            em.getTransaction().commit();
            return buyer;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(String id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            BuyerEntity buyer = em.find(BuyerEntity.class, id);
            if (buyer != null) {
                em.remove(buyer);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
