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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.asset.mapper.PollToCommandRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Date;

@Stateless
@LocalBean
public class PluginServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(PluginServiceBean.class);

    private static final String EXCHANGE_MODULE_NAME = "exchange";
    private static final String DELIMETER = ".";
    private static final String INTERNAL_DELIMETER = ",";
    private static final String SETTING_KEY_DNID_LIST = "DNIDS";

    @Inject
    private PollToCommandRequestMapper pollToCommandRequestMapper;

    @Resource(name = "java:global/exchange_endpoint")
    private String exchangeEndpoint;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AcknowledgeTypeType sendPoll(PollResponseType poll) {
        try {
            String username = poll.getUserName();
            PollType pollType = pollToCommandRequestMapper.mapToPollType(poll);
            String pluginServiceName = poll.getMobileTerminal().getPlugin().getServiceName();
            SetCommandRequest request = createSetCommandRequest(pluginServiceName, CommandTypeType.POLL, username, null);
            request.getCommand().setPoll(pollType);

            Client client = ClientBuilder.newClient();
            Response response = client.target(exchangeEndpoint + "/api/pluginCommand")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), Response.class);

            if(response == null || response.getStatus() != 200) {
                if(response != null) {
                    LOG.info("Send poll failed with status {} due to: {}", response.getStatus(), response.readEntity(String.class));
                }else{
                    LOG.info("Send poll failed due to null response");
                }
                return AcknowledgeTypeType.NOK;
            }
            LOG.debug("Poll: " + poll.getPollId().getGuid() + " sent to exchange. Response: " + AcknowledgeTypeType.OK);
            return AcknowledgeTypeType.OK;
        } catch (RuntimeException e) {
            LOG.error("Failed to send poll command! Poll with guid {} was created but not sent. Error: {}", poll.getPollId().getGuid(), e);
            return AcknowledgeTypeType.NOK;
        }
    }

    private SetCommandRequest createSetCommandRequest(String pluginName, CommandTypeType type, String username, String fwdRule) {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        CommandType commandType = new CommandType();
        commandType.setTimestamp(Date.from(Instant.now()));
        commandType.setCommand(type);
        commandType.setPluginName(pluginName);
        commandType.setFwdRule(fwdRule);
        request.setUsername(username);
        request.setCommand(commandType);
        return request;
    }
}
