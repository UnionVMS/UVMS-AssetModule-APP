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

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;
import java.util.stream.Collectors;

import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByAssetHistGuidResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByMultipleAssetHistGuidsResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertFishingGearModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistGuidIdWithVesselIdentifiers;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponseElement;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGear;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateType;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.VesselIdentifiersHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 **/
public class AssetModuleResponseMapper {

    final static Logger LOG = LoggerFactory.getLogger(AssetModuleResponseMapper.class);

    private static void validateResponse(TextMessage response, String correlationId) throws AssetModelValidationException, JMSException {

        if (response == null) {
            throw new AssetModelValidationException("Error when validating response in ResponseMapper: Reesponse is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new AssetModelValidationException("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new AssetModelValidationException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

        try {
            AssetFault fault = JAXBMarshaller.unmarshallTextMessage(response, AssetFault.class);
            throw new AssetModelValidationException(fault.getCode() + " : " + fault.getFault());
        } catch (AssetModelMarshallException e) {
            //everything is well
        }
    }

    public static Asset mapToAssetFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            GetAssetModuleResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, GetAssetModuleResponse.class);
            return mappedResponse.getAsset();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to single asset response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning asset from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static List<Asset> mapToAssetListFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            ListAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
            return mappedResponse.getAsset();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to list asset response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning assetList from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static List<BatchAssetListResponseElement> mapToBatchAssetListFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            BatchAssetListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, BatchAssetListResponse.class);
            return mappedResponse.getBatchList();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to list asset response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning assetList from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static List<AssetGroup> mapToAssetGroupListFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            ListAssetGroupResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
            return mappedResponse.getAssetGroup();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to list asset response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning assetList from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static String mapToAssetListByAssetGroupResponse(List<Asset> assets) throws AssetModelMarshallException {
        ListAssetResponse response = new ListAssetResponse();
        response.getAsset().addAll(assets);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToAssetGroupListResponse(List<AssetGroup> assetGrup) throws AssetModelMarshallException {
        ListAssetGroupResponse response = new ListAssetGroupResponse();
        response.getAssetGroup().addAll(assetGrup);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapAssetModuleResponse(Asset asset) throws AssetModelMapperException {
        GetAssetModuleResponse response = createGetAssetModuleResponse(asset);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapFlagStateModuleResponse(FlagStateType flagState) throws AssetModelMapperException {
        FlagStateResponse response = new FlagStateResponse();
        response.setFlagState(flagState);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapAssetModuleResponse(ListAssetResponse response) throws AssetModelMapperException {
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    private static GetAssetModuleResponse createGetAssetModuleResponse(Asset asset) {
        GetAssetModuleResponse response = new GetAssetModuleResponse();
        response.setAsset(asset);
        return response;
    }

    public static String mapToBatchListAssetModuleResponse(List<BatchAssetListResponseElement> batchList) throws AssetModelMarshallException {
        BatchAssetListResponse response = new BatchAssetListResponse();
        response.getBatchList().addAll(batchList);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
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

    public static String createUpsertFishingGearModuleResponse(FishingGear fishingGear) throws AssetModelMapperException {
            UpsertFishingGearModuleResponse upsertResponse = new UpsertFishingGearModuleResponse();
            upsertResponse.setFishingGear(fishingGear);
            return JAXBMarshaller.marshallJaxBObjectToString(upsertResponse);
    }

    public static String createFindVesselIdsByAssetHistGuidResponse(Asset asset) throws AssetModelMarshallException {
        FindVesselIdsByAssetHistGuidResponse response = new FindVesselIdsByAssetHistGuidResponse();
        VesselIdentifiersHolder identifiersHolder = assetToVesselIdentifierHolder(asset);
        response.setIdentifiers(identifiersHolder);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createFindVesselIdsByAssetHistGuidResponse(List<Asset> assets) throws AssetModelMarshallException {
        FindVesselIdsByMultipleAssetHistGuidsResponse response = new FindVesselIdsByMultipleAssetHistGuidsResponse();
        List<AssetHistGuidIdWithVesselIdentifiers> identifiers = assets.stream().map(asset -> {
           AssetHistGuidIdWithVesselIdentifiers guidWithIds = new AssetHistGuidIdWithVesselIdentifiers();
           guidWithIds.setAssetHistGuid(asset.getEventHistory().getEventId());
           guidWithIds.setIdentifiers(assetToVesselIdentifierHolder(asset));
           return guidWithIds;
        }).collect(Collectors.toList());
        response.getIdentifiers().addAll(identifiers);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    private static VesselIdentifiersHolder assetToVesselIdentifierHolder(Asset asset) {
        VesselIdentifiersHolder identifiersHolder = new VesselIdentifiersHolder();
        if(asset!=null){
            identifiersHolder.setCfr(asset.getCfr());
            identifiersHolder.setIrcs(asset.getIrcs());
            identifiersHolder.setIccat(asset.getIccat());
            identifiersHolder.setExtMark(asset.getExternalMarking());
            identifiersHolder.setUvi(asset.getUvi());
        }
        return identifiersHolder;
    }
}