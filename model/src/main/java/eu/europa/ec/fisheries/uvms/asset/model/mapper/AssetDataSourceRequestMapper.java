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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.wsdl.asset.config.ConfigField;
import eu.europa.ec.fisheries.wsdl.asset.config.ConfigRequest;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearByIdRequest;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearListRequest;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.UpsertFishingGearRequest;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.group.CreateAssetGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.group.DeleteAssetGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.group.GetAssetGroupListByAssetGuidRequest;
import eu.europa.ec.fisheries.wsdl.asset.group.GetAssetGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.group.UpdateAssetGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.history.AssetHistoryListByAssetIdRequest;
import eu.europa.ec.fisheries.wsdl.asset.history.GetAssetHistoryRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.AssetListByAssetGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.AssetListGroupByFlagStateRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.AssetListRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.CreateAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.GetAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.UpdateAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.source.UpsertAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetDataSourceMethod;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGear;
import eu.europa.ec.fisheries.wsdl.asset.types.SingleAssetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import eu.europa.ec.fisheries.wsdl.transportMeans.module.AssetListGroupByFlagStateRequest;

/**
 **/
public class AssetDataSourceRequestMapper {
    
    final static Logger LOG = LoggerFactory.getLogger(AssetDataSourceRequestMapper.class);
    
    private static GetAssetRequest createGetAssetRequest() {
        GetAssetRequest request = new GetAssetRequest();
        request.setMethod(AssetDataSourceMethod.GET);
        return request;
    }
    
    private static CreateAssetRequest createCreateAssetRequest() {
        CreateAssetRequest request = new CreateAssetRequest();
        request.setMethod(AssetDataSourceMethod.CREATE);
        return request;
    }
    
    private static UpdateAssetRequest createUpdateAssetRequest() {
        UpdateAssetRequest request = new UpdateAssetRequest();
        request.setMethod(AssetDataSourceMethod.UPDATE);
        return request;
    }
    
    private static AssetListRequest createAssetListRequest() {
        AssetListRequest request = new AssetListRequest();
        request.setMethod(AssetDataSourceMethod.LIST);
        return request;
    }
    
    private static CreateAssetGroupRequest createCreateAssetGroupRequest() {
        CreateAssetGroupRequest request = new CreateAssetGroupRequest();
        request.setMethod(AssetDataSourceMethod.GROUP_CREATE);
        return request;
    }
    
    private static UpdateAssetGroupRequest createUpdateAssetGroupRequest() {
        UpdateAssetGroupRequest request = new UpdateAssetGroupRequest();
        request.setMethod(AssetDataSourceMethod.GROUP_UPDATE);
        return request;
    }
    
    private static GetAssetGroupRequest createGetAssetGroupRequest() {
        GetAssetGroupRequest request = new GetAssetGroupRequest();
        request.setMethod(AssetDataSourceMethod.GROUP_GET);
        return request;
    }
    
    private static AssetGroupListByUserRequest createAssetGroupListByUserRequest() {
        AssetGroupListByUserRequest request = new AssetGroupListByUserRequest();
        request.setMethod(AssetDataSourceMethod.GROUP_LIST);
        return request;
    }
    
    private static AssetListByAssetGroupRequest createGetAssetListByAssetGroupRequest() {
        AssetListByAssetGroupRequest request = new AssetListByAssetGroupRequest();
        request.setMethod(AssetDataSourceMethod.LIST_GET_BY_GROUP);
        return request;
    }
    
    private static DeleteAssetGroupRequest createDeleteAssetGroupRequest() {
        DeleteAssetGroupRequest request = new DeleteAssetGroupRequest();
        request.setMethod(AssetDataSourceMethod.GROUP_DELETE);
        return request;
    }
    
    private static UpsertAssetRequest createUpsertAssetRequest() {
        UpsertAssetRequest request = new UpsertAssetRequest();
        request.setMethod(AssetDataSourceMethod.UPSERT);
        return request;
    }
    
    private static SingleAssetResponse createSingleAssetResponse(Asset asset) {
        SingleAssetResponse response = new SingleAssetResponse();
        response.setAsset(asset);
        return response;
    }
    
    private static AssetId createAssetId(String value, AssetIdType type) {
        AssetId vesseId = new AssetId();
        vesseId.setType(type);
        vesseId.setValue(value);
        return vesseId;
    }
    
    private static AssetHistoryId createHistoryAssetId(String guid) throws AssetModelMapperException {
        try {
            AssetHistoryId histId = new AssetHistoryId();
            histId.setEventId(guid);
            return histId;
        } catch (NullPointerException e) {
            LOG.error("[ Error when creating history asset ID. ] {}", e.getMessage());
            throw new AssetModelMapperException(e.getMessage());
        }
    }

