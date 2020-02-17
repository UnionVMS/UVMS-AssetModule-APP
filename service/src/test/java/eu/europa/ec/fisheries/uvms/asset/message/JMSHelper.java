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
package eu.europa.ec.fisheries.uvms.asset.message;

import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;

import javax.jms.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JMSHelper {

    private static final long TIMEOUT = 20000;
    private static final String ASSET_QUEUE = "UVMSAssetEvent";
    private static final String RESPONSE_QUEUE = "IntegrationTestsResponseQueue";

    public Asset upsertAsset(Asset asset) throws Exception {
        String request = AssetModuleRequestMapper.createUpsertAssetModuleRequest(asset, "Test user");
        sendAssetMessage(request);
        return asset;
    }
    
    public Asset getAssetById(String value, AssetIdType type) throws Exception {
        String msg = AssetModuleRequestMapper.createGetAssetModuleRequest(value, type);
        String correlationId = sendAssetMessage(msg);
        Message response = listenForResponse(correlationId);
        GetAssetModuleResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage((TextMessage) response, GetAssetModuleResponse.class);
        return assetModuleResponse.getAsset();
    }

    public List<Asset> getAssetByAssetListQuery(AssetListQuery assetListQuery) throws Exception {
        String msg = AssetModuleRequestMapper.createAssetListModuleRequest(assetListQuery);
        String correlationId = sendAssetMessage(msg);
        Message response = listenForResponse(correlationId);
        ListAssetResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage((TextMessage) response, ListAssetResponse.class);
        return assetModuleResponse.getAsset();
    }
    
    public String sendAssetMessage(String text) throws Exception {
        Connection connection = getConnectionFactory().createConnection("test", "test");
        connection.setClientID(UUID.randomUUID().toString());
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue assetQueue = session.createQueue(ASSET_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            session.createProducer(assetQueue).send(message);

            return message.getJMSMessageID();
        } finally {
            connection.close();
        }
    }

    public String sendAssetMessageWithFunction(String text, String function) throws Exception {
        Connection connection = getConnectionFactory().createConnection("test", "test");
        connection.setClientID(UUID.randomUUID().toString());
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue assetQueue = session.createQueue(ASSET_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY, function);
            message.setText(text);

            session.createProducer(assetQueue).send(message);

            return message.getJMSMessageID();
        } finally {
            connection.close();
        }
    }

    public Message listenForResponse(String correlationId) throws Exception {
        Connection connection = getConnectionFactory().createConnection("test", "test");
        connection.setClientID(UUID.randomUUID().toString());
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);

            return session.createConsumer(responseQueue).receive(TIMEOUT);
        } finally {
            connection.close();
        }
    }

    private ConnectionFactory getConnectionFactory() {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        return ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF,transportConfiguration);
    }

    public void assetInfo(List<eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset> asset) throws Exception {

        //Jsonb jsonb = new JsonBConfigurator().getContext(null);
        Jsonb jsonb = JsonbBuilder.create();

        String json = jsonb.toJson(asset);
        sendAssetMessageWithFunction(json, "ASSET_INFORMATION");
    }




}
