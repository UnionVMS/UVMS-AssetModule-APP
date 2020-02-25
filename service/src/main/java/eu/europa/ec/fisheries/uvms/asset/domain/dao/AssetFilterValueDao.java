package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

@Stateless
public class AssetFilterValueDao {
	 	
		@PersistenceContext
	    private EntityManager em;

	    public AssetFilterValue create(AssetFilterValue value) {
	        em.persist(value);
	        return value;
	    }

	    public AssetFilterValue get(UUID id) {
	        try {
	            TypedQuery<AssetFilterValue> query = em.createNamedQuery(AssetFilterValue.ASSETFILTER_VALUE_GETBYID,
	            		AssetFilterValue.class);
	            query.setParameter("id", id);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	    }

	    public AssetFilterValue update(AssetFilterValue value) {
	        return em.merge(value);
	    }

	    public AssetFilterValue delete(AssetFilterValue value) {
	        em.remove(value);
	        return value;
	    }

	    public List<AssetFilterValue> retrieveValuesForFilterQuery(AssetFilterQuery assetFilterquery) {
	        TypedQuery<AssetFilterValue> query = em.createNamedQuery(AssetFilterValue.ASSETFILTER_RETRIEVE_VALUES_FOR_QUERY,
	        		AssetFilterValue.class);
	        query.setParameter("assetFilterquery", assetFilterquery);
	        return query.getResultList();
	    }

}
