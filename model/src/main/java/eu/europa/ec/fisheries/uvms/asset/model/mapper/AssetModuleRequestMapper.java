/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.model.mapper;

import java.util.Collection;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.ActivityRulesAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.ActivityRulesAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetListModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.FindAssetHistoriesByCfrModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindAssetHistoriesByCfrModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetGroupListByAssetGuidRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertFishingGearModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGear;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 **/
public class AssetModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(AssetModuleRequestMapper.class);

    public static String createGetAssetModuleRequest(String value, AssetIdType type) throws AssetModelMapperException {
        GetAssetModuleRequest request = new GetAssetModuleRequest();
        request.setMethod(AssetModuleMethod.GET_ASSET);
        request.setId(createAssetId(value, type));
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static AssetId createAssetId(String value, AssetIdType type) throws AssetModelValidationException {
    	if(value == null) {
    		throw new AssetModelValidationException("No id value set");
    	}
    	if(type == null) {
    		throw new AssetModelValidationException("No id type set");
    	}
        AssetId vesseId = new AssetId();
        vesseId.setType(type);
        vesseId.setValue(value);
        return vesseId;
    }

    public static String createAssetListModuleRequest(AssetListQuery query) throws AssetModelMapperException {
        AssetListModuleRequest request = new AssetListModuleRequest();
        request.setMethod(AssetModuleMethod.ASSET_LIST);
        request.setQuery(query);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    /**
     *
     * Creates a request to the module with a list of assetgroups that is used
     * to query for a asset list based on the search criterias
     *
     * @param assetGroups
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException
     */
    public static String createAssetListModuleRequest(List<AssetGroup> assetGroups) throws AssetModelMapperException {
        GetAssetListByAssetGroupsRequest request = new GetAssetListByAssetGroupsRequest();
        request.setMethod(AssetModuleMethod.ASSET_LIST_BY_GROUP);
        request.getGroups().addAll(assetGroups);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    /**
     *
     * @param userName
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException
     */
    public static String createAssetGroupListByUserModuleRequest(String userName) throws AssetModelMapperException {
        AssetGroupListByUserRequest request = new AssetGroupListByUserRequest();
        request.setMethod(AssetModuleMethod.ASSET_GROUP);
        request.setUser(userName);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createAssetGroupListByAssetGuidRequest(String assetGuid) throws AssetModelMapperException {
        GetAssetGroupListByAssetGuidRequest request = new GetAssetGroupListByAssetGuidRequest();
        request.setMethod(AssetModuleMethod.ASSET_GROUP_LIST_BY_ASSET_GUID);
        request.setAssetGuid(assetGuid);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static AssetListCriteriaPair createCriteriaPair(ConfigSearchField key, String value) {
        AssetListCriteriaPair criteria = new AssetListCriteriaPair();
        criteria.setKey(key);
        criteria.setValue(value);
        return criteria;
    }

    public static String createUpsertAssetModuleRequest(Asset asset, String username) throws AssetModelMarshallException {
        UpsertAssetModuleRequest upsertAssetModuleRequest = new UpsertAssetModuleRequest();
        upsertAssetModuleRequest.setMethod(AssetModuleMethod.UPSERT_ASSET);
        upsertAssetModuleRequest.setAsset(asset);
        upsertAssetModuleRequest.setUserName(username);
        return JAXBMarshaller.marshallJaxBObjectToString(upsertAssetModuleRequest);
    }

    public static String createUpsertFishingGearModuleRequest(FishingGear fishingGear, String username) throws AssetModelMarshallException {
        UpsertFishingGearModuleRequest request = new UpsertFishingGearModuleRequest();
        request.setMethod(AssetModuleMethod.FISHING_GEAR_UPSERT);
        request.setUsername(username);
        request.setFishingGear(fishingGear);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFindAssetByCfrRequest(String cfr) throws AssetModelMarshallException {
        FindAssetHistoriesByCfrModuleRequest findAssetByCfrModuleRequest = new FindAssetHistoriesByCfrModuleRequest();
        findAssetByCfrModuleRequest.setMethod(AssetModuleMethod.FIND_ASSET_HISTORIES_BY_CFR);
        findAssetByCfrModuleRequest.setCfr(cfr);
        return JAXBMarshaller.marshallJaxBObjectToString(findAssetByCfrModuleRequest);
    }

    public static String createFindAssetByCfrResponse(Collection<Asset> assets) throws AssetModelMarshallException {
        FindAssetHistoriesByCfrModuleResponse findAssetByCfrModuleResponse = new FindAssetHistoriesByCfrModuleResponse();
        findAssetByCfrModuleResponse.getAssetHistories().addAll(assets);
        return JAXBMarshaller.marshallJaxBObjectToString(findAssetByCfrModuleResponse);
    }

    public static String createActivityRulesAssetModuleRequest(AssetListCriteria criteria) throws AssetModelMarshallException {
        ActivityRulesAssetModuleRequest request = new ActivityRulesAssetModuleRequest();
        request.setMethod(AssetModuleMethod.FIND_ASSET_ACTIVITY_RULES);
        request.getCriteria().addAll(criteria.getCriterias());
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createActivityRulesAssetModuleResponse(Collection<Asset> assets) throws AssetModelMarshallException {
        ActivityRulesAssetModuleResponse response = new ActivityRulesAssetModuleResponse();
        response.getAssetHistories().addAll(assets);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

}