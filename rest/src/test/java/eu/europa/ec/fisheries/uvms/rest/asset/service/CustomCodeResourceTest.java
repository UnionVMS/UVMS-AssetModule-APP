package eu.europa.ec.fisheries.uvms.rest.asset.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class CustomCodeResourceTest extends AbstractAssetRestTest {
    // TODO also implement tests for embedded json when the need appears

    private ObjectMapper MAPPER;

    @Before
    public void before() {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getConstants() {
        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        List<String> constants = getWebTarget()
                .path("customcodes")
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(new GenericType<List<String>>(){});

        // resultSet must at least contain a constants with our created customCode
        boolean found = false;
        for (String constant : constants) {
            if (constant.toUpperCase().endsWith(txt.toUpperCase())) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getCodesPerConstant() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        createACustomCodeHelperMultipleCodesPerConstant(txt);

        // this is actually not a test yet but it shows how to parse resulting json without a DTO

        // get a list of constants;
        List<String> constants = getWebTarget()
                .path("customcodes")
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(new GenericType<List<String>>(){});

        // for every constants
        Boolean found = false;
        for (String constant : constants) {
            String json = getWebTarget()
                    .path("customcodes")
                    .path("listcodesforconstant")
                    .path(constant)
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getToken())
                    .get(String.class);
            TypeReference typeref = new TypeReference<List<CustomCode>>() {
            };
            List<CustomCode> codes = MAPPER.readValue(json, typeref);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void createACustomCode() throws IOException {
        String txt = UUID.randomUUID().toString();
        String createdJson = createACustomCodeHelper(txt);
        CustomCode customCodes = MAPPER.readValue(createdJson, CustomCode.class);
        assertTrue(customCodes.getPrimaryKey().getConstant().endsWith(txt));
    }

    // TODO DO NOT DELETE THIS !!!!!
    @Test
    @Ignore
    @RunAsClient
    public void createACustomCodeNoDateLimit() throws IOException {

        String createdJson = "";

        createdJson = createACustomCodeHelperNoDateLimit("UNIT_TONNAGE", "LONDON", "London");
        createdJson = createACustomCodeHelperNoDateLimit("UNIT_TONNAGE", "OSLO", "Oslo");
        createdJson = createACustomCodeHelperNoDateLimit("UNIT_LENGTH", "LOA", "Loa");
        createdJson = createACustomCodeHelperNoDateLimit("UNIT_LENGTH", "LBP", "Lbp");
        createdJson = createACustomCodeHelperNoDateLimit("ASSET_TYPE", "VESSEL", "Vessel");
        createdJson = createACustomCodeHelperNoDateLimit("LICENSE_TYPE", "MOCK-license-DB", "MOCK-license-DB");
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

        createdJson = createACustomCodeHelperNoDateLimit("FISHING_TYPE", "P", "Pelagic");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_TYPE", "D", "Demersal");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_TYPE", "DP", "Demersal and pelagic");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_TYPE", "U", "Unknown");

        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Trawl", "Trawl");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Scrapingtool", "Scrapingtool");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Lift nets", "Lift nets");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Gillnets and similar nets", "Gillnets and similar nets");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Trap", "Fishing trap");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Hooks and lines", "Hooks and lines");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Unknown", "Unknown fishing tool");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "None", "No fishing tool");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Surrounding nets", "Surrounding nets");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_TYPE", "Seines", "Seines");

        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_MOBILITY", "S", "Stationary");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_MOBILITY", "T", "Towed");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_MOBILITY", "M", "Mobil");
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR_MOBILITY", "U", "Unknown");


        // @formatter:off
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "PS", "Purse seines",                                  Arrays.asList("FISHING_GEAR_TYPE","Surrounding nets"         ,"FISHING_GEAR_MOBILITY","M","FISHING_TYPE","P"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LA", "Lampara nets",                                  Arrays.asList("FISHING_GEAR_TYPE","Surrounding nets"         ,"FISHING_GEAR_MOBILITY","M","FISHING_TYPE","P"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "SB", "Beach seines",                                  Arrays.asList("FISHING_GEAR_TYPE","Seines"                   ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "SDN", "Danish seines",                                Arrays.asList("FISHING_GEAR_TYPE","Seines"                   ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "SSC", "Scottish seines",                              Arrays.asList("FISHING_GEAR_TYPE","Seines"                   ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "SPR", "Pair seines",                                  Arrays.asList("FISHING_GEAR_TYPE","Seines"                   ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "TBB", "Beam trawls",                                  Arrays.asList("FISHING_GEAR_TYPE","Trawl"                    ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "OTB", "Bottom otter trawls",                          Arrays.asList("FISHING_GEAR_TYPE","Trawl"                    ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "PTB", "Bottom pair trawls",                           Arrays.asList("FISHING_GEAR_TYPE","Trawl"                    ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "OTM", "Midwater otter trawls",                        Arrays.asList("FISHING_GEAR_TYPE","Trawl"                    ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "PTM", "Pelagic pair trawls",                          Arrays.asList("FISHING_GEAR_TYPE","Trawl"                    ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "OTT", "Otter twin trawls",                            Arrays.asList("FISHING_GEAR_TYPE","Trawl"                    ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "DRB", "Boat dredges",                                 Arrays.asList("FISHING_GEAR_TYPE","Scrapingtool"             ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "DRH", "Hand dredges used on board a vessel",          Arrays.asList("FISHING_GEAR_TYPE","Scrapingtool"             ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "HMD", "Mechanised dredges including suction dredges", Arrays.asList("FISHING_GEAR_TYPE","Scrapingtool"             ,"FISHING_GEAR_MOBILITY","T","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LNB", "Boat operated lift nets",                      Arrays.asList("FISHING_GEAR_TYPE","Lift nets"                ,"FISHING_GEAR_MOBILITY","M","FISHING_TYPE","P"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LNS", "Shore-operated stationary lift nets",          Arrays.asList("FISHING_GEAR_TYPE","Lift nets"                ,"FISHING_GEAR_MOBILITY","M","FISHING_TYPE","P"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "GNS", "Set (anchored) gillnets",                      Arrays.asList("FISHING_GEAR_TYPE","Gillnets and similar nets","FISHING_GEAR_MOBILITY","S","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "GND", "Driftnets",                                    Arrays.asList("FISHING_GEAR_TYPE","Gillnets and similar nets","FISHING_GEAR_MOBILITY","S","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "GNC", "Encircling gillnets",                          Arrays.asList("FISHING_GEAR_TYPE","Gillnets and similar nets","FISHING_GEAR_MOBILITY","S","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "GTR", "Trammel nets",                                 Arrays.asList("FISHING_GEAR_TYPE","Gillnets and similar nets","FISHING_GEAR_MOBILITY","S","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "GTN", "Combined trammel and gillnets",                Arrays.asList("FISHING_GEAR_TYPE","Gillnets and similar nets","FISHING_GEAR_MOBILITY","S","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "FPO", "Pots (traps)",                                 Arrays.asList("FISHING_GEAR_TYPE","Trap"                     ,"FISHING_GEAR_MOBILITY","S","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LHP", "Hand lines and pole lines (hand operated)",    Arrays.asList("FISHING_GEAR_TYPE","Hooks and lines"          ,"FISHING_GEAR_MOBILITY","S","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LHM", "Hand lines and pole lines (mechanised)",       Arrays.asList("FISHING_GEAR_TYPE","Hooks and lines"          ,"FISHING_GEAR_MOBILITY","S","FISHING_TYPE","DP"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LLS", "Set longlines",                                Arrays.asList("FISHING_GEAR_TYPE","Hooks and lines"          ,"FISHING_GEAR_MOBILITY","S","FISHING_TYPE","D"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LLD", "Longlines (drifting)",                         Arrays.asList("FISHING_GEAR_TYPE","Hooks and lines"          ,"FISHING_GEAR_MOBILITY","S","FISHING_TYPE","P"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "LTL", "Troll lines",                                  Arrays.asList("FISHING_GEAR_TYPE","Hooks and lines"          ,"FISHING_GEAR_MOBILITY","M","FISHING_TYPE","P"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "NK", "Unknown",                                       Arrays.asList("FISHING_GEAR_TYPE","Unknown"                  ,"FISHING_GEAR_MOBILITY","U","FISHING_TYPE","U"));
        createdJson = createACustomCodeHelperNoDateLimit("FISHING_GEAR", "NO", "No gear",                                       Arrays.asList("FISHING_GEAR_TYPE","None"                     ,"FISHING_GEAR_MOBILITY","U","FISHING_TYPE","U"));
// @formatter:on
    }

    private String createACustomCodeHelperNoDateLimit(String constant, String code, String descr, List<String> references) throws JsonProcessingException {

        CustomCodesPK primaryKey = new CustomCodesPK(constant, code);
        CustomCode customCode = new CustomCode();
        customCode.setPrimaryKey(primaryKey);
        if (descr.length() < 1) {
            customCode.setDescription(code);
        } else {
            customCode.setDescription(descr);
        }

        Map<String,String> nvp = new HashMap<>();

        //  0 2 4 6 n = keys   1 3 5 7 m = values
        int n = references.size();
        int i = 0;
        while(i < n){
            nvp.put(references.get(i),references.get(i + 1));
            i = i + 2;
        }

        customCode.setNameValue(nvp);

        // TODO add code for Map<String,String >   property in CustomCode
        // AND a rest endpoint

        String json = MAPPER.writeValueAsString(customCode);

        String created = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(json), String.class);

        return "";
    }

    private String createACustomCodeHelperNoDateLimit(String constant, String code, String descr) {
        CustomCodesPK primaryKey = new CustomCodesPK(constant, code);
        CustomCode customCode = new CustomCode();
        customCode.setPrimaryKey(primaryKey);
        if (descr.length() < 1) {
            customCode.setDescription(code);
        } else {
            customCode.setDescription(descr);
        }
        String created = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(customCode), String.class);
        return created;
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

        String fromDate = customCodesPk.getValidFromDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String toDate = customCodesPk.getValidToDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String json = getWebTarget()
                .path("customcodes")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);
        CustomCode customCodes = MAPPER.readValue(json, CustomCode.class);

        assertTrue(customCodes.getPrimaryKey().getConstant().endsWith(txt));
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteCustomCode() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();

        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();
        String fromDate = customCodesPk.getValidFromDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String toDate = customCodesPk.getValidToDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Boolean exists = getWebTarget()
                .path("customcodes")
                .path("exists")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(Boolean.class);

        assertTrue(exists);

        String jsondelete = getWebTarget()
                .path("customcodes")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .delete(String.class);


        exists = getWebTarget()
                .path("customcodes")
                .path("exists")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(fromDate)
                .path(toDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(Boolean.class);

        Assert.assertFalse(exists);
    }

    private String createACustomCodeHelper(String txt) {

        OffsetDateTime from = OffsetDateTime.now(Clock.systemUTC());
        from = from.minusDays(5);
        OffsetDateTime to = OffsetDateTime.now(Clock.systemUTC());
        to = from.plusDays(5);

        String constant = "CST____" + txt;
        String code = "CODE___" + txt;
        String descr = "DESCR__" + txt;

        CustomCodesPK primaryKey = new CustomCodesPK(constant, code, from, to);
        CustomCode customCode = new CustomCode();
        customCode.setPrimaryKey(primaryKey);
        customCode.setDescription(descr);

        Map<String,String> nvp = new HashMap<>();
        nvp.put("fishmobil", "testtesttest");
        nvp.put("fishskr", "zzzzzzzzz");
        customCode.setNameValue(nvp);

        String created = getWebTarget()
                .path("customcodes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(customCode), String.class);
        return created;
    }


    private void createACustomCodeHelperMultipleCodesPerConstant(String txt) {

        OffsetDateTime from = OffsetDateTime.now(Clock.systemUTC());
        from = from.minusDays(5);
        OffsetDateTime to = OffsetDateTime.now(Clock.systemUTC());
        to = from.plusDays(5);

        for (int i = 0; i < 5; i++) {

            String constant = "CST____" + txt;
            String code = "CODE___" + String.valueOf(i) + "_" + txt;
            String descr = "DESCR__" + txt;

            CustomCodesPK primaryKey = new CustomCodesPK(constant, code, from, to);
            CustomCode customCode = new CustomCode();
            customCode.setPrimaryKey(primaryKey);
            customCode.setDescription(descr);

            String created = getWebTarget()
                    .path("customcodes")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getToken())
                    .post(Entity.json(customCode), String.class);

        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void verifyCustomCodeInDateRangePositive() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        OffsetDateTime  date  = customCodesPk.getValidFromDate();

        OffsetDateTime  dateWithinRange = date.plusDays(1);

        String dateToTest = dateWithinRange.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String json = getWebTarget()
                .path("customcodes")
                .path("getfordate")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);
        // record existed alles ok
        TypeReference typeref = new TypeReference<List<CustomCode>>() {
        };
        List<CustomCode> codes = MAPPER.readValue(json, typeref);

        assertNotNull(codes);
        assertTrue(codes.size() > 0);
    }


    @Test
    @OperateOnDeployment("normal")
    public void verifyCustomCodeInDateRangeNegative() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        OffsetDateTime  date  = customCodesPk.getValidFromDate();

        OffsetDateTime  dateWithoutRange = date.minusDays(2);

        String dateToTest = dateWithoutRange.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String json = getWebTarget()
                .path("customcodes")
                .path("getfordate")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);

        // record existed NOT as expected alles ok
        TypeReference typeReference = new TypeReference<List<CustomCode>>() {
        };
        List<CustomCode> codes = MAPPER.readValue(json, typeReference);
        assertNotNull(codes);
        assertEquals(0, codes.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void existsCustomCodeInDateRangePositive() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        OffsetDateTime  date  = customCodesPk.getValidFromDate();

        OffsetDateTime  dateWithinRange = date.plusDays(1);

        String dateToTest = dateWithinRange.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Boolean ret = getWebTarget()
                .path("customcodes")
                .path("verify")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(Boolean.class);
        // record existed alles ok
        assertTrue(ret);
    }

    @Test
    @OperateOnDeployment("normal")
    public void existsCustomCodeInDateRangeNegative() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);

        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();

        OffsetDateTime  date  = customCodesPk.getValidFromDate();

        OffsetDateTime  dateWithoutRange = date.minusDays(2);

        String dateToTest = dateWithoutRange.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Boolean ret = getWebTarget()
                .path("customcodes")
                .path("verify")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateToTest)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(Boolean.class);

        // record existed NOT as expected alles ok
        Assert.assertFalse(ret);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getCodeAtDateWithinRange() throws IOException {

        String txt = UUID.randomUUID().toString().toUpperCase();
        String createdJson = createACustomCodeHelper(txt);
        CustomCode customCode = MAPPER.readValue(createdJson, CustomCode.class);
        CustomCodesPK customCodesPk = customCode.getPrimaryKey();
        OffsetDateTime  date  = customCodesPk.getValidFromDate();

        OffsetDateTime  dateWithinRange = date.plusDays(2);

        String dateWithin = dateWithinRange.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String json = getWebTarget()
                .path("customcodes")
                .path("getfordate")
                .path(customCodesPk.getConstant())
                .path(customCodesPk.getCode())
                .path(dateWithin)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);

        TypeReference typeref = new TypeReference<List<CustomCode>>() {
        };
        List<CustomCode> codes = MAPPER.readValue(json, typeref);
        assertNotNull(codes);
        assertTrue(codes.size() > 0);
    }
}