    /**
     * Marshalls a AssetId to a String representing the WSDL request This
     * method only applies when getting a single Assets history event by the
     * eventId
     *
     * @param assetHistoryGuid
     * @return
     * @throws AssetModelMapperException
     */
    public static String mapGetAssetHistoryByGuid(String assetHistoryGuid) throws AssetModelMapperException {
        try {
            GetAssetHistoryRequest request = new GetAssetHistoryRequest();
            request.setMethod(AssetDataSourceMethod.HISTORY_GET);
            request.setId(createHistoryAssetId(assetHistoryGuid));
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset history by ID. ] {}", e.getMessage());
            throw new AssetModelMapperException(e.getMessage());
        }
    }

    /**
     * Marshalls a Assethistory Event ID to a String representing the WSDL
     * request This method only applies when getting a single assets history
     * event by the eventId
     *
     * @param assetIdGuid
     * @return
     * @throws AssetModelMapperException
     */
    public static String mapGetAssetHistoryListByAssetId(String assetIdGuid, Integer maxNbr) throws AssetModelMapperException {
        AssetHistoryListByAssetIdRequest request = new AssetHistoryListByAssetIdRequest();
        request.setMethod(AssetDataSourceMethod.HISTORY_LIST);
        request.setMaxNbr(maxNbr);
        request.setAssetId(createAssetId(assetIdGuid, AssetIdType.GUID));
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapGetAssetList(AssetListQuery query) throws AssetModelMapperException {
        AssetListRequest request = createAssetListRequest();
        AssetListQuery mappedQuery = mapQuery(query);
        request.setQuery(mappedQuery);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    private static AssetListQuery mapQuery(AssetListQuery query) throws AssetModelValidationException {
        AssetListQuery mappedQuery = new AssetListQuery();
        mappedQuery.setPagination(validatePagination(query.getPagination()));
        if (query.getAssetSearchCriteria() != null) {
            AssetListCriteria criteria = new AssetListCriteria();
            criteria.setIsDynamic(query.getAssetSearchCriteria().isIsDynamic());
            List<AssetListCriteriaPair> pairs = mapCriterias(query.getAssetSearchCriteria().getCriterias());
            criteria.getCriterias().addAll(pairs);
            mappedQuery.setAssetSearchCriteria(criteria);
        }
        return mappedQuery;
    }
    
    private static AssetListPagination validatePagination(AssetListPagination pagination) throws AssetModelValidationException {
        if(pagination == null){
            throw new AssetModelValidationException("Cannot get asset list because pagination is null");
        }
    	if(pagination.getListSize() < 1) throw new AssetModelValidationException("Page list size must be > 1");
    	if(pagination.getPage() < 1) throw new AssetModelValidationException("Page must be > 1");
    	return pagination;
    }

    private static List<AssetListCriteriaPair> mapCriterias(List<AssetListCriteriaPair> criterias) throws AssetModelValidationException {
        List<AssetListCriteriaPair> mappedCriterias = new ArrayList<>();
        for (AssetListCriteriaPair pair : criterias) {
            if (pair.getKey() == ConfigSearchField.ASSET_TYPE) {
                if(!"ASSET".equalsIgnoreCase(pair.getValue())) {
                    throw new AssetModelValidationException("Can only search for asset type ASSET");
                }
            }
            else {
                mappedCriterias.add(pair);
            }
        }

        return mappedCriterias;
    }

    /**
     * Marshalls a asset to a String representing the WSDL request This method
     * only applies when creating a asset
     *
     * @param asset
     * @return
     * @throws AssetModelMapperException
     */
    public static String mapCreateAsset(Asset asset, String username) throws AssetModelMapperException {
        AssetDataSourceRequestValidator.validateCreateAsset(asset);
        CreateAssetRequest request = createCreateAssetRequest();
        request.setUsername(username);
        request.setAsset(asset);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    /**
     * Marshalls a Asset to a String representing the WSDL request This method
     * only applies when updating an existing a asset
     *
     * @param asset
     * @return
     * @throws AssetModelMapperException
     */
    public static String mapUpdateAsset(Asset asset, String username) throws AssetModelMapperException {
        AssetDataSourceRequestValidator.validateUpdateAsset(asset);
        UpdateAssetRequest request = createUpdateAssetRequest();
        request.setUsername(username);
        request.setAsset(asset);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    /**
     * Marshalls a asset to a String representing the WSDL request This method
     * only applies when getting a asset by id
     *
     * @param id
     * @param idType
     * @return
     * @throws AssetModelMapperException
     */
    public static String mapGetAssetById(String id, AssetIdType idType) throws AssetModelMapperException {
        GetAssetRequest request = createGetAssetRequest();
        request.setId(createAssetId(id, idType));
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapCreateAssetGroup(AssetGroup assetGroup, String username) throws AssetModelMapperException {
        CreateAssetGroupRequest request = createCreateAssetGroupRequest();
        request.setUsername(username);
        request.setAssetGroup(assetGroup);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapUpdateAssetGroup(AssetGroup assetGroup, String username) throws AssetModelMapperException {
        UpdateAssetGroupRequest request = createUpdateAssetGroupRequest();
        request.setUsername(username);
        request.setAssetGroup(assetGroup);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapGetAssetGroupById(String id) throws AssetModelMapperException {
        GetAssetGroupRequest request = createGetAssetGroupRequest();
        request.setGuid(id);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapAssetGroupListByUserRequest(String user) throws AssetModelMapperException {
        AssetGroupListByUserRequest request = createAssetGroupListByUserRequest();
        request.setUser(user);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapRemoveAssetGroupById(String id, String username) throws AssetModelMapperException {
        DeleteAssetGroupRequest request = createDeleteAssetGroupRequest();
        request.setUsername(username);
        request.setGuid(id);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapUpsertAsset(Asset asset, String username) throws AssetModelMapperException {
        AssetDataSourceRequestValidator.validateCreateAsset(asset);
        UpsertAssetRequest request = createUpsertAssetRequest();
        request.setUsername(username);
        request.setAsset(asset);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String mapGetAssetListByAssetGroupRequest(List<AssetGroup> groups) throws AssetModelMapperException {
        AssetListByAssetGroupRequest request = createGetAssetListByAssetGroupRequest();
        request.getGroups().addAll(groups);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

	public static String mapGetAllConfiguration() throws AssetModelMapperException {
		ConfigRequest request = new ConfigRequest();
		request.setMethod(AssetDataSourceMethod.CONFIG);
		request.setConfig(ConfigField.ALL);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}

    public static String mapAssetGroupListByAssetGuidRequest(String assetGuid) throws AssetModelMarshallException {
        GetAssetGroupListByAssetGuidRequest request = createAssetGroupListByAssetRequest();
        request.setAssetGuid(assetGuid);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static GetAssetGroupListByAssetGuidRequest createAssetGroupListByAssetRequest() {
        GetAssetGroupListByAssetGuidRequest request = new GetAssetGroupListByAssetGuidRequest();
        request.setMethod(AssetDataSourceMethod.GROUP_LIST_BY_ASSET_GUID);
        return request;
    }

    public static String mapGetAssetListGroupByFlagStateRequest(List assetIds) throws AssetModelMapperException {
        AssetListGroupByFlagStateRequest assetListGroupByFlagStateRequest = new AssetListGroupByFlagStateRequest();
        assetListGroupByFlagStateRequest.setMethod(AssetDataSourceMethod.ASSET_LIST_GROUP_BY_FLAGSTATE);
        assetListGroupByFlagStateRequest.getAssetIds().addAll(assetIds);
        return JAXBMarshaller.marshallJaxBObjectToString(assetListGroupByFlagStateRequest);
    }

    public static String mapUpsertFishingGearRequest(FishingGear fishingGear, String username) throws AssetModelMarshallException {
        UpsertFishingGearRequest upsertFishingGearListRequest = new UpsertFishingGearRequest();
        upsertFishingGearListRequest.setMethod(AssetDataSourceMethod.FISHING_GEAR_UPSERT);
        upsertFishingGearListRequest.setUsername(username);
        upsertFishingGearListRequest.setFishingGear(fishingGear);
        return JAXBMarshaller.marshallJaxBObjectToString(upsertFishingGearListRequest);
    }

    public static String mapFishingGearByIdRequest(Long id, String username) throws AssetModelMarshallException {
        FishingGearByIdRequest request = new FishingGearByIdRequest();
        request.setMethod(AssetDataSourceMethod.FISHING_GEAR_BY_ID);
        request.setUsername(username);
        request.setId(BigInteger.valueOf(id));
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String mapFishingGearByExternalIdRequest(Long id, String username) throws AssetModelMarshallException {
        FishingGearByIdRequest request = new FishingGearByIdRequest();
        request.setMethod(AssetDataSourceMethod.FISHING_GEAR_BY_EXT_ID);
        request.setUsername(username);
        request.setId(BigInteger.valueOf(id));
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String mapFishingGearListRequest(String username) throws AssetModelMarshallException {
        FishingGearListRequest request = new FishingGearListRequest();
        request.setMethod(AssetDataSourceMethod.FISHING_GEAR_LIST);
        request.setUsername(username);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }


}