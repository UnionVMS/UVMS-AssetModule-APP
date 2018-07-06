package eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeValidationException;
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
    
    public static String mapSetCommandResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        SetCommandResponse response = new SetCommandResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static String mapSendMovementToPluginResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
    	SendMovementToPluginResponse response = new SendMovementToPluginResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
    
    public static String mapUpdateSettingResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
    	UpdatePluginSettingResponse response = new UpdatePluginSettingResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static ExchangeFault createFaultMessage(FaultCode code, String message) {
    	ExchangeFault fault = new ExchangeFault();
    	fault.setCode(code.getCode());
    	fault.setMessage(message);
    	return fault;
    }

	public static String mapServiceListResponse(List<ServiceResponseType> serviceList) throws ExchangeModelMarshallException {
		GetServiceListResponse response = new GetServiceListResponse();
		response.getService().addAll(serviceList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
	
	public static List<ServiceResponseType> mapServiceListResponse(TextMessage response, String correlationId) throws ExchangeModelMapperException {
		try {
			validateResponse(response, correlationId);
			GetServiceListResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, GetServiceListResponse.class);
			return unmarshalledResponse.getService();
		} catch(JMSException | ExchangeValidationException e) {
			LOG.error("[ Error when mapping response to service types ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to service types ] " + e.getMessage());
		}
	}

	public static AcknowledgeType mapSetCommandResponse(TextMessage response, String correlationId) throws ExchangeModelMapperException {
		try {
			validateResponse(response, correlationId);
			SetCommandResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, SetCommandResponse.class);
			return unmarshalledResponse.getResponse();
			//TODO handle ExchangeValidationException - extract fault...
		} catch(JMSException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to service types ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to service types ] " + e.getMessage());
		}
	}

	private static void validateResponse(TextMessage response, String correlationId) throws JMSException, ExchangeValidationException {

		if (response == null) {
			throw new ExchangeValidationException("Error when validating response in ResponseMapper: Response is Null");
		}

		if (response.getJMSCorrelationID() == null) {
			throw new ExchangeValidationException("No corelationId in response (Null) . Expected was: " + correlationId);
		}

		if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
			throw new ExchangeValidationException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: "
					+ response.getJMSCorrelationID());
		}

		try {
			ExchangeFault fault = JAXBMarshaller.unmarshallTextMessage(response, ExchangeFault.class);
			//TODO use fault
			throw new ExchangeValidationException(fault.getCode() + " - " + fault.getMessage());
		} catch (ExchangeModelMarshallException e) {
			//everything went well
		}
	}
}