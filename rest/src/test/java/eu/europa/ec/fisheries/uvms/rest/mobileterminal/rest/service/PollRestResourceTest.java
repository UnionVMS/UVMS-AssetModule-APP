package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPluginCapability;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class PollRestResourceTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void getRunningProgramPollsTest() {

        Response response = getWebTargetExternal()
                .path("/poll/running")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollTest() {
        PollRequestType input = new PollRequestType();
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(createdMT.getAsset().getId().toString());
        pmt.setMobileTerminalId(createdMT.getId().toString());
        input.getMobileTerminals().add(pmt);

        input.setPollType(PollType.MANUAL_POLL);
        input.setComment("Test Comment");
        input.setUserName("Test User");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, createdPoll.getSentPolls().size() + createdPoll.getUnsentPolls().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollUsingOnlyAssetTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);



        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("poll")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("comment", "Test comment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(""), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, createdPoll.getSentPolls().size() + createdPoll.getUnsentPolls().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollUsingOnlyAssetWOaMTTest() {
        Asset asset = createAndRestBasicAsset();



        Response response = getWebTargetExternal()
                .path("poll")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .queryParam("comment", "Test comment")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(""), Response.class);

        assertNotNull(response);
        assertEquals(500, response.getStatus());
        String jsonResponse = response.readEntity(String.class);
        assertTrue(jsonResponse, jsonResponse.contains("No active MT for this asset, unable to poll"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createConfigurationPollTest() {
        PollRequestType pollRequest = new PollRequestType();
        Asset asset = createAndRestBasicAsset();

        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(asset);

        MobileTerminal createdMT = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);

        PluginCapability configurable = new PluginCapability();
        configurable.setName(PluginCapabilityType.CONFIGURABLE);
        configurable.setValue("TRUE");

        PluginCapability pollable = new PluginCapability();
        pollable.setName(PluginCapabilityType.POLLABLE);
        pollable.setValue("TRUE");

        PluginService pluginService = new PluginService();
        pluginService.setLabelName("Thrane&Thrane");
        pluginService.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        pluginService.setInactive(false);
        pluginService.setSatelliteType("INMARSAT_C");
        pluginService.getCapability().add(configurable);
        pluginService.getCapability().add(pollable);

        List<MobileTerminalPlugin> pluginList = getWebTargetExternal()
                .path("plugin")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(Collections.singletonList(pluginService)),
                        new GenericType<List<MobileTerminalPlugin>>() {});

        assertNotNull(pluginList);

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(createdMT.getAsset().getId().toString());
        pmt.setMobileTerminalId(createdMT.getId().toString());
        pollRequest.getMobileTerminals().add(pmt);

        PollAttribute attrFrequency = new PollAttribute();
        attrFrequency.setKey(PollAttributeType.REPORT_FREQUENCY);
        attrFrequency.setValue("11000");

        PollAttribute attrGracePeriod = new PollAttribute();
        attrGracePeriod.setKey(PollAttributeType.GRACE_PERIOD);
        attrGracePeriod.setValue("11020");

        PollAttribute attrInPortGrace = new PollAttribute();
        attrInPortGrace.setKey(PollAttributeType.IN_PORT_GRACE);
        attrInPortGrace.setValue("11040");

        pollRequest.getAttributes().addAll(Arrays.asList(attrFrequency, attrGracePeriod, attrInPortGrace));

        pollRequest.setPollType(PollType.CONFIGURATION_POLL);
        pollRequest.setComment("Test Comment");
        pollRequest.setUserName("Test User");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollRequest), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, createdPoll.getSentPolls().size() + createdPoll.getUnsentPolls().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopAndStartProgramPollTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        //Create program poll
        PollRequestType input = createProgramPoll(createdMT);

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        //Stopping since autostart
        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollDto pollDto = getWebTargetExternal()
                .path("/poll/stop/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(PollDto.class);

        assertNotNull(pollDto);

        assertFalse(pollDto.getValue().isEmpty());
        assertEquals(pollGuid, pollDto.getValue().get(2).getValue());
        assertEquals("FALSE", pollDto.getValue().get(8).getValue());

        //and starting again
        pollDto = getWebTargetExternal()
                .path("/poll/start/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(PollDto.class);

        assertNotNull(pollDto);

        assertFalse(pollDto.getValue().isEmpty());
        assertEquals(pollGuid, pollDto.getValue().get(2).getValue());
        assertEquals("TRUE", pollDto.getValue().get(8).getValue());
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveProgramPollTest() throws Exception{
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        //Create program poll
        PollRequestType input = createProgramPoll(createdMT);

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        //Archiving
        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }
        PollDto pollDto = getWebTargetExternal()
                .path("/poll/inactivate/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(PollDto.class);

        assertNotNull(pollGuid);

        assertFalse(pollDto.getValue().isEmpty());
        assertEquals(pollGuid, pollDto.getValue().get(2).getValue());
        assertEquals("FALSE", pollDto.getValue().get(8).getValue());

        String retVal = getWebTargetExternal()
                .path("/poll/program/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        assertTrue(retVal.contains(String.valueOf(Response.Status.OK.getStatusCode())));

        ObjectMapper objectMapper = new ObjectMapper();
        PollProgram checkThatThePollIsArchived = objectMapper.readValue(retVal, PollProgram.class);

        assertEquals(PollStateEnum.ARCHIVED, checkThatThePollIsArchived.getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollBySearchCriteria() {
        PollRequestType pollRequestType = new PollRequestType();
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(createdMT.getAsset().getId().toString());
        pmt.setMobileTerminalId(createdMT.getId().toString());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollRequestType), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.POLL_ID);
        listCriteria.setValue(pollGuid);
        pollSearchCriteria.getCriterias().add(listCriteria);
        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getWebTargetExternal()
                .path("/poll/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), PollChannelListDto.class);

        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValue().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollByTwoSearchCriteria() {
        PollRequestType pollRequestType = new PollRequestType();
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(createdMT.getAsset().getId().toString());
        pmt.setMobileTerminalId(createdMT.getId().toString());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollRequestType), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.POLL_ID);
        listCriteria.setValue(pollGuid);
        pollSearchCriteria.getCriterias().add(listCriteria);

        listCriteria = new eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria();
        listCriteria.setKey(SearchKey.USER);
        listCriteria.setValue("Test User");
        pollSearchCriteria.getCriterias().add(listCriteria);
        input.setPollSearchCriteria(pollSearchCriteria);


        PollChannelListDto pollChannelListDto = getWebTargetExternal()
                .path("/poll/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), PollChannelListDto.class);

        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValue().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getTwoPollsBySearchCriteria() {
        PollRequestType pollRequestType = new PollRequestType();
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(createdMT.getAsset().getId().toString());
        pmt.setMobileTerminalId(createdMT.getId().toString());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollRequestType), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollRequestType pollRequestType2 = new PollRequestType();
        Asset asset2 = createAndRestBasicAsset();
        MobileTerminal createdMT2 = createAndRestMobileTerminal(asset2);

        PollMobileTerminal pmt2 = new PollMobileTerminal();
        pmt2.setComChannelId(createdMT2.getChannels().iterator().next().getId().toString());
        pmt2.setConnectId(createdMT2.getAsset().getId().toString());
        pmt2.setMobileTerminalId(createdMT2.getId().toString());
        pollRequestType2.getMobileTerminals().add(pmt2);

        pollRequestType2.setPollType(PollType.MANUAL_POLL);
        pollRequestType2.setComment("Test Comment");
        pollRequestType2.setUserName("Test User");

        CreatePollResultDto createdPoll2 = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollRequestType2), CreatePollResultDto.class);

        assertNotNull(createdPoll2);

        String pollGuid2;
        if(createdPoll2.isUnsentPoll()){
            pollGuid2 = createdPoll2.getUnsentPolls().get(0);
        }else{
            pollGuid2 = createdPoll2.getSentPolls().get(0);
        }

        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.POLL_ID);
        listCriteria.setValue(pollGuid);
        pollSearchCriteria.getCriterias().add(listCriteria);

        ListCriteria listCriteria2 = new ListCriteria();
        listCriteria2.setKey(SearchKey.POLL_ID);
        listCriteria2.setValue(pollGuid2);
        pollSearchCriteria.getCriterias().add(listCriteria2);
        input.setPollSearchCriteria(pollSearchCriteria);


        PollChannelListDto pollChannelListDto = getWebTargetExternal()
                .path("/poll/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), PollChannelListDto.class);

        assertNotNull(pollChannelListDto);

        assertEquals(2, pollChannelListDto.getPollableChannels().size());
        assertTrue(pollChannelListDto.getPollableChannels().stream().anyMatch(c -> c.getConnectId().equals(asset.getId().toString())));
        assertTrue(pollChannelListDto.getPollableChannels().stream().anyMatch(c -> c.getConnectId().equals(asset2.getId().toString())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollableChannelsTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);
        PollRequestType pollRequestType = new PollRequestType();

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(createdMT.getAsset().getId().toString());
        pmt.setMobileTerminalId(createdMT.getId().toString());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollRequestType), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        PollableQuery input = new PollableQuery();
        ListPagination hate = new ListPagination();
        hate.setPage(1);
        hate.setListSize(100);
        input.setPagination(hate);
        input.getConnectIdList().add(asset.getId().toString());

        PollChannelListDto pollChannelListDto = getWebTargetExternal()
                .path("/poll/pollable")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(input), PollChannelListDto.class);

        assertNotNull(pollChannelListDto);

        boolean contains = false;
        for (PollChannelDto pollChannelDto: pollChannelListDto.getPollableChannels()) {
            if(pollChannelDto.getMobileTerminalId().equals(createdMT.getId().toString())){
                contains = true;
                break;
            }
        }
        assertTrue(contains);
    }

    private MobileTerminal createAndRestMobileTerminal(Asset asset) {

        MobileTerminal response = MobileTerminalTestHelper.createRestMobileTerminal(getWebTargetExternal(), asset, getTokenExternal());
        assertNotNull(response);
        return response;
    }

    private Asset createAndRestBasicAsset(){
        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }

    private PollRequestType createProgramPoll(MobileTerminal mobileTerminal){
        PollRequestType pollRequestType = new PollRequestType();
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(mobileTerminal.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(mobileTerminal.getAsset().getId().toString());
        pmt.setMobileTerminalId(mobileTerminal.getId().toString());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.PROGRAM_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.USER);
        pollAttribute.setValue("Test User");
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.FREQUENCY);          //this is probably in seconds
        pollAttribute.setValue("20");
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        pollAttribute.setValue(DateUtils.parseOffsetDateTimeToString(OffsetDateTime.now(ZoneId.of("UTC"))));
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.END_DATE);
        pollAttribute.setValue(DateUtils.parseOffsetDateTimeToString(OffsetDateTime.now(ZoneId.of("UTC")).plusDays(1))); //one day later
        pollRequestType.getAttributes().add(pollAttribute);

        return pollRequestType;
    }
}
