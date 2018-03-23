package eu.europa.ec.fisheries.uvms.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.AuditDisjunction;
import org.hibernate.envers.query.criteria.ExtendableCriterion;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldType;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@Stateless
public class AssetDao {

    @PersistenceContext
    private EntityManager em;

    public Asset createAsset(Asset asset) {
        em.persist(asset);
        return asset;
    }

    public Asset getAssetById(UUID id) {
        return em.find(Asset.class, id);
    }

    public Asset getAssetByCfr(String cfr) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_CFR, Asset.class);
            query.setParameter("cfr", cfr);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByIrcs(String ircs) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IRCS, Asset.class);
            query.setParameter("ircs", ircs);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByImo(String imo) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IMO, Asset.class);
            query.setParameter("imo", imo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByMmsi(String mmsi) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_MMSI, Asset.class);
            query.setParameter("mmsi", mmsi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByIccat(String iccat) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_ICCAT, Asset.class);
            query.setParameter("iccat", iccat);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByUvi(String uvi) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_UVI, Asset.class);
            query.setParameter("uvi", uvi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset getAssetByGfcm(String gfcm) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_GFCM, Asset.class);
            query.setParameter("gfcm", gfcm);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Asset updateAsset(Asset asset) {
        return em.merge(asset);
    }

    public void deleteAsset(Asset asset) {
        em.remove(asset);
    }

    public List<Asset> getAssetListAll() {
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_ALL, Asset.class);
        return query.getResultList();
    }

    public Long getAssetCount(List<SearchKeyValue> searchFields, Boolean isDynamic) {
        AuditQuery query = createQuery(searchFields, isDynamic);
        return (Long) query.addProjection(AuditEntity.id().count()).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize,
                                                   List<SearchKeyValue> searchFields, boolean isDynamic) {
        AuditQuery query = createQuery(searchFields, isDynamic);
        query.setFirstResult(pageSize * (pageNumber - 1));
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    private AuditQuery createQuery(List<SearchKeyValue> searchFields, boolean isDynamic) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true);

        if (!searchRevisions(searchFields)) {
            query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
        }

        ExtendableCriterion operator;
        if (isDynamic) {
            operator = AuditEntity.conjunction();
        } else {
            operator = AuditEntity.disjunction();
        }

        for (SearchKeyValue searchKeyValue : searchFields) {
            if (useLike(searchKeyValue)) {
                AuditDisjunction op = AuditEntity.disjunction();
                for (String value : searchKeyValue.getSearchValues()) {
                    op.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).like("%" + value.replace("*", "") + "%"));
                }
                operator.add(op);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ge(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).le(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(searchKeyValue.getSearchValues()));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                List<Integer> intValues = searchKeyValue.getSearchValues().stream().map(Integer::parseInt).collect(Collectors.toList());
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(intValues));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                List<UUID> ids = searchKeyValue.getSearchValues().stream().map(UUID::fromString).collect(Collectors.toList());
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(ids));
            } else { // Boolean
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).eq(searchKeyValue.getSearchValues().get(0)));
            }
        }
        query.add((AuditCriterion) operator);
        return query;
    }

    private boolean searchRevisions(List<SearchKeyValue> searchFields) {
        return searchFields.stream().filter(s -> s.getSearchField().equals(SearchFields.HIST_GUID)).count() > 0;
    }

    private boolean useLike(SearchKeyValue entry) {
        for (String searchValue : entry.getSearchValues()) {
            if (searchValue.contains("*")) {
                return true;
            }
        }
        return false;
    }

    public List<Asset> getRevisionsForAsset(Asset asset) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Asset> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(Asset.class, asset.getId());
        for (Number rev : revisionNumbers) {
            Asset audited = auditReader.find(Asset.class, asset.getId(), rev);
            resultList.add(audited);
        }
        return resultList;
    }

    public List<Asset> getAssetListByAssetGuids(List<UUID> idList) {
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IDS, Asset.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    public Asset getAssetFromAssetId(AssetId assetId) {
        Asset asset = null;
        switch (assetId.getType()) {
            case INTERNAL_ID:
                break;
            case CFR:
                asset = getAssetByCfr(assetId.getValue());
                break;
            case IRCS:
                asset = getAssetByIrcs(assetId.getValue());
                break;
            case IMO:
                asset = getAssetByImo(assetId.getValue());
                break;
            case MMSI:
                asset = getAssetByMmsi(assetId.getValue());
                break;
            case GUID:
                asset = getAssetById(UUID.fromString(assetId.getValue()));
                break;
            case ICCAT:
                asset = getAssetByIccat(assetId.getValue());
                break;
            case UVI:
                asset = getAssetByUvi(assetId.getValue());
                break;
            case GFCM:
                asset = getAssetByGfcm(assetId.getValue());
                break;
            default:
                throw new IllegalArgumentException("Could not create query. Check your code AssetIdType is invalid");
        }
        return asset;
    }

    public Asset getAssetFromAssetIdAtDate(AssetId assetId, LocalDateTime date) {
        Asset asset = getAssetFromAssetId(assetId);
        if (asset != null) {
            return getAssetAtDate(asset, date);
        } else {
            return null;
        }
    }
    
    public Asset getAssetAtDate(Asset asset, LocalDateTime localDateTime) {
        Date date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        AuditReader auditReader = AuditReaderFactory.get(em);
        return auditReader.find(Asset.class, asset.getId(), date);
    }

    public Asset getAssetRevisionForHistoryId(UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        return (Asset) auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true)
                .add(AuditEntity.property("historyId").eq(historyId))
                .getSingleResult();
    }
}
