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

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.PingResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

import static eu.europa.ec.fisheries.uvms.mobileterminal.exception.ErrorCode.UNMARSHALLING_ERROR;

public class MobileTerminalDataSourceResponseMapper {

    private MobileTerminalDataSourceResponseMapper() {}

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalDataSourceResponseMapper.class);

    private static void validateResponse(TextMessage response, String correlationId) throws JMSException {

        if (response == null) {
            throw new NullPointerException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new NullPointerException("No correlationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new IllegalArgumentException("Wrong correlationId in response. Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

        try {
            MobileTerminalFault fault = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalFault.class);
            throw new RuntimeException("Fault found when validate response: " + fault.toString());
        } catch (AssetException e) {
            //everything is well
        }
    }

    public static MobileTerminalType mapToMobileTerminalFromResponse(TextMessage response, String messageId) throws AssetException {
        try {
            validateResponse(response, messageId);
            MobileTerminalResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalResponse.class);
            return unmarshalledResponse.getMobilTerminal();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal. ] " + e);
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + MobileTerminalResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }

    }

    public static boolean mapDNIDUpdatedMobileTerminalResponse(TextMessage response, String messageId) throws AssetException {
    	try {
            validateResponse(response, messageId);
            MobileTerminalResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalResponse.class);
            return unmarshalledResponse.isDnidListUpdated();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal. DNIDList updated] " + e);
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + MobileTerminalResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }

    public static MobileTerminalListResponse mapToMobileTerminalListFromResponse(TextMessage response, String correlationId) throws AssetException {
        try {
            validateResponse(response, correlationId);
            return JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalListResponse.class);
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal list. ] {}", e);
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + MobileTerminalListResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }

    public static List<TerminalSystemType> mapToTerminalSystemList(TextMessage response, String correlationId) throws AssetException {
        try {
            validateResponse(response, correlationId);
            TerminalSystemListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, TerminalSystemListResponse.class);
            return mappedResponse.getTerminalSystem();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to terminal system list. ] {}", e);
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + TerminalSystemListResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }

	public static List<String> mapToChannelNames(TextMessage response, String correlationId) throws AssetException {
		try {
            validateResponse(response, correlationId);
            ComchannelNameResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ComchannelNameResponse.class);
            return mappedResponse.getComchannelName();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to comchannel list. ] {}", e);
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + ComchannelNameResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
	}

    public static List<ConfigList> mapToConfigList(TextMessage response, String correlationId) throws AssetException {
    	try {
    		validateResponse(response, correlationId);
    		ConfigResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ConfigResponse.class);
    		return mappedResponse.getConfig();
    	} catch (AssetException| JMSException e) {
    		LOG.error("[ Error when mapping response to config list. ] {}", e);
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + ConfigResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
    	}
	}
    
    public static List<Plugin> mapToPluginList(TextMessage response, String correlationId) throws AssetException {
    	try {
            validateResponse(response, correlationId);
            UpsertPluginListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, UpsertPluginListResponse.class);
            return mappedResponse.getPlugin();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to plugin list. ] ");
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + UpsertPluginListResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
	}
    
    public static UpdatedDNIDListResponse mapToUpdatedDNIDList(TextMessage response, String correlationId) throws AssetException {
    	try {
            validateResponse(response, correlationId);
            return JAXBMarshaller.unmarshallTextMessage(response, UpdatedDNIDListResponse.class);
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when mapping response to updated DNID list response. ] ");
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + UpdatedDNIDListResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }

    public static String createTerminalSystemListResponse(List<TerminalSystemType> systemList) throws AssetException {
        TerminalSystemListResponse response = new TerminalSystemListResponse();
        response.getTerminalSystem().addAll(systemList);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static MobileTerminalFault createFaultMessage(int code, String message) {
        MobileTerminalFault fault = new MobileTerminalFault();
        fault.setCode(code);
        fault.setMessage(message);
        return fault;
    }

    public static String createMobileTerminalResponse(MobileTerminalType terminal) throws AssetException {
        MobileTerminalResponse response = new MobileTerminalResponse();
        response.setMobilTerminal(terminal);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createPingResponse(String responseMessage) throws AssetException {
		PingResponse pingResponse = new PingResponse();
		pingResponse.setResponse(responseMessage);
		return JAXBMarshaller.marshallJaxBObjectToString(pingResponse);
    }
}
