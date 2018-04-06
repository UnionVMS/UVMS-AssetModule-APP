package eu.europa.ec.fisheries.uvms.asset.rest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodes;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RunWith(Arquillian.class)
public class CustomCodesResourceTest extends AbstractAssetRestTest {

    private ObjectMapper MAPPER = new ObjectMapper();

    @Test
    @RunAsClient
    public void getConstants() {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        List<String> constants = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);
        // resultset must at least contain a constant with our created customcode

        Boolean found = false;
        for (String constant : constants) {
            if (constant.toUpperCase().endsWith(txt.toUpperCase())) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    @RunAsClient
    public void getCodesPerConstant() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        // this is actually not a test yet but it shows how to parse resulting json without a DTO

        // get a list of constants;
        List<String> constants = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);



        // for every constant
        Boolean found = false;
        for (String constant : constants) {

            String json = getWebTarget()
                    .path("customcodes")
                    .path("getcodesforconstant")
                    .path(constant)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);

            JsonNode jsonNode = MAPPER.readTree(json);

            for (JsonNode val : jsonNode) {
                String cst = val.path("primaryKey").path("constant").asText();
                String cd = val.path("primaryKey").path("code").asText();
                String description = val.path("description").asText();
                String embeddedJson = val.path("jsonstr").asText();

                // here we could parse the embedded json  (probably different for every constant

                if (cd.toUpperCase().endsWith(txt.toUpperCase())) {
                    found = true;
                }
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    @RunAsClient
    public void createACustomCode() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        CustomCodes customCodes = MAPPER.readValue(createdJson, CustomCodes.class);


        /*
        System.out.println("       " + txt);
        System.out.println(customCodes.getPrimaryKey().getConstant());
        System.out.println(customCodes.getPrimaryKey().getCode());
        System.out.println(customCodes.getDescription());
        System.out.println(customCodes.getJsonstr());
        */

        Assert.assertTrue(customCodes.getPrimaryKey().getConstant().endsWith(txt));


    }


    private String createACustomCodeHelper(String txt) {


        String constant = "CST____" + txt;
        String code = "CODE___" + txt;
        String descr = "DESCR__" + txt;
        String embeddedjson = "EMB____" + txt;


        Response resp = getWebTarget()
                .path("customcodes")
                .path(constant)
                .path(code)
                .path(descr)
                .path(embeddedjson)
                .request(MediaType.APPLICATION_JSON)
                .post(null, Response.class);
        String entity = resp.readEntity(String.class);
        return entity;

    }


}





