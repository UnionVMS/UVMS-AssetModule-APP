package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_BY_GUID;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

@Stateless
public class AssetFilterQueryDao {
	@PersistenceContext
    private EntityManager em;

    public AssetFilterQuery create(AssetFilterQuery query) {
        em.persist(query);
        return query;
    }

    public AssetFilterQuery get(UUID id) {
        try {
            TypedQuery<AssetFilterQuery> query = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_QUERY_GETBYID,
            		AssetFilterQuery.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetFilterQuery update(AssetFilterQuery qvalue) {
        return em.merge(qvalue);
    }

    public AssetFilterQuery delete(AssetFilterQuery qvalue) {
        em.remove(qvalue);
        return qvalue;
    }

    public List<AssetFilterQuery> retrieveFilterQuerysForAssetFilter(AssetFilter assetFilter) {
        TypedQuery<AssetFilterQuery> query = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER,
        		AssetFilterQuery.class);
        query.setParameter("assetFilter", assetFilter);
        return query.getResultList();
    }
    
	public void removeValuesFromFilterQuery(AssetFilterQuery assetFilterQuery) {
		Query qry = em.createNamedQuery(AssetFilterValue.ASSETFILTER_VALUE_CLEAR);
        qry.setParameter("assetFilterQuery", assetFilterQuery);
        qry.executeUpdate();
	}

	public void removeQuerysFromFilter(AssetFilter assetFilter) {
		Query qry = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_QUERY_CLEAR);
        qry.setParameter("assetFilter", assetFilter);
        qry.executeUpdate();
	}
	
	public AssetFilterQuery getAssetFilterQueryByGuid(UUID assetFilterQueryId) {
		 try {
	            TypedQuery<AssetFilterQuery> query = em.createNamedQuery(AssetFilterQuery.ASSETFILTER_QUERY_BY_GUID, AssetFilterQuery.class);
	            query.setParameter("guid", assetFilterQueryId);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	}
	
	public AssetFilterValue getAssetFilterValueByGuid(UUID assetFilterValueId) {
		 try {
	            TypedQuery<AssetFilterValue> query = em.createNamedQuery(AssetFilterValue.ASSETFILTER_VALUE_GETBYID, AssetFilterValue.class);
	            query.setParameter("id", assetFilterValueId);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	}
	public AssetFilter getAssetFilterByGuid(UUID filterId) {
		try {
			TypedQuery<AssetFilter> query = em.createNamedQuery(ASSETFILTER_BY_GUID, AssetFilter.class);
	            query.setParameter("guid", filterId);
	            return query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	    }
}
