package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
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
import eu.europa.ec.fisheries.uvms.asset.rest.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
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
