package eu.europa.ec.fisheries.uvms.asset.rest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;


@RunWith(Arquillian.class)
public class CustomCodesResourceTest extends AbstractAssetRestTest {

    private ObjectMapper MAPPER = new ObjectMapper();

    @Test
    @RunAsClient
    public void getConstants() {
        List<String> constants = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);
        System.out.println(constants);
    }

    @Test
    @RunAsClient
    public void getCodesPerConstant() throws IOException {

        // this is actually not a test yet but it shows how to parse resulting json without a DTO

        // get a list of constants;
        List<String> constants = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);


        // for every constant
        for(String constant : constants){


            String json = getWebTarget()
                    .path("customcodes")
                    .path("getcodesforconstant")
                    .path(constant)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);


            JsonNode jsonNode = MAPPER.readTree(json);

            for(JsonNode val : jsonNode){
                String cst = val.path("primaryKey").path("constant").asText();
                String cd = val.path("primaryKey").path("code").asText();
                String description = val.path("description").asText();
                String embeddedJson = val.path("jsonstr").asText();

                // here we could parse the embedded json  (probably different for every constant




                System.out.println("------------------------------------------------------");
                System.out.println(cst);
                System.out.println(cd);
                System.out.println(description);
                System.out.println(embeddedJson);
            }









        }


    }
}





