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
package eu.europa.ec.fisheries.uvms.asset.message;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.ServiceToPluginMapper;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/topic/EventStream"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "asset"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "asset"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "event='Service Registered' OR event='Service Unregistered'")
    })
public class EventConsumer implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventConsumer.class);

    @Inject
    private ConfigServiceBeanMT configService;

    private Jsonb jsonb;

    @PostConstruct
    public void init() {
        jsonb = new JsonBConfigurator().getContext(null);
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String eventType = message.getStringProperty("event");
            if (eventType != null && eventType.startsWith("Service")) {
                ServiceResponseType service = jsonb.fromJson(textMessage.getText(), ServiceResponseType.class);
                LOG.info("Received event: {} for plugin {}", eventType, service.getServiceClassName());
                if (service.getPluginType().equals(PluginType.SATELLITE_RECEIVER)) {
                    PluginService plugin = ServiceToPluginMapper.mapToPlugin(service);
                    if (eventType.equals("Service Registered")) {
                        configService.upsertPlugin(plugin);
                    } else if (eventType.equals("Service Unregistered")) {
                        configService.inactivatePlugin(plugin);
                    }
                }
            }
        } catch (JMSException e) {
            LOG.error("Could not handle event message", e);
        }
    }
}
