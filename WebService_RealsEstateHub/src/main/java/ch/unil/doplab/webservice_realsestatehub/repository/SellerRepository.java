package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.SellerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SellerRepository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RealEstateHubPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<SellerEntity> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM SellerEntity s", SellerEntity.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<SellerEntity> findById(String id) {
        EntityManager em = getEntityManager();
        try {
            SellerEntity seller = em.find(SellerEntity.class, id);
            return Optional.ofNullable(seller);
        } finally {
            em.close();
        }
    }

    public Optional<SellerEntity> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            List<SellerEntity> results = em.createQuery("SELECT s FROM SellerEntity s WHERE s.email = :email", SellerEntity.class)
                    .setParameter("email", email)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<SellerEntity> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            List<SellerEntity> results = em.createQuery("SELECT s FROM SellerEntity s WHERE s.username = :username", SellerEntity.class)
                    .setParameter("username", username)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public SellerEntity save(SellerEntity seller) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (em.find(SellerEntity.class, seller.getUserId()) != null) {
                seller = em.merge(seller);
            } else {
                em.persist(seller);
            }
            em.getTransaction().commit();
            return seller;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public SellerEntity update(SellerEntity seller) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            seller = em.merge(seller);
            em.getTransaction().commit();
            return seller;
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
            SellerEntity seller = em.find(SellerEntity.class, id);
            if (seller != null) {
                em.remove(seller);
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
