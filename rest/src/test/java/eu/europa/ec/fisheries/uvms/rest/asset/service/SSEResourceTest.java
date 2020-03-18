/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.rest.asset.service;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetRemapMapping;
import eu.europa.ec.fisheries.uvms.mobileterminal.timer.AssetRemapTask;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import eu.europa.ec.fisheries.uvms.rest.asset.AuthorizationHeaderWebTarget;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 *
 * @author Jem
 */
@RunWith(Arquillian.class)
//@RunAsClient
public class SSEResourceTest extends AbstractAssetRestTest {
    private final static Logger LOG = LoggerFactory.getLogger(SSEResourceTest.class);
    
    private static String dataString = "";
    private static String errorString = "";

    @Inject
    private AssetDao assetDao;

    @Inject
    private AssetRemapTask assetRemapTask;

    @Before
    public void clearStrings(){
        dataString = "";
        errorString = "";
    }

    @Test
    @OperateOnDeployment("normal")
    public void SSEBroadcastTest() throws Exception{
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/test/rest/sse/subscribe");
        AuthorizationHeaderWebTarget jwtTarget = new AuthorizationHeaderWebTarget(target, getTokenInternal());

        try (SseEventSource source = SseEventSource.target(jwtTarget).reconnectingEvery(1, TimeUnit.SECONDS).build()) {
            source.register(onEvent, onError, onComplete);
            source.open();
            assertTrue(source.isOpen());

            Asset asset = createAndRestBasicAsset();

            asset.setName("new test name");
            asset = updateAsset(asset);
            Thread.sleep(100);
            asset.setFlagStateCode("UNK");
            asset = updateAsset(asset);
            Thread.sleep(100);
            asset.setLengthOverAll(42d);
            asset = updateAsset(asset);

            Thread.sleep(1000);
            assertTrue(source.isOpen());
            assertTrue(errorString,errorString.isEmpty());
            assertEquals(dataString,4 ,dataString.split("\\}\\{").length);
            assertTrue(dataString, dataString.contains("new test name"));
            assertTrue(dataString, dataString.contains("UNK"));
            assertTrue(dataString, dataString.contains("42"));
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkThatMergeMessageComesOnSSEStreamTest() throws Exception{
        Asset oldAsset = createAndRestBasicAsset();
        Asset newAsset = createAndRestBasicAsset();
        AssetRemapMapping assetRemapMapping = new AssetRemapMapping();
        assetRemapMapping.setOldAssetId(oldAsset.getId());
        assetRemapMapping.setNewAssetId(newAsset.getId());
        assetRemapMapping.setCreatedDate(Instant.now().minus(4, ChronoUnit.HOURS));

        assetRemapMapping = assetDao.createAssetRemapMapping(assetRemapMapping);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/test/rest/sse/subscribe");
        AuthorizationHeaderWebTarget jwtTarget = new AuthorizationHeaderWebTarget(target, getTokenInternal());

        try (SseEventSource source = SseEventSource.target(jwtTarget).reconnectingEvery(1, TimeUnit.SECONDS).build()) {
            source.register(onEvent, onError, onComplete);
            source.open();
            assertTrue(source.isOpen());

            assetRemapTask.remap();

            Thread.sleep(1000);
            assertTrue(source.isOpen());
            assertTrue(errorString,errorString.isEmpty());
            assertTrue(dataString, dataString.contains(assetRemapMapping.getOldAssetId().toString()));
            assertTrue(dataString, dataString.contains(assetRemapMapping.getNewAssetId().toString()));
        }
    }

    private static Consumer<InboundSseEvent> onEvent = (inboundSseEvent) -> {
        String data = inboundSseEvent.readData();
        dataString = dataString.concat(data);
    };

    //Error
    private static Consumer<Throwable> onError = (throwable) -> {
        LOG.error("Error while testing sse: ", throwable);
        errorString = throwable.getMessage();
    };

    //Connection close and there is nothing to receive
    private static Runnable onComplete = () -> {
        System.out.println("Done!");
    };

    private Asset createAndRestBasicAsset(){
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);
        return createdAsset;
    }

    private Asset updateAsset(Asset asset){
        return getWebTargetInternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(asset), Asset.class);
    }
    
}
