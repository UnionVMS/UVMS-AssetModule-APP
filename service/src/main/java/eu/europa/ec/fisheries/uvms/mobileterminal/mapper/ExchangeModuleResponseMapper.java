package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

public class ExchangeModuleResponseMapper {

	private static Logger LOG = LoggerFactory.getLogger(ExchangeModuleResponseMapper.class);

    public static AcknowledgeType mapAcknowledgeTypeOK() {
    	AcknowledgeType ackType = new AcknowledgeType();
    	ackType.setType(AcknowledgeTypeType.OK);
    	return ackType;
    }
    
    public static AcknowledgeType mapAcknowledgeTypeOK(String messageId, String message) {
        AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        ackType.setMessage(message);
        ackType.setMessageId(messageId);
        return ackType;
    }

    public static AcknowledgeType mapAcknowledgeTypeNOK(String messageId, String errorMessage) {
    	AcknowledgeType ackType = new AcknowledgeType();
    	ackType.setMessage(errorMessage);
    	ackType.setMessageId(messageId);
    	ackType.setType(AcknowledgeTypeType.NOK);
    	return ackType;
    }
    
    public static String mapSetCommandResponse(AcknowledgeType ackType) {
        SetCommandResponse response = new SetCommandResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static String mapSendMovementToPluginResponse(AcknowledgeType ackType) {
    	SendMovementToPluginResponse response = new SendMovementToPluginResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
    
    public static String mapUpdateSettingResponse(AcknowledgeType ackType)  {
    	UpdatePluginSettingResponse response = new UpdatePluginSettingResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
	public static String mapServiceListResponse(List<ServiceResponseType> serviceList) {
		GetServiceListResponse response = new GetServiceListResponse();
		response.getService().addAll(serviceList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}


	public static AcknowledgeType mapSetCommandResponse(TextMessage response) {
		try {
			SetCommandResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, SetCommandResponse.class);
			return unmarshalledResponse.getResponse();
			//TODO handle ExchangeValidationException - extract fault...
		} catch(RuntimeException e) {
			LOG.error("[ Error when mapping response to service types ]");
			throw new RuntimeException("[ Error when mapping response to service types ] " , e);
		}
	}

}