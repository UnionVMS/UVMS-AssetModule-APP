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
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.*;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import lombok.extern.slf4j.Slf4j;

import static eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper.toAssetIdsForGroupGuidResponseElement;

@ApplicationScoped
@Slf4j
public class AssetFacadeNew {

    @Inject
    private AssetDao assetDao;

    @Inject
    private AssetDomainModelBean assetDomainModel;

    @Inject
    private AssetHistoryService assetHistoryService;

    @Inject
    private AssetService assetService;


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
    public List<Asset> findHistoryOfAssetBy(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat, String uvi) {
        List<Asset> assets = new ArrayList<>();
        try {
            List<AssetHistory> assetHistories = assetDao._getAssetHistoryByCriteria(reportDate, cfr, regCountry, ircs, extMark, iccat, uvi);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FindVesselIdsByAssetHistGuidResponse findHistoryOfAssetsByGuids(FindVesselIdsByAssetHistGuidRequest request) {
        try {
            Asset asset = assetHistoryService.getAssetHistoryByAssetHistGuid(request.getAssetHistoryGuid());
            FindVesselIdsByAssetHistGuidResponse response =
                    AssetModuleResponseMapper.mapFindVesselIdsByAssetHistGuidResponse(asset);
            log.info("[INFO] Finding history of multiple assets by their guids..");
            return response;
        } catch (AssetException e) {
            return new FindVesselIdsByAssetHistGuidResponse();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FindVesselIdsByMultipleAssetHistGuidsResponse findHistoriesOfAssetsByGuids(
            FindVesselIdsByMultipleAssetHistGuidsRequest request) {
        log.info("[INFO] Finding history asset by its guid..");
        try {
            List<Asset> assets = assetHistoryService.getAssetHistoriesByAssetHistGuids(request.getAssetHistoryGuids());
            AssetModuleResponseMapper.createFindVesselIdsByAssetHistGuidResponse(assets);
            return AssetModuleResponseMapper.mapFindVesselIdsByAssetHistGuidResponse(assets);
        } catch (AssetException e) {
            e.printStackTrace();
            return new FindVesselIdsByMultipleAssetHistGuidsResponse();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FindAssetHistGuidByAssetGuidAndOccurrenceDateResponse findHistoryOfAssetsByGuidAndDate(FindAssetHistGuidByAssetGuidAndOccurrenceDateRequest request) {
        try {
            Asset asset = assetHistoryService.getAssetHistoryByAssetIdAndOccurrenceDate(request.getAssetGuid(), request.getOccurrenceDate());
            FindAssetHistGuidByAssetGuidAndOccurrenceDateResponse response =
                    AssetModuleResponseMapper.mapFindAssetHistGuidByAssetGuidAndOccurrenceDateResponse(asset);
            log.info("[INFO] Finding history of asset by its guid and date..");
            return response;
        } catch (AssetException e) {
            return new FindAssetHistGuidByAssetGuidAndOccurrenceDateResponse();
        }
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AssetGroupsForAssetResponse findAssetGroupsForAsset(AssetGroupsForAssetRequest request) {
        try {
            List<AssetGroupsForAssetResponseElement> assetGroupsForAssets =
                    assetService.findAssetGroupsForAssets(request.getAssetGroupsForAssetQueryElement());
            AssetGroupsForAssetResponse response =
                    AssetModuleResponseMapper.mapToAssetGroupsForResponse(assetGroupsForAssets);
            log.info("[INFO] Finding group for asset..");
            return response;
        } catch (AssetException e) {
            return new AssetGroupsForAssetResponse();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AssetIdsForGroupGuidResponseElement findAssetIdentifiersForGroupGuid(AssetIdsForGroupRequest request) {
        try {
            AssetIdsForGroupGuidQueryElement queryElement = request.getAssetIdsForGroupGuidQueryElement();

            AssetListPagination pagination = queryElement.getPagination();
            AssetIdsForGroupGuidResponseElement responseElement = toAssetIdsForGroupGuidResponseElement(
                    assetService.findAssetHistoriesByGuidAndOccurrenceDate(queryElement.getAssetGuid(),
                            queryElement.getOccurrenceDate(),pagination.getPage(),pagination.getListSize()));
            log.info("[INFO] Finding asset identifiers for group guid..");
            return responseElement;
        } catch (AssetException e) {
            return new AssetIdsForGroupGuidResponseElement();
        }
    }
}
