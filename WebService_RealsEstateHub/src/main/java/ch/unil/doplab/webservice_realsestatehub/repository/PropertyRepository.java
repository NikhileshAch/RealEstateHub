package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.PropertyEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PropertyRepository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RealEstateHubPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<PropertyEntity> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM PropertyEntity p", PropertyEntity.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<PropertyEntity> findById(String id) {
        EntityManager em = getEntityManager();
        try {
            PropertyEntity property = em.find(PropertyEntity.class, id);
            return Optional.ofNullable(property);
        } finally {
            em.close();
        }
    }

    public List<PropertyEntity> findByOwnerId(String ownerId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM PropertyEntity p WHERE p.ownerId = :ownerId", PropertyEntity.class)
                    .setParameter("ownerId", ownerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<PropertyEntity> findForSale() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM PropertyEntity p WHERE p.status = :status", PropertyEntity.class)
                    .setParameter("status", PropertyEntity.PropertyStatus.FOR_SALE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public PropertyEntity save(PropertyEntity property) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (em.find(PropertyEntity.class, property.getPropertyId()) != null) {
                property = em.merge(property);
            } else {
                em.persist(property);
            }
            em.getTransaction().commit();
            return property;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public PropertyEntity update(PropertyEntity property) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            property = em.merge(property);
            em.getTransaction().commit();
            return property;
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
            PropertyEntity property = em.find(PropertyEntity.class, id);
            if (property != null) {
                em.remove(property);
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

    public void deleteByOwnerId(String ownerId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM PropertyEntity p WHERE p.ownerId = :ownerId")
                    .setParameter("ownerId", ownerId)
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
