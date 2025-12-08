package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.BuyerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Buyer entity operations.
 * Uses container-managed persistence with @PersistenceContext.
 */
@ApplicationScoped
public class BuyerRepository {

    @PersistenceContext(unitName = "RealEstateHubPU")
    private EntityManager em;

    /**
     * Find all buyers.
     */
    public List<BuyerEntity> findAll() {
        return em.createQuery("SELECT b FROM BuyerEntity b", BuyerEntity.class)
                .getResultList();
    }

    /**
     * Find buyer by ID.
     */
    public Optional<BuyerEntity> findById(String id) {
        BuyerEntity buyer = em.find(BuyerEntity.class, id);
        return Optional.ofNullable(buyer);
    }

    /**
     * Find buyer by email using named query.
     */
    public Optional<BuyerEntity> findByEmail(String email) {
        TypedQuery<BuyerEntity> query = em.createNamedQuery("Buyer.findByEmail", BuyerEntity.class);
        query.setParameter("email", email);
        List<BuyerEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find buyer by username using named query.
     */
    public Optional<BuyerEntity> findByUsername(String username) {
        TypedQuery<BuyerEntity> query = em.createNamedQuery("Buyer.findByUsername", BuyerEntity.class);
        query.setParameter("username", username);
        List<BuyerEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Save (create or update) a buyer.
     */
    @Transactional
    public BuyerEntity save(BuyerEntity buyer) {
        if (em.find(BuyerEntity.class, buyer.getUserId()) != null) {
            return em.merge(buyer);
        } else {
            em.persist(buyer);
            return buyer;
        }
    }

    /**
     * Update an existing buyer.
     */
    @Transactional
    public BuyerEntity update(BuyerEntity buyer) {
        return em.merge(buyer);
    }

    /**
     * Delete a buyer by ID.
     * Cascade delete will automatically remove all associated offers.
     */
    @Transactional
    public void delete(String id) {
        BuyerEntity buyer = em.find(BuyerEntity.class, id);
        if (buyer != null) {
            em.remove(buyer);
        }
    }
}
