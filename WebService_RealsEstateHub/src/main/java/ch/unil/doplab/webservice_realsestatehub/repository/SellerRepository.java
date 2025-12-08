package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.SellerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Seller entity operations.
 * Uses container-managed persistence with @PersistenceContext.
 */
@ApplicationScoped
public class SellerRepository {

    @PersistenceContext(unitName = "RealEstateHubPU")
    private EntityManager em;

    /**
     * Find all sellers.
     */
    public List<SellerEntity> findAll() {
        return em.createQuery("SELECT s FROM SellerEntity s", SellerEntity.class)
                .getResultList();
    }

    /**
     * Find seller by ID.
     */
    public Optional<SellerEntity> findById(String id) {
        SellerEntity seller = em.find(SellerEntity.class, id);
        return Optional.ofNullable(seller);
    }

    /**
     * Find seller by email using named query.
     */
    public Optional<SellerEntity> findByEmail(String email) {
        TypedQuery<SellerEntity> query = em.createNamedQuery("Seller.findByEmail", SellerEntity.class);
        query.setParameter("email", email);
        List<SellerEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find seller by username using named query.
     */
    public Optional<SellerEntity> findByUsername(String username) {
        TypedQuery<SellerEntity> query = em.createNamedQuery("Seller.findByUsername", SellerEntity.class);
        query.setParameter("username", username);
        List<SellerEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Save (create or update) a seller.
     */
    @Transactional
    public SellerEntity save(SellerEntity seller) {
        if (em.find(SellerEntity.class, seller.getUserId()) != null) {
            return em.merge(seller);
        } else {
            em.persist(seller);
            return seller;
        }
    }

    /**
     * Update an existing seller.
     */
    @Transactional
    public SellerEntity update(SellerEntity seller) {
        return em.merge(seller);
    }

    /**
     * Delete a seller by ID.
     * Cascade delete will automatically remove all associated properties and
     * offers.
     */
    @Transactional
    public void delete(String id) {
        SellerEntity seller = em.find(SellerEntity.class, id);
        if (seller != null) {
            em.remove(seller);
        }
    }
}
