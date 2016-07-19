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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.ConfigService;
import eu.europa.ec.fisheries.wsdl.asset.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;

@Stateless
public class ConfigServiceBean implements ConfigService {

    final static Logger LOG = LoggerFactory.getLogger(ConfigServiceBean.class);

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetQueueConsumer reciever;

    @EJB
    ParameterService parameterService;

    @EJB
    ConfigHelper configHelper;

    @Override
    public List<Config> getConfiguration() throws AssetException {
        LOG.info("Get configuration.");

        String data = AssetDataSourceRequestMapper.mapGetAllConfiguration();
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);

        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return AssetDataSourceResponseMapper.mapToConfiguration(response, messageId);
    }

    @Override
    public Map<String, String> getParameters() throws AssetException {
        try {
            LOG.info("Get parameters");
            Map<String, String> parameters = new HashMap<>();
            for (SettingType settingType : parameterService.getAllSettings()) {
                parameters.put(settingType.getKey(), settingType.getValue());
            }

            return parameters;
        } catch (ConfigServiceException e) {
            LOG.error("[ Error when getting asset parameters from local database. ] {}", e.getMessage());
            throw new AssetException("Couldn't get parameters");
        }
    }

}
