package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.asset.rest.AssetHelper;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;

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
}
