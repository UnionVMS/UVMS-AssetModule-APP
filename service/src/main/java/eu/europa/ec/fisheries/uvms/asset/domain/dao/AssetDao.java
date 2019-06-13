package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchFieldType;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.dto.MicroAsset;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.AuditDisjunction;
import org.hibernate.envers.query.criteria.ExtendableCriterion;
import org.hibernate.envers.query.criteria.MatchMode;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class AssetDao {

    @PersistenceContext
    private EntityManager em;

    public Asset createAsset(Asset asset) {
        em.persist(asset);
        return asset;
    }

    public int getNextUnknownShipNumber() {
        Query query = em.createNativeQuery("select nextval('next_unknown_ship_seq')");
        return ((BigInteger) query.getSingleResult()).intValue();
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
        Asset updated = em.merge(asset);
        em.flush();
        return updated;
    }

    public void deleteAsset(Asset asset) {
        em.remove(em.contains(asset) ? asset : em.merge(asset));
    }

    public List<Asset> getAssetListAll() {
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_ALL, Asset.class);
        return query.getResultList();
    }

    public Long getAssetCount(List<SearchKeyValue> searchFields, Boolean isDynamic, boolean includeInactivated) {
        try {
            AuditQuery query = createQuery(searchFields, isDynamic, includeInactivated);
            return (Long) query.addProjection(AuditEntity.id().count()).getSingleResult();
        } catch (AuditException e) {
            return 0L;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, List<SearchKeyValue> searchFields,
                                                   boolean isDynamic, boolean includeInactivated) {
        try {
            AuditQuery query = createQuery(searchFields, isDynamic, includeInactivated);
            query.setFirstResult(pageSize * (pageNumber - 1));
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (AuditException e) {
            return Collections.emptyList();
        }
    }

    private AuditQuery createQuery(List<SearchKeyValue> searchFields, boolean isDynamic, boolean includeInactivated) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query;
        SearchKeyValue dateSearchField = getDateSearchField(searchFields);
        if (dateSearchField != null) {
            Instant date = Instant.parse(dateSearchField.getSearchValues().get(0));
            Number revisionNumberForDate = auditReader.getRevisionNumberForDate(Date.from(date));
            query = auditReader.createQuery().forEntitiesAtRevision(Asset.class, revisionNumberForDate);
        } else {
            query = auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true);

            if (!searchRevisions(searchFields)) {
                query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
            }
            if(!includeInactivated) {
                query.add(AuditEntity.property("active").eq(true));
            }
        }

        ExtendableCriterion operator;
        if (isDynamic) {
            operator = AuditEntity.conjunction();
        } else {
            operator = AuditEntity.disjunction();
        }

        boolean operatorUsed = false;
        for (SearchKeyValue searchKeyValue : searchFields) {
            if (useLike(searchKeyValue)) {
                AuditDisjunction op = AuditEntity.disjunction();
                for (String value : searchKeyValue.getSearchValues()) {
                    op.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ilike(value.replace("*", "%").toLowerCase(), MatchMode.ANYWHERE));
                }
                operatorUsed = true;
                operator.add(op);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ge(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).le(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                operatorUsed = true;
                AuditDisjunction disjunctionOperator = AuditEntity.disjunction();
                for (String v : searchKeyValue.getSearchValuesAsLowerCase()) {
                    disjunctionOperator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ilike(v, MatchMode.ANYWHERE));
                }
                operator.add(disjunctionOperator);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                List<Integer> intValues = searchKeyValue.getSearchValues().stream().map(Integer::parseInt).collect(Collectors.toList());
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(intValues));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                List<UUID> ids = searchKeyValue.getSearchValues().stream().map(UUID::fromString).collect(Collectors.toList());
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(ids));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN) ||
                    searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.STRING)) {
                operatorUsed = true;
                operator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).eq(searchKeyValue.getSearchValues().get(0)));
            }
        }
        if (operatorUsed) {
            query.add((AuditCriterion) operator);
        }
        return query;
    }

    private boolean searchRevisions(List<SearchKeyValue> searchFields) {
        for (SearchKeyValue searchKeyValue : searchFields) {
            if (searchKeyValue.getSearchField().equals(SearchFields.HIST_GUID)) {
                return true;
            }
        }
        return false;
    }

    private SearchKeyValue getDateSearchField(List<SearchKeyValue> searchFields) {
        for (SearchKeyValue searchKeyValue : searchFields) {
            if (searchKeyValue.getSearchField().equals(SearchFields.DATE)) {
                return searchKeyValue;
            }
        }
        return null;
    }

    private boolean useLike(SearchKeyValue entry) {
        for (String searchValue : entry.getSearchValues()) {
            if (searchValue.contains("*")) {
                return true;
            }
        }
        return false;
    }

    public List<Asset> getRevisionsForAsset(UUID id) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Asset> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(Asset.class, id);
        for (Number rev : revisionNumbers) {
            Asset audited = auditReader.find(Asset.class, id, rev);
            resultList.add(audited);
        }
        return resultList;
    }

    public List<Asset> getAssetListByAssetGuids(List<UUID> idList) {
        if(idList.isEmpty()){
            return new ArrayList<>();
        }
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IDS, Asset.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    public List<MicroAsset> getMicroAssetListByAssetGuids(List<UUID> idList) {
        if(idList.isEmpty()){
            return new ArrayList<>();
        }
        TypedQuery<MicroAsset> query = em.createNamedQuery(Asset.ASSET_MICRO_ASSET_BY_LIST, MicroAsset.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    public Asset getAssetFromAssetId(AssetIdentifier assetId, String value) {
        Asset asset;
        switch (assetId) {
            case CFR:
                asset = getAssetByCfr(value);
                break;
            case IRCS:
                asset = getAssetByIrcs(value);
                break;
            case IMO:
                asset = getAssetByImo(value);
                break;
            case MMSI:
                asset = getAssetByMmsi(value);
                break;
            case GUID:
                asset = getAssetById(UUID.fromString(value));
                break;
            case ICCAT:
                asset = getAssetByIccat(value);
                break;
            case UVI:
                asset = getAssetByUvi(value);
                break;
            case GFCM:
                asset = getAssetByGfcm(value);
                break;
            default:
                throw new IllegalArgumentException("Could not create query. Check your code AssetIdType is invalid");
        }
        return asset;
    }

    public Asset getAssetFromAssetIdAtDate(AssetIdentifier assetId, String value, OffsetDateTime date) {
        Asset asset = getAssetFromAssetId(assetId, value);
        if (asset != null) {
            return getAssetAtDate(asset, date);
        } else {
            return null;
        }
    }

    public Asset getAssetAtDate(Asset asset, OffsetDateTime offsetDateTime) {
        Date date = Date.from(offsetDateTime.toInstant());
        AuditReader auditReader = AuditReaderFactory.get(em);
        try {
            return auditReader.find(Asset.class, asset.getId(), date);
        } catch (RevisionDoesNotExistException ex) {
            return getFirstRevision(asset);
        }
    }

    public Asset getFirstRevision(Asset asset) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Number> revisions = auditReader.getRevisions(Asset.class, asset.getId());
        if (!revisions.isEmpty()) {
            return auditReader.find(Asset.class, asset.getId(), revisions.get(0));
        }
        return null;
    }

    public Asset getAssetRevisionForHistoryId(UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        return (Asset) auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true)
                .add(AuditEntity.property("historyId").eq(historyId))
                .getSingleResult();
    }
}
