package eu.europa.ec.fisheries.uvms.asset.rest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        createACustomCodeHelperMultipleCodesPerConstant(txt);

        // this is actually not a test yet but it shows how to parse resulting json without a DTO

        // get a list of constants;
        List<String> constants = getWebTarget()
                .path("customcodes")
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);

        // for every constant
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
    public void createACustomCodeNoDateLimit() throws IOException {

        String createdJson = "";

        createdJson = createACustomCodeHelperNoDateLimit("UNIT_TONNAGE", "LONDON", "London");
        createdJson = createACustomCodeHelperNoDateLimit("UNIT_TONNAGE", "OSLO", "Oslo");
        createdJson = createACustomCodeHelperNoDateLimit("UNIT_LENGTH", "LOA", "Loa");
        createdJson = createACustomCodeHelperNoDateLimit("UNIT_LENGTH", "LBP", "Lbp");
        createdJson = createACustomCodeHelperNoDateLimit("ASSET_TYPE", "VESSEL", "Vessel");
        createdJson = createACustomCodeHelperNoDateLimit("LICENSE_TYPE", "MOCK-license-DB", "MOCK-license-DB");
        createdJson = createACustomCodeHelperNoDateLimit("GEAR_TYPE", "PELAGIC", "Pelagic");
        createdJson = createACustomCodeHelperNoDateLimit("GEAR_TYPE", "DERMERSAL", "Demersal");
        createdJson = createACustomCodeHelperNoDateLimit("GEAR_TYPE", "DEMERSAL_AND_PELAGIC", "Demersal and pelagic");
        createdJson = createACustomCodeHelperNoDateLimit("GEAR_TYPE", "UNKNOWN", "Unknown");
        createdJson = createACustomCodeHelperNoDateLimit("FLAG_STATE", "SWE", "Sverige");
        createdJson = createACustomCodeHelperNoDateLimit("FLAG_STATE", "DNK", "Danmark");
        createdJson = createACustomCodeHelperNoDateLimit("FLAG_STATE", "NOR", "Norge");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_LENGTH_LOA", "0-11,99", "0-11,99");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_LENGTH_LOA", "12-14,99", "12-14,99");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_LENGTH_LOA", "15-17,99", "15-17,99");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_LENGTH_LOA", "18-23,99", "18-23,99");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_LENGTH_LOA", "24+", "24+");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_POWER_MAIN", "0-99", "0-99");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_POWER_MAIN", "100-199", "100-199");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_POWER_MAIN", "200-299", "200-299");
        createdJson = createACustomCodeHelperNoDateLimit("SPAN_POWER_MAIN", "300+", "300+");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "0", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "1", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "10", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "12", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "14", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "2", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "3", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "39", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "4", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "5", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "6", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "7", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "71", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "8", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "80", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "81", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "82", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "9", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "90", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "98", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "99", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "999", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "EL1", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "EL2", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "EL3", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "EL4", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K01", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K02", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K03", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K04", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K06", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K07", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K08", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "K09", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "SAN", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "SAT", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "T1", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "T2", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "U1", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "U2", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "U3", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V00", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V01", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V02", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V03", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V04", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V05", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V06", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V07", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V08", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V09", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V10", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V11", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V12", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V13", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V14", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V15", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V16", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V17", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V18", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V19", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V20", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V21", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V22", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V23", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V24", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V25", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V26", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V28", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V29", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V30", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V31", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V32", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V33", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V35", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V40", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V41", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V42", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V43", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V45", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V49", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V50", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V51", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V52", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V53", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V54", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V60", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V61", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V62", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V63", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V65", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V75", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V80", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V85", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V86", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V90", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V92", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V93", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V95", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V96", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V98", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "V99", "");
        createdJson = createACustomCodeHelperNoDateLimit("ACTIVITY_CODE", "X1", "");
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

    private String createACustomCodeHelperNoDateLimit(String constant,String code, String descr) {




        CustomCodesPK primaryKey = new CustomCodesPK(constant, code);
        CustomCode customCode = new CustomCode();
        customCode.setPrimaryKey(primaryKey);

        if(descr.length() < 1){
            customCode.setDescription(code);
        }else {
            customCode.setDescription(descr);
        }



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





