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

import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.uvms.user.model.mapper.UserModuleResponseMapper;
import eu.europa.ec.fisheries.wsdl.user.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
/*
@MessageDriven(mappedName = "jms/queue/UVMSUserEvent", activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSUserEvent")})
        */
public class MTUserModuleMock /*implements MessageListener*/ {
/*
    private final static Logger LOG = LoggerFactory.getLogger(MTUserModuleMock.class);

    @Override
    public void onMessage(Message message) {
        try {
            UserContext userContext = getMobileTerminalUserContext();
            String responseString;
            responseString = UserModuleResponseMapper.mapToGetUserContextResponse(userContext);

            new AbstractProducer() {
                @Override
                public String getDestinationName() {
                    return "jms/queue/UVMSMobileTerminal";
                }
            }.sendResponseMessageToSender((TextMessage) message, responseString);

        } catch (Exception e) {
            LOG.error("MTUserModuleMock Error", e);
        }
    }

    private UserContext getMobileTerminalUserContext() {
        UserContext userContext = new UserContext();
        userContext.setContextSet(new ContextSet());
        Context context = new Context();
        context.setRole(new Role());

        Feature manageVesselsFeature = new Feature();
        manageVesselsFeature.setName(UnionVMSFeature.manageMobileTerminals.name());
        context.getRole().getFeature().add(manageVesselsFeature);

        Feature viewVesselsFeature = new Feature();
        viewVesselsFeature.setName(UnionVMSFeature.viewVesselsAndMobileTerminals.name());
        context.getRole().getFeature().add(viewVesselsFeature);

        Feature viewMobileTerminalPolls = new Feature();
        viewMobileTerminalPolls.setName(UnionVMSFeature.viewMobileTerminalPolls.name());
        context.getRole().getFeature().add(viewMobileTerminalPolls);

        Feature managePolls = new Feature();
        managePolls.setName(UnionVMSFeature.managePolls.name());
        context.getRole().getFeature().add(managePolls);

        userContext.getContextSet().getContexts().add(context);
        return userContext;
    }*/
}
