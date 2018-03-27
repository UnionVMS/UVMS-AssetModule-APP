package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.UUID;
import javax.ws.rs.client.Entity;
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

@RunWith(Arquillian.class)
public class InternalResourceTest extends AbstractAssetRestTest {

    @Test
    @RunAsClient
    public void getAssetByIdNonValidIdentifierTest() {
        
        Response response = getWebTarget()
                .path("/internal/asset/apa/" + UUID.randomUUID())
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        assertThat(response.getStatus(), is(Status.NOT_FOUND.getStatusCode()));
                
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdGUIDTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/guid/" + createdAsset.getId())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdCfrTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/cfr/" + createdAsset.getCfr())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdIrcsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/ircs/" + createdAsset.getIrcs())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdImoTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/imo/" + createdAsset.getImo())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdMmsiTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/mmsi/" + createdAsset.getMmsi())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdIccatTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/iccat/" + createdAsset.getIccat())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdUviTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/uvi/" + createdAsset.getUvi())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIdGfcmTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        Asset fetchedAsset = getWebTarget()
                .path("/internal/asset/gfcm/" + createdAsset.getGfcm())
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
        
        assertThat(fetchedAsset, is(AssetMatcher.assetEquals(createdAsset)));
    }
}
