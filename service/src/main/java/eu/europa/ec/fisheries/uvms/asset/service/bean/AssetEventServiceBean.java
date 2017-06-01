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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.*;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.service.AssetEventService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.FishingGearService;
import eu.europa.ec.fisheries.uvms.asset.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.asset.service.property.ParameterKey;
import eu.europa.ec.fisheries.uvms.bean.FishingGearDomainModelBean;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearListResponse;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearResponse;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.PingResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;

@Stateless
public class AssetEventServiceBean implements AssetEventService {

    final static Logger LOG = LoggerFactory.getLogger(AssetEventServiceBean.class);

    @EJB
    AssetService service;

    @EJB
    ParameterService parameters;

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetGroupService assetGroup;

    @EJB
    private FishingGearService fishingGearService;

//    @EJB(lookup = ServiceConstants.DB_ACCESS_FISHING_GEAR_DOMAIN_MODEL)
    @EJB
    private FishingGearDomainModelBean fishingGearDomainModel;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Override
    public void getAsset(@Observes @GetAssetMessageEvent AssetMessageEvent message) {
        LOG.info("Getting asset.");
        AssetDataSourceQueue dataSource = null;
        Asset asset = null;
        boolean messageSent = false;

        try {
            dataSource = decideDataflow(message.getAssetId());
            LOG.debug("Got message to AssetModule, Executing Get asset from datasource {}", dataSource.name());
            asset = service.getAssetById(message.getAssetId(), dataSource);
        } catch (AssetException e) {
            LOG.error("[ Error when getting asset from source {}. ] ", dataSource.name());
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset from source : " + dataSource.name() + " Error message: " + e.getMessage())));
            messageSent = true;
            asset = null;
        }

        if (asset != null && !dataSource.equals(AssetDataSourceQueue.INTERNAL)) {
            try {
                Asset upsertedAsset = service.upsertAsset(asset, dataSource.name());
                asset.getAssetId().setGuid(upsertedAsset.getAssetId().getGuid());
            } catch (AssetException e) {
                LOG.error("[ Couldn't upsert asset in internal ]");
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, e.getMessage())));
                messageSent = true;
            }
        }

        if (!messageSent) {
            try {
                messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(asset));
            } catch (AssetModelMapperException e) {
                LOG.error("[ Error when mapping asset ] ");
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e.getMessage())));
            }
        }
    }

    private AssetDataSourceQueue decideDataflow(AssetId assetId) throws AssetServiceException {

        try {
            // If search is made by guid, no other source is relevant
            if (AssetIdType.GUID.equals(assetId.getType())) {
                return AssetDataSourceQueue.INTERNAL;
            }

            Boolean xeu = parameters.getBooleanValue(ParameterKey.EU_USE.getKey());
            Boolean national = parameters.getBooleanValue(ParameterKey.NATIONAL_USE.getKey());
            LOG.debug("Settings for dataflow are: XEU: {0} NATIONAL: {1}", new Object[]{xeu, national});
            if (xeu && national) {
                return AssetDataSourceQueue.NATIONAL;
            }
            if (national) {
                return AssetDataSourceQueue.NATIONAL;
            } else if (xeu) {
                return AssetDataSourceQueue.XEU;
            } else {
                return AssetDataSourceQueue.INTERNAL;
            }
        } catch (ConfigServiceException e) {
            LOG.error("[ Error when deciding data flow. ] ");
            throw new AssetServiceException(e.getMessage());
        }

    }

    @Override
    public void getAssetList(@Observes @GetAssetListMessageEvent AssetMessageEvent message) {
        LOG.info("Get asset list");
        try {
            ListAssetResponse response = service.getAssetList(message.getQuery());

            LOG.debug("Send back assetlist response.");
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetlist from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist [ " + e.getMessage())));
        }
    }

    @Override
    public void getAssetGroupByUserName(@Observes @GetAssetGroupEvent AssetMessageEvent message) {
        LOG.info("Get asset group");
        try {
            AssetGroupListByUserRequest request = message.getRequest();
            List<AssetGroup> response = assetGroup.getAssetGroupList(request.getUser());

            LOG.debug("Send back assetGroupList response.");
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }

    @Override
    public void getAssetGroupListByAssetEvent(@Observes @GetAssetGroupListByAssetGuidEvent AssetMessageEvent message) {
        LOG.info("Get asset group by asset guid");
        try {
            List<AssetGroup> response = assetGroup.getAssetGroupListByAssetGuid(message.getAssetGuid());
            LOG.debug("Send back assetGroupList response.");
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }

    @Override
    public void getAssetListByAssetGroups(@Observes @GetAssetListByAssetGroupEvent AssetMessageEvent message) {
        LOG.info("Get asset list by asset groups");
        try {
            GetAssetListByAssetGroupsRequest request = message.getAssetListByGroup();

            if (request == null) {
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetListByVesselGroups [ Request is null ]")));
                return;
            }

            List<Asset> response = service.getAssetListByAssetGroups(request.getGroups());

            LOG.debug("Send back Asset List by Groups response from Module.");
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetListByAssetGroupResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetListByVesselGroups [ " + e.getMessage())));
        }
    }

    @Override
    public void ping(@Observes @PingEvent AssetMessageEvent message) {
        try {
            PingResponse pingResponse = new PingResponse();
            pingResponse.setResponse("pong");
            messageProducer.sendModuleResponseMessage(message.getMessage(), JAXBMarshaller.marshallJaxBObjectToString(pingResponse));
        } catch (AssetModelMarshallException e) {
            LOG.error("[ Error when marshalling ping response ]");
        }
    }

    @Override
    public void upsertAsset(@Observes @UpsertAssetMessageEvent AssetMessageEvent message){
            try {
                service.upsertAsset(message.getAsset(), AssetDataSourceQueue.INTERNAL.name());
                LOG.error("########## Update asset in the local database");
            } catch (AssetException e) {
                LOG.error("Could not update asset in the local database");
            }
    }

    @Override
    public void upsertFishingGears(@Observes @UpsertFishingGearsMessageEvent AssetMessageEvent messageEvent){
        fishingGearDomainModel.upsertFishingGear(messageEvent.getFishingGear(), messageEvent.getUsername());
    }
}