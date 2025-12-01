package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.OfferEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OfferRepository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RealEstateHubPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<OfferEntity> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT o FROM OfferEntity o", OfferEntity.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<OfferEntity> findById(String id) {
        EntityManager em = getEntityManager();
        try {
            OfferEntity offer = em.find(OfferEntity.class, id);
            return Optional.ofNullable(offer);
        } finally {
            em.close();
        }
    }

    public List<OfferEntity> findByPropertyId(String propertyId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT o FROM OfferEntity o WHERE o.propertyId = :propertyId", OfferEntity.class)
                    .setParameter("propertyId", propertyId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<OfferEntity> findByBuyerId(String buyerId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT o FROM OfferEntity o WHERE o.buyerId = :buyerId", OfferEntity.class)
                    .setParameter("buyerId", buyerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public OfferEntity save(OfferEntity offer) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (em.find(OfferEntity.class, offer.getOfferId()) != null) {
                offer = em.merge(offer);
            } else {
                em.persist(offer);
            }
            em.getTransaction().commit();
            return offer;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public OfferEntity update(OfferEntity offer) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            offer = em.merge(offer);
            em.getTransaction().commit();
            return offer;
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
            OfferEntity offer = em.find(OfferEntity.class, id);
            if (offer != null) {
                em.remove(offer);
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

    public void deleteByPropertyId(String propertyId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM OfferEntity o WHERE o.propertyId = :propertyId")
                    .setParameter("propertyId", propertyId)
                    .executeUpdate();
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
