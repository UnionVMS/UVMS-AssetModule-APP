package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldType;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Stateless
public class AssetSEDao {

    @PersistenceContext
    private EntityManager em;

    public AssetSE createAsset(AssetSE asset)   throws AssetDaoException {

        try {
            em.persist(asset);
            return asset;
        }catch(Exception e){
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetById(UUID id)  throws AssetDaoException {

        try {
        return em.find(AssetSE.class, id);
        }catch(Exception e){
            throw new AssetDaoException(e.toString(), e);
        }

    }

    public AssetSE getAssetByCfr(String cfr)  throws AssetDaoException {

        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_CFR, AssetSE.class);
            query.setParameter("cfr", cfr);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetByIrcs(String ircs) throws AssetDaoException {

        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_IRCS, AssetSE.class);
            query.setParameter("ircs", ircs);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetByImo(String imo) throws AssetDaoException {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_IMO, AssetSE.class);
            query.setParameter("imo", imo);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetByMmsi(String mmsi) throws AssetDaoException {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_MMSI, AssetSE.class);
            query.setParameter("mmsi", mmsi);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetByIccat(String iccat) throws AssetDaoException
    {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_ICCAT, AssetSE.class);
            query.setParameter("iccat", iccat);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetByUvi(String uvi) throws AssetDaoException
    {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_UVI, AssetSE.class);
            query.setParameter("uvi", uvi);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetByGfcm(String gfcm) throws AssetDaoException
    {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_GFCM, AssetSE.class);
            query.setParameter("gfcm", gfcm);
            AssetSE rs = query.getSingleResult();
            return rs;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE updateAsset(AssetSE asset) throws AssetDaoException {

        try {
            return em.merge(asset);
        } catch (Exception e) {
            throw new AssetDaoException("[ update asset, id: " + asset.getId() + " ] " + e.getMessage(), e);
        }

    }

    public void deleteAsset(AssetSE assetSE)  throws AssetDaoException{

        try {
        em.remove(assetSE);
        } catch (Exception e) {
            throw new AssetDaoException("[ remove asset, id: " + assetSE.getId() + " ] " + e.getMessage(), e);
        }

    }

    public List<AssetSE> getAssetListAll() throws AssetDaoException {

        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_ALL, AssetSE.class);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            throw new AssetDaoException("[ get all asset ] " + e.getMessage(), e);
        }


    }

    private boolean useLike(SearchKeyValue entry) {
        for (String searchValue : entry.getSearchValues()) {
            if (searchValue.contains("*")) {
                return true;
            }
        }
        return false;
    }


    private <T> TypedQuery<T>  createQuery(String theSQL, List<SearchKeyValue> searchFields, Class<T> resultClass){

        TypedQuery<T> query = em.createQuery(theSQL, resultClass);

        for (SearchKeyValue field : searchFields) {
            if (useLike(field)) {
                int containsCount = 0;
                for (String searchValue : field.getSearchValues()) {
                    containsCount++;
                    String tmpValue = searchValue.replace("*", "");
                    query.setParameter(field.getSearchField().getValueName() + containsCount, "%" + tmpValue + "%");
                }
            } else {
                if (field.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    List<Integer> parameter = new ArrayList<>();
                    for (String param : field.getSearchValues()) {
                        parameter.add(Integer.parseInt(param));
                    }
                    query.setParameter(field.getSearchField().getValueName(), parameter);
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                    query.setParameter(field.getSearchField().getValueName(), field.getSearchValues());
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) { //BOOLEAN only one value
                    query.setParameter(field.getSearchField().getValueName(), Boolean.parseBoolean(field.getSearchValues().get(0)));
                } else { //DECIMAL, only one value
                    query.setParameter(field.getSearchField().getValueName(), new BigDecimal(field.getSearchValues().get(0)));
                }
            }
        }

        return query;
    }



    public Long getAssetCount(String countSql, List<SearchKeyValue> searchFields) throws AssetDaoException {

        TypedQuery<Long> query = createQuery(countSql, searchFields, Long.class);
        try {
            return query.getSingleResult();
        }catch(Exception e){
            throw new AssetDaoException("[ get all asset ] " + e.getMessage(), e);
        }

    }

    public List<AssetSE> getRevisionsForAsset(AssetSE asset) throws AssetDaoException {

        try {
            AuditReader auditReader = AuditReaderFactory.get(em);
            List<AssetSE> resultList = new ArrayList<>();

            List<Number> revisionNumbers = auditReader.getRevisions(AssetSE.class, asset.getId());
            for (Number rev : revisionNumbers) {
                AssetSE audited = auditReader.find(AssetSE.class, asset.getId(), rev);
                resultList.add(audited);
            }
            return resultList;
        }catch(Exception e){
            throw new AssetDaoException("[ get all asset ] " + e.getMessage(), e);
        }
    }

    private  Date asDate(LocalDateTime localDate) {
        Instant instant = localDate.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        return date;
    }

    public AssetSE getAssetAtDate(AssetSE asset, LocalDateTime theDate ) throws AssetDaoException {

        try {
            Date date = asDate(theDate);
            AuditReader auditReader = AuditReaderFactory.get(em);
            AssetSE audited = auditReader.find(AssetSE.class, asset.getId(), date);
            return audited;
        }catch(Exception e){
            throw new AssetDaoException("[ get all asset ] " + e.getMessage(), e);
        }
    }

    public List<AssetSE> getAssetListByAssetGuids(List<UUID> idList) throws AssetDaoException {
        try {
            TypedQuery<AssetSE> query = em.createNamedQuery(AssetSE.ASSET_FIND_BY_IDS, AssetSE.class);
            query.setParameter("idList", idList);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            throw new AssetDaoException("[ get all asset ] " + e.getMessage());
        }
    }

    private String assembleQueryString(AssetId assetId) throws AssetDaoException {

        AssetIdType assetIdType = assetId.getType();
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
                throw new AssetDaoException("Could not create query. Check your code AssetIdType is invalid");
        }
        return hql;
     }

    public AssetSE getAssetFromAssetId(AssetId assetId) throws AssetDaoException {

        String keyval = assetId.getValue();
        String hql = assembleQueryString(assetId);

        TypedQuery<AssetSE> query = em.createQuery(hql,AssetSE.class);
        query.setParameter("keyval", keyval);
        try {
            AssetSE asset = query.getSingleResult();
            return asset;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new AssetDaoException(e.toString(), e);
        }
    }

    public AssetSE getAssetFromAssetIdAtDate(AssetId assetId, LocalDateTime date) throws AssetDaoException {

        AssetSE asset = getAssetFromAssetId(assetId);
        if(asset != null) {
            return getAssetAtDate(asset, date);
        }else{
            throw new AssetDaoException("Internal error");
        }

    }


    public List<AssetSE> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {


        // TODO also add searchFields Logic

        TypedQuery<AssetSE> query = em.createQuery(sql, AssetSE.class);
        query.setFirstResult(pageSize * (pageNumber - 1));
        query.setMaxResults(pageSize);
        return query.getResultList();
    }


    // TODO should these be moved to appropriate dao:s
    public List<NotesActivityCode> getNoteActivityCodes() {
        throw new IllegalStateException("Not implemented yet!");
    }



}
