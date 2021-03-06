/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.asset.message;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetRemapMapping;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMergeInfo;
import eu.europa.ec.fisheries.uvms.asset.dto.MicroAsset;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.timer.AssetRemapTask;
import eu.europa.ec.fisheries.uvms.tests.BuildAssetServiceDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.*;
import javax.json.bind.Jsonb;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 *
 * @author Jem
 */
@RunWith(Arquillian.class)
public class EventStreamSenderTest extends BuildAssetServiceDeployment {
    
    private final static Logger LOG = LoggerFactory.getLogger(EventStreamSenderTest.class);

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    AssetDao assetDao;

    @Inject
    AssetRemapTask assetRemapTask;

    JMSHelper jmsHelper;
    MessageConsumer subscriber;
    Topic eventBus;
    Session session;

    private Jsonb jsonb;

    @Before
    public void init() {
        jmsHelper = new JMSHelper();
        jsonb =  new JsonBConfigurator().getContext(null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckEventStream() throws Exception {
        eu.europa.ec.fisheries.wsdl.asset.types.Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        registerSubscriber();
        TextMessage message = (TextMessage)listenOnEventStream(5000l);
        assertNotNull(message);

        assertEquals("Updated Asset", message.getStringProperty(MessageConstants.EVENT_STREAM_EVENT));
        assertNull(message.getStringProperty(MessageConstants.EVENT_STREAM_SUBSCRIBER_LIST));
        MicroAsset micro = jsonb.fromJson(message.getText(), MicroAsset.class);
        assertNotNull(micro);

        assertEquals(asset.getIrcs(), micro.getIrcs());
        assertEquals(asset.getCfr(), micro.getCfr());
        assertEquals(asset.getName(), micro.getAssetName());

    }

    @Test
    @OperateOnDeployment("normal")
    public void checkThatMergeMessageComesOnSSEStreamTest() throws Exception{
        eu.europa.ec.fisheries.wsdl.asset.types.Asset asset1 = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset1);
        registerSubscriber();
        TextMessage message = (TextMessage)listenOnEventStream(5000l);
        assertNotNull(message);
        assertEquals("Updated Asset", message.getStringProperty(MessageConstants.EVENT_STREAM_EVENT));
        MicroAsset oldAsset = jsonb.fromJson(message.getText(), MicroAsset.class);

        eu.europa.ec.fisheries.wsdl.asset.types.Asset asset2 = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset2);
        registerSubscriber();
        message = (TextMessage)listenOnEventStream(5000l);
        assertNotNull(message);
        assertEquals("Updated Asset", message.getStringProperty(MessageConstants.EVENT_STREAM_EVENT));
        MicroAsset newAsset = jsonb.fromJson(message.getText(), MicroAsset.class);

        AssetRemapMapping assetRemapMapping = new AssetRemapMapping();
        assetRemapMapping.setOldAssetId(oldAsset.getAssetId());
        assetRemapMapping.setNewAssetId(newAsset.getAssetId());
        assetRemapMapping.setCreatedDate(Instant.now().minus(4, ChronoUnit.HOURS));

        assetRemapMapping = assetDao.createAssetRemapMapping(assetRemapMapping);

        registerSubscriber();
        System.setProperty("MovementsRemapped", "0");
        assetRemapTask.remap();
        System.clearProperty("MovementsRemapped");
        message = (TextMessage)listenOnEventStream(5000l);
        assertNotNull(message);
        assertEquals("Merged Asset", message.getStringProperty(MessageConstants.EVENT_STREAM_EVENT));

        AssetMergeInfo mergeInfo = jsonb.fromJson(message.getText(), AssetMergeInfo.class);
        assertEquals(oldAsset.getAssetId().toString(), mergeInfo.getOldAssetId());
        assertEquals(newAsset.getAssetId().toString(), mergeInfo.getNewAssetId());

    }


    public void registerSubscriber() throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        eventBus = session.createTopic("EventStream");
        subscriber = session.createConsumer(eventBus, null, true);
    }


    public Message listenOnEventStream(Long timeoutInMillis) throws Exception {

        try {
            return subscriber.receive(timeoutInMillis);
        } finally {
            subscriber.close();
        }
    }
    
}
