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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.ExchangeModuleResponseMapper;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

@MessageDriven(mappedName = "jms/queue/UVMSExchangeEvent", activationConfig = {@ActivationConfigProperty(
        propertyName = "messagingType", propertyValue = "javax.jms.MessageListener"), @ActivationConfigProperty(
                propertyName = "destinationType", propertyValue = "javax.jms.Queue"), @ActivationConfigProperty(
                        propertyName = "destination", propertyValue = "UVMSExchangeEvent")})
public class ExchangeModuleMock implements MessageListener {

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        try {
            List<ServiceResponseType> serviceResponse = new ArrayList<ServiceResponseType>();
            ServiceResponseType serviceResponseType = new ServiceResponseType();
            serviceResponseType.setServiceClassName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
            serviceResponseType.setName("Thrane&Thrane");
            serviceResponseType.setSatelliteType("INMARSAT_C");
            serviceResponseType.setActive(true);
            CapabilityListType capabilityList = new CapabilityListType();
            CapabilityType capabilityType = new CapabilityType();
            capabilityType.setType(CapabilityTypeType.POLLABLE);
            capabilityType.setValue("TRUE");
            capabilityList.getCapability().add(capabilityType);

            /*capabilityType = new CapabilityType();
            capabilityType.setType(CapabilityTypeType.CONFIGURABLE);
            capabilityType.setValue("TRUE");
            capabilityList.getCapability().add(capabilityType);
            serviceResponseType.setCapabilityList(capabilityList);*/

            serviceResponse.add(serviceResponseType);
            String response = ExchangeModuleResponseMapper.mapServiceListResponse(serviceResponse);

            new AbstractProducer() {
                @Override
                public String getDestinationName() {
                    return "jms/queue/UVMSMobileTerminal";
                }
            }.sendResponseMessageToSender((TextMessage) message, response);
        } catch (Exception e) {
        }
    }

}
