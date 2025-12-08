package ch.unil.doplab.webservice_realsestatehub.repository;

import ch.unil.doplab.webservice_realsestatehub.entity.BuyerEntity;
import ch.unil.doplab.webservice_realsestatehub.entity.OfferEntity;
import ch.unil.doplab.webservice_realsestatehub.entity.PropertyEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Offer entity operations.
 * Uses container-managed persistence with @PersistenceContext.
 */
@ApplicationScoped
public class OfferRepository {

    @PersistenceContext(unitName = "RealEstateHubPU")
    private EntityManager em;

    public List<OfferEntity> findAll() {
        return em.createQuery("SELECT o FROM OfferEntity o", OfferEntity.class)
                .getResultList();
    }

    public Optional<OfferEntity> findById(String id) {
        OfferEntity offer = em.find(OfferEntity.class, id);
        return Optional.ofNullable(offer);
    }

    /**
     * Find offers for a specific property.
     */
    public List<OfferEntity> findByProperty(PropertyEntity property) {
        return em.createQuery("SELECT o FROM OfferEntity o WHERE o.property = :property", OfferEntity.class)
                .setParameter("property", property)
                .getResultList();
    }

    /**
     * Find offers made by a specific buyer.
     */
    public List<OfferEntity> findByBuyer(BuyerEntity buyer) {
        return em.createQuery("SELECT o FROM OfferEntity o WHERE o.buyer = :buyer", OfferEntity.class)
                .setParameter("buyer", buyer)
                .getResultList();
    }

    @Transactional
    public OfferEntity save(OfferEntity offer) {
        if (em.find(OfferEntity.class, offer.getOfferId()) != null) {
            return em.merge(offer);
        } else {
            em.persist(offer);
            return offer;
        }
    }

    @Transactional
    public OfferEntity update(OfferEntity offer) {
        return em.merge(offer);
    }

    @Transactional
    public void delete(String id) {
        OfferEntity offer = em.find(OfferEntity.class, id);
        if (offer != null) {
            em.remove(offer);
        }
    }
}
