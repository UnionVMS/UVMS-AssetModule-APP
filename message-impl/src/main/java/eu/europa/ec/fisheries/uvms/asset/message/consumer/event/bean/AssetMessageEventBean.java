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
package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AssetModelMapper;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.PingResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

@Stateless
public class AssetMessageEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMessageEventBean.class);

    @Inject
    private AssetService assetService;

    @Inject
    private AssetGroupService assetGroup;

    @Inject
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAsset(TextMessage textMessage, AssetId assetId) {

        Asset asset = null;
        boolean messageSent = false;

        try {
            AssetIdentifier assetIdentity = AssetModelMapper.mapToAssetIdentity(assetId.getType());
            asset = assetService.getAssetById(assetIdentity, assetId.getValue());
        } catch (Exception e) {
            LOG.error("Error when getting asset by id", assetId.getValue(), e);
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset by id : " + assetId.getValue() + " Error message: " + e.getMessage())));
            messageSent = true;
            asset = null;
        }

        if (!messageSent) {
            try {
                String response = AssetModuleResponseMapper.mapAssetModuleResponse(AssetModelMapper.toAssetModel(asset));
                messageProducer.sendModuleResponseMessage(textMessage, response);
            } catch (AssetModelMapperException e) {
                LOG.error("[ Error when mapping asset ] ");
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e.getMessage())));
            }
        }
    }
    
    public void getAssetList(AssetMessageEvent message) {
        try {
            AssetListQuery query = message.getQuery();
            List<SearchKeyValue> searchValues = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria().getCriterias());
            int page = query.getPagination().getPage();
            int listSize = query.getPagination().getListSize();
            Boolean dynamic = query.getAssetSearchCriteria().isIsDynamic();
            
            
            AssetListResponse assetList = assetService.getAssetList(searchValues, page, listSize, dynamic);
            
            ListAssetResponse response = AssetModelMapper.toListAssetResponse(assetList); 
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(response));
        } catch (AssetException e) {
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist: " + e.getMessage())));
        }
    }

    public void getAssetGroupByUserName(AssetMessageEvent message) {
        LOG.info("Get asset group");
        try {
            AssetGroupListByUserRequest request = message.getRequest();
            List<eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup> assetGroups = assetGroup.getAssetGroupList(request.getUser());
            List<AssetGroup> response = assetGroups.stream().map(AssetModelMapper::toAssetGroupModel).collect(Collectors.toList());

            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }
    
    public void getAssetGroupListByAssetEvent(AssetMessageEvent message) {
        LOG.info("Get asset group by asset guid");
        try {
            List<eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup> assetGroups = assetGroup.getAssetGroupListByAssetId(UUID.fromString(message.getAssetGuid()));
            List<AssetGroup> response = assetGroups.stream().map(AssetModelMapper::toAssetGroupModel).collect(Collectors.toList());
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }
    
    public void getAssetListByAssetGroups(AssetMessageEvent message) {
        try {
            GetAssetListByAssetGroupsRequest request = message.getAssetListByGroup();

            if (request == null) {
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetListByVesselGroups [ Request is null ]")));
                return;
            }
            List<eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup> assetGroupModels = request.getGroups().stream().map(AssetModelMapper::toAssetGroupEntity).collect(Collectors.toList());
            List<Asset> assets = assetService.getAssetListByAssetGroups(assetGroupModels);

            List<eu.europa.ec.fisheries.wsdl.asset.types.Asset> assetModels = assets.stream().map(AssetModelMapper::toAssetModel).collect(Collectors.toList());
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetListByAssetGroupResponse(assetModels));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetListByVesselGroups [ " + e.getMessage())));
        }
    }
    
    public void ping(AssetMessageEvent message) {
        try {
            PingResponse pingResponse = new PingResponse();
            pingResponse.setResponse("pong");
            messageProducer.sendModuleResponseMessage(message.getMessage(), JAXBMarshaller.marshallJaxBObjectToString(pingResponse));
        } catch (AssetModelMarshallException e) {
            LOG.error("[ Error when marshalling ping response ]");
        }
    }
    
    public void upsertAsset(AssetMessageEvent message){
        try {
            Asset assetEntity = AssetModelMapper.toAssetEntity(message.getAsset());
            assetService.upsertAsset(assetEntity, "");
        } catch (Exception e) {
            LOG.error("Could not update asset in the local database");
        }
    }
}
