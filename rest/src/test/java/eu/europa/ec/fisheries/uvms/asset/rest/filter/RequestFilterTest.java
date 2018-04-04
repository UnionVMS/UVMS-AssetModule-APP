package eu.europa.ec.fisheries.uvms.asset.rest.filter;

import static org.junit.Assert.assertThat;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;

@RunWith(Arquillian.class)
public class RequestFilterTest extends AbstractAssetRestTest {

    @Test
    @RunAsClient
    public void checkMDCNoHeaderTest() {
        Response response = getWebTarget()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        String requestId = response.getHeaderString("requestId");
        assertThat(requestId, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
    
    @Test
    @RunAsClient
    public void checkMDCHeaderSetTest() {
        String requestId = UUID.randomUUID().toString();
        Response response = getWebTarget()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .get();
        
        String returnedRequestId = response.getHeaderString("requestId");
        assertThat(returnedRequestId, CoreMatchers.is(requestId));
    }
}
