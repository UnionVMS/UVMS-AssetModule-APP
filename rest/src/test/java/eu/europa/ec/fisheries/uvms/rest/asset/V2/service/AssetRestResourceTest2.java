package eu.europa.ec.fisheries.uvms.rest.asset.V2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetMatcher;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetRestResourceTest2 extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckResponseCodeTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTargetExternal()
                .path("asset2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset));

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        assertNotNull(createdAsset);
        assertThat(createdAsset.getCfr(), is(asset.getCfr()));
        assertEquals(EventCode.MOD.value(), createdAsset.getEventCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Asset fetchedAsset = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNotNull(fetchedAsset);
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetWithAttachedMTTest() throws IOException {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(createdAsset);
        MobileTerminal createdMT = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);


        String fetchedAssetString = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        assertEquals(2, fetchedAssetString.split(createdMT.getId().toString()).length);

        ObjectMapper om = new ObjectMapper();
        Asset fetchedAsset = om.readValue(fetchedAssetString, Asset.class);
        assertNotNull(fetchedAsset);
        assertTrue(fetchedAsset.getMobileTerminalIdList().size() == 1);
        assertEquals(createdMT.getId(), UUID.fromString(fetchedAsset.getMobileTerminalIdList().get(0)));

    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetWithAttachedMTUpdateTheMTACoupleOfTimesTest() throws IOException {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(createdAsset);
        MobileTerminal createdMT = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        createdMT.setAsset(createIdOnlyAsset(createdMT.getAssetId()));
        createdMT.setComment("Updated comment 1");
        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "Updated comment 1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdMT), MobileTerminal.class);

        createdMT.setComment("Updated comment 2");
        updated = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "Updated comment 2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdMT), MobileTerminal.class);

        createdMT.setComment("Updated comment 3");
        updated = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "Updated comment 3")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdMT), MobileTerminal.class);


        String fetchedAssetString = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        assertEquals(2, fetchedAssetString.split(createdMT.getId().toString()).length);

        ObjectMapper om = new ObjectMapper();
        Asset fetchedAsset = om.readValue(fetchedAssetString, Asset.class);
        assertNotNull(fetchedAsset);
        assertTrue(fetchedAsset.getMobileTerminalIdList().size() == 1);
        assertEquals(createdMT.getId(), UUID.fromString(fetchedAsset.getMobileTerminalIdList().get(0)));
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetWithAttachedMTUpdateTheMTAndAssetACoupleOfTimesTest() throws IOException {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(createdAsset);
        MobileTerminal createdMT = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        createdMT.setAsset(createIdOnlyAsset(createdMT.getAssetId()));
        createdMT.setComment("Updated comment 1");
        MobileTerminal updatedMT = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "Updated comment 1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdMT), MobileTerminal.class);

        createdMT.setComment("Updated comment 2");
        updatedMT = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "Updated comment 2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdMT), MobileTerminal.class);

        String fetchedAssetString = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        assertEquals(2, fetchedAssetString.split(createdMT.getId().toString()).length);

        ObjectMapper om = new ObjectMapper();
        Asset fetchedAsset = om.readValue(fetchedAssetString, Asset.class);

        fetchedAsset.setOwnerName("New test owner");
        Asset updatedAsset = restUpdateAsset(fetchedAsset);

        createdMT.setComment("Updated comment 3");
        updatedMT = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "Updated comment 3")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdMT), MobileTerminal.class);


        fetchedAssetString = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        assertEquals(2, fetchedAssetString.split(createdMT.getId().toString()).length);

        fetchedAsset = om.readValue(fetchedAssetString, Asset.class);

        assertNotNull(fetchedAsset);
        assertTrue(fetchedAsset.getMobileTerminalIdList().size() == 1);
        assertEquals(createdMT.getId(), UUID.fromString(fetchedAsset.getMobileTerminalIdList().get(0)));
        assertEquals(updatedAsset.getOwnerName(), fetchedAsset.getOwnerName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdRandomValueTest() {
        Asset asset = getWebTargetExternal()
                .path("asset2")
                .path(UUID.randomUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNull(asset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdInvalidIdTest() {
        Response response = getWebTargetExternal()
                .path("asset2")
                .path("nonExistingAssetId")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertNotNull(response);
        //until someone has made a better errorHandler that can send a 404 only when necessary, this one will return 500
        assertThat(response.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetChangedNameTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        Asset updatedAsset = restUpdateAsset(createdAsset);

        assertThat(updatedAsset.getName(), is(newName));
        assertEquals(EventCode.MOD.value(), updatedAsset.getEventCode());

        Response response = getWebTargetExternal()
                .path("asset2")
                .path(updatedAsset.getId().toString())
                .path("history")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        List<Asset> assetRevisions = response.readEntity(new GenericType<List<Asset>>() {});

        assertNotNull(assetRevisions);
        assertThat(assetRevisions.size() , is(2));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetRetainConnectedMTTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal mt = MobileTerminalTestHelper.createRestMobileTerminal(getWebTargetExternal(), createdAsset, getTokenExternal());

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        createdAsset.getMobileTerminals().add(mt);
        Asset updatedAsset = restUpdateAsset(createdAsset);

        assertThat(updatedAsset.getName(), is(newName));
        assertEquals(EventCode.MOD.value(), updatedAsset.getEventCode());
        assertEquals(mt.getId(), UUID.fromString(updatedAsset.getMobileTerminalIdList().get(0)));

    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetNonExistingAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTargetExternal()
                .path("asset2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(asset));

        assertNotNull(response);
        // You really could argue that this should be a bad request but the server was returning 400 for everything,
        // if there is only one thing returned for every error it is better if it is a 500
        assertThat(response.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Asset archivedAsset = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .path("archive")
                .queryParam("comment", "Archive comment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), Asset.class);

        assertNotNull(archivedAsset);
        assertThat(archivedAsset.getActive() , is(false));
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAsset_ThenVerifyMobileTerminalUnlinkedAndInactivatedTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal terminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal createdMT = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(terminal), MobileTerminal.class);

        assertTrue(createdMT.getActive());

        MobileTerminal assignedMT = getWebTargetExternal()
                .path("mobileterminal2")
                .path(createdMT.getId().toString())
                .path("assign")
                .path(createdAsset.getId().toString())
                .queryParam("comment", "assign")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        assertNotNull(assignedMT.getAssetId());

        Asset fetchedAsset = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertTrue(fetchedAsset.getMobileTerminalIdList().size() > 0);
        fetchedAsset.setMobileTerminals(createListOfIdOnlyMTs(fetchedAsset.getMobileTerminalIdList()));

        Asset archivedAsset = getWebTargetExternal()
                .path("asset2")
                .path(fetchedAsset.getId().toString())
                .path("archive")
                .queryParam("comment", "archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), Asset.class);

        assertFalse(archivedAsset.getActive());

        MobileTerminal fetchedMT = getWebTargetExternal()
                .path("mobileterminal2")
                .path(createdMT.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(MobileTerminal.class);

        assertNull(fetchedMT.getAsset());
        assertFalse(fetchedMT.getActive());
    }

    @Test
    @OperateOnDeployment("normal")
    public void unarchiveAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Asset archivedAsset = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .path("archive")
                .queryParam("comment", "archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), Asset.class);

        assertNotNull(archivedAsset);
        assertThat(archivedAsset.getActive() , is(false));

        Asset unarchivedAsset = getWebTargetExternal()
                .path("asset2")
                .path(archivedAsset.getId().toString())
                .path("unarchive")
                .queryParam("comment", "unarchive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), Asset.class);

        assertNotNull(unarchivedAsset);
        assertThat(unarchivedAsset.getActive() , is(true));
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAssetNonExistingAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTargetExternal()
                .path("asset2")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(asset));

        assertNotNull(response);
        // You really could argue that this should be a bad request but the server was returning 400 for everything,
        // if there is only one thing returned for every error it is better if it is a 500
        assertThat(response.getStatus() , is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFromAssetIdAndDateCfrTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        OffsetDateTime firstTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        restUpdateAsset(createdAsset);

        OffsetDateTime secondTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        Asset assetByCfrAndTimestamp1 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path("history")
                .queryParam("date", firstTimeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertThat(assetByCfrAndTimestamp1.getName(), is(asset.getName()));

        Asset assetByCfrAndTimestamp2 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path("history")
                .queryParam("date", secondTimeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertThat(assetByCfrAndTimestamp2.getName(), is(newName));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFromAssetIdPastDateTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        Asset assetByCfrAndTimestamp1 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path("history")
                .queryParam("date", timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNotNull(assetByCfrAndTimestamp1);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPastAssetFromAssetIdPastDateTest() {
        Asset asset = AssetHelper.createBasicAsset();
        String originalName = "Original Name";
        asset.setName(originalName);
        Asset createdAsset = restCreateAsset(asset);

        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        createdAsset.setName("New Name");
        Asset updatedAsset = restUpdateAsset(createdAsset);
        assertNotNull(updatedAsset);

        Asset assetByCfrAndTimestamp1 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path("history")
                .queryParam("date", timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNotNull(assetByCfrAndTimestamp1);
        assertEquals(originalName, assetByCfrAndTimestamp1.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkPastNumberOfMTTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal mobileTerminal1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal1.setAsset(createdAsset);

        String response = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal1), String.class);

        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        MobileTerminal mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.setAsset(createdAsset);
        mobileTerminal2.setActive(false);

        String response2 = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal2), String.class);

        Asset presentAsset = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        Asset pastAsset = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path("history")
                .queryParam("date", timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNotNull(pastAsset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAndConnectedMobileTerminalTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        MobileTerminal mobileTerminal1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal1.setAsset(createdAsset);

        MobileTerminal response = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal1), MobileTerminal.class);
        assertNotNull(response);

        Asset fetchedAsset = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNotNull(fetchedAsset);
        assertTrue(fetchedAsset.getMobileTerminalIdList().size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFromAssetIdPastDateTestWithDateToEarly() {
        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Asset assetByCfrAndTimestamp1 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path("history")
                .queryParam("date", timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertNull(assetByCfrAndTimestamp1);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryByAssetHistGuidTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Asset fetchedAsset = getWebTargetExternal()
                .path("asset2")
                .path("history")
                .path(createdAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryByAssetHistGuidTwoRevisionsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        Asset updatedAsset = restUpdateAsset(createdAsset);

        Asset fetchedAsset = getWebTargetExternal()
                .path("asset2")
                .path("history")
                .path(createdAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getId(), is(createdAsset.getId()));

        Asset fetchedUpdatedAsset = getWebTargetExternal()
                .path("asset2")
                .path("history")
                .path(updatedAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertThat(fetchedUpdatedAsset, is(AssetMatcher.assetEquals(updatedAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNoteTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Note note = AssetHelper.createBasicNote();
        note.setAssetId(createdAsset.getId());

        Note createdNote = getWebTargetExternal()
                .path("asset2")
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(note), Note.class);

        assertNotNull(createdNote);
        assertThat(createdNote.getNotes(), is(note.getNotes()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistory_ThenVerifyItHoldsMobileTerminalHistory() {

        // Create Asset
        Asset asset = AssetHelper.createBasicAsset();
        String cfr = asset.getCfr();
        Asset createdAsset = restCreateAsset(asset);

        // Create MobileTerminal
        MobileTerminal terminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal createdMT = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(terminal), MobileTerminal.class);

        // Assign MobileTerminal
        MobileTerminal assignedMT = getWebTargetExternal()
                .path("mobileterminal2")
                .path(createdMT.getId().toString())
                .path("assign")
                .path(createdAsset.getId().toString())
                .queryParam("comment", "assign")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        // Verify Updated Asset holds correct MobileTerminal history
        OffsetDateTime firstTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        Asset assetHistory1 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(cfr)
                .path("history")
                .queryParam("date", firstTimeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        //List<MobileTerminal> mtList = assetHistory1.getMobileTerminals();
        assertEquals(1, assetHistory1.getMobileTerminalIdList().size());

        // Update MobileTerminal
        assignedMT.setAsset(createIdOnlyAsset(assignedMT.getAssetId()));
        assignedMT.setAntenna("New Improved Antenna");
        getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "New Comment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(assignedMT), MobileTerminal.class);

        String newCfr = "CRF" + AssetHelper.getRandomIntegers(9);
        // Update Asset
        OffsetDateTime secondTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        createdAsset.setCfr(newCfr);

        restUpdateAsset(createdAsset);

        // Verify Updated Asset holds correct MobileTerminal history
        Asset assetHistory2 = getWebTargetExternal()
                .path("asset2")
                .path("cfr")
                .path(newCfr)
                .path("history")
                .queryParam("date", secondTimeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Asset.class);

        assertEquals(1, assetHistory2.getMobileTerminalIdList().size());

    }

    @Test
    @OperateOnDeployment("normal")
    public void getNotesForAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Note note = AssetHelper.createBasicNote();
        note.setAssetId(createdAsset.getId());

        Note createdNote = getWebTargetExternal()
                .path("asset2")
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(note), Note.class);

        Response response = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        List<Note> fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(1));
        assertThat(fetchedNotes.get(0).getNotes(), is(createdNote.getNotes()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteNoteTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        Note note = AssetHelper.createBasicNote();
        note.setAssetId(createdAsset.getId());

        // Create note
        Note createdNote = getWebTargetExternal()
                .path("asset2")
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(note), Note.class);

        Response response = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        List<Note> fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(1));

        // Delete note
        response = getWebTargetExternal()
                .path("asset2")
                .path("notes")
                .path(createdNote.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .delete();

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        response = getWebTargetExternal()
                .path("asset2")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getContactInfo(){
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        // CREATE AN CONTACTINFO
        ContactInfo contactInfo = AssetHelper.createBasicContactInfo();
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
        contactInfo.setAssetId(createdAsset.getId());
        ContactInfo createdContactInfo = getWebTargetExternal()
                .path("asset2")
                .path("contacts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(contactInfo), ContactInfo.class);

        ContactInfo gottenContactInfo = getWebTargetExternal()
                .path("asset2")
                .path("contact")
                .path(createdContactInfo.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(ContactInfo.class);

        assertNotNull(gottenContactInfo);
        assertEquals(createdContactInfo.getId(), gottenContactInfo.getId());
        assertEquals(contactInfo.getName(), gottenContactInfo.getName());
        assertEquals(contactInfo.getAssetId(), gottenContactInfo.getAssetId());
        assertEquals(contactInfo.getEmail(), gottenContactInfo.getEmail());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetAndContactInfoAndCompareHistoryItemsTest() throws InterruptedException {

        // CREATE AN ASSET
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = restCreateAsset(asset);

        // CREATE AN CONTACTINFO
        ContactInfo contactInfo = AssetHelper.createBasicContactInfo();
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
        contactInfo.setAssetId(createdAsset.getId());
        ContactInfo createdContactInfo = getWebTargetExternal()
                .path("asset2")
                .path("contacts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(contactInfo), ContactInfo.class);

        Thread.sleep(3000);

        // UPDATE THE ASSET
        final String newAssetName = "NewAssetName";
        createdAsset.setName(newAssetName);
        Asset updatedAsset = restUpdateAsset(createdAsset);

        assertEquals(newAssetName, updatedAsset.getName());
        assertEquals(EventCode.MOD.value(), updatedAsset.getEventCode());

        // UPDATE THE CONTACTINFO
        String newContactInfoName = "NewContactInfoName";
        createdContactInfo.setName(newContactInfoName);
        createdContactInfo.setAssetUpdateTime(updatedAsset.getUpdateTime());
        ContactInfo updatedContactInfo = getWebTargetExternal()
                .path("asset2")
                .path("contacts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdContactInfo), ContactInfo.class);

        assertEquals(newContactInfoName, updatedContactInfo.getName());

        // GET ASSET HISTORY
        Response response = getWebTargetExternal()
                .path("asset2")
                .path(updatedAsset.getId().toString())
                .path("history")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        List<Asset> assetRevisions = response.readEntity(new GenericType<List<Asset>>() {});

        assertNotNull(assetRevisions);
        assertEquals(2, assetRevisions.size());

        // GET CONTACTINFO HISTORY FOR ASSET
        Response res = getWebTargetExternal()
                .path("asset2")
                .path(updatedAsset.getId().toString())
                .path("contacts")
                .queryParam("date", updatedAsset.getUpdateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        List<ContactInfo> contactInfoRevisions = res.readEntity(new GenericType<List<ContactInfo>>() {});

        assertNotNull(contactInfoRevisions);
        assertEquals(1, contactInfoRevisions.size());
    }
    private Asset restCreateAsset(Asset asset){
        Asset createdAsset = getWebTargetExternal()
                .path("asset2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        return createdAsset;
    }

    private Asset restUpdateAsset(Asset asset){
        Asset updatedAsset = getWebTargetExternal()
                .path("asset2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(asset), Asset.class);
        return updatedAsset;
    }

    private Asset createIdOnlyAsset(String id){
        Asset asset = new Asset();
        asset.setId(UUID.fromString(id));
        return asset;
    }

    private List<MobileTerminal> createListOfIdOnlyMTs(List<String> idList){
        List<MobileTerminal> mtList = new ArrayList<>();
        for (String s : idList) {
            MobileTerminal mt = new MobileTerminal();
            mt.setId(UUID.fromString(s));
            mtList.add(mt);
        }
        return mtList;
    }

}
