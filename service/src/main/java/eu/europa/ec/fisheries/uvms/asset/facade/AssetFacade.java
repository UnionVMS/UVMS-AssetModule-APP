package eu.europa.ec.fisheries.uvms.asset.facade;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.List;
import eu.europa.ec.fisheries.uvms.asset.ejb.client.IAssetFacade;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
public class AssetFacade implements IAssetFacade {

    @EJB
    private AssetDao assetDao;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Asset> findHistoryOfAssetByCfr(String cfr) {
        List<Asset> assetList = new ArrayList<>();
        try {
            Asset asset =  EntityToModelMapper.toAssetFromEntity(assetDao.getAssetByCfr(cfr));
            assetList.add(asset);
        }  catch (AssetDaoException e) {
            log.error("Exception calling getAssetByCfr", e);
        }
        return assetList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Asset> findHistoryOfAssetBy(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat) {
        List<Asset> assets = new ArrayList<>();
        try {
            List<AssetHistory> assetHistories  =  assetDao._getAssetHistoryByCriteria(reportDate, cfr, regCountry, ircs, extMark, iccat);
            assets = EntityToModelMapper.toAssetFromAssetHistory(assetHistories);
        } catch (AssetDaoException e) {
            log.error("Exception calling _getAssetHistoryByCriteria", e);
        }
        return assets;
    }



}
