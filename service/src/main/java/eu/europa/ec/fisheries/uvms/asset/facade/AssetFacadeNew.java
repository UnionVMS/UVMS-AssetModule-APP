/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.facade;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

@ApplicationScoped
@Slf4j
public class AssetFacadeNew {

    @Inject
    private AssetDao assetDao;

    @Inject
    private AssetDomainModelBean assetDomainModel;


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
