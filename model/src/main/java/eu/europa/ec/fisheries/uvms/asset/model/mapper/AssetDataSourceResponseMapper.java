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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetMessageValidationExcpetion;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.wsdl.asset.config.Config;
import eu.europa.ec.fisheries.wsdl.asset.config.ConfigResponse;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearListResponse;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearResponse;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.group.SingleAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListGroupByFlagStateResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.SingleAssetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetDataSourceResponseMapper {

    final static Logger LOG = LoggerFactory.getLogger(AssetDataSourceResponseMapper.class);

    private static void validateResponse(TextMessage response, String correlationId) throws JMSException, AssetMessageValidationExcpetion {

        if (response == null) {
            throw new AssetMessageValidationExcpetion("Error when validating response in ResponseMapper: Response is null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new AssetMessageValidationExcpetion("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new AssetMessageValidationExcpetion("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

        try {
            AssetFault fault = JAXBMarshaller.unmarshallTextMessage(response, AssetFault.class);
            throw new AssetMessageValidationExcpetion(fault.getCode() + " : " + fault.getFault());
        } catch (AssetModelMarshallException e) {
            //everything is well
        }
    }

    public static Asset mapToAssetFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            long start = System.currentTimeMillis();
            validateResponse(response, correlationId);
            SingleAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, SingleAssetResponse.class);
            long diff = System.currentTimeMillis() - start;
            LOG.debug("mapToAssetFromResponse: ------ TIME ------ " + diff + "ms");
            return mappedResponse.getAsset();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to transportMeans. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning transportMeans from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static List<Asset> mapToAssetListFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            ListAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
            return mappedResponse.getAsset();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to transportMeans list. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning assetList from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static ListAssetResponse mapToAssetListResponseFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            ListAssetResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
            return mappedResponse;
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to transportMeans list response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning assetList from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static AssetGroup mapToAssetGroupFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            SingleAssetGroupResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, SingleAssetGroupResponse.class);
            return mappedResponse.getAssetGroup();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to single transportMeans group response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning single transportMeans group from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static List<AssetGroup> mapToAssetGroupListFromResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            ListAssetGroupResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
            return mappedResponse.getAssetGroup();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to list transportMeans group response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning transportMeans group list from response in ResponseMapper: " + e.getMessage());
        }
    }

	public static List<Config> mapToConfiguration(TextMessage response, String correlationId) throws AssetModelMapperException {
		try {
            validateResponse(response, correlationId);
            ConfigResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ConfigResponse.class);
            return mappedResponse.getConfig();
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to list transportMeans group response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning transportMeans group list from response in ResponseMapper: " + e.getMessage());
        }
	}
    public static AssetListGroupByFlagStateResponse mapToAssetListGroupByFlagStateResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            AssetListGroupByFlagStateResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, AssetListGroupByFlagStateResponse.class);
            return mappedResponse;
        } catch (AssetModelMarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to transportMeans list response. ] {}", e.getMessage());
            throw new AssetModelMapperException("Error when returning assetList from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static FishingGearResponse mapToUpsertFishingGearResponse(TextMessage response, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(response, correlationId);
            FishingGearResponse upsertFishingGearListResponse = JAXBMarshaller.unmarshallTextMessage(response, FishingGearResponse.class);
            return upsertFishingGearListResponse;
        } catch (JMSException | AssetMessageValidationExcpetion assetMessageValidationExcpetion) {
            LOG.error("FishingGearListResponse text message is not valid", assetMessageValidationExcpetion.getMessage());
            throw new AssetModelMapperException("Could not marshall the response to FishingGearListResponse");
        } catch (AssetModelMarshallException e) {
            LOG.error("Could not marshall the response to FishingGearListResponse", e.getMessage());
            throw new AssetModelMapperException("Could not marshall the response to FishingGearListResponse");
        }
    }

    public static FishingGearListResponse mapToFishingGearListResponse(TextMessage textMessage, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(textMessage, correlationId);
            FishingGearListResponse response = JAXBMarshaller.unmarshallTextMessage(textMessage, FishingGearListResponse.class);
            return response;
        } catch (JMSException | AssetMessageValidationExcpetion assetMessageValidationExcpetion) {
            LOG.error("FishingGearListResponse text message is not valid", assetMessageValidationExcpetion.getMessage());
            throw new AssetModelMapperException("Could not marshall the textMessage to FishingGearListResponse");
        } catch (AssetModelMarshallException e) {
            LOG.error("Could not marshall the textMessage to FishingGearListResponse", e.getMessage());
            throw new AssetModelMapperException("Could not marshall the textMessage to FishingGearListResponse");
        }
    }

    public static FishingGearResponse mapToFishingGearResponse(TextMessage textMessage, String correlationId) throws AssetModelMapperException {
        try {
            validateResponse(textMessage, correlationId);
            FishingGearResponse response = JAXBMarshaller.unmarshallTextMessage(textMessage, FishingGearResponse.class);
            return response;
        } catch (JMSException | AssetMessageValidationExcpetion assetMessageValidationExcpetion) {
            LOG.error("FishingGearResponse text message is not valid", assetMessageValidationExcpetion.getMessage());
            throw new AssetModelMapperException("Could not marshall the textMessage to FishingGearResponse");
        } catch (AssetModelMarshallException e) {
            LOG.error("Could not marshall the textMessage to FishingGearResponse", e.getMessage());
            throw new AssetModelMapperException("Could not marshall the textMessage to FishingGearResponse");
        }
    }
}