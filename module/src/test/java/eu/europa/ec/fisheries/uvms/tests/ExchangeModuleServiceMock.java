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
package eu.europa.ec.fisheries.uvms.tests;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.json.bind.Jsonb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;

@Startup
@Singleton
public class ExchangeModuleServiceMock {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeModuleServiceMock.class);

    @Inject
    @JMSConnectionFactory("java:/ConnectionFactory")
    JMSContext context;

    @Resource(mappedName = "java:/" + MessageConstants.EVENT_STREAM_TOPIC)
    private Topic destination;

    private Jsonb jsonb = new JsonBConfigurator().getContext(null);

    @PostConstruct
    public void initPlugins() {
        try {
            LOG.info("Sending plugin information to event topic");
            ServiceResponseType serviceResponseType = new ServiceResponseType();
            serviceResponseType.setServiceClassName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
            serviceResponseType.setName("Thrane&Thrane");
            serviceResponseType.setSatelliteType("INMARSAT_C");
            serviceResponseType.setPluginType(PluginType.SATELLITE_RECEIVER);
            serviceResponseType.setActive(true);
            CapabilityListType capabilityList = new CapabilityListType();
            CapabilityType capabilityType = new CapabilityType();
            capabilityType.setType(CapabilityTypeType.POLLABLE);
            capabilityType.setValue("TRUE");
            capabilityList.getCapability().add(capabilityType);
            CapabilityType configurable = new CapabilityType();
            configurable.setType(CapabilityTypeType.CONFIGURABLE);
            configurable.setValue("TRUE");
            capabilityList.getCapability().add(configurable);
            serviceResponseType.setCapabilityList(capabilityList);
            
            String message = jsonb.toJson(serviceResponseType);
            TextMessage textMessage = this.context.createTextMessage(message);
            textMessage.setStringProperty(MessageConstants.EVENT_STREAM_EVENT, "Service Registered");
            textMessage.setStringProperty(MessageConstants.EVENT_STREAM_SUBSCRIBER_LIST, null);
            MappedDiagnosticContext.addThreadMappedDiagnosticContextToMessageProperties(textMessage);
    
            context.createProducer()
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .send(destination, textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}