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

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupsForAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetGroupsForAssetResponseElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;


@Stateless
public class AssetGroupsForAssetEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(AssetGroupsForAssetEventBean.class);

    @EJB
    private AssetMessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetService service;

    public void getAssetGroupsFromAssets(AssetMessageEvent message) {
        TextMessage jmsMessage = message.getMessage();
        AssetGroupsForAssetRequest assetGroupsForAssetRequest = message.getAssetGroupsForAssetRequest();

        if (assetGroupsForAssetRequest == null) {
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupsForAssetRequest [ Request is null ]")));
            return;
        }

        try {
            List<AssetGroupsForAssetResponseElement> assetGroupsForAssets = service.findAssetGroupsForAssets(assetGroupsForAssetRequest.getAssetGroupsForAssetQueryElement());
            messageProducer.sendModuleResponseMessageOv(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupsForAssetResponse(assetGroupsForAssets));
            LOG.info("Response sent back to request on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (JMSException | AssetException e) {
            LOG.error("Error when getting JMS response from source. ",e);
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupsForAssetEventBean [ " + e.getMessage())));
        }
    }
}
