package eu.europa.ec.fisheries.uvms.rest.asset.filter;

import static org.junit.Assert.assertThat;
import java.util.UUID;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class MTRequestFilterTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void checkMDCNoHeaderTest() {
        Response response = getWebTarget()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get();
        
        String requestId = response.getHeaderString("requestId");
        assertThat(requestId, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void checkMDCHeaderSetTest() {
        String requestId = UUID.randomUUID().toString();
        Response response = getWebTarget()
                .path("internal")
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get();
        
        String returnedRequestId = response.getHeaderString("requestId");
        assertThat(returnedRequestId, CoreMatchers.is(requestId));
    }
}
