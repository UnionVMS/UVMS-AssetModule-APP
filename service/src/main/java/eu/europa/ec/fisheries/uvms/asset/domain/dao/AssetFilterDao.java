package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_FIND_ALL;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_BY_USER;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_BY_GUID;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_GUID_LIST;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;

@Stateless
public class AssetFilterDao {
	@PersistenceContext
    private EntityManager em;


    public AssetFilter createAssetFilter(AssetFilter filter) {
        em.persist(filter);
        return filter;
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
    
    public AssetFilter updateAssetFilter(AssetFilter filter) {
        em.merge(filter);
        return filter;
    }

    public AssetFilter deleteAssetFilter(AssetFilter filter) {
        em.remove(filter);
        return filter;
    }

    public List<AssetFilter> getAssetFilterAll() {
        TypedQuery<AssetFilter> query = em.createNamedQuery(ASSETFILTER_FIND_ALL, AssetFilter.class);
        return query.getResultList();
    }

    public List<AssetFilter> getAssetFilterByUser(String user) {
        TypedQuery<AssetFilter> query = em.createNamedQuery(ASSETFILTER_BY_USER, AssetFilter.class);
        query.setParameter("owner", user);
        return query.getResultList();
    }

    public List<AssetFilter> getAssetFiltersByValueGuidList(List<UUID> guidList) {
        TypedQuery<AssetFilter> query = em.createNamedQuery(ASSETFILTER_GUID_LIST, AssetFilter.class);
        query.setParameter("guidList", guidList);
        return query.getResultList();
    }

}
