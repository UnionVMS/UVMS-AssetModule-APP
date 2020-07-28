package eu.europa.ec.fisheries.uvms.asset.facade;


import eu.europa.ec.fisheries.uvms.asset.ejb.client.IAssetFacade;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Stateless
public class AssetFacade implements IAssetFacade {

    @EJB
    private AssetDao assetDao;

    @EJB
    private AssetDomainModelBean assetDomainModel;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Asset> findHistoryOfAssetByCfr(String cfr) {
        List<Asset> assetList = new ArrayList<>();
        try {
            Asset asset = EntityToModelMapper.toAssetFromEntity(assetDao.getAssetByCfr(cfr));
            assetList.add(asset);
        } catch (AssetDaoException e) {
            log.error("Exception calling getAssetByCfr", e);
        }
        return assetList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Asset> findHistoryOfAssetBy(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat) {
        List<Asset> assets = new ArrayList<>();
        try {
            List<AssetHistory> assetHistories = assetDao._getAssetHistoryByCriteria(reportDate, cfr, regCountry, ircs, extMark, iccat);
            assets = EntityToModelMapper.toAssetFromAssetHistory(assetHistories);
        } catch (AssetDaoException e) {
            log.error("Exception calling _getAssetHistoryByCriteria", e);
        }
        return assets;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ListAssetResponse getAssetList(AssetListQuery query) {
        try {
            log.info("[INFO] Finding ASSET_LIST..");
            GetAssetListResponseDto assetList = assetDomainModel.getAssetList(query);
            ListAssetResponse listAssetResponse = new ListAssetResponse();
            listAssetResponse.setCurrentPage(assetList.getCurrentPage());
            listAssetResponse.setTotalNumberOfPages(assetList.getTotalNumberOfPages());
            listAssetResponse.getAsset().addAll(assetList.getAssetList());
            log.info("[ASSET_LIST END] Response sent back..");
            return listAssetResponse;
        } catch (AssetException e) {
            return new ListAssetResponse();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Asset> getAssetGroup(List<AssetGroup> assetGroupQuery) {
        try {
            log.info("[INFO] Finding ASSET_GROUP..");
            return assetDomainModel.getAssetListByAssetGroup(assetGroupQuery);
        } catch (AssetException e) {
            return new ArrayList();
        }
    }

}
