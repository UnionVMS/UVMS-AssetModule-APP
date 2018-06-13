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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

public class MobileTerminalModuleResponseMapper {
    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalModuleResponseMapper.class);

    private static void validateResponse(TextMessage response, String correlationId) throws MobileTerminalValidationException, JMSException {

        if (response == null) {
            throw new MobileTerminalValidationException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new MobileTerminalValidationException("No correlationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new MobileTerminalValidationException("Wrong correlationId in response. Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

        try {
            MobileTerminalFault fault = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalFault.class);
            throw new MobileTerminalValidationException(fault.getCode() + " : " + fault.getMessage());
        } catch (MobileTerminalUnmarshallException e) {
            e.printStackTrace();
        }
    }

    private static String createMobileTerminalResponse(MobileTerminalType data) throws MobileTerminalModelMapperException {
        MobileTerminalResponse response = new MobileTerminalResponse();
        response.setMobilTerminal(data);
        return JAXBMarshaller.marshallJaxBObjectToString(data);
    }

    public static String createPingResponse(String responseMessage) throws MobileTerminalModelMapperException {
		PingResponse pingResponse = new PingResponse();
		pingResponse.setResponse(responseMessage);
		return JAXBMarshaller.marshallJaxBObjectToString(pingResponse);
    }

    public static MobileTerminalType mapToMobileTerminalResponse(TextMessage message) throws MobileTerminalModelMapperException, JMSException, MobileTerminalUnmarshallException {
        validateResponse(message, message.getJMSCorrelationID());
        MobileTerminalResponse response = JAXBMarshaller.unmarshallTextMessage(message, MobileTerminalResponse.class);
        return response.getMobilTerminal();
    }

    public static List<MobileTerminalType> mapToMobileTerminalListResponse(TextMessage message) throws MobileTerminalModelMapperException, JMSException, MobileTerminalUnmarshallException {
        validateResponse(message, message.getJMSCorrelationID());
        MobileTerminalListResponse response = JAXBMarshaller.unmarshallTextMessage(message, MobileTerminalListResponse.class);
        return response.getMobileTerminal();
    }
}
