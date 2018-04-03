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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public class JMSHelper {

    private static final long TIMEOUT = 3000;
    private static final String ASSET_QUEUE = "UVMSAssetEvent";
    private static final String RESPONSE_QUEUE = "AssetTestQueue";

    private ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

    public Asset upsertAsset(Asset asset) throws Exception {
        String request = AssetModuleRequestMapper.createUpsertAssetModuleRequest(asset, "Test user");
        sendAssetMessage(request);
        return asset;
    }

    public String sendAssetMessage(String text) throws Exception {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
        Queue assetQueue = session.createQueue(ASSET_QUEUE);
        
        TextMessage message = session.createTextMessage();
        message.setJMSReplyTo(responseQueue);
        message.setText(text);

        session.createProducer(assetQueue).send(message);

        return message.getJMSMessageID();
    }

    public Message listenForResponse(String correlationId) throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
        
        return session.createConsumer(responseQueue).receive(TIMEOUT);
    }
}
