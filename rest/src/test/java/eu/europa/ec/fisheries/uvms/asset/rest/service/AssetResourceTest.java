package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.asset.rest.AssetHelper;
import eu.europa.ec.fisheries.uvms.asset.rest.AssetMatcher;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.Note;

@RunWith(Arquillian.class)
public class AssetResourceTest extends AbstractAssetRestTest {
       
    @Test
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
    public void getAssetByIdRandomValueTest() throws Exception {
        Asset asset = getWebTarget()
                .path("asset")
                .path(UUID.randomUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertTrue(asset == null);
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdInvalidIdTest() throws Exception {
        Response response = getWebTarget()
                .path("asset")
                .path("nonExistingAssetId")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        assertTrue(response != null);
        assertThat(response.getStatus(), is(Status.NOT_FOUND.getStatusCode()));
    }
    
    @Test
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
    public void getAssetFromAssetIdAndDateCfrTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        LocalDateTime firstTimeStamp = LocalDateTime.now(ZoneOffset.UTC);
        
        String newName = "NewAssetName";
        createdAsset.setName(newName);
        getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);
        
        LocalDateTime secondTimeStamp = LocalDateTime.now(ZoneOffset.UTC);
        
        Asset assetByCfrAndTimestamp1 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(firstTimeStamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(assetByCfrAndTimestamp1.getName(), is(asset.getName()));
        
        Asset assetByCfrAndTimestamp2 = getWebTarget()
                .path("asset")
                .path("history")
                .path("cfr")
                .path(createdAsset.getCfr())
                .path(secondTimeStamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(assetByCfrAndTimestamp2.getName(), is(newName));
    }
    
    @Test
    @RunAsClient
    public void getAssetFromAssetIdPastDateTest() throws Exception {
        LocalDateTime timeStamp = LocalDateTime.now(ZoneOffset.UTC);

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
                .path(timeStamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertNull(assetByCfrAndTimestamp1);
    }
    
    @Test
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
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
    @RunAsClient
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
