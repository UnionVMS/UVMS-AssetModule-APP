package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.asset.rest.AssetHelper;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.entity.Asset;

@RunWith(Arquillian.class)
public class AssetResourceTest extends AbstractAssetRestTest {
    
    @Test
    @RunAsClient
    public void getNotesActivityCodesTest() throws Exception {
        Response response = getWebTarget()
                .path("/asset/activitycodes")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
    }
    
    @Test
    @RunAsClient
    public void createAssetTest() throws Exception {
        
        Asset asset = AssetHelper.createBasicAsset();
        
        Response response = getWebTarget()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset));
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
    }
    
    @Ignore
    @Test
    @RunAsClient
    public void getAssetListQueryTest() {
        AssetListQuery query = AssetHelper.createBasicQuery();
        AssetListCriteriaPair criteria = new AssetListCriteriaPair();
        criteria.setKey(ConfigSearchFieldEnum.FLAG_STATE);
        criteria.setValue("SWE");
        query.getAssetSearchCriteria().getCriterias().add(criteria);
        
        Response response = getWebTarget()
                .path("/asset/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query));
        
        assertTrue(response != null);
        assertEquals(200, response.getStatus());
    }
}
