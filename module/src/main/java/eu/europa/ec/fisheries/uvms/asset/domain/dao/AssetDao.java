package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetRemapMapping;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.ExtendableCriterion;
import org.hibernate.envers.query.criteria.MatchMode;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

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

    public Asset getAssetByNational(Long national) {
        try {
            TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_NATIONAL_ID, Asset.class);
            query.setParameter("nationalId", national);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Asset> getAssetByMmsiOrIrcs(String mmsi, String ircs) {
        String correctedIrcs = ircs != null ? ircs.replace("-", "").replace(" ", "") : ircs;

        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_MMSI_OR_IRCS, Asset.class);
        query.setParameter("mmsi", mmsi);
        query.setParameter("ircs", correctedIrcs);
        return query.getResultList();
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

    public List<String> getAllAvailableVesselTypes() {
        TypedQuery<String> query = em.createNamedQuery(Asset.ASSET_ALL_AVAILABLE_VESSEL_TYPES, String.class);
        return query.getResultList();
    }

    public Long getAssetCount(SearchBranch queryTree, boolean includeInactivated) {
        if (isHistoricSearch(queryTree) == true) {
            return getAssetCountHistoric(queryTree, includeInactivated);
        }
        return getAssetCountCB(queryTree, includeInactivated);
    }

    public Long getAssetCountHistoric(SearchBranch queryTree, boolean includeInactivated) {
        try {
            AuditQuery query = createAuditQuery(queryTree, includeInactivated);
            return (Long) query.addProjection(AuditEntity.id().count()).getSingleResult();
        } catch (AuditException e) {
            return 0L;
        }
    }

    public Long getAssetCountCB(SearchBranch queryTree, boolean includeInactivated) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        Root<Asset> asset = cq.from(Asset.class);

        cq.select(criteriaBuilder.countDistinct(asset));
        Predicate predicateQuery = queryBuilderPredicate(queryTree, criteriaBuilder, asset);

        if (!includeInactivated) {
            Predicate predicateOnlyActive = criteriaBuilder.equal(asset.get("active"), true);
            if (predicateQuery != null) {
                cq.where(criteriaBuilder.and(predicateOnlyActive, predicateQuery));
            } else {
                cq.where(predicateOnlyActive);
            }
        } else {
            if (predicateQuery != null) {
                cq.where(predicateQuery);
            }
        }
        return em.createQuery(cq).getSingleResult();
    }

    public List<Asset> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, SearchBranch queryTree, boolean includeInactivated) {
        if (isHistoricSearch(queryTree)) {
            return getAssetListSearchPaginatedHistoric(pageNumber, pageSize, queryTree, includeInactivated);
        }
        return getAssetListSearchPaginatedCriteriaBuilder(pageNumber, pageSize, queryTree, includeInactivated);
    }

    private List<Asset> getAssetListSearchPaginatedHistoric(Integer pageNumber, Integer pageSize, SearchBranch queryTree, boolean includeInactivated) {
        try {
            AuditQuery query = createAuditQuery(queryTree, includeInactivated);
            query.setFirstResult(pageSize * (pageNumber - 1));
            query.setMaxResults(pageSize);
            return (List<Asset>) query.getResultList();
        } catch (AuditException e) {
            return Collections.emptyList();
        }
    }

    private boolean isHistoricSearch(SearchBranch queryTree) {
        SearchLeaf dateSearchField = getDateSearchField(queryTree);
        SearchLeaf historySearchField = getHistoryIdSearchField(queryTree);
        if (dateSearchField != null || historySearchField != null) {
            return true;
        }
        return false;
    }

    private List<Asset> getAssetListSearchPaginatedCriteriaBuilder(Integer pageNumber, Integer pageSize, SearchBranch queryTree, boolean includeInactivated) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Asset> cq = criteriaBuilder.createQuery(Asset.class);
        cq.distinct(true);
        Root<Asset> asset = cq.from(Asset.class);

        Predicate predicateQuery = queryBuilderPredicate(queryTree, criteriaBuilder, asset);

        if (!includeInactivated) {
            Predicate predicateOnlyActive = criteriaBuilder.equal(asset.get("active"), true);
            if (predicateQuery != null) {
                cq.where(criteriaBuilder.and(predicateOnlyActive, predicateQuery));
            } else {
                cq.where(predicateOnlyActive);
            }
        } else {
            if (predicateQuery != null) {
                cq.where(predicateQuery);
            }
        }
        cq.orderBy(criteriaBuilder.desc(asset.get("updateTime")));
        TypedQuery<Asset> query = em.createQuery(cq);
        query.setFirstResult(pageSize * (pageNumber - 1)); // offset
        query.setMaxResults(pageSize); // limit
        return query.getResultList();
    }
    
    private Predicate queryBuilderPredicate(SearchBranch query, CriteriaBuilder criteriaBuilder, Root<Asset> asset) {
        if (query.getFields() == null || query.getFields().isEmpty() || query.getFields().size() < 1) {
            return null;
        }
        List<Predicate> predicates = new ArrayList<>();

        for (AssetSearchInterface field : query.getFields()) {
            if (!field.isLeaf()) {
                if (!((SearchBranch) field).getFields().isEmpty()) {
                    predicates.add(queryBuilderPredicate((SearchBranch) field, criteriaBuilder, asset));
                }
            } else {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchValue().contains("*")) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    asset.get(
                                            leaf.getSearchField().getFieldName()
                                    )
                            ), "%" + leaf.getSearchValue().replace("*", "%").toLowerCase() + "%"
                            )
                    );
                } else if (leaf.getSearchField().isFuzzySearch()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.function("REPLACE"
                                            , String.class
                                            , criteriaBuilder.function("REPLACE"
                                                    , String.class
                                                    , asset.get(leaf.getSearchField().getFieldName())
                                                    , criteriaBuilder.literal("-")
                                                    , criteriaBuilder.literal("")
                                            )
                                            , criteriaBuilder.literal(" ")
                                            , criteriaBuilder.literal("")
                                    )
                            )
                            , "%" + leaf.getSearchValue().replace(" ", "").replace("-", "").toLowerCase() + "%"
                    ));
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.DECIMAL)) {
                    if (leaf.getOperator().equalsIgnoreCase(">=")) {
                        predicates.add(criteriaBuilder.ge(asset.get(leaf.getSearchField().getFieldName()), Double.valueOf(leaf.getSearchValue())));
                    } else if (leaf.getOperator().equalsIgnoreCase("<=")) {
                        predicates.add(criteriaBuilder.le(asset.get(leaf.getSearchField().getFieldName()), Double.valueOf(leaf.getSearchValue())));
                    } else if (leaf.getOperator().equalsIgnoreCase("!=")) {
                        predicates.add(criteriaBuilder.notEqual(asset.get(leaf.getSearchField().getFieldName()), Double.valueOf(leaf.getSearchValue())));
                    } else {
                        predicates.add(criteriaBuilder.equal(asset.get(leaf.getSearchField().getFieldName()), Double.valueOf(leaf.getSearchValue())));
                    }
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    asset.get(
                                            leaf.getSearchField().getFieldName()
                                    )
                            ),
                            "%" + leaf.getSearchValue().toLowerCase() + "%"));
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    Integer intValue = Integer.parseInt(leaf.getSearchValue());
                    predicates.add(criteriaBuilder.equal(asset.get(leaf.getSearchField().getFieldName()), intValue));
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                    UUID id = UUID.fromString(leaf.getSearchValue());
                    predicates.add(criteriaBuilder.equal(asset.get(leaf.getSearchField().getFieldName()), id));
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) {
                    if (leaf.getSearchField().equals(SearchFields.HAS_MOBILETERMINAL)) {
                        Join<Asset, MobileTerminal> mobileterminal = asset.join(leaf.getSearchField().getFieldName(), JoinType.LEFT);
                        if (Boolean.TRUE.equals(Boolean.valueOf(leaf.getSearchValue()))) {
                            predicates.add(criteriaBuilder.isNotNull(mobileterminal.get("id")));
                        } else {
                            predicates.add(criteriaBuilder.isNull(mobileterminal.get("id")));
                        }
                    } else {
                        predicates.add(criteriaBuilder.equal(asset.get(leaf.getSearchField().getFieldName()), Boolean.valueOf(leaf.getSearchValue())));
                    }
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.STRING)){
                    predicates.add(criteriaBuilder.equal(asset.get(leaf.getSearchField().getFieldName()), leaf.getSearchValue()));
                }
            }
        }
        if (query.isLogicalAnd()) {
            return criteriaBuilder.and(predicates.stream().toArray(Predicate[]::new));
        } else {
            return criteriaBuilder.or(predicates.stream().toArray(Predicate[]::new));
        }
    }

    private AuditQuery createAuditQuery(SearchBranch queryTree, boolean includeInactivated) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        AuditQuery query;
        SearchLeaf dateSearchField = getDateSearchField(queryTree);
        if (dateSearchField != null) {
            Instant date = DateUtils.stringToDate(dateSearchField.getSearchValue());
            Number revisionNumberForDate = auditReader.getRevisionNumberForDate(Date.from(date));
            query = auditReader.createQuery().forEntitiesAtRevision(Asset.class, revisionNumberForDate);
        } else {
            query = auditReader.createQuery().forRevisionsOfEntity(Asset.class, true, true);

            if (!searchRevisions(queryTree)) {
                query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
            }
            if (!includeInactivated) {
                query.add(AuditEntity.property("active").eq(true));
            }
        }

        AuditCriterion auditCriterion = queryBuilder(queryTree);
        if (auditCriterion != null) {
            query.add(auditCriterion);
        }
        return query;
    }

    private AuditCriterion queryBuilder(SearchBranch query) {
        ExtendableCriterion operator;
        boolean operatorUsed = false;
        if (query.isLogicalAnd()) {
            operator = AuditEntity.conjunction();           //and
        } else {
            operator = AuditEntity.disjunction();           //or
        }
        for (AssetSearchInterface field : query.getFields()) {
            if (!field.isLeaf()) {
                AuditCriterion auditCriterion = queryBuilder((SearchBranch) field);
                if (auditCriterion != null) {
                    operator.add(auditCriterion);
                    operatorUsed = true;
                }
            } else {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchValue().contains("*")) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ilike(leaf.getSearchValue().replace("*", "%").toLowerCase(), MatchMode.ANYWHERE));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.DECIMAL)) {
                    Double doubleValue = Double.parseDouble(leaf.getSearchValue());
                    if (leaf.getOperator().equalsIgnoreCase(">=")) {
                        operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ge(doubleValue));
                    } else if (leaf.getOperator().equalsIgnoreCase("<=")) {
                        operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).le(doubleValue));
                    } else if (leaf.getOperator().equalsIgnoreCase("!=")) {
                        operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ne(doubleValue));
                    } else {
                        operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(doubleValue));
                    }
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).ilike(leaf.getSearchValue(), MatchMode.ANYWHERE));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                    UUID id = UUID.fromString(leaf.getSearchValue());
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(id));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) {
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(Boolean.valueOf(leaf.getSearchValue())));
                    operatorUsed = true;
                } else if (leaf.getSearchField().getFieldType().equals(SearchFieldType.STRING)){
                    operator.add(AuditEntity.property(leaf.getSearchField().getFieldName()).eq(leaf.getSearchValue()));
                    operatorUsed = true;
                }

            }
        }
        if (operatorUsed) {
            return (AuditCriterion) operator;
        }
        return null;
    }

    private SearchLeaf getDateSearchField(SearchBranch searchFields) {
        for (AssetSearchInterface field : searchFields.getFields()) {
            if (!field.isLeaf()) {
                SearchLeaf leaf = getDateSearchField((SearchBranch) field);

                if (leaf != null) {
                    return leaf;
                }
            } else {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchField().equals(SearchFields.DATE)) {
                    return leaf;
                }
            }
        }
        return null;
    }

    private SearchLeaf getHistoryIdSearchField(SearchBranch searchFields) {
        for (AssetSearchInterface field : searchFields.getFields()) {
            if (!field.isLeaf()) {
                SearchLeaf leaf = getHistoryIdSearchField((SearchBranch) field);
                if (leaf != null) {
                    return leaf;
                }
            } else {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchField().equals(SearchFields.HIST_GUID)) {
                    return leaf;
                }
            }
        }
        return null;
    }

    private boolean searchRevisions(SearchBranch searchFields) {
        for (AssetSearchInterface field : searchFields.getFields()) {
            if (!field.isLeaf()) {
                boolean leaf = searchRevisions((SearchBranch) field);
                if (leaf) {
                    return true;
                }
            } else {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchField().equals(SearchFields.HIST_GUID)) {
                    return true;
                }
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
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<Asset> query = em.createNamedQuery(Asset.ASSET_FIND_BY_IDS, Asset.class);
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
            case NATIONAL:
                asset = getAssetByNational(Long.parseLong(value));
                break;
            default:
                throw new IllegalArgumentException("Could not create query. Check your code AssetIdType is invalid");
        }
        return asset;
    }

    public Asset getAssetFromAssetIdAtDate(AssetIdentifier assetId, String value, Instant date) {
        Asset asset = getAssetFromAssetId(assetId, value);
        if (asset != null) {
            return getAssetAtDate(asset.getId(), date);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAssetsAtDate(List<UUID> assetIds, Instant date) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        Number revision;
        try {
            revision = auditReader.getRevisionNumberForDate(Date.from(date));
        } catch (RevisionDoesNotExistException ex) {
            revision = 1;
        }
        return auditReader.createQuery()
            .forEntitiesAtRevision(Asset.class, revision)
            .add(AuditEntity.property("id").in(assetIds))
            .getResultList();
    }

    public Asset getAssetAtDate(UUID assetId, Instant instant) {
        Date date = Date.from(instant);
        AuditReader auditReader = AuditReaderFactory.get(em);
        try {
            return auditReader.find(Asset.class, assetId, date);
        } catch (RevisionDoesNotExistException ex) {
            return getFirstRevision(assetId);
        }
    }

    public Asset getFirstRevision(UUID assetId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Number> revisions = auditReader.getRevisions(Asset.class, assetId);
        if (!revisions.isEmpty()) {
            return auditReader.find(Asset.class, assetId, revisions.get(0));
        }
        return null;
    }

    public Asset getAssetRevisionForHistoryId(UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        return (Asset) auditReader.createQuery()
                .forRevisionsOfEntity(Asset.class, true, true)
                .add(AuditEntity.property("historyId").eq(historyId))
                .getSingleResult();
    }

    public AssetRemapMapping createAssetRemapMapping(AssetRemapMapping mapping) {
        em.persist(mapping);
        return mapping;
    }

    public List<AssetRemapMapping> getAllAssetRemappings() {
        Query query = em.createQuery("from AssetRemapMapping", AssetRemapMapping.class);
        return query.getResultList();
    }

    public void deleteAssetMapping(AssetRemapMapping mapping) {
        em.remove(em.contains(mapping) ? mapping : em.merge(mapping));
    }

    public ContactInfo getContactById(UUID contactId) {
        return em.find(ContactInfo.class, contactId);
    }

}
