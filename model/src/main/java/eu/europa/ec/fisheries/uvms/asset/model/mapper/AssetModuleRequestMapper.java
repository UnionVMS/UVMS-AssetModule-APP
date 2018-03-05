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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupWSDL;
import eu.europa.ec.fisheries.wsdl.asset.module.*;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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
    public static String createAssetListModuleRequest(List<AssetGroupWSDL> assetGroups) throws AssetModelMapperException {
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

    public static String createUpsertFishingGearModuleRequest(FishingGearDTO fishingGear, String username) throws AssetModelMarshallException {
        UpsertFishingGearModuleRequest request = new UpsertFishingGearModuleRequest();
        request.setMethod(AssetModuleMethod.FISHING_GEAR_UPSERT);
        request.setUsername(username);
        request.setFishingGear(fishingGear);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFlagStateRequest(String guid, Date date) throws AssetModelMarshallException {


        //DateUtils
        String dateStr = "";
     //   try {
            //dateStr = URLEncoder.encode(parseUTCDateToString(date), "UTF-8");
            dateStr = parseUTCDateToString(date);
      //  } catch (UnsupportedEncodingException e) {
      //      throw new AssetModelMarshallException(e.toString());
      //  }


        GetFlagStateByGuidAndDateRequest request = new GetFlagStateByGuidAndDateRequest();
        request.setAssetGuid(guid);
        request.setDate(dateStr);
        request.setMethod(AssetModuleMethod.GET_FLAGSTATE_BY_ID_AND_DATE);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static String dateToString(Date date) {
        String dateString = null;
        if (date != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            dateString = df.format(date);
        }

        return dateString;
    }

    private static String parseUTCDateToString(Date date) {
        return dateToString(date);
    }


}