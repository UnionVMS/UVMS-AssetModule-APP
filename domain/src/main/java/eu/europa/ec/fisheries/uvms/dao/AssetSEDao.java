package eu.europa.ec.fisheries.uvms.dao;

import java.math.BigDecimal;
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
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.asset.types.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldType;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@Stateless
public class AssetSEDao {

    @PersistenceContext
    private EntityManager em;

    public AssetSE createAsset(AssetSE asset) {
        em.persist(asset);
        return asset;
    }

    public AssetSE getAssetById(UUID id) {
        return em.find(AssetSE.class, id);
    }

    public AssetSE getAssetByCfr(String cfr) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_CFR, AssetSE.class);
            query.setParameter("cfr", cfr);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetByIrcs(String ircs) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_IRCS, AssetSE.class);
            query.setParameter("ircs", ircs);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetByImo(String imo) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_IMO, AssetSE.class);
            query.setParameter("imo", imo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetByMmsi(String mmsi) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_MMSI, AssetSE.class);
            query.setParameter("mmsi", mmsi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetByIccat(String iccat) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_ICCAT, AssetSE.class);
            query.setParameter("iccat", iccat);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetByUvi(String uvi) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_UVI, AssetSE.class);
            query.setParameter("uvi", uvi);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetByGfcm(String gfcm) {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_GFCM, AssetSE.class);
            query.setParameter("gfcm", gfcm);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE updateAsset(AssetSE asset) {
        return em.merge(asset);
    }

    public void deleteAsset(AssetSE asset) {
        em.remove(asset);
    }

    public List<AssetSE> getAssetListAll() throws AssetDaoException {
        TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_ALL, AssetSE.class);
        return query.getResultList();
    }

    public Long getAssetCount(List<SearchKeyValue> searchFields, Boolean isDynamic) {
        AuditQuery query = createQuery(searchFields, isDynamic);
        return (Long) query.addProjection(AuditEntity.id().count()).getSingleResult();
    }

    public List<AssetSE> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize,
            List<SearchKeyValue> searchFields, boolean isDynamic) {
        AuditQuery query = createQuery(searchFields, isDynamic);
        query.setFirstResult(pageSize * (pageNumber - 1));
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    private AuditQuery createQuery(List<SearchKeyValue> searchFields, boolean isDynamic) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(AssetSE.class, true, true);

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
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ge(new BigDecimal(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).le(new BigDecimal(searchKeyValue.getSearchValues().get(0))));
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

    public List<AssetSE> getRevisionsForAsset(AssetSE asset) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<AssetSE> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(AssetSE.class, asset.getId());
        for (Number rev : revisionNumbers) {
            AssetSE audited = auditReader.find(AssetSE.class, asset.getId(), rev);
            resultList.add(audited);
        }
        return resultList;
    }

    public AssetSE getAssetAtDate(AssetSE asset, LocalDateTime localDateTime) {
        Date date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        AuditReader auditReader = AuditReaderFactory.get(em);
        return auditReader.find(AssetSE.class, asset.getId(), date);
    }

    public List<AssetSE> getAssetListByAssetGuids(List<UUID> idList) {
        TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_IDS, AssetSE.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    private String assembleQueryString(AssetId assetId) {

        AssetIdTypeEnum assetIdType = assetId.getType();
        String hql = "select ah.asset from AssetSE ah where %s = :keyval ";
        switch (assetIdType) {
            case INTERNAL_ID:
                break;
            case CFR:
                hql = String.format(hql, "ah.cfr");
                break;
            case IRCS:
                hql = String.format(hql, "ah.ircs");
                break;
            case IMO:
                hql = String.format(hql, "ah.imo");
                break;
            case MMSI:
                hql = String.format(hql, "ah.mmsi");
                break;
            case GUID:
                hql = String.format(hql, "ah.guid");
                break;
            case ICCAT:
                hql = String.format(hql, "ah.iccat");
                break;
            case UVI:
                hql = String.format(hql, "ah.uvi");
                break;
            case GFCM:
                hql = String.format(hql, "ah.gfcm");
                break;
            default:
                throw new RuntimeException("Could not create query. Check your code AssetIdType is invalid");
        }
        return hql;
     }

    public AssetSE getAssetFromAssetId(AssetId assetId) {

        String keyval = assetId.getValue();
        String hql = assembleQueryString(assetId);

        TypedQuery<AssetSE> query = em.createQuery(hql, AssetSE.class);
        query.setParameter("keyval", keyval);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetSE getAssetFromAssetIdAtDate(AssetId assetId, LocalDateTime date) {
        AssetSE asset = getAssetFromAssetId(assetId);
        if (asset != null) {
            return getAssetAtDate(asset, date);
        } else {
            return null;
        }
    }

    // TODO should these be moved to appropriate dao:s
    public List<NotesActivityCode> getNoteActivityCodes() {
        throw new IllegalStateException("Not implemented yet!");
    }

    // TODO if when the framework supports querying on specific columns in nnn_AUD table, use that unstead
    public AssetSE getAssetRevisionForHistoryId(AssetSE asset, UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Number> revisionNumbers = auditReader.getRevisions(AssetSE.class, asset.getId());
        for (Number rev : revisionNumbers) {
            AssetSE audited = auditReader.find(AssetSE.class, asset.getId(), rev);
            if (audited.getHistoryId().equals(historyId)) {
                return audited;
            }
        }
        return null;
    }
}
