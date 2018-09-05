package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetMatcher;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetResourceTest extends AbstractAssetRestTest {
       
    @Test
    public void createAssetCheckResponseCodeTest() throws Exception {
        
        Asset asset = AssetHelper.createBasicAsset();
        
        Response response = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset));
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
    }
    
    @Test
    public void createAssetTest() throws Exception {
        
        Asset asset = AssetHelper.createBasicAsset();
        
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        assertTrue(createdAsset != null);
        
        assertThat(createdAsset.getCfr(), is(asset.getCfr()));
    }
    
    @Test
    public void getAssetByIdTest() throws Exception {
        
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
        
        assertTrue(fetchedAsset != null);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    public void getAssetByIdRandomValueTest() throws Exception {
        Asset asset = getWebTarget()
                .path("asset")
                .path(UUID.randomUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertTrue(asset == null);
    }
    
    @Test
    public void getAssetByIdInvalidIdTest() throws Exception {
        Response response = getWebTarget()
                .path("asset")
                .path("nonExistingAssetId")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        assertTrue(response != null);
        assertThat(response.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode())); //until someone has made a better errorhandler that can send a 404 only when neccessary, this one will return 500
    }
    
    @Test
    public void updateAssetChangedNameTest() throws Exception {
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
        
        Response response = getWebTarget()
                .path("asset")
                .path("history/asset")
                .path(updatedAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<Asset> assetRevisions = response.readEntity(new GenericType<List<Asset>>() {});
        
        assertTrue(assetRevisions != null);
        assertThat(assetRevisions.size() , is(2));
    }
    
    @Test
    public void updateAssetNonExistingAssetTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        Response response = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(asset));
        
        assertTrue(response != null);
        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
    }
    
    @Test
    public void archiveAssetTest() throws Exception {
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
        
        assertTrue(archivedAsset != null);
        assertThat(archivedAsset.getActive() , is(false));
    }
    
    @Test
    public void archiveAssetNonExistingAssetTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        
        Response response = getWebTarget()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(asset));
        
        assertTrue(response != null);
        assertThat(response.getStatus() , is(Status.BAD_REQUEST.getStatusCode()));
    }
    
    @Test
    public void getAssetFromAssetIdAndDateCfrTest() throws Exception {
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
    public void getAssetFromAssetIdPastDateTest() throws Exception {


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
    public void getPastAssetFromAssetIdPastDateTest() throws Exception {


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

    @Test
    public void checkPastNumberOfMTTest() throws Exception {


        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        MobileTerminalType mobileTerminal1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal1.setConnectId(createdAsset.getId().toString());
        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal1), String.class);


        OffsetDateTime timeStamp = OffsetDateTime.now(ZoneOffset.UTC);

        MobileTerminalType mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.setConnectId(createdAsset.getId().toString());
        String response2 = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal2), String.class);

        Asset presentAsset = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertEquals(2, presentAsset.getMobileTerminalEvent().size());

        Asset pastAsset = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(timeStamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(pastAsset);
        assertEquals(1, pastAsset.getMobileTerminalEvent().size());

    }

    @Test
    public void getAssetAndConnectedMobileTerminalTest() throws Exception {


        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminalType.setConnectId(createdAsset.getId().toString());
        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalType), String.class);
        assertNotNull(response);
        MobileTerminalType mobileTerminal = deserializeResponseDto(response, MobileTerminalType.class);

        Asset fetchedAsset = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(fetchedAsset);
        assertEquals(1, fetchedAsset.getMobileTerminalEvent().size());
        assertEquals(mobileTerminal.getMobileTerminalId().getGuid(), fetchedAsset.getMobileTerminalEvent().get(0).getMobileterminal().getId().toString());

    }

    @Test
    public void getAssetFromAssetIdPastDateTestWithDateToEarly() throws Exception {

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
    public void getAssetHistoryByAssetHistGuidTest() {
        WebTarget webTarget = getWebTarget();

        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = webTarget
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = webTarget
                .path("asset")
                .path("history")
                .path(createdAsset.getHistoryId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    public void getAssetHistoryByAssetHistGuidTwoRevisionsTest() throws Exception {
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
        
        assertTrue(createdNote != null);
        assertThat(createdNote.getNotes(), is(note.getNotes()));
    }
    
    @Test
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
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
        
        List<Note> fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(1));
        assertThat(fetchedNotes.get(0).getNotes(), is(createdNote.getNotes()));
    }
    
    @Test
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
        
        assertTrue(response != null);
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
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
        
        response = getWebTarget()
                .path("asset")
                .path(createdAsset.getId().toString())
                .path("notes")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
        
        fetchedNotes = response.readEntity(new GenericType<List<Note>>() {});
        assertThat(fetchedNotes.size(), is(0));
    }
}
