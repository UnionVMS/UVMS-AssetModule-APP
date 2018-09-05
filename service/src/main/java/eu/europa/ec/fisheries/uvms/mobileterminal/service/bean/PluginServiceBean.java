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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.asset.mapper.PollToCommandRequestMapper;
import eu.europa.ec.fisheries.uvms.config.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.config.model.mapper.ModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.ExchangeModuleResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
@LocalBean
public class PluginServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(PluginServiceBean.class);

    private static final String EXCHANGE_MODULE_NAME = "exchange";
    private static final String DELIMETER = ".";
    private static final String INTERNAL_DELIMETER = ",";
    private static final String SETTING_KEY_DNID_LIST = "DNIDS";

    @EJB
    private MTMessageProducer MTMessageProducer;

    @EJB
    private MTMessageConsumer MTMessageConsumer;

//    @EJB(lookup = ServiceConstants.DB_ACCESS_CONFIG_MODEL)
    @EJB
    private ConfigServiceBeanMT configModel;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AcknowledgeTypeType sendPoll(PollResponseType poll, String username) {
        try {
            PollType pollType = PollToCommandRequestMapper.mapToPollType(poll);
            String pluginServiceName = poll.getMobileTerminal().getPlugin().getServiceName();
            String exchangeData = ExchangeModuleRequestMapper.createSetCommandSendPollRequest(pluginServiceName, pollType, username, null);
            String messageId = MTMessageProducer.sendModuleMessage(exchangeData, ModuleQueue.EXCHANGE);
            TextMessage response = MTMessageConsumer.getMessage(messageId, TextMessage.class);
            if(response == null)
                return AcknowledgeTypeType.NOK;
            AcknowledgeType ack = ExchangeModuleResponseMapper.mapSetCommandResponse(response, messageId);
            LOG.debug("Poll: " + poll.getPollId().getGuid() + " sent to exchange. Response: " + ack.getType());
            return ack.getType();
        } catch (ExchangeModelMapperException | RuntimeException e) {
            LOG.error("Failed to send poll command! Poll with guid {} was created but not sent", poll.getPollId().getGuid());
            return AcknowledgeTypeType.NOK;
        }
    }

    public void processUpdatedDNIDList(String pluginName) {
            List<String> dnidList = configModel.updatedDNIDList(pluginName);
            if(dnidList.size() < 1){
                LOG.error("Couldn't get updated DNID List");
                return;
            }

            String settingKey = pluginName + DELIMETER + SETTING_KEY_DNID_LIST;
            StringBuilder builder = new StringBuilder();
            for (String dnid : dnidList) {
                builder.append(dnid).append(INTERNAL_DELIMETER);
            }
            String settingValue = builder.toString();

            try {
                sendUpdatedDNIDListToConfig(settingKey, settingValue);
            } catch (ModelMarshallException e) {
                LOG.debug("Couldn't send to config module. Sending to exchange module.");
                sendUpdatedDNIDListToExchange(pluginName, SETTING_KEY_DNID_LIST, settingValue);
            }
    }

    private void sendUpdatedDNIDListToConfig(String settingKey, String settingValue) throws ModelMarshallException {
        SettingType setting = new SettingType();
        setting.setKey(settingKey);
        setting.setModule(EXCHANGE_MODULE_NAME);
        setting.setDescription("DNID list for all active mobile terminals. Plugin use it to know which channels it should be listening to");
        setting.setGlobal(false);
        setting.setValue(settingValue);

        String setSettingRequest = ModuleRequestMapper.toSetSettingRequest(EXCHANGE_MODULE_NAME, setting, "UVMS");
        String messageId = MTMessageProducer.sendModuleMessage(setSettingRequest, ModuleQueue.CONFIG);
        TextMessage response = MTMessageConsumer.getMessage(messageId, TextMessage.class);
        LOG.info("UpdatedDNIDList sent to config module");
    }

    private void sendUpdatedDNIDListToExchange(String pluginName, String settingKey, String settingValue) {
        try {
            String request = ExchangeModuleRequestMapper.createUpdatePluginSettingRequest(pluginName, settingKey, settingValue);
            String messageId = MTMessageProducer.sendModuleMessage(request, ModuleQueue.EXCHANGE);
            TextMessage response = MTMessageConsumer.getMessage(messageId, TextMessage.class);
            LOG.info("UpdatedDNIDList sent to exchange module {} {}",pluginName,settingKey);
        } catch (ExchangeModelMarshallException | RuntimeException e) {
            LOG.error("Failed to send updated DNID list {} {} {}",pluginName,settingKey,e);
        }
    }
}
