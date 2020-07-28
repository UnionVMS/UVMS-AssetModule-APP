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
package eu.europa.ec.fisheries.uvms.asset.message.consumer.bean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.service.bean.AssetGroupsForAssetEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.AssetIdsForGroupGuidEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.FindAssetByCfrBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.FindAssetsByFacadeEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.FindAssetHistGuidByAssetGuidAndOccurrenceDateBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.FindVesselIdsByAssetHistGuidBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.FindVesselIdsByMultipleAssetHistGuidsBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetGroupEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetGroupListByAssetGuidEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListByAssetGroupEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.PingEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.UpsertAssetMessageEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.UpsertFishingGearsMessageEventBean;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupsForAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetIdsForGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetListModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.BatchAssetListModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindAssetHistGuidByAssetGuidAndOccurrenceDateRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindAssetHistoriesByCfrModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindHistoryOfAssetByCfrFacadeRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindHistoryOfAssetFacadeRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindHistoryOfAssetFacadeResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByAssetHistGuidRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByMultipleAssetHistGuidsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetGroupListByAssetGuidRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertFishingGearModuleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageDriven(mappedName = AssetConstants.QUEUE_ASSET_EVENT, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = AssetConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = AssetConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = AssetConstants.QUEUE_NAME_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = AssetConstants.QUEUE_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = AssetConstants.CONNECTION_FACTORY)
})
public class AssetsMessageConsumerBean implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(AssetsMessageConsumerBean.class);

    @EJB
    private GetAssetEventBean getAssetEventBean;

    @EJB
    private GetAssetListEventBean getAssetListEventBean;

    @EJB
    private GetAssetGroupEventBean getAssetGroupEventBean;

    @EJB
    private GetAssetListByAssetGroupEventBean getAssetListByAssetGroupEventBean;

    @EJB
    private GetAssetGroupListByAssetGuidEventBean getAssetGroupListByAssetGuidEventBean;

    @EJB
    private UpsertAssetMessageEventBean upsertAssetMessageEventBean;

    @EJB
    private UpsertFishingGearsMessageEventBean upsertFishingGearsMessageEventBean;

    @EJB
    private FindAssetByCfrBean findAssetByCfrBean;

    @Inject
    private FindVesselIdsByAssetHistGuidBean findVesselIdsByAssetHistGuidBean;

    @Inject
    private FindVesselIdsByMultipleAssetHistGuidsBean findVesselIdsByMultipleAssetHistGuidBean;

    @Inject
    private FindAssetHistGuidByAssetGuidAndOccurrenceDateBean findAssetHistGuidByAssetGuidAndOccurrenceDateBean;

    @Inject
    private FindAssetsByFacadeEventBean findAssetsByFacadeEventBean;

    @EJB
    private AssetGroupsForAssetEventBean assetGroupsForAssetEventBean;

    @EJB
    private PingEventBean pingEventBean;

    @EJB
    private AssetIdsForGroupGuidEventBean assetIdsForGroupGuidEventBean;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;


    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            AssetModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetModuleRequest.class);
            AssetModuleMethod assetsMethod = request.getMethod();
            LOG.info("Message received in AssetModule with assetsMethod : [ {} ]", assetsMethod);
            switch (assetsMethod) {
                case GET_ASSET:
                    GetAssetModuleRequest getRequest = (GetAssetModuleRequest) request;
                    getAssetEventBean.getAsset(textMessage, getRequest.getId()); // the textMessage is used only for error handling, so letting it pass through for now
                    break;
                case ASSET_LIST:
                    AssetListModuleRequest listRequest = (AssetListModuleRequest) request;
                    AssetMessageEvent listEvent = new AssetMessageEvent(textMessage, listRequest.getQuery());
                    getAssetListEventBean.getAssetList(listEvent);
                    break;
                case BATCH_ASSET_LIST:
                    BatchAssetListModuleRequest batchListRequest = (BatchAssetListModuleRequest) request;
                    AssetMessageEvent batchListEvent = new AssetMessageEvent(textMessage, batchListRequest.getQueryList());
                    getAssetListEventBean.getAssetListBatch(batchListEvent);
                    break;
                case ASSET_GROUP:
                    AssetGroupListByUserRequest groupListRequest = (AssetGroupListByUserRequest) request;
                    AssetMessageEvent assetGroupListEvent = new AssetMessageEvent(textMessage, groupListRequest);
                    getAssetGroupEventBean.getAssetGroupByUserName(assetGroupListEvent);
                    break;
                case ASSET_GROUP_LIST_BY_ASSET_GUID:
                    GetAssetGroupListByAssetGuidRequest getAssetGroupListByAssetGuidRequest = (GetAssetGroupListByAssetGuidRequest) request;
                    AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage, getAssetGroupListByAssetGuidRequest.getAssetGuid());
                    getAssetGroupListByAssetGuidEventBean.getAssetGroupListByAssetEvent(assetMessageEvent);
                    break;
                case ASSET_LIST_BY_GROUP:
                    GetAssetListByAssetGroupsRequest assetListByGroupListRequest = (GetAssetListByAssetGroupsRequest) request;
                    AssetMessageEvent assetListByGroupListEvent = new AssetMessageEvent(textMessage, assetListByGroupListRequest);
                    getAssetListByAssetGroupEventBean.getAssetListByAssetGroups(assetListByGroupListEvent);
                    break;
                case PING:
                    pingEventBean.ping(new AssetMessageEvent(textMessage));
                    break;
                case UPSERT_ASSET:
                    UpsertAssetModuleRequest upsertRequest = (UpsertAssetModuleRequest) request;
                    AssetMessageEvent upsertAssetMessageEvent = new AssetMessageEvent(textMessage, upsertRequest.getAsset(), upsertRequest.getUserName());
                    upsertAssetMessageEventBean.upsertAsset(upsertAssetMessageEvent);
                    break;
                case FISHING_GEAR_UPSERT:
                    UpsertFishingGearModuleRequest upsertFishingGearListModuleRequest = (UpsertFishingGearModuleRequest) request;
                    AssetMessageEvent fishingGearMessageEvent = new AssetMessageEvent(textMessage, upsertFishingGearListModuleRequest.getFishingGear(), upsertFishingGearListModuleRequest.getUsername());
                    upsertFishingGearsMessageEventBean.upsertFishingGears(fishingGearMessageEvent);
                    break;
                    /**TODO: remove if not used **/
                case FIND_ASSET_HISTORIES_BY_CFR:
                    FindAssetHistoriesByCfrModuleRequest findAssetByCfrModuleRequest = (FindAssetHistoriesByCfrModuleRequest) request;
                    findAssetByCfrBean.findAssetByCfr(findAssetByCfrModuleRequest, textMessage);
                    break;
                case FIND_VESSEL_IDS_BY_ASSET_HIST_GUID:
                    FindVesselIdsByAssetHistGuidRequest vesselIdentifiersRequest = (FindVesselIdsByAssetHistGuidRequest) request;
                    findVesselIdsByAssetHistGuidBean.findIdentifiers(textMessage, vesselIdentifiersRequest);
                    break;
                case FIND_VESSEL_IDS_BY_MULTIPLE_ASSET_HIST_GUID:
                    FindVesselIdsByMultipleAssetHistGuidsRequest multipleVesselIdentifiersRequest = (FindVesselIdsByMultipleAssetHistGuidsRequest) request;
                    findVesselIdsByMultipleAssetHistGuidBean.findIdentifiers(textMessage, multipleVesselIdentifiersRequest);
                    break;
                case FIND_ASSET_HIST_GUID_BY_ASSET_GUID_AND_OCCURRENCE_DATE:
                    FindAssetHistGuidByAssetGuidAndOccurrenceDateRequest findAssetHistRequest = (FindAssetHistGuidByAssetGuidAndOccurrenceDateRequest) request;
                    findAssetHistGuidByAssetGuidAndOccurrenceDateBean.findAssetHistGuid(textMessage, findAssetHistRequest);
                    break;
                case ASSET_GROUPS_FOR_ASSET:
                    AssetGroupsForAssetRequest assetGroupsForAssetRequest = (AssetGroupsForAssetRequest) request;
                    AssetMessageEvent assetGroupsForAssetEvent = new AssetMessageEvent(textMessage, assetGroupsForAssetRequest);
                    assetGroupsForAssetEventBean.getAssetGroupsFromAssets(assetGroupsForAssetEvent);
                    break;
                case ASSET_IDS_FOR_GROUP_GUID:
                    AssetIdsForGroupRequest queryElement = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetIdsForGroupRequest.class);
                    assetIdsForGroupGuidEventBean.findAndSendAssetIdsForGroupGuid(new AssetMessageEvent(textMessage,queryElement));
                    break;
                case FIND_HISTORY_OF_ASSET_BY_CFR_FACADE:
                    FindHistoryOfAssetByCfrFacadeRequest findHistoryOfAssetByCfrFacadeRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, FindHistoryOfAssetByCfrFacadeRequest.class);
                    findAssetsByFacadeEventBean.findHistoryOfAssetByCfr(textMessage, findHistoryOfAssetByCfrFacadeRequest);
                    break;
                case FIND_HISTORY_OF_ASSET_FACADE:
                    FindHistoryOfAssetFacadeRequest findHistoryOfAssetFacadeRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, FindHistoryOfAssetFacadeRequest.class);
                    findAssetsByFacadeEventBean.findHistoryOfAsset(textMessage, findHistoryOfAssetFacadeRequest);
                default:
                    LOG.error("[ Not implemented assetsMethod consumed: {} ]", assetsMethod);
                    assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Method not implemented")));
            }

        } catch (AssetModelMarshallException e) {
            LOG.error("[ Error when receiving message in AssetModule. ]");
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Method not implemented")));
        }
    }
}
