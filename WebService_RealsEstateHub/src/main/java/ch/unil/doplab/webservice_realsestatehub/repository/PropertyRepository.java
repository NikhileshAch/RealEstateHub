package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.PropertyEntity;
import ch.unil.doplab.webservice_realsestatehub.entity.SellerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Property entity operations.
 * Uses container-managed persistence with @PersistenceContext.
 */
@ApplicationScoped
public class PropertyRepository {

    @PersistenceContext(unitName = "RealEstateHubPU")
    private EntityManager em;

    public List<PropertyEntity> findAll() {
        return em.createQuery("SELECT p FROM PropertyEntity p", PropertyEntity.class)
                .getResultList();
    }

    public Optional<PropertyEntity> findById(String id) {
        PropertyEntity property = em.find(PropertyEntity.class, id);
        return Optional.ofNullable(property);
    }

    /**
     * Find properties by owner (seller).
     */
    public List<PropertyEntity> findByOwner(SellerEntity owner) {
        return em.createQuery("SELECT p FROM PropertyEntity p WHERE p.owner = :owner", PropertyEntity.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    /**
     * Find properties that are for sale.
     */
    public List<PropertyEntity> findForSale() {
        return em.createQuery("SELECT p FROM PropertyEntity p WHERE p.status = :status", PropertyEntity.class)
                .setParameter("status", PropertyEntity.PropertyStatus.FOR_SALE)
                .getResultList();
    }

    @Transactional
    public PropertyEntity save(PropertyEntity property) {
        if (em.find(PropertyEntity.class, property.getPropertyId()) != null) {
            return em.merge(property);
        } else {
            em.persist(property);
            return property;
        }
    }

    @Transactional
    public PropertyEntity update(PropertyEntity property) {
        return em.merge(property);
    }

    /**
     * Delete a property by ID.
     * Cascade delete will automatically remove all associated offers.
     */
    @Transactional
    public void delete(String id) {
        PropertyEntity property = em.find(PropertyEntity.class, id);
        if (property != null) {
            em.remove(property);
        }
    }
}
