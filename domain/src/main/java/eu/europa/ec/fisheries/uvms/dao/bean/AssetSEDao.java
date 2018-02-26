package eu.europa.ec.fisheries.uvms.dao.bean;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;

@Stateless
public class AssetSEDao {

    @PersistenceContext
    EntityManager em;

    public AssetSE createAsset(AssetSE asset) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetById(Long id) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByCfr(String cfr) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByIrcs(String ircs) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByGuid(String guid) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByImo(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByMmsi(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE updateAsset(AssetSE asset) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public void deleteAsset(AssetSE assetSE) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public List<AssetSE> getAssetListAll() throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetSEByGuid(String guid) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public Long getAssetCount(String countSql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public List<AssetSE> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public List<AssetSE> getAssetListSearchNotPaginated(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public List<AssetSE> getAssetListByAssetGuids(List<String> assetGuids) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByCfrExcludeArchived(String cfr) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByIrcsExcludeArchived(String ircs) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByImoExcludeArchived(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByMmsiExcludeArchived(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public List<NotesActivityCode> getNoteActivityCodes() { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByIccat(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByUvi(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetByGfcm(String value) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public FlagState getAssetFlagStateByIdAndDate(String  assetGuid, Date date) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}

    public AssetSE getAssetFromAssetIdAndDate(AssetId assetId, Date date) throws AssetDaoException { throw new IllegalStateException("Not implemented yet!");}
}
