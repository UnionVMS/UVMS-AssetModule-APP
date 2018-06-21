package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;

@RunWith(Arquillian.class)
public class AssetGroupResourceTest extends AbstractAssetRestTest {

    @Test
    @RunAsClient
    public void createAssetGroupCheckResponseCodeTest() throws Exception {
        
        AssetGroup assetGroup = AssetHelper.createBasicAssetGroup();
        
        Response response = getWebTarget()
                .path("/group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(assetGroup));
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
    }
    
    @Test
    @RunAsClient
    public void createAssetTest() throws Exception {

        AssetGroup assetGroup = AssetHelper.createBasicAssetGroup();
        
        AssetGroup createdAssetGroup = getWebTarget()
                .path("/group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(assetGroup), AssetGroup.class);
        
        assertTrue(createdAssetGroup != null);
        
        assertThat(createdAssetGroup.getName(), is(assetGroup.getName()));
    }
    
    @Test
    @RunAsClient
    public void getAssetGroupListByUserNoUserParamTest() throws Exception {
        Response response = getWebTarget()
                .path("group")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
    }
    
    @Test
    @RunAsClient
    public void getAssetGroupListByUserTest() throws Exception {

        Response responseBefore = getWebTarget()
                .path("group")
                .path("list")
                .queryParam("user", "MOCK_USER") // From mock filter
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<AssetGroup> groupsBefore = responseBefore.readEntity(new GenericType<List<AssetGroup>>() {});
        
        getWebTarget()
                .path("group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(AssetHelper.createBasicAssetGroup()), AssetGroup.class);
        
        Response response = getWebTarget()
                .path("group")
                .path("list")
                .queryParam("user", "MOCK_USER") // From mock filter
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<AssetGroup> fetchedAssetGroups = response.readEntity(new GenericType<List<AssetGroup>>() {});
        
        assertTrue(fetchedAssetGroups != null);
        assertThat(fetchedAssetGroups.size(), is(groupsBefore.size() + 1));
    }
    
    @Test
    @RunAsClient
    public void getAssetGroupListByUserTwoGroupsTest() throws Exception {

        Response responseBefore = getWebTarget()
                .path("group")
                .path("list")
                .queryParam("user", "MOCK_USER") // From mock filter
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<AssetGroup> groupsBefore = responseBefore.readEntity(new GenericType<List<AssetGroup>>() {});
        
        getWebTarget()
                .path("group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(AssetHelper.createBasicAssetGroup()), AssetGroup.class);
        
        getWebTarget()
                .path("group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(AssetHelper.createBasicAssetGroup()), AssetGroup.class);
        
        Response response = getWebTarget()
                .path("group")
                .path("list")
                .queryParam("user", "MOCK_USER")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<AssetGroup> fetchedAssetGroups = response.readEntity(new GenericType<List<AssetGroup>>() {});
        
        assertTrue(fetchedAssetGroups != null);
        assertThat(fetchedAssetGroups.size(), is(groupsBefore.size() + 2));
    }
    
    @Test
    @RunAsClient
    public void getAssetGroupByIdTest() throws Exception {

        AssetGroup assetGroup = AssetHelper.createBasicAssetGroup();
        AssetGroup createdAssetGroup = getWebTarget()
                .path("group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(assetGroup), AssetGroup.class);
        
        AssetGroup fetchedAssetGroup = getWebTarget()
                .path("group")
                .path(createdAssetGroup.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get(AssetGroup.class);
        
       
        assertTrue(fetchedAssetGroup != null);
        assertThat(fetchedAssetGroup.getId(), is(createdAssetGroup.getId()));
        assertThat(fetchedAssetGroup.getName(), is(createdAssetGroup.getName()));
    }
    
    @Test
    @RunAsClient
    public void getAssetGroupListByAssetId() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        AssetGroup assetGroup = AssetHelper.createBasicAssetGroup();
        AssetGroup createdAssetGroup = getWebTarget()
                .path("group")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(assetGroup), AssetGroup.class);
        
        AssetGroupField field = new AssetGroupField();
        field.setField("GUID");
        field.setValue(createdAsset.getId().toString());
        
        getWebTarget()
                .path("group")
                .path(createdAssetGroup.getId().toString())
                .path("field")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(field));
        
        Response response = getWebTarget()
                .path("group")
                .path("asset")
                .path(createdAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<AssetGroup> fetchedAssetGroups = response.readEntity(new GenericType<List<AssetGroup>>() {});
       
        assertTrue(fetchedAssetGroups != null);
        assertThat(fetchedAssetGroups.size(), is(1));
        assertThat(fetchedAssetGroups.get(0).getId(), is(createdAssetGroup.getId()));
        assertThat(fetchedAssetGroups.get(0).getName(), is(createdAssetGroup.getName()));
    }
    
}
