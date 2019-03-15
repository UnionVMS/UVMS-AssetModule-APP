package eu.europa.ec.fisheries.uvms.rest.asset.service;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetResourceTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckResponseCodeTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset));

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);
        assertThat(createdAsset.getCfr(), is(asset.getCfr()));
        assertEquals(EventCode.MOD.value(), createdAsset.getEventCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(fetchedAsset);
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdRandomValueTest() {
        Asset asset = getWebTarget()
                .path("asset")
                .path(UUID.randomUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNull(asset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIdInvalidIdTest() {
        Response response = getWebTarget()
                .path("asset")
                .path("nonExistingAssetId")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        //until someone has made a better errorHandler that can send a 404 only when necessary, this one will return 500
        assertThat(response.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetChangedNameTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        Asset updatedAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);

        assertThat(updatedAsset.getName(), is(newName));
        assertEquals(EventCode.MOD.value(), updatedAsset.getEventCode());

        Response response = getWebTarget()
                .path("asset")
                .path("history/asset")
                .path(updatedAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<Asset> assetRevisions = response.readEntity(new GenericType<List<Asset>>() {});

        assertNotNull(assetRevisions);
        assertThat(assetRevisions.size() , is(2));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetNonExistingAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
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
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Asset archivedAsset = getWebTarget()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);

        assertNotNull(archivedAsset);
        assertThat(archivedAsset.getActive() , is(false));
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAssetWithMobileTerminal_ThenMobileTerminalIsInactivatedTest() {

        // Create Asset
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        assertNotNull(createdAsset.getId());

        // Create MobileTerminal
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal createdMT = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);
        assertNotNull(createdMT.getId());

        // Assign MobileTerminal
        MobileTerminal assignedToAsset = getWebTarget()
                .path("mobileterminal")
                .path("assign")
                .queryParam("comment", "Assigning")
                .queryParam("connectId", createdAsset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdMT.getId()), MobileTerminal.class);
        assertNotNull(assignedToAsset.getId());

        // Archive Asset
        Asset archivedAsset = getWebTarget()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);
        assertNotNull(archivedAsset);
        assertThat(archivedAsset.getActive() , is(false));
        assertTrue(archivedAsset.getMobileTerminals().isEmpty());

        // UnAssign MobileTerminal
        MobileTerminal unassigned = getWebTarget()
                .path("mobileterminal")
                .path("unassign")
                .queryParam("comment", "UnAssigning")
                .queryParam("connectId", createdAsset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdMT.getId()), MobileTerminal.class);
        assertNotNull(unassigned.getId());

        // Inactivate MobileTerminal
        MobileTerminal inactivated = getWebTarget()
                .path("mobileterminal")
                .path("status")
                .path("inactivate")
                .queryParam("comment", "UnAssigning")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(unassigned.getId()), MobileTerminal.class);
        assertNotNull(inactivated.getId());
        assertTrue(inactivated.getInactivated());
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAssetNonExistingAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTarget()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
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
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        OffsetDateTime firstTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);

        OffsetDateTime secondTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        Asset assetByCfrAndTimestamp1 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(firstTimeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertThat(assetByCfrAndTimestamp1.getName(), is(asset.getName()));

        Asset assetByCfrAndTimestamp2 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(secondTimeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertThat(assetByCfrAndTimestamp2.getName(), is(newName));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFromAssetIdPastDateTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        Asset assetByCfrAndTimestamp1 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(assetByCfrAndTimestamp1);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPastAssetFromAssetIdPastDateTest() {
        Asset asset = AssetHelper.createBasicAsset();
        String originalName = "Original Name";
        asset.setName(originalName);
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        createdAsset.setName("New Name");
        Asset updatedAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);
        assertNotNull(updatedAsset);

        Asset assetByCfrAndTimestamp1 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(assetByCfrAndTimestamp1);
        assertEquals(originalName, assetByCfrAndTimestamp1.getName());
    }

    @Ignore //since we no longer serialize the connection between asset and MT this will not work
    @Test
    @OperateOnDeployment("normal")
    public void checkPastNumberOfMTTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        MobileTerminal mobileTerminal1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal1.setAsset(createdAsset);

        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal1), String.class);

        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        MobileTerminal mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.setAsset(createdAsset);

        String response2 = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal2), String.class);

        Asset presentAsset = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        Asset pastAsset = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(pastAsset);
    }

    @Ignore //since we no longer serialize the connection between asset and MT this will not work
    @Test
    @OperateOnDeployment("normal")
    public void getAssetAndConnectedMobileTerminalTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        MobileTerminal mobileTerminal1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal1.setAsset(createdAsset);

        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal1), String.class);
        assertNotNull(response);
        MobileTerminalType mobileTerminal = deserializeResponseDto(response, MobileTerminalType.class);

        Asset fetchedAsset = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(fetchedAsset);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFromAssetIdPastDateTestWithDateToEarly() {
        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Asset assetByCfrAndTimestamp1 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNull(assetByCfrAndTimestamp1);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryByAssetHistGuidTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Asset fetchedAsset = getWebTarget()
                .path("asset")
                .path("history")
                .path(createdAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetHistoryByAssetHistGuidTwoRevisionsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        String newName = "NewAssetName";
        createdAsset.setName(newName);
        Asset updatedAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);

        Asset fetchedAsset = getWebTarget()
                .path("asset")
                .path("history")
                .path(createdAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getId(), is(createdAsset.getId()));

        Asset fetchedUpdatedAsset = getWebTarget()
                .path("asset")
                .path("history")
                .path(updatedAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertThat(fetchedUpdatedAsset, is(AssetMatcher.assetEquals(updatedAsset)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNoteTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Note note = AssetHelper.createBasicNote();

        Note createdNote = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(note), Note.class);

        assertNotNull(createdNote);
        assertThat(createdNote.getNotes(), is(note.getNotes()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getNotesForAssetTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Note note = AssetHelper.createBasicNote();

        Note createdNote = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(note), Note.class);

        Response response = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
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
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        Note note = AssetHelper.createBasicNote();

        // Create note
        Note createdNote = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(note), Note.class);

        Response response = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        List<Note> fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(1));

        // Delete note
        response = getWebTarget()
                .path("asset")
                .path("notes")
                .path(createdNote.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        response = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        
        fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetAndContactInfoAndCompareHistoryItemsTest() throws InterruptedException {

        // CREATE AN ASSET
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        // CREATE AN CONTACTINFO
        ContactInfo contactInfo = AssetHelper.createBasicContactInfo();
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
        ContactInfo createdContactInfo = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("contacts")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(contactInfo), ContactInfo.class);

        Thread.sleep(3000);

        // UPDATE THE ASSET
        final String newAssetName = "NewAssetName";
        createdAsset.setName(newAssetName);
        Asset updatedAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);

        assertEquals(newAssetName, updatedAsset.getName());
        assertEquals(EventCode.MOD.value(), updatedAsset.getEventCode());

        // UPDATE THE CONTACTINFO
        String newContactInfoName = "NewContactInfoName";
        createdContactInfo.setName(newContactInfoName);
        createdContactInfo.setAssetUpdateTime(updatedAsset.getUpdateTime());
        ContactInfo updatedContactInfo = getWebTarget()
                .path("asset")
                .path("contacts")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdContactInfo), ContactInfo.class);

        assertEquals(newContactInfoName, updatedContactInfo.getName());

        // GET ASSET HISTORY
        Response response = getWebTarget()
                .path("asset")
                .path("history/asset")
                .path(updatedAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<Asset> assetRevisions = response.readEntity(new GenericType<List<Asset>>() {});

        assertNotNull(assetRevisions);
        assertEquals(2, assetRevisions.size());

        // GET CONTACTINFO HISTORY FOR ASSET
        Response res = getWebTarget()
                .path("asset")
                .path(updatedAsset.getId().toString())
                .path("contacts")
                .path(updatedAsset.getUpdateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<ContactInfo> contactInfoRevisions = res.readEntity(new GenericType<List<ContactInfo>>() {});

        assertNotNull(contactInfoRevisions);
        assertEquals(1, contactInfoRevisions.size());
    }
}
