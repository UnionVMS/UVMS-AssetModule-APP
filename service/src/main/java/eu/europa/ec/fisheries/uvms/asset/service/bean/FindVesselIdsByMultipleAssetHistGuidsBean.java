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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByMultipleAssetHistGuidsRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Slf4j
public class FindVesselIdsByMultipleAssetHistGuidsBean {

    private final static Logger LOG = LoggerFactory.getLogger(FindVesselIdsByMultipleAssetHistGuidsBean.class);

    @Inject
    private AssetMessageProducer messageProducer;

    @Inject
    AssetHistoryService service;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void findIdentifiers(TextMessage jmsMessage, FindVesselIdsByMultipleAssetHistGuidsRequest request) {
        try {
            List<Asset> assets = service.getAssetHistoriesByAssetHistGuids(request.getAssetHistoryGuids());
            String response = AssetModuleResponseMapper.createFindVesselIdsByAssetHistGuidResponse(assets);
            messageProducer.sendModuleResponseMessageOv(jmsMessage, response);
            log.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException | JMSException e) {
            LOG.error("Error when getting vessel identifiers by asset history guid.",e);
            assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting vessel identifiers by asset history guid [ " + e.getMessage())));
        }
    }

}
