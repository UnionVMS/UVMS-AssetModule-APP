package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.AbstractMTRestTest;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;

import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
//@RunAsClient
public class PollRestResourceTest extends AbstractAssetRestTest {

    @Test
    public void getRunningProgramPollsTest() {

        String response = getWebTarget()
                .path("/poll/running")
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();

        assertThat(jsonObject.getInt("code"), CoreMatchers.is(MTResponseCode.OK.getCode()));
    }
}
