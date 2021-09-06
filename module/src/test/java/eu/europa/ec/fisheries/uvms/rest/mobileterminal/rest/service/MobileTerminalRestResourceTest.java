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
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChangeHistoryRow;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChangeType;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChannelChangeHistory;
import eu.europa.ec.fisheries.uvms.asset.util.JsonBConfiguratorAsset;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalStatus;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.filter.AppError;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTQuery;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import static javax.ws.rs.core.Response.Status.OK;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class MobileTerminalRestResourceTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
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
    @OperateOnDeployment("normal")
    public void createMobileTerminalWithLongCommentTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        String comment = "This comment is longer then 255 characters. This comment is longer then 255 characters. " +
                "This comment is longer then 255 characters. This comment is longer then 255 characters. " +
                "This comment is longer then 255 characters. This comment is longer then 255 characters. " +
                "This comment is longer then 255 characters. This comment is longer then 255 characters.";
        mobileTerminal.setComment(comment);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);
        assertEquals(comment, created.getComment());
    }

    @Test
    public void createMobileTerminalWithMultipleChannelsTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        Channel channel2 = new Channel();
        channel2.setName("VMS2");
        channel2.setFrequencyGracePeriod(Duration.ofSeconds(53000));
        channel2.setMemberNumber(Integer.parseInt(MobileTerminalTestHelper.generateARandomStringWithMaxLength(3)));
        channel2.setExpectedFrequency(Duration.ofSeconds(7100));
        channel2.setExpectedFrequencyInPort(Duration.ofSeconds(10400));
        channel2.setLesDescription("Thrane&Thrane");
        channel2.setDnid(Integer.parseInt("1" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(3)));
        channel2.setArchived(false);
        channel2.setConfigChannel(true);
        channel2.setDefaultChannel(true);
        channel2.setPollChannel(true);
        channel2.setMobileTerminal(mobileTerminal);

        mobileTerminal.getChannels().add(channel2);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);
        Set<Channel> channels = created.getChannels();
        assertEquals(2, channels.size());
    }

    @Test
    public void createTwoMobileTerminalsUsingTheSameSerialNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        String serialNr = mobileTerminal.getSerialNo();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setSerialNo(serialNr);

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal));
        assertEquals(200, response.getStatus());
        Integer code = response.readEntity(AppError.class).code;
        assertThat(code, is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByIdTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminal fetched = getWebTargetExternal()
                .path("mobileterminal/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get()
                .readEntity(MobileTerminal.class);

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalWithAssetByIdTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        Asset asset = createAndRestBasicAsset();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(asset);
        assertNotNull(created);

        MobileTerminal fetched = getWebTargetExternal()
                .path("mobileterminal/" + created.getId())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get()
                .readEntity(MobileTerminal.class);

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getAssetUUID(), fetched.getAssetUUID());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createSeveralActiveMTOnOneAssetTest() {
        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);


        assertNotNull(created);

        MobileTerminal mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.setAsset(asset);

        Response failed = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal2), Response.class);

        assertNotNull(failed);
        assertEquals(200, failed.getStatus());
        Integer code  = failed.readEntity(AppError.class).code;
        assertThat(code, is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

    }

    @Test
    @OperateOnDeployment("normal")
    public void createActiveAndInactiveMTOnOneAssetTest() {
        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);


        assertNotNull(created);

        MobileTerminal mobileTerminal2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal2.setActive(false);
        mobileTerminal2.setAsset(asset);

        MobileTerminal inactiveMT = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal2), MobileTerminal.class);

        assertNotNull(inactiveMT);
        assertFalse(inactiveMT.getActive());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);
        assertNotEquals(MobileTerminalTypeEnum.IRIDIUM, created.getMobileTerminalType());

        created.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        created.getChannels().iterator().next().setName("BETTER_VMS");

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals(MobileTerminalTypeEnum.IRIDIUM, updated.getMobileTerminalType());
        assertEquals("BETTER_VMS", updated.getChannels().iterator().next().getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateActiveMobileTerminalWithAttachedAssetTest() {
        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);
        assertNotEquals(MobileTerminalTypeEnum.IRIDIUM, created.getMobileTerminalType());

        created.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        created.getChannels().iterator().next().setName("BETTER_VMS");

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals(MobileTerminalTypeEnum.IRIDIUM, updated.getMobileTerminalType());
        assertEquals("BETTER_VMS", updated.getChannels().iterator().next().getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_TwoChannelsWithSameDnidTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created.getId());
        assertEquals(1, created.getChannels().size());


        int firstChannelDNID = mobileTerminal.getChannels().iterator().next().getDnid();
        Channel c2 = MobileTerminalTestHelper.createBasicChannel();
        c2.setDnid(firstChannelDNID);
        c2.setConfigChannel(false);
        c2.setDefaultChannel(false);
        c2.setPollChannel(false);
        c2.setMobileTerminal(mobileTerminal);

        created.getChannels().add(c2);

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);

        assertNotNull(updated.getId());
        assertEquals(2, updated.getChannels().size());
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminalTest_RemoveOneOfTwoChannels() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        Channel c2 = MobileTerminalTestHelper.createBasicChannel();
        c2.setConfigChannel(false);
        c2.setDefaultChannel(false);
        c2.setPollChannel(false);
        c2.setMobileTerminal(mobileTerminal);

        mobileTerminal.getChannels().add(c2);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created.getId());
        assertEquals(2, created.getChannels().size());

        Channel c1 = created.getChannels().iterator().next();
        created.getChannels().remove(c1);

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);

        assertNotNull(updated.getId());
        assertEquals(1, updated.getChannels().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(created.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        assertNotNull(response);
        assertEquals(created.getId(), response.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignActiveMobileTerminalToAssetWithAlreadyAnActiveMTTest() {
        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MobileTerminal unassignedMT = MobileTerminalTestHelper.createBasicMobileTerminal();
        unassignedMT = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(unassignedMT), MobileTerminal.class);

        assertNotNull(unassignedMT);


        Response response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(unassignedMT.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), Response.class);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        Integer code  = response.readEntity(AppError.class).code;
        assertThat(code, is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void unAssignMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal responseAssign = getWebTargetExternal()
                .path("/mobileterminal")
                .path(created.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        assertNotNull(responseAssign);
        assertNotNull(responseAssign.getAssetUUID());
        assertEquals(created.getId(), responseAssign.getId());

        MobileTerminal responseUnAssign = getWebTargetExternal()
                .path("/mobileterminal")
                .path(created.getId().toString())
                .path("unassign")
                .path(asset.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        assertNotNull(responseUnAssign);
        assertNull(responseUnAssign.getAssetUUID());
        assertEquals(created.getId(), responseUnAssign.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateActivateAndArchiveMobileTerminal() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(null);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);

        assertTrue(created.getActive());
        assertFalse(created.getArchived());

        MobileTerminal response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Inactivate")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.INACTIVE))
                .readEntity(MobileTerminal.class);

        assertNotNull(response);
        assertFalse(response.getActive());

        response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Activate")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.ACTIVE))
                .readEntity(MobileTerminal.class);

        assertTrue(response.getActive());

        response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Remove")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.ARCHIVE))
                .readEntity(MobileTerminal.class);

        assertFalse(response.getActive());
        assertTrue(response.getArchived());

        //checking the events as well
        Response res = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("history")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertEquals(200, res.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void ActivateMTConnectedToAnAssetWithAnAlreadyActiveMT() {
        Asset asset = createAndRestBasicAsset();
        assertNotNull(asset);

        MobileTerminal activeMT = MobileTerminalTestHelper.createBasicMobileTerminal();
        activeMT.setAsset(asset);

        activeMT = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(activeMT), MobileTerminal.class);

        assertTrue(activeMT.getActive());

        MobileTerminal inactiveMT = MobileTerminalTestHelper.createBasicMobileTerminal();
        inactiveMT.setActive(false);
        inactiveMT.setAsset(asset);

        inactiveMT = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(inactiveMT), MobileTerminal.class);

        assertFalse(inactiveMT.getActive());

        Response failed = getWebTargetExternal()
                .path("mobileterminal")
                .path(inactiveMT.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Activate")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.ACTIVE), Response.class);


        assertNotNull(failed);
        Integer code  = failed.readEntity(AppError.class).code;
        assertThat(code, is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        assertEquals(200, failed.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveAndUnarchiveMobileTerminal() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(null);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);

        assertTrue(created.getActive());
        assertFalse(created.getArchived());

        MobileTerminal response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.ARCHIVE))
                .readEntity(MobileTerminal.class);

        assertNotNull(response);
        assertTrue(response.getArchived());

        response = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("status")
                .queryParam("comment", "Test Comment Unarchive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(MobileTerminalStatus.UNARCHIVE))
                .readEntity(MobileTerminal.class);

        assertFalse(response.getArchived());

        //checking the events as well
        Response res = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("history")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertEquals(200, res.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalRevisionsByAssetId() {
        MobileTerminal mt1 = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal mt2 = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt2.setActive(false);

        MobileTerminal created1 = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt1), MobileTerminal.class);

        MobileTerminal created2 = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt2), MobileTerminal.class);

        Asset asset = createAndRestBasicAsset();

        getWebTargetExternal()
                .path("/mobileterminal")
                .path(created1.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "assignTerminal1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        getWebTargetExternal()
                .path("/mobileterminal")
                .path(created2.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "assignTerminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        getWebTargetExternal()
                .path("/mobileterminal")
                .path(created2.getId().toString())
                .path("unassign")
                .path(asset.getId().toString())
                .queryParam("comment", "unassignTerminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        Response response = getWebTargetExternal()
                .path("/mobileterminal/history/getMtHistoryForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        String json = response.readEntity(String.class);
        Map<UUID, ChangeHistoryRow> mtRevisions = new JsonBConfiguratorAsset().getContext(null)
                .fromJson(json, new HashMap<UUID, ChangeHistoryRow>(){}.getClass().getGenericSuperclass());

        assertEquals(5, mtRevisions.size());
        List<ChangeHistoryRow> updateRevisions = mtRevisions.values().stream()
                .filter(row -> row.getChangeType().equals(ChangeType.UPDATED)).collect(Collectors.toList());
        assertEquals(3, updateRevisions.size());
        assertTrue(updateRevisions.stream().allMatch(row -> row.getChanges().size() == 2));
        assertTrue(updateRevisions.stream().allMatch((row -> row.getHistoryId() != null)));

        assertTrue(mtRevisions.values().stream().anyMatch((row -> row.getId().equals(created1.getId()))));
        assertTrue(mtRevisions.values().stream().anyMatch((row -> row.getId().equals(created2.getId()))));

    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalRevisionsByAssetIdSingleRevision() {
        MobileTerminal mt1 = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created1 = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt1), MobileTerminal.class);

        created1.setComment("new comment");
        Response updatedResponse = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created1), Response.class);
        assertEquals(200, updatedResponse.getStatus());
        
        Asset asset = createAndRestBasicAsset();

        getWebTargetExternal()
                .path("/mobileterminal")
                .path(created1.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "assignTerminal1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        Response response = getWebTargetExternal()
                .path("/mobileterminal/history/getMtHistoryForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        String json = response.readEntity(String.class);
        Map<UUID, ChangeHistoryRow> mtRevisions = new JsonBConfiguratorAsset().getContext(null)
                .fromJson(json, new HashMap<UUID, ChangeHistoryRow>(){}.getClass().getGenericSuperclass());

        assertEquals(1, mtRevisions.size());
        assertTrue(mtRevisions.values().stream().allMatch(row -> row.getChangeType().equals(ChangeType.UPDATED)));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalRevisionsByAssetIdMTIsUnassaignedAndUpdated() {
        MobileTerminal mt1 = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created1 = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt1), MobileTerminal.class);

        Asset asset = createAndRestBasicAsset();

        getWebTargetExternal()
                .path("/mobileterminal")
                .path(created1.getId().toString())
                .path("assign")
                .path(asset.getId().toString())
                .queryParam("comment", "assignTerminal1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);


        getWebTargetExternal()
                .path("/mobileterminal")
                .path(created1.getId().toString())
                .path("unassign")
                .path(asset.getId().toString())
                .queryParam("comment", "unassignTerminal1")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), MobileTerminal.class);

        created1.setComment("new comment");
        Response updatedResponse = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created1), Response.class);
        assertEquals(200, updatedResponse.getStatus());

        Response response = getWebTargetExternal()
                .path("/mobileterminal/history/getMtHistoryForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        String json = response.readEntity(String.class);
        Map<UUID, ChangeHistoryRow> mtRevisions = new JsonBConfiguratorAsset().getContext(null)
                .fromJson(json, new HashMap<UUID, ChangeHistoryRow>(){}.getClass().getGenericSuperclass());

        assertEquals(3, mtRevisions.size());
        List<ChangeHistoryRow> updateRevisions = mtRevisions.values().stream()
                .filter(row -> row.getChangeType().equals(ChangeType.UPDATED)).collect(Collectors.toList());
        assertEquals(2, updateRevisions.size());
        assertTrue(updateRevisions.stream().allMatch(row -> row.getChanges().size() == 2));

    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetRevisionsByMobileTerminalId() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        mobileTerminal = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Asset asset1 = createAndRestBasicAsset();
        Asset asset2 = createAndRestBasicAsset();

        Response response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("assign")
                .path(asset1.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""));

        assertEquals(200, response.getStatus());

        response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("unassign")
                .path(asset1.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""));

        assertEquals(200, response.getStatus());

        response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("assign")
                .path(asset2.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""));

        assertEquals(200, response.getStatus());

        List<Asset> assetRevisions = getWebTargetExternal()
                .path("/mobileterminal/history/getAssetHistoryForMT")
                .path(mobileTerminal.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(new GenericType<List<Asset>>() {
                });

        assertEquals(2, assetRevisions.size());
    }

    @Test
    public void updateMobileTerminal_ChannelHasNoMobileTerminalTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        UUID channelId = created.getChannels().iterator().next().getId();

        created.getChannels().iterator().next().setMobileTerminal(null);

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals(channelId, updated.getChannels().iterator().next().getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkIfNonExistantSerialNumberExistsTest() {

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("serialNr")
                .path("DoesNotExist")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        String returnString = response.readEntity(String.class);
        assertTrue(returnString, returnString.contains("false"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkIfNonExistantSerialNumberExistsWithParamToReturnWholeObjectTest() {

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("serialNr")
                .path("DoesNotExist")
                .queryParam("returnWholeObject", true)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        String returnString = response.readEntity(String.class);
        assertTrue(returnString, returnString.contains("false"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkIfExistantSerialNumberExistsTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("serialNr")
                .path(created.getSerialNo())
                .queryParam("returnWholeObject", false)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        String returnString = response.readEntity(String.class);
        assertTrue(returnString, returnString.contains("true"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkIfExistantSerialNumberExistsReturnWholeObjectTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("serialNr")
                .path(created.getSerialNo())
                .queryParam("returnWholeObject", true)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        MobileTerminal returnString = response.readEntity(MobileTerminal.class);
        assertEquals(created.getId(), returnString.getId());
    }


    @Test
    @OperateOnDeployment("normal")
    public void checkIfNonExistantMemberDnidComboNumberExistsTest() {

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("memberNbr/dnid")
                .path("5555/5555")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        String returnString = response.readEntity(String.class);
        assertTrue(returnString, returnString.contains("false"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkIfHalfExistantSerialNumberExistsTest() {

        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("memberNbr/dnid")
                .path(String.valueOf(created.getChannels().iterator().next().getMemberNumber()))
                .path("5555")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        String returnString = response.readEntity(String.class);
        assertTrue(returnString, returnString.contains("false"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void checkIfExistantMemberNbrDnidComboExistsTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Channel channel = created.getChannels().iterator().next();
        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("checkIfExists")
                .path("memberNbr/dnid")
                .path(String.valueOf(channel.getMemberNumber()))
                .path(String.valueOf(channel.getDnid()))
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);

        assertEquals(200, response.getStatus());
        String returnString = response.readEntity(String.class);
        assertTrue(returnString, returnString.contains("true"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithAssetIdAsNullTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        Response response = getWebTargetExternal()
                .path("mobileterminal")
                .path("notConnectedToAssetList")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();
        assertEquals(OK.getStatusCode(), response.getStatus());

        String returnValue = response.readEntity(String.class);
        List<MobileTerminal> ret = new JsonBConfigurator().getContext(null)
                .fromJson(returnValue, new ArrayList<MobileTerminal>(){}.getClass().getGenericSuperclass());
        assertNotNull(ret);
        assertFalse(ret.isEmpty());
        assertTrue(ret.stream().anyMatch(mt -> mt.getId().equals(created.getId())));
        assertTrue(ret.stream().allMatch(mt -> mt.getAsset() == null));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalWithMultipleChannels_ThenVerifyChannelOrder() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        // Setup
        Channel vms2 = MobileTerminalTestHelper.createBasicChannel();
        vms2.setName("VMS2");
        vms2.setDefaultChannel(false);
        vms2.setPollChannel(false);
        vms2.setConfigChannel(false);

        Channel vms3 = MobileTerminalTestHelper.createBasicChannel();
        vms3.setName("VMS3");
        vms3.setDefaultChannel(false);
        vms3.setPollChannel(false);
        vms3.setConfigChannel(false);

        Channel vms4 = MobileTerminalTestHelper.createBasicChannel();
        vms4.setName("VMS4");
        vms4.setDefaultChannel(false);
        vms4.setPollChannel(false);
        vms4.setConfigChannel(false);

        mobileTerminal.getChannels().addAll(Arrays.asList(vms2, vms3, vms4));

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);
        assertNotNull(created);

        // Fetch MobileTerminal
        MTQuery query = new MTQuery();
        query.setMobileterminalIds(Collections.singletonList(created.getId().toString()));

        MobileTerminal fetched = getWebTargetExternal()
                .path("mobileterminal")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), MobileTerminal.class);

        List<String> nameList1 = new ArrayList<>();
        for (Channel c : fetched.getChannels()) {
            nameList1.add(c.getName());
        }

        // Verify Channels order
        for(int i = 0; i < 10; i++) {
            MobileTerminal fetched2 = getWebTargetExternal()
                    .path("mobileterminal")
                    .path("list")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                    .post(Entity.json(query), MobileTerminal.class);

            List<String> nameList2 = new ArrayList<>();
            for (Channel c : fetched2.getChannels()) {
                nameList2.add(c.getName());
            }
            assertEquals(nameList1, nameList2);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminalAndChannelAndGetChangeHistory() throws InterruptedException {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(null);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);

        Channel vms2 = MobileTerminalTestHelper.createBasicChannel();
        vms2.setName("VMS2");
        vms2.setDefaultChannel(false);
        vms2.setPollChannel(false);
        vms2.setConfigChannel(false);
        created.getChannels().add(vms2);
        created.setComment("NEW TEST COMMENT 1");

        Response updatedResponse = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), Response.class);
        assertEquals(200, updatedResponse.getStatus());

        MobileTerminal updated = updatedResponse.readEntity(MobileTerminal.class);

        updated.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        updated.getChannels().iterator().next().setName("BETTER_VMS");
        updated.setComment("NEW TEST COMMENT 2");

        updatedResponse = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(updated), Response.class);
        assertEquals(200, updatedResponse.getStatus());
        
        Response mTChangesResponse = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("changeHistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertEquals(200, mTChangesResponse.getStatus());
        Map<UUID, ChangeHistoryRow> mtChangesResponse = mTChangesResponse.readEntity(new GenericType<Map<UUID, ChangeHistoryRow>>() {});
        List<ChangeHistoryRow> mtChanges = new ArrayList<>(mtChangesResponse.values());
        mtChanges.sort(Comparator.comparing(ChangeHistoryRow::getUpdateTime));

        assertEquals(3, mtChanges.size());
        // After store_data_at_delete = true Change 1 -> 2
        // For some reason mtChange at index 1 seems to have 2 changes more often then index 2...


        assertEquals(2, mtChanges.get(1).getChannelChanges().size());
        
        //one subclass should have 8 changes 9 if count deleted
        Optional<ChannelChangeHistory> eightChangesChannel = mtChanges.get(1).getChannelChanges().values().stream()
                .filter(list -> list.getChanges().size() == 9).findAny();
        assertTrue(eightChangesChannel.isPresent());
        assertEquals(ChangeType.CREATED, eightChangesChannel.get().getChangeType());
        assertTrue(eightChangesChannel.get().getChanges().stream().allMatch(item ->item.getOldValue() == null));
        assertTrue(eightChangesChannel.get().getChanges().stream().allMatch(item ->item.getNewValue() != null));

        assertEquals(2, mtChanges.get(2).getChanges().size());
        assertEquals(2, mtChanges.get(1).getChannelChanges().size());
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminalRemoveChannelAndGetChangeHistory() {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(null);

        Channel vms2 = MobileTerminalTestHelper.createBasicChannel();
        vms2.setName("VMS2");
        vms2.setDefaultChannel(false);
        vms2.setPollChannel(false);
        vms2.setConfigChannel(false);
        mt.getChannels().add(vms2);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);

        created.setComment("NEW TEST COMMENT 1");
        created.getChannels().removeIf(channel -> !channel.isDefaultChannel());

        Response updatedResponse = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), Response.class);
        assertEquals(200, updatedResponse.getStatus());


        Response mTChangesResponse = getWebTargetExternal()
                .path("mobileterminal")
                .path(created.getId().toString())
                .path("changeHistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertEquals(200, mTChangesResponse.getStatus());
        Map<UUID, ChangeHistoryRow> mtChangesResponse = mTChangesResponse.readEntity(new GenericType<Map<UUID, ChangeHistoryRow>>() {});
        List<ChangeHistoryRow> mtChanges = new ArrayList<>(mtChangesResponse.values());
        mtChanges.sort(Comparator.comparing(ChangeHistoryRow::getUpdateTime));

        assertEquals(2, mtChanges.size());
        assertEquals(2, mtChanges.get(1).getChannelChanges().size());

        //one subclass should have 8 changes 9 if count deleted
        Optional<ChannelChangeHistory> eightChangesChannel = mtChanges.get(1).getChannelChanges().values().stream()
                .filter(list -> list.getChanges().size() == 9).findAny();
        
        assertTrue(eightChangesChannel.isPresent());
        assertEquals(ChangeType.REMOVED, eightChangesChannel.get().getChangeType());
        assertTrue(eightChangesChannel.get().getChanges().stream().allMatch(item ->item.getOldValue() != null));
        assertTrue(eightChangesChannel.get().getChanges().stream().allMatch(item ->item.getNewValue() == null));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalChangeHistoryWithMultipleAssets() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        mobileTerminal = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        Asset asset1 = createAndRestBasicAsset();
        Asset asset2 = createAndRestBasicAsset();

        Response response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("assign")
                .path(asset1.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""));

        assertEquals(200, response.getStatus());

        response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("unassign")
                .path(asset1.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""));

        assertEquals(200, response.getStatus());

        response = getWebTargetExternal()
                .path("/mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("assign")
                .path(asset2.getId().toString())
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""));

        assertEquals(200, response.getStatus());

        Response mTChangesResponse = getWebTargetExternal()
                .path("mobileterminal")
                .path(mobileTerminal.getId().toString())
                .path("changeHistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertEquals(200, mTChangesResponse.getStatus());
        Map<UUID, ChangeHistoryRow> mtChangesResponse = mTChangesResponse.readEntity(new GenericType<Map<UUID, ChangeHistoryRow>>() {});
        List<ChangeHistoryRow> mtChanges = new ArrayList<>(mtChangesResponse.values());
        mtChanges.sort(Comparator.comparing(ChangeHistoryRow::getUpdateTime));

        assertNotNull(mtChanges);
        assertEquals(4, mtChanges.size());

        assertEquals("user", mtChanges.get(1).getUpdatedBy());
        assertEquals(2, mtChanges.get(1).getChanges().size());
        assertEquals(mobileTerminal.getId(), mtChanges.get(1).getId());
        assertNotNull(mtChanges.get(1).getHistoryId());
        assertEquals(ChangeType.UPDATED, mtChanges.get(1).getChangeType());

        assertEquals("user", mtChanges.get(3).getUpdatedBy());
        assertEquals(1, mtChanges.get(3).getChanges().size());
        assertEquals(mobileTerminal.getId(), mtChanges.get(3).getId());
        assertNotNull(mtChanges.get(3).getHistoryId());
        assertTrue(mtChanges.get(3).getChanges().stream().anyMatch(item -> item.getNewValue().equals(asset2.getId().toString())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getLowestFreeMemberNumberForDnidTest() {

        Response response = getWebTargetExternal()
                .path("/mobileterminal")
                .path("lowestFreeMemberNumberForDnid")
                .path("20745")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertEquals(200, response.getStatus());
        Integer lowestMemberNumber = response.readEntity(Integer.class);
        assertNotNull(lowestMemberNumber);
        assertEquals(1, lowestMemberNumber.intValue());

    }
    
    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalCreateChannelAndDeleteChannel() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);
        
        assertNotNull(created);

        assertEquals(1, created.getChannels().size());
        
        created.getChannels().clear();
        
        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);
        
        assertEquals(0, updated.getChannels().size());

    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getChangeHistoryRowWithAssetName() throws InterruptedException {
        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();

        Asset asset = createAndRestBasicAsset();
        mt.setAsset(asset);
        mt.setAssetUUID(asset.getId().toString()); 
        
        MobileTerminal mtCreated = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);
        
        Response response = getWebTargetExternal()
                .path("/mobileterminal/")
                .path(mtCreated.getId().toString())
                .path("/changeHistory/")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        assertEquals(200, response.getStatus());
        
        Map<UUID, ChangeHistoryRow> jsonMap = response.readEntity(new GenericType<Map<UUID, ChangeHistoryRow>>() {});
        
        assertEquals(asset.getName(), jsonMap.values().stream()
                .filter(row -> row.getAssetName() != null)
                .filter(row -> row.getAssetName().equals(asset.getName()))
                .map(row -> row.getAssetName())
                .findFirst()
                .orElse("Not!!AssetName"));
    }

    private Asset createAndRestBasicAsset() {
        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }
}
