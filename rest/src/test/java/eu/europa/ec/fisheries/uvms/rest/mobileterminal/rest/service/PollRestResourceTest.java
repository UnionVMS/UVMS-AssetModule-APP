package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.ProgramPoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.filter.AppError;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.CommentDto;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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
        PollRequestType input = createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        constructPollMobileTerminalAndAddToRequest(input, createdMT);

        CreatePollResultDto createdPoll = createPoll(input);
        assertNotNull(createdPoll);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, createdPoll.getSentPolls().size() + createdPoll.getUnsentPolls().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollUsingOnlyAssetTest() {
        Asset asset = createAndRestBasicAsset();
        createAndRestMobileTerminal(asset);

        CommentDto pollDto = new CommentDto();
        pollDto.setComment("Test comment");

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("poll")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollDto), CreatePollResultDto.class);

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
        assertEquals(200, response.getStatus());
        Integer code  = response.readEntity(AppError.class).code;
        assertThat(code, is(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void createConfigurationPollTest() {
        PollRequestType pollRequest = createPollRequestType(PollType.CONFIGURATION_POLL);

        Asset asset = createAndRestBasicAsset();

        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(asset);

        MobileTerminal createdMT = createMobileTerminalWithPluginAndCapabilities(mt);

        constructPollMobileTerminalAndAddToRequest(pollRequest, createdMT);

        createPollAttributesForRequest(pollRequest);

        CreatePollResultDto createdPoll = createPoll(pollRequest);

        assertNotNull(createdPoll);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, createdPoll.getSentPolls().size() + createdPoll.getUnsentPolls().size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createConfigurationPoll_ThenGetPollListByMultipleSearchCriteriaTest() {
        PollRequestType pollRequest = createPollRequestType(PollType.CONFIGURATION_POLL);

        Asset asset = createAndRestBasicAsset();

        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(asset);

        MobileTerminal createdMT = createMobileTerminalWithPluginAndCapabilities(mt);

        constructPollMobileTerminalAndAddToRequest(pollRequest, createdMT);

        createPollAttributesForRequest(pollRequest);

        CreatePollResultDto createdPoll = createPoll(pollRequest);

        assertNotNull(createdPoll);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, createdPoll.getSentPolls().size() + createdPoll.getUnsentPolls().size());

        PollListQuery query = createPollListQueryWithPagination();
        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollTypeListCriteria = createListCriteria(SearchKey.POLL_TYPE, PollTypeEnum.CONFIGURATION_POLL.name());
        pollSearchCriteria.getCriterias().add(pollTypeListCriteria);

        ListCriteria connectIdListCriteria = createListCriteria(SearchKey.CONNECT_ID, asset.getId().toString());
        pollSearchCriteria.getCriterias().add(connectIdListCriteria);

        ListCriteria pollIdListCriteria = createListCriteria(SearchKey.POLL_ID, createdPoll.getSentPolls().get(0));
        pollSearchCriteria.getCriterias().add(pollIdListCriteria);

        query.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(query);

        assertNotNull(pollChannelListDto.getPollableChannels());
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopAndStartProgramPollTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        //Create program poll
        PollRequestType input = createProgramPoll(createdMT);

        CreatePollResultDto createdPoll = createPoll(input);

        assertNotNull(createdPoll);

        //Stopping since autostart
        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollDto pollDto = getWebTargetExternal()
                .path("poll")
                .path(pollGuid)
                .path("stop")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), PollDto.class);

        assertNotNull(pollDto);

        assertFalse(pollDto.getValues().isEmpty());
        assertEquals(pollGuid, pollDto.getValues().get(2).getValue());
        assertEquals("FALSE", pollDto.getValues().get(8).getValue());

        //and starting again
        pollDto = getWebTargetExternal()
                .path("poll")
                .path(pollGuid)
                .path("start")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), PollDto.class);

        assertNotNull(pollDto);

        assertFalse(pollDto.getValues().isEmpty());
        assertEquals(pollGuid, pollDto.getValues().get(2).getValue());
        assertEquals("TRUE", pollDto.getValues().get(8).getValue());
    }

    @Test
    @OperateOnDeployment("normal")
    public void archiveProgramPollTest() throws Exception{
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        //Create program poll
        PollRequestType input = createProgramPoll(createdMT);

        CreatePollResultDto createdPoll = createPoll(input);

        assertNotNull(createdPoll);

        //Archiving
        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }
        PollDto pollDto = getWebTargetExternal()
                .path("poll")
                .path(pollGuid)
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(""), PollDto.class);

        assertNotNull(pollGuid);

        assertFalse(pollDto.getValues().isEmpty());
        assertEquals(pollGuid, pollDto.getValues().get(2).getValue());
        assertEquals("FALSE", pollDto.getValues().get(8).getValue());

        ProgramPoll retVal = getWebTargetExternal()
                .path("/poll/program/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(ProgramPoll.class);

        assertEquals(PollStateEnum.ARCHIVED, retVal.getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollBySearchCriteria() {
        PollRequestType pollRequestType = createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);

        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValues().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollByTwoSearchCriteria() {
        PollRequestType pollRequestType = createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        ListCriteria userCriteria = createListCriteria(SearchKey.USER, "Test User");
        pollSearchCriteria.getCriterias().add(userCriteria);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);

        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValues().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getTwoPollsBySearchCriteria() {
        PollRequestType pollRequestType = createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollRequestType pollRequestType2 = createPollRequestType(PollType.MANUAL_POLL);
        Asset asset2 = createAndRestBasicAsset();
        MobileTerminal createdMT2 = createAndRestMobileTerminal(asset2);

        constructPollMobileTerminalAndAddToRequest(pollRequestType2, createdMT2);

        CreatePollResultDto createdPoll2 = createPoll(pollRequestType2);

        assertNotNull(createdPoll2);

        String pollGuid2;
        if(createdPoll2.isUnsentPoll()){
            pollGuid2 = createdPoll2.getUnsentPolls().get(0);
        } else {
            pollGuid2 = createdPoll2.getSentPolls().get(0);
        }

        PollListQuery input = createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        ListCriteria pollIdCriteria2 = createListCriteria(SearchKey.POLL_ID, pollGuid2);
        pollSearchCriteria.getCriterias().add(pollIdCriteria2);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);

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
        PollRequestType pollRequestType = createPollRequestType(PollType.MANUAL_POLL);

        constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        PollableQuery input = new PollableQuery();
        ListPagination pagination = new ListPagination();
        pagination.setPage(1);
        pagination.setListSize(100);
        input.setPagination(pagination);
        input.getConnectIdList().add(asset.getId().toString());

        PollChannelListDto pollChannelListDto = getWebTargetExternal()
                .path("/poll/getPollable")
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

    private CreatePollResultDto createPoll(PollRequestType request) {
        return getWebTargetExternal()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(request), CreatePollResultDto.class);
    }

    private PollChannelListDto getPollList(PollListQuery query) {
        return getWebTargetExternal()
                .path("/poll/list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), PollChannelListDto.class);
    }

    private void createPollAttributesForRequest(PollRequestType pollRequest) {
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
    }

    private MobileTerminal createMobileTerminalWithPluginAndCapabilities(MobileTerminal mt) {
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
        return createdMT;
    }

    private PollListQuery createPollListQueryWithPagination() {
        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);
        return input;
    }

    private ListCriteria createListCriteria(SearchKey key, String value) {
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(key);
        listCriteria.setValue(value);
        return listCriteria;
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
        PollRequestType pollRequestType = createPollRequestType(PollType.PROGRAM_POLL);
        constructPollMobileTerminalAndAddToRequest(pollRequestType, mobileTerminal);

        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.USER);
        pollAttribute.setValue("Test User");
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.FREQUENCY);
        pollAttribute.setValue("20");
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        pollAttribute.setValue(DateUtils.dateToEpochMilliseconds(Instant.now()));
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.END_DATE);
        pollAttribute.setValue(DateUtils.dateToEpochMilliseconds(Instant.now().plus(1, ChronoUnit.DAYS)));
        pollRequestType.getAttributes().add(pollAttribute);

        return pollRequestType;
    }

    private PollRequestType createPollRequestType(PollType type) {
        PollRequestType pollRequest = new PollRequestType();
        pollRequest.setPollType(type);
        pollRequest.setComment("Test Comment");
        pollRequest.setUserName("Test User");
        return pollRequest;
    }

    private void constructPollMobileTerminalAndAddToRequest(PollRequestType request, MobileTerminal terminal) {
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(terminal.getChannels().iterator().next().getId().toString());
        pmt.setConnectId(terminal.getAssetUUID());
        pmt.setMobileTerminalId(terminal.getId().toString());
        request.getMobileTerminals().add(pmt);
    }
}
