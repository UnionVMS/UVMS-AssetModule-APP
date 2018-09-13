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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.CreatePollResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.SinglePollResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

import static eu.europa.ec.fisheries.uvms.mobileterminal.exception.ErrorCode.*;

public class PollDataSourceResponseMapper {

    private static Logger LOG = LoggerFactory.getLogger(PollDataSourceResponseMapper.class);

    private static void validateResponse(TextMessage response, String correlationId) throws JMSException {

        if (response == null) {
            throw new NullPointerException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new NullPointerException("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new IllegalArgumentException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

        try {
            MobileTerminalFault fault = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalFault.class);
            throw new RuntimeException(fault.getCode() + " : " + fault.getMessage());
        } catch (AssetException e) {
            // everything is well
        }
    }

    public static List<PollResponseType> mapCreatePollResponse(TextMessage response, String messageId) throws AssetException {
        try {
            validateResponse(response, messageId);
            CreatePollResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, CreatePollResponse.class);
            return unmarshalledResponse.getPollList();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when unmarshalling poll create responses. ] {}", e.getMessage());
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + CreatePollResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }

    public static List<PollResponseType> mapToPollList(TextMessage response, String messageId) throws AssetException {
        try {
            validateResponse(response, messageId);
            PollListResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, PollListResponse.class);
            return unmarshalledResponse.getPollList();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when unmarshalling poll list responses. ] {}", e.getMessage());
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + PollListResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }

    public static PollListResponse mapPollListResponse(TextMessage response, String messageId) throws AssetException {
        try {
            validateResponse(response, messageId);
            return JAXBMarshaller.unmarshallTextMessage(response, PollListResponse.class);
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when unmarshalling poll list responses. ] {}", e.getMessage());
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + PollListResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
	}
    
    public static PollResponseType mapPollResponse(TextMessage response, String messageId) throws AssetException {
        try {
            validateResponse(response, messageId);
            SinglePollResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, SinglePollResponse.class);
            return unmarshalledResponse.getPoll();
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when unmarshalling single poll responses. ] {}", e.getMessage());
            throw new AssetException(UNMARSHALLING_ERROR.getMessage() + SinglePollResponse.class.getName() , e, UNMARSHALLING_ERROR.getCode());
        }
    }
}
