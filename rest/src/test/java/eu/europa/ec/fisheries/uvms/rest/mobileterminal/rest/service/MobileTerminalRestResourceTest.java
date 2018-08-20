/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;


import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class MobileTerminalRestResourceTest extends AbstractAssetRestTest {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalRestResourceTest.class);

    @Test
    public void createMobileTerminalTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String response = getWebTarget()
                                .path("mobileterminal")
                                .request(MediaType.APPLICATION_JSON)
                                .post(Entity.json(mobileTerminal), String.class);
        
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();
        
        assertThat(jsonObject.getInt("code"), CoreMatchers.is(MTResponseCode.OK.getCode()));
    }

    @Test
    public void createTwoMobileTerminalsUsingTheSameSerialNumberTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        String serialNr = mobileTerminal.getAttributes().get(0).getValue();

        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();

        assertThat(jsonObject.getInt("code"), CoreMatchers.is(MTResponseCode.OK.getCode()));

        mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.getAttributes().get(0).setValue(serialNr);

        response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertThat(jsonObject.getInt("code"), CoreMatchers.is(MTResponseCode.UNDEFINED_ERROR.getCode()));
    }

    @Test
    public void getMobileTerminalByIdTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        JsonObject data = jsonObject.getJsonObject("data");
        JsonObject terminalId = data.getJsonObject("mobileTerminalId");
        String guid = terminalId.getString("guid");

        String res = getWebTarget()
                .path("mobileterminal/" + guid)
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        assertTrue(res.contains(guid));
    }

    @Test
    public void updateMobileTerminalTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());
        assertFalse(created.contains("IRIDIUM"));

        JsonObject data = jsonObject.getJsonObject("data");
        JsonNumber id = data.getJsonNumber("id");
        JsonObject terminalId = data.getJsonObject("mobileTerminalId");
        String guid = terminalId.getString("guid");

        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(guid);
        mobileTerminal.setId(id.intValue());
        mobileTerminal.setMobileTerminalId(mobileTerminalId);
        mobileTerminal.setType("IRIDIUM");

        String updated = getWebTarget()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(mobileTerminal), String.class);

        assertTrue(updated.contains("IRIDIUM"));
        assertTrue(updated.contains(guid));
        assertTrue(updated.contains(String.valueOf(id.intValue())));
    }

    @Test
    public void getMobileTerminalListTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();

        String response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
    }

    @Test
    public void getMobileTerminalListWithWildCardsInSerialNumberTest() throws Exception{
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        String serialNumber = mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).getValue();

        //wildcard in front of serial
        String wildCardInFront = "*" + serialNumber.substring(3);

        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(wildCardInFront);

        String response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
        assertEquals(1, deserializeResponseDto(response, MobileTerminalListResponse.class).getMobileTerminal().size());  //only one returnee

        //wildcard in back of serial
        String wildCardInBack = serialNumber.substring(0, serialNumber.length()-3) + "*";
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(wildCardInBack);


        response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
        assertEquals(1, deserializeResponseDto(response, MobileTerminalListResponse.class).getMobileTerminal().size());  //only one returnee

        //wildcard at both ends
        String wildCardAtBothEnds = "*" + serialNumber.substring(3, serialNumber.length()-3) + "*";
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(wildCardAtBothEnds);


        response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
        assertEquals(1, deserializeResponseDto(response, MobileTerminalListResponse.class).getMobileTerminal().size());  //only one returnee
    }

    @Test
    public void getMobileTerminalListWithSatelliteNrTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.SATELLITE_NUMBER);
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(mobileTerminal.getAttributes().get(1).getValue());

        String response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
    }

    @Test
    public void getMobileTerminalListWithDNIDTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.DNID);
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(mobileTerminal.getChannels().get(0).getAttributes().get(5).getValue());

        String response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
    }

    @Test
    public void getMobileTerminalListWithMemberNumberTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.MEMBER_NUMBER);
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(mobileTerminal.getChannels().get(0).getAttributes().get(1).getValue());

        String response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
    }

    @Test
    public void getMobileTerminalListWithSatelliteAndDNIDTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        //one thing from channel and one from MobileTerminalEvents
        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.DNID);
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(mobileTerminal.getChannels().get(0).getAttributes().get(5).getValue());
        ListCriteria criteria = new ListCriteria();
        criteria.setKey(SearchKey.SATELLITE_NUMBER);
        criteria.setValue(mobileTerminal.getAttributes().get(1).getValue());
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().add(criteria);

        String response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), String.class);

        assertNotNull(response);
        jsonReader = Json.createReader(new StringReader(response));
        jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        assertTrue(response.contains(MobileTerminalTestHelper.getSerialNumber()));
        assertTrue(response.contains("INMARSAT_C"));
        assertTrue(response.contains(MobileTerminalSource.INTERNAL.value()));
    }

    @Test
    public void assignMobileTerminalTest() {

        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(MTResponseCode.OK.getCode(), jsonObject.getInt("code"));

        JsonObject data = jsonObject.getJsonObject("data");
        JsonObject terminalId = data.getJsonObject("mobileTerminalId");
        String guid = terminalId.getString("guid");

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        String connectId = UUID.randomUUID().toString();
        query.setConnectId(connectId);

        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(guid);
        query.setMobileTerminalId(mobileTerminalId);

        String response = getWebTarget()
                .path("/mobileterminal/assign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), String.class);

        assertNotNull(response);
        assertTrue(response.contains(guid));
    }
    
    @Test
    public void unAssignMobileTerminalTest() {
        MobileTerminalType mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        String created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(created));
        JsonObject jsonObject = jsonReader.readObject();

        assertEquals(jsonObject.getInt("code"), MTResponseCode.OK.getCode());

        JsonObject data = jsonObject.getJsonObject("data");
        JsonObject terminalId = data.getJsonObject("mobileTerminalId");
        String guid = terminalId.getString("guid");

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        String connectId = UUID.randomUUID().toString();
        query.setConnectId(connectId);

        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(guid);
        query.setMobileTerminalId(mobileTerminalId);


        String responseAssign = getWebTarget()
                .path("/mobileterminal/assign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), String.class);

        assertNotNull(responseAssign);
        assertTrue(responseAssign.contains(guid));

        String responseUnAssign = getWebTarget()
                .path("/mobileterminal/unassign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), String.class);

        assertNotNull(responseUnAssign);
        assertTrue(responseUnAssign.contains(guid));
    }

    @Test
    public void inactivateActivateAndArchiveMobileTerminal() throws Exception{
        MobileTerminalType mobileTerminalType = createAndRestMobileTerminal("Special Test Boat");
        assertFalse(mobileTerminalType.isInactive());
        assertFalse(mobileTerminalType.isArchived());

        String response = getWebTarget()
                .path("mobileterminal/status/inactivate")
                .queryParam("comment", "Test Comment Inactivate")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(mobileTerminalType.getMobileTerminalId()), String.class);


        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        MobileTerminalType changedMT = deserializeResponseDto(response, MobileTerminalType.class);

        assertTrue(changedMT.isInactive());

        response = getWebTarget()
                .path("mobileterminal/status/activate")
                .queryParam("comment", "Test Comment Activate")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(mobileTerminalType.getMobileTerminalId()), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        changedMT = deserializeResponseDto(response, MobileTerminalType.class);

        assertFalse(changedMT.isInactive());

        response = getWebTarget()
                .path("mobileterminal/status/remove")
                .queryParam("comment", "Test Comment Remove")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(mobileTerminalType.getMobileTerminalId()), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        changedMT = deserializeResponseDto(response, MobileTerminalType.class);

        assertTrue(changedMT.isInactive());
        assertTrue(changedMT.isArchived());

        //checking the events as well
        response = getWebTarget()
                .path("mobileterminal/history/" + mobileTerminalType.getMobileTerminalId().getGuid())
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        MobileTerminalHistory mobileTerminalHistory = deserializeResponseDto(response, MobileTerminalHistory.class);

        assertEquals(4, mobileTerminalHistory.getEvents().size()); //one for each of the operations above and one for the creation of the MT
        boolean created = false, activated = false, inactivate = false, archived = false;
        boolean nothingElse = true;
        for (MobileTerminalEvents mte: mobileTerminalHistory.getEvents()) {
            switch (mte.getEventCode()) {
                case CREATE:
                    created = true;
                    break;
                case ACTIVATE:
                    activated = true;
                    break;
                case INACTIVATE:
                    inactivate = true;
                    break;
                case ARCHIVE:
                    archived = true;
                    break;
                default:
                    nothingElse = false;
            }
        }
        assertTrue(created && activated && inactivate && archived && nothingElse);


    }

    private MobileTerminalType createAndRestMobileTerminal(String boat) throws Exception {
        MobileTerminalType mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setConnectId(boat);

        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mt), String.class);


        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        MobileTerminalType createdMT = deserializeResponseDto(response, MobileTerminalType.class);
        return createdMT;
    }
}
