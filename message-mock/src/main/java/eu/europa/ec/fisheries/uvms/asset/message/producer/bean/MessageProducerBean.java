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
package eu.europa.ec.fisheries.uvms.asset.message.producer.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetSuccessfulTestEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.SuccessfulTestEvent;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {


    final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

    private static final int CONFIG_TTL = 30000;


    @Inject
    @AssetSuccessfulTestEvent
    Event<SuccessfulTestEvent> successfulTestEvent;

    @PostConstruct
    public void init() {
    }

    @Override
    public String sendDataSourceMessage(String text, AssetDataSourceQueue queue) {
        return "MOCK";
    }

    @Override
    public String sendModuleMessage(String text, ModuleQueue queue) throws AssetMessageException {
        return text;
    }

    @Override
    public void sendModuleResponseMessage(TextMessage message, String text) {
        successfulTestEvent.fire(new SuccessfulTestEvent(text));
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
    }

    @Override
    public String sendConfigMessage(String text) throws ConfigMessageException {
        return text;
    }


}