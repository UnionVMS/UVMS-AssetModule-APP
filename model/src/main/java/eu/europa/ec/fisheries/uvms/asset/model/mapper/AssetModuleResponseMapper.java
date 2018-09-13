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

import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.*;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.*;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

public class AssetModuleResponseMapper {

    private final static Logger LOG = LoggerFactory.getLogger(AssetModuleResponseMapper.class);

    private static void validateResponse(TextMessage response, String correlationId) throws JMSException {

        if (response == null) {
            throw new NullPointerException("Error when validating response in ResponseMapper: Response is null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new NullPointerException("CorrelationId in response is Null. Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new IllegalArgumentException("Wrong correlationId in response. Expected was: "
                    + correlationId + " and Actual is: " + response.getJMSCorrelationID());
        }

        try {
            AssetFault fault = JAXBMarshaller.unmarshallTextMessage(response, AssetFault.class);
            throw new AssetException(fault.getFault() + " : ", fault.getCode());
        } catch (AssetException e) {
            //everything is well
        }
    }

    public static Asset mapToAssetFromResponse(TextMessage response, String correlationId) throws AssetException {
        try {
            validateResponse(response, correlationId);
            GetAssetModuleResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, GetAssetModuleResponse.class);
            return mappedResponse.getAsset();
        } catch (AssetException | JMSException ex) {
            LOG.error("[ Error when mapping response to single asset response. ] {}", ex);
            throw new AssetException(ErrorCode.ASSET_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_MAPPING_ERROR.getCode());
        }
    }

    public static List<Asset> mapToAssetListFromResponse(TextMessage response, String correlationId) throws AssetException {
        try {
            validateResponse(response, correlationId);
            ListAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
            return mappedResponse.getAsset();
        } catch (AssetException | JMSException ex) {
            LOG.error("[ Error when mapping response to list asset response. ] {}", ex);
            throw new AssetException(ErrorCode.ASSET_LIST_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_LIST_MAPPING_ERROR.getCode());
        }
    }

    public static List<AssetGroup> mapToAssetGroupListFromResponse(TextMessage response, String correlationId) throws AssetException {
        try {
            validateResponse(response, correlationId);
            ListAssetGroupResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
            return mappedResponse.getAssetGroup();
        } catch (AssetException | JMSException ex) {
            LOG.error("[ Error when mapping response to list asset response. ] {}", ex);
            throw new AssetException(ErrorCode.ASSET_GROUP_LIST_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_GROUP_LIST_MAPPING_ERROR.getCode());
        }
    }

    public static String mapToAssetListByAssetGroupResponse(List<Asset> assets) throws AssetException {
        ListAssetResponse response = new ListAssetResponse();
        response.getAsset().addAll(assets);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToAssetGroupListResponse(List<AssetGroup> assetGrup) throws AssetException {
        ListAssetGroupResponse response = new ListAssetGroupResponse();
        response.getAssetGroup().addAll(assetGrup);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapAssetModuleResponse(Asset asset) throws AssetException {
        GetAssetModuleResponse response = createGetAssetModuleResponse(asset);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapFlagStateModuleResponse(FlagStateType flagState) throws AssetException {

        FlagStateResponse response = new FlagStateResponse();
        response.setFlagState(flagState);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapAssetModuleResponse(ListAssetResponse response) throws AssetException {
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    private static GetAssetModuleResponse createGetAssetModuleResponse(Asset asset) {
        GetAssetModuleResponse response = new GetAssetModuleResponse();
        response.setAsset(asset);
        return response;
    }

    public static AssetFault createFaultMessage(FaultCode code, String message) {
        AssetFault fault = new AssetFault();
        fault.setCode(code.getCode());
        fault.setFault(message);
        return fault;
    }

    public static UpsertAssetModuleResponse createUpsertAssetModuleResponse(Asset asset){
        UpsertAssetModuleResponse response = new UpsertAssetModuleResponse();
        response.setAsset(asset);
        return response;
    }

    public static UpsertAssetModuleResponse createUpsertAssetListModuleResponse(Asset asset){
        UpsertAssetModuleResponse response = new UpsertAssetModuleResponse();
        response.setAsset(asset);
        return response;
    }

    public static String createUpsertFishingGearModuleResponse(FishingGear fishingGear) throws AssetException {
            UpsertFishingGearModuleResponse upsertResponse = new UpsertFishingGearModuleResponse();
            upsertResponse.setFishingGear(fishingGear);
            return JAXBMarshaller.marshallJaxBObjectToString(upsertResponse);
    }
}
