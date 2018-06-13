package eu.europa.ec.fisheries.uvms.asset.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.asset.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RunWith(Arquillian.class)
public class CustomCodeResourceTest extends AbstractAssetRestTest {
    // TODO also implement tests for embedded json when the need appears

    private ObjectMapper MAPPER;


    @Before
    public void before() {

        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());

    }

    @Test
    @RunAsClient
    public void getConstants() {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        List<String> constants = getWebTarget()
                .path("customcodes")
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);
        // resultset must at least contain a constants with our created customcode

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
        createACustomCodeHelperMultipleCodesPerConstant(txt);

        // this is actually not a test yet but it shows how to parse resulting json without a DTO

        // get a list of constants;
        List<String> constants = getWebTarget()
                .path("customcodes")
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);

        // for every constants
        Boolean found = false;
        for (String constant : constants) {
            String json = getWebTarget()
                    .path("customcodes")
                    .path("listcodesforconstant")
                    .path(constant)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            TypeReference typeref = new TypeReference<List<CustomCode>>() {};
            List<CustomCode> codes = MAPPER.readValue(json, typeref);
        }
       // Assert.assertTrue(found);
    }

    @Test
    @RunAsClient
    public void createACustomCode() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        CustomCode customCodes = MAPPER.readValue(createdJson, CustomCode.class);

        Assert.assertTrue(customCodes.getPrimaryKey().getConstant().endsWith(txt));
    }

    @Test
    @RunAsClient
    public void getACustomCode() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();

        String createdJson = createACustomCodeHelper(txt);
        String constant = "CST____" + txt;
        String code = "CODE___" + txt;


        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        String fromDate = customCodesPk.getValidFromDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String toDate = customCodesPk.getValidToDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);


        String json = getWebTarget()
                .path("customcodes")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        CustomCode customCodes = MAPPER.readValue(json, CustomCode.class);


        Assert.assertTrue(customCodes.getPrimaryKey().getConstant().endsWith(txt));


    }

    @Test
    @RunAsClient
    public void deleteCustomCode() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();

        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();
        String fromDate = customCodesPk.getValidFromDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String toDate = customCodesPk.getValidToDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Boolean exists = getWebTarget()
                .path("customcodes")
                .path("exists")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .get(Boolean.class);

        Assert.assertTrue(exists);

        String jsondelete = getWebTarget()
                .path("customcodes")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .delete(String.class);



        exists = getWebTarget()
                .path("customcodes")
                .path("exists")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .get(Boolean.class);

        Assert.assertFalse(exists);

    }


    private String createACustomCodeHelper(String txt) {

        LocalDateTime from = LocalDateTime.now(Clock.systemUTC());
        from = from.minusDays(5);
        LocalDateTime to = LocalDateTime.now(Clock.systemUTC());
        to = from.plusDays(5);


        String constant = "CST____" + txt;
        String code = "CODE___" + txt;
        String descr = "DESCR__" + txt;

        CustomCodesPK primaryKey = new CustomCodesPK(constant, code, from, to);
        CustomCode customCode = new CustomCode();
        customCode.setPrimaryKey(primaryKey);
        customCode.setDescription(descr);



        String created = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(customCode), String.class);
        return created;
    }

    private void createACustomCodeHelperMultipleCodesPerConstant(String txt) {

        LocalDateTime from = LocalDateTime.now(Clock.systemUTC());
        from = from.minusDays(5);
        LocalDateTime to = LocalDateTime.now(Clock.systemUTC());
        to = from.plusDays(5);


        for(int i = 0 ; i < 5 ; i++) {

            String constant = "CST____" + txt;
            String code = "CODE___" + String.valueOf(i) + "_"+txt;
            String descr = "DESCR__" + txt;

            CustomCodesPK primaryKey = new CustomCodesPK(constant, code, from, to);
            CustomCode customCode = new CustomCode();
            customCode.setPrimaryKey(primaryKey);
            customCode.setDescription(descr);

            String created = getWebTarget()
                    .path("customcodes")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(customCode), String.class);

        }
    }




    @Test
    @RunAsClient
    public void verifyCustomCodeInDateRangePositive() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        LocalDateTime  date  = customCodesPk.getValidFromDate();

        LocalDateTime  dateWithinRange = date.plusDays(1);

        String dateToTest = dateWithinRange.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String  json = getWebTarget()
                .path("customcodes")
                .path("getfordate")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        // record existed alles ok
        TypeReference typeref = new TypeReference<List<CustomCode>>() {};
        List<CustomCode> codes = MAPPER.readValue(json, typeref);

        Assert.assertTrue(codes != null);
        Assert.assertTrue(codes.size() > 0);
    }


    @Test
    @RunAsClient
    public void verifyCustomCodeInDateRangeNegative() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        LocalDateTime  date  = customCodesPk.getValidFromDate();

        LocalDateTime  dateWithoutRange = date.minusDays(2);

        String dateToTest = dateWithoutRange.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String json = getWebTarget()
                .path("customcodes")
                .path("getfordate")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        // record existed NOT as expected alles ok
        TypeReference typeref = new TypeReference<List<CustomCode>>() {};
        List<CustomCode> codes = MAPPER.readValue(json, typeref);
        Assert.assertTrue(codes != null);
        Assert.assertTrue(codes.size() == 0);


    }


    @Test
    @RunAsClient
    public void existsCustomCodeInDateRangePositive() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        LocalDateTime  date  = customCodesPk.getValidFromDate();

        LocalDateTime  dateWithinRange = date.plusDays(1);

        String dateToTest = dateWithinRange.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Boolean ret = getWebTarget()
                .path("customcodes")
                .path("verify")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .get(Boolean.class);
        // record existed alles ok
        Assert.assertTrue(ret);
    }


    @Test
    @RunAsClient
    public void existsCustomCodeInDateRangeNegative() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        LocalDateTime  date  = customCodesPk.getValidFromDate();

        LocalDateTime  dateWithoutRange = date.minusDays(2);

        String dateToTest = dateWithoutRange.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Boolean ret = getWebTarget()
                .path("customcodes")
                .path("verify")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .get(Boolean.class);

        // record existed NOT as expected alles ok
        Assert.assertFalse(ret);
    }

    @Test
    @RunAsClient
    public void getCodeAtDateWithinRange() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();
        LocalDateTime  date  = customCodesPk.getValidFromDate();

        LocalDateTime  dateWithinRange = date.plusDays(2);

        String dateWithin = dateWithinRange.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String json = getWebTarget()
                .path("customcodes")
                .path("getfordate")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateWithin)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        TypeReference typeref = new TypeReference<List<CustomCode>>() {};
        List<CustomCode> codes = MAPPER.readValue(json, typeref);
        Assert.assertTrue(codes != null);
        Assert.assertTrue(codes.size() > 0);


    }







}





