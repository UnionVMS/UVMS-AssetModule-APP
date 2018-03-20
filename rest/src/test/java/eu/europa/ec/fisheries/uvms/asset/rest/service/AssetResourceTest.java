package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;

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
    
}
