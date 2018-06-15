///*
//﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
//© European Union, 2015-2016.
//
//This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
//redistribute it and/or modify it under the terms of the GNU General Public License as published by the
//Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
//the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
//copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
// */
//package eu.europa.ec.fisheries.uvms.asset.model.mapper;
//
//import eu.europa.ec.fisheries.uvms.asset.model.exception.*;
//import eu.europa.ec.fisheries.wsdl.asset.config.Config;
//import eu.europa.ec.fisheries.wsdl.asset.config.ConfigResponse;
//import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearListResponse;
//import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearResponse;
//import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
//import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
//import eu.europa.ec.fisheries.wsdl.asset.group.SingleAssetGroupResponse;
//import eu.europa.ec.fisheries.wsdl.asset.types.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.jms.JMSException;
//import javax.jms.TextMessage;
//import java.util.List;
//
//public class AssetDataSourceResponseMapper {
//
//    private final static Logger LOG = LoggerFactory.getLogger(AssetDataSourceResponseMapper.class);
//
//    private static void validateResponse(TextMessage response, String correlationId) throws JMSException {
//
//        if (response == null) {
//            throw new NullPointerException("Error when validating response in ResponseMapper: Response is null");
//        }
//        if (response.getJMSCorrelationID() == null) {
//            throw new NullPointerException("CorrelationId in response is Null. Expected was: " + correlationId);
//        }
//        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
//            throw new IllegalArgumentException("Wrong correlationId in response. Expected was: "
//                    + correlationId + " and Actual is: " + response.getJMSCorrelationID());
//        }
//        try {
//            AssetFault fault = JAXBMarshaller.unmarshallTextMessage(response, AssetFault.class);
//            throw new AssetException(fault.getFault() + " : ", fault.getCode());
//        } catch (AssetException e) {
//            //everything is well
//        }
//    }
//
//    public static Asset mapToAssetFromResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            long start = System.currentTimeMillis();
//            validateResponse(response, correlationId);
//            SingleAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, SingleAssetResponse.class);
//            long diff = System.currentTimeMillis() - start;
//            LOG.debug("mapToAssetFromResponse: ------ TIME ------ " + diff + "ms");
//            return mappedResponse.getAsset();
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to asset. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.ASSET_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_MAPPING_ERROR.getCode());
//        }
//    }
//
//    public static List<Asset> mapToAssetListFromResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            validateResponse(response, correlationId);
//            ListAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
//            return mappedResponse.getAsset();
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to asset list. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.ASSET_LIST_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_LIST_MAPPING_ERROR.getCode());
//        }
//    }
//
//    public static ListAssetResponse mapToAssetListResponseFromResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            validateResponse(response, correlationId);
//            return JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to asset list response. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.ASSET_LIST_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_LIST_MAPPING_ERROR.getCode());
//        }
//    }
//
//    public static AssetGroup mapToAssetGroupFromResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            validateResponse(response, correlationId);
//            SingleAssetGroupResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, SingleAssetGroupResponse.class);
//            return mappedResponse.getAssetGroup();
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to single asset group response. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.ASSET_GROUP_MAPPING_ERROR.getMessage(), ex, ErrorCode.ASSET_GROUP_MAPPING_ERROR.getCode());
//        }
//    }
//
//    public static List<AssetGroup> mapToAssetGroupListFromResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            validateResponse(response, correlationId);
//            ListAssetGroupResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
//            return mappedResponse.getAssetGroup();
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to list asset group response. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.ASSET_GROUP_LIST_MAPPING_ERROR.getMessage(), ex,
//                    ErrorCode.ASSET_GROUP_LIST_MAPPING_ERROR.getCode());
//        }
//    }
//
//	public static List<Config> mapToConfiguration(TextMessage response, String correlationId) throws AssetException {
//		try {
//            validateResponse(response, correlationId);
//            ConfigResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ConfigResponse.class);
//            return mappedResponse.getConfig();
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to list asset group response. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.CONFIG_LIST_MAPPING_ERROR.getMessage(), ex,
//                    ErrorCode.CONFIG_LIST_MAPPING_ERROR.getCode());
//        }
//	}
//    public static AssetListGroupByFlagStateResponse mapToAssetListGroupByFlagStateResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            validateResponse(response, correlationId);
//            return JAXBMarshaller.unmarshallTextMessage(response, AssetListGroupByFlagStateResponse.class);
//        } catch (AssetException | JMSException ex) {
//            LOG.error("[ Error when mapping response to asset list response. ] {}", ex.getMessage());
//            throw new AssetException(ErrorCode.ASSET_GROUP_LIST_MAPPING_ERROR.getMessage(), ex,
//                    ErrorCode.ASSET_GROUP_LIST_MAPPING_ERROR.getCode());
//        }
//    }
//
//    public static FishingGearResponse mapToFishingGearResponse(TextMessage response, String correlationId) throws AssetException {
//        try {
//            validateResponse(response, correlationId);
//            return JAXBMarshaller.unmarshallTextMessage(response, FishingGearResponse.class);
//        } catch (JMSException | AssetException ex) {
//            LOG.error("FishingGearListResponse text message is not valid", ex.getMessage());
//            throw new AssetException(ErrorCode.FISHING_GEAR_RESPONSE_UNMARSHALLER_ERROR.getMessage(), ex,
//                    ErrorCode.FISHING_GEAR_RESPONSE_UNMARSHALLER_ERROR.getCode());
//        }
//    }
//
//    public static FishingGearListResponse mapToFishingGearListResponse(TextMessage textMessage, String correlationId) throws AssetException {
//        try {
//            validateResponse(textMessage, correlationId);
//            return JAXBMarshaller.unmarshallTextMessage(textMessage, FishingGearListResponse.class);
//        } catch (JMSException | AssetException ex) {
//            LOG.error("FishingGearListResponse text message is not valid", ex.getMessage());
//            throw new AssetException(ErrorCode.FISHING_GEAR_LIST_RESPONSE_UNMARSHALLER_ERROR.getMessage(), ex,
//                    ErrorCode.FISHING_GEAR_LIST_RESPONSE_UNMARSHALLER_ERROR.getCode());
//        }
//    }
//}