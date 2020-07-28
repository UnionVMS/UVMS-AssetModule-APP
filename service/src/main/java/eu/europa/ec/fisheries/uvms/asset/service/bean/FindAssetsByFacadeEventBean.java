/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2020.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import static eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper.createFaultMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;
import java.util.stream.Collectors;

import eu.europa.ec.fisheries.uvms.asset.ejb.client.IAssetFacade;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.wsdl.asset.module.FindHistoryOfAssetByCfrFacadeRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.FindHistoryOfAssetFacadeRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.FindHistoryOfAssetFacadeCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.FindHistoryOfAssetFacadeRequestElement;
import eu.europa.ec.fisheries.wsdl.asset.types.FindHistoryOfAssetFacadeResponseElement;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Slf4j
public class FindAssetsByFacadeEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(FindAssetsByFacadeEventBean.class);

    @Inject
    private AssetMessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    @Inject
    private IAssetFacade assetFacade;

    public void findHistoryOfAssetByCfr(TextMessage jmsMessage, FindHistoryOfAssetByCfrFacadeRequest request) {
        try {
            List<FindHistoryOfAssetFacadeResponseElement> history = request.getCfrIds()
                                                                            .stream()
                                                                            .map(cfr -> createResponseElement(cfr, assetFacade.findHistoryOfAssetByCfr(cfr)))
                                                                            .collect(Collectors.toList());
            String response = AssetModuleResponseMapper.createFindHistoryOfAssetByCfrFacadeResponse(history);
            messageProducer.sendModuleResponseMessageOv(jmsMessage, response);
            log.info("Response sent back to requester on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException  | JMSException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when on findHistoryOfAssetByCfr " + e.getMessage())));
        }
    }

    public void findHistoryOfAsset(TextMessage jmsMessage, FindHistoryOfAssetFacadeRequest request) {
        try {
            List<FindHistoryOfAssetFacadeResponseElement> history = request.getCriteria().stream()
                    .map(this::findAssetWithCriteria)
                    .collect(Collectors.toList());
            String response = AssetModuleResponseMapper.createFindHistoryOfAssetByCfrFacadeResponse(history);
            messageProducer.sendModuleResponseMessageOv(jmsMessage, response);
            log.info("Response sent back to requester on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException  | JMSException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when on findHistoryOfAsset " + e.getMessage())));
        }
    }

    private FindHistoryOfAssetFacadeResponseElement findAssetWithCriteria(FindHistoryOfAssetFacadeRequestElement element) {
        FindHistoryOfAssetFacadeCriteria criteria = element.getCriteria();
        List<Asset> history = assetFacade.findHistoryOfAssetBy(criteria.getReportDate(), criteria.getCfr(), criteria.getRegCountry(), criteria.getIrcs(), criteria.getExtMark(), criteria.getIccat());
        return createResponseElement(element.getId(), history);
    }

    private FindHistoryOfAssetFacadeResponseElement createResponseElement(String cfr, List<Asset> history) {
        FindHistoryOfAssetFacadeResponseElement element = new FindHistoryOfAssetFacadeResponseElement();
        element.setIdentifier(cfr);
        element.getAssets().addAll(history);
        return element;
    }
}
