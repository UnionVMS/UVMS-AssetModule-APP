package eu.europa.ec.fisheries.uvms.rest.asset.service;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetBO;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetMatcher;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.bind.Jsonb;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class InternalRestResourceTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdNonValidIdentifierTest() {
        Response response = getWebTargetInternal()
                .path("/internal/asset/apa/" + UUID.randomUUID())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get();
        

        assertThat(response.getStatus(), is(Status.OK.getStatusCode()));

    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdGUIDTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/guid/" + createdAsset.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdCfrTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/cfr/" + createdAsset.getCfr())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdIrcsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/ircs/" + createdAsset.getIrcs())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdImoTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/imo/" + createdAsset.getImo())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdMmsiTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/mmsi/" + createdAsset.getMmsi())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdIccatTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/iccat/" + createdAsset.getIccat())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdUviTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/uvi/" + createdAsset.getUvi())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdGfcmTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTargetInternal()
                .path("/internal/asset/gfcm/" + createdAsset.getGfcm())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        Asset upsertedAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), Asset.class);
        
        assertThat(upsertedAsset, is(CoreMatchers.notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetRetainMMSIAndCommentTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setComment("Update Asset Retain Comment");
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO createdAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(createdAsset, is(CoreMatchers.notNullValue()));

        createdAsset.getAsset().setMmsi(null);
        createdAsset.getAsset().setComment(null);
        assetBo.setAsset(createdAsset.getAsset());

        AssetBO updatedAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(updatedAsset, is(CoreMatchers.notNullValue()));
        assertEquals(asset.getComment(), updatedAsset.getAsset().getComment());
        assertEquals(asset.getMmsi(), updatedAsset.getAsset().getMmsi());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetOverwriteMMSIAndCommentTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset.setComment("Update Asset Discard This Comment");
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO createdAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(createdAsset, is(CoreMatchers.notNullValue()));

        createdAsset.getAsset().setMmsi("MMSI" + AssetHelper.getRandomIntegers(5));
        createdAsset.getAsset().setComment("It Should Be This Comment");
        assetBo.setAsset(createdAsset.getAsset());

        AssetBO updatedAsset = getWebTargetInternal()
                .path("internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(assetBo), AssetBO.class);

        assertThat(updatedAsset, is(CoreMatchers.notNullValue()));
        assertEquals(createdAsset.getAsset().getComment(), updatedAsset.getAsset().getComment());
        assertEquals(createdAsset.getAsset().getMmsi(), updatedAsset.getAsset().getMmsi());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollTest() {      //just checking that the endpoint exists, there are better tests for the logic in pollRestResources
        PollRequestType input = new PollRequestType();

        PollMobileTerminal pmt = new PollMobileTerminal();
        input.getMobileTerminals().add(pmt);

        input.setPollType(PollType.MANUAL_POLL);
        input.setComment("Test Comment");
        input.setUserName("Test User");

        Response response = getWebTargetInternal()
                .path("/internal/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(input), Response.class);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollUsingOnlyAssetTest() {      //just checking that the endpoint exists, there are better tests for the logic in pollRestResources
        Asset asset = AssetHelper.createBasicAsset();
        asset = getWebTargetInternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(asset), Asset.class);
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(asset);

        Jsonb jsonb = new JsonBConfigurator().getContext(null); //for some reason serializing the mt gives a stack overflow error while serializing using the client, so we do it manually b4 instead
        String json = jsonb.toJson(mt);

        Response mtResponse = getWebTargetInternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                .post(Entity.json(json), Response.class);
        assertEquals(200, mtResponse.getStatus());

        CreatePollResultDto response = getWebTargetInternal()
                .path("/internal")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("username", "Test User")
                .queryParam("comment", "Test comment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(""), CreatePollResultDto.class);

        assertNotNull(response);
    }
}
