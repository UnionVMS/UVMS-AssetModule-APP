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

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import static eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper.createFaultMessage;
import static eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper.toAssetIdsForGroupGuidResponseElement;

import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetIdsForGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetIdsForGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdsForGroupGuidQueryElement;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdsForGroupGuidResponseElement;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

@Stateless
@LocalBean
public class AssetIdsForGroupGuidEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(AssetIdsForGroupGuidEventBean.class);

    @EJB
    private AssetMessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetService service;

    public void findAndSendAssetIdsForGroupGuid(AssetMessageEvent assetMessageEvent) {

        try {
            AssetIdsForGroupRequest requestElement = assetMessageEvent.getAssetIdsForGroupRequest();
            TextMessage message = assetMessageEvent.getMessage();

            if (requestElement == null || requestElement.getAssetIdsForGroupGuidQueryElement() == null
                    || StringUtils.isEmpty(requestElement.getAssetIdsForGroupGuidQueryElement().getAssetGuid())) {
                assetErrorEvent.fire(new AssetMessageEvent(message, createFaultMessage(FaultCode.ASSET_MESSAGE, "Error fetching asset ids [ queryElement or assetGuid is null ]")));
                return;
            }

            AssetIdsForGroupGuidQueryElement queryElement = requestElement.getAssetIdsForGroupGuidQueryElement();

            AssetListPagination pagination = queryElement.getPagination();
            AssetIdsForGroupGuidResponseElement responseElement = toAssetIdsForGroupGuidResponseElement(
                    service.findAssetHistoriesByGuidAndOccurrenceDate(queryElement.getAssetGuid(),queryElement.getOccurrenceDate(),pagination.getPage(),pagination.getListSize()));
            AssetIdsForGroupResponse wrapperResponse = new AssetIdsForGroupResponse();
            wrapperResponse.setAssetIdsForGroupGuidResponseElement(responseElement);
            LOG.info(JAXBMarshaller.marshallJaxBObjectToString(wrapperResponse));
            messageProducer.sendModuleResponseMessageOv(message, JAXBMarshaller.marshallJaxBObjectToString(wrapperResponse));
            LOG.info("Response sent back to requestor on queue [ {} ]", message!= null ? message.getJMSReplyTo() : "Null!!!");
        } catch (AssetException  | JMSException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(assetMessageEvent.getMessage(), createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when on findAndSendAssetIdsForGroupGuid " + e.getMessage())));
        }
    }
}
