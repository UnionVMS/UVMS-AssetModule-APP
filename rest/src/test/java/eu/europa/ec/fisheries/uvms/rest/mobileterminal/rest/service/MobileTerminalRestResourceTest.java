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

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.ListCriteria;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.MTListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.MobileTerminalListQuery;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.SearchKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class MobileTerminalRestResourceTest extends AbstractAssetRestTest {

    @Test
    public void createMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                                .path("mobileterminal")
                                .request(MediaType.APPLICATION_JSON)
                                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);
        Set<Channel> channels = created.getChannels();
        Optional<Channel> first = channels.stream()
                .filter(channel -> channel.getName().equals(mobileTerminal.getChannels().iterator().next().getName()))
                .findFirst();

        assertTrue(first.isPresent());
        assertEquals(mobileTerminal.getChannels().iterator().next().getName(), first.get().getName());
    }

    @Test
    public void createTwoMobileTerminalsUsingTheSameSerialNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        String serialNr = mobileTerminal.getSerialNo();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setSerialNo(serialNr);

        Response response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMobileTerminalByIdTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminal fetched = getWebTarget()
                .path("mobileterminal/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(MobileTerminal.class);

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    public void getMobileTerminalWithAssetByIdTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        Asset asset = createAndRestBasicAsset();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(asset);
        assertNotNull(created);

        MobileTerminal fetched = getWebTarget()
                .path("mobileterminal/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(MobileTerminal.class);

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getAsset().getId(), fetched.getAsset().getId());
    }

    @Test
    public void updateMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);
        assertNotEquals(MobileTerminalTypeEnum.IRIDIUM, created.getMobileTerminalType());

        created.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        created.getChannels().iterator().next().setName("BETTER_VMS");

        MobileTerminal updated = getWebTarget()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created), MobileTerminal.class);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals(MobileTerminalTypeEnum.IRIDIUM, updated.getMobileTerminalType());
        assertEquals("BETTER_VMS", updated.getChannels().iterator().next().getName());
    }

    @Test
    public void getMobileTerminalListTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();

        MTListResponse response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    public void getMobileTerminalListWithWildCardsInSerialNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        String serialNumber = mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).getValue();

        // Wildcard in front of serialNumber
        String wildCardInFront = "*" + serialNumber.substring(3);

        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(wildCardInFront);

        MTListResponse response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());

        // Wildcard in back of serial
        String wildCardInBack = serialNumber.substring(0, serialNumber.length()-3) + "*";
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(wildCardInBack);


        response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());

        // Wildcard at both ends
        String wildCardAtBothEnds = "*" + serialNumber.substring(3, serialNumber.length()-3) + "*";
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(wildCardAtBothEnds);


        response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    public void getMobileTerminalListWithSatelliteNrTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.SATELLITE_NUMBER);
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(mobileTerminal.getSatelliteNumber());

        MTListResponse response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    public void getMobileTerminalListWithDNIDTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.DNID);
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(channelList.get(0).getDNID());

        MTListResponse response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    public void getMobileTerminalListWithMemberNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.MEMBER_NUMBER);
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(channelList.get(0).getMemberNumber());

        MTListResponse response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(1, response.getMobileTerminalList().size());

        assertEquals(MobileTerminalTestHelper.getSerialNumber(), terminal.getSerialNo());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    public void getMobileTerminalListWithSatelliteAndDNIDTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        ListCriteria criteria = new ListCriteria();
        criteria.setKey(SearchKey.SATELLITE_NUMBER);
        criteria.setValue(mobileTerminal.getSatelliteNumber());

        // One thing from channel
        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setKey(SearchKey.DNID);
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().get(0).setValue(channelList.get(0).getDNID());
        mobileTerminalListQuery.getMobileTerminalSearchCriteria().getCriterias().add(criteria);

        MTListResponse response = getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    public void assignMobileTerminalTest() {

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal response = getWebTarget()
                .path("/mobileterminal/assign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .queryParam("connectId", asset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()), MobileTerminal.class);

        assertNotNull(response);
        assertEquals(created.getId(), response.getId());
    }

    @Test
    public void unAssignMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal responseAssign = getWebTarget()
                .path("/mobileterminal/assign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .queryParam("connectId", asset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()), MobileTerminal.class);

        assertNotNull(responseAssign);
        assertNotNull(responseAssign.getAsset());
        assertEquals(created.getId(), responseAssign.getId());

        MobileTerminal responseUnAssign = getWebTarget()
                .path("/mobileterminal/unassign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .queryParam("connectId", asset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()), MobileTerminal.class);

        assertNotNull(responseUnAssign);
        assertNull(responseUnAssign.getAsset());
        assertEquals(created.getId(), responseUnAssign.getId());
    }

    @Test
    public void inactivateActivateAndArchiveMobileTerminal() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(null);

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mt), MobileTerminal.class);

        assertFalse(created.getInactivated());
        assertFalse(created.getArchived());

        MobileTerminal response = getWebTarget()
                .path("mobileterminal/status/inactivate")
                .queryParam("comment", "Test Comment Inactivate")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()))
                .readEntity(MobileTerminal.class);

        assertNotNull(response);
        assertTrue(response.getInactivated());

        response = getWebTarget()
                .path("mobileterminal/status/activate")
                .queryParam("comment", "Test Comment Activate")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()))
                .readEntity(MobileTerminal.class);

        assertFalse(response.getInactivated());

        response = getWebTarget()
                .path("mobileterminal/status/remove")
                .queryParam("comment", "Test Comment Remove")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()))
                .readEntity(MobileTerminal.class);

        assertTrue(response.getInactivated());
        assertTrue(response.getArchived());

        //checking the events as well
        Response res = getWebTarget()
                .path("mobileterminal/history/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, res.getStatus());
    }

    @Test
    public void searchForSerialNumberAfterCreatingNewEvents() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        Asset asset = createAndRestBasicAsset();

        MobileTerminal created = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminal), MobileTerminal.class);


        MobileTerminalListQuery mobileTerminalListQuery = MobileTerminalTestHelper.createMobileTerminalListQuery();

        // Check the search result
        MTListResponse returnList = sendMTListQuery(mobileTerminalListQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());

        MobileTerminal response = getWebTarget()
                .path("/mobileterminal/assign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .queryParam("connectId", asset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()), MobileTerminal.class);

        assertNotNull(response);

        // Check the search result
        returnList = sendMTListQuery(mobileTerminalListQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());

        // Unassign
        MobileTerminal responseUnAssign = getWebTarget()
                .path("/mobileterminal/unassign")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .queryParam("connectId", asset.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()), MobileTerminal.class);

        assertNotNull(responseUnAssign);

        //check the search result
        returnList = sendMTListQuery(mobileTerminalListQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());

        //And inactivate
        response = getWebTarget()
                .path("mobileterminal/status/inactivate")
                .queryParam("comment", "Test Comment Inactivate")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(created.getId()), MobileTerminal.class);

        assertNotNull(response);

        //check the search result
        returnList = sendMTListQuery(mobileTerminalListQuery);
        assertEquals(1, returnList.getMobileTerminalList().size());
    }

    private Asset createAndRestBasicAsset() {
        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }

    private MTListResponse sendMTListQuery(MobileTerminalListQuery mobileTerminalListQuery) {
        return getWebTarget()
                .path("/mobileterminal/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mobileTerminalListQuery), MTListResponse.class);
    }
}
