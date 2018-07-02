package eu.europa.ec.fisheries.uvms.rest.asset.filter;

import static org.junit.Assert.assertThat;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class MTRequestFilterTest extends AbstractAssetRestTest {

    @Test
    public void checkMDCNoHeaderTest() {
        Response response = getExternalWebTarget()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        String requestId = response.getHeaderString("requestId");
        assertThat(requestId, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
    
    @Test
    public void checkMDCHeaderSetTest() {
        String requestId = UUID.randomUUID().toString();
        Response response = getExternalWebTarget()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .get();
        
        String returnedRequestId = response.getHeaderString("requestId");
        assertThat(returnedRequestId, CoreMatchers.is(requestId));
    }
}
