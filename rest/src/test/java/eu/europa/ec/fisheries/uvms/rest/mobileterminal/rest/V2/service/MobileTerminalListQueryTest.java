package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.V2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTQuery;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@RunAsClient
public class MobileTerminalListQueryTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(Arrays.asList(MobileTerminalTestHelper.getSerialNumber()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithWildCardsInSerialNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        String serialNumber = MobileTerminalTestHelper.getSerialNumber();
        // Wildcard in front of serialNumber
        String wildCardInFront = "*" + serialNumber.substring(3);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(Arrays.asList(wildCardInFront));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());

        // Wildcard in back of serial
        String wildCardInBack = serialNumber.substring(0, serialNumber.length()-3) + "*";

        mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(Arrays.asList(wildCardInBack));

        response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());

        // Wildcard at both ends
        String wildCardAtBothEnds = "*" + serialNumber.substring(3, serialNumber.length()-3) + "*";

        mtQuery = new MTQuery();
        mtQuery.setSerialNumbers(Arrays.asList(wildCardAtBothEnds));

        response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithSatelliteNrTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);


        MTQuery mtQuery = new MTQuery();
        mtQuery.setSateliteNumbers(Arrays.asList(mobileTerminal.getSatelliteNumber()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithDNIDTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MTQuery mtQuery = new MTQuery();
        mtQuery.setDnids(Arrays.asList(channelList.get(0).getDNID()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithMemberNumberTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMemberNumbers(Arrays.asList(channelList.get(0).getMemberNumber()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        assertEquals(1, response.getMobileTerminalList().size());
        MobileTerminal terminal = response.getMobileTerminalList().get(0);


        assertEquals(MobileTerminalTestHelper.getSerialNumber(), terminal.getSerialNo());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithSatelliteAndDNIDTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);


        // One thing from channel
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MTQuery mtQuery = new MTQuery();
        mtQuery.setSateliteNumbers(Arrays.asList(mobileTerminal.getSatelliteNumber()));
        mtQuery.setDnids(Arrays.asList(channelList.get(0).getDNID()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

        assertEquals(1, response.getMobileTerminalList().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithConnectIDTest() throws JsonProcessingException {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);


        MTQuery mtQuery = new MTQuery();
        List<String> inputList = new ArrayList<>();
        inputList.add(asset.getId().toString());
        mtQuery.setAssetIds(inputList);


        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .queryParam("includeArchived", false)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
        assertEquals(asset.getId().toString(), terminal.getAssetId());

    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithMtIdTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MTQuery mtQuery = new MTQuery();
        List<String> inputList = new ArrayList<>();
        inputList.add(created.getId().toString());
        mtQuery.setMobileterminalIds(inputList);

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());

    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithTwoChannelsTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        channelList.get(0).setConfigChannel(false);

        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setConfigChannel(true);
        channel.setPollChannel(false);
        channel.setDefaultChannel(false);
        mobileTerminal.getChannels().add(channel);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMobileterminalIds(Arrays.asList(created.getId().toString()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalListWithTwoChannelsAndAnUpdateTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());
        channelList.get(0).setConfigChannel(false);

        Channel channel = MobileTerminalTestHelper.createBasicChannel();
        channel.setConfigChannel(true);
        channel.setPollChannel(false);
        channel.setDefaultChannel(false);
        mobileTerminal.getChannels().add(channel);

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        created.setSoftwareVersion("B");

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);
        assertNotNull(updated);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMobileterminalIds(Arrays.asList(created.getId().toString()));

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(updated.getSoftwareVersion(), terminal.getSoftwareVersion());
        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getHistoricalMobileTerminalListTest() {
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        List<Channel> channelList = new ArrayList<>(mobileTerminal.getChannels());

        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mobileTerminal), MobileTerminal.class);

        assertNotNull(created);

        created.setSoftwareVersion("B");
        Instant now = Instant.now();

        MobileTerminal updated = getWebTargetExternal()
                .path("mobileterminal2")
                .queryParam("comment", "NEW_TEST_COMMENT")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(created), MobileTerminal.class);
        assertNotNull(updated);

        MTQuery mtQuery = new MTQuery();
        mtQuery.setMobileterminalIds(Arrays.asList(created.getId().toString()));
        mtQuery.setDate(now);

        MTListResponse response = getWebTargetExternal()
                .path("/mobileterminal2/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mtQuery), MTListResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getMobileTerminalList().size());

        MobileTerminal terminal = response.getMobileTerminalList().get(0);

        assertEquals(mobileTerminal.getSoftwareVersion(), terminal.getSoftwareVersion());
        assertEquals(terminal.getSerialNo(), MobileTerminalTestHelper.getSerialNumber());
        assertEquals(MobileTerminalTypeEnum.INMARSAT_C, terminal.getMobileTerminalType());
        assertEquals(TerminalSourceEnum.INTERNAL, terminal.getSource());
    }

    private Asset createAndRestBasicAsset() {
        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTargetExternal()
                .path("asset2")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }
}
