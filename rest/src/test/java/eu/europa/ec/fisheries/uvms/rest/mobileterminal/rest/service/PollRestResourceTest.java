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
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.filter.AppError;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.SimpleCreatePoll;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.dto.PollTestHelper;
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
        PollRequestType input = PollTestHelper.createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(input, createdMT);

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

        SimpleCreatePoll pollDto = new SimpleCreatePoll();
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

        SimpleCreatePoll pollDto = new SimpleCreatePoll();
        pollDto.setComment("Test comment");

        Response response = getWebTargetExternal()
                .path("poll")
                .path("createPollForAsset")
                .path(asset.getId().toString())
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
    public void createProgramPollUsingOnlyAssetTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal mt = createAndRestMobileTerminal(asset);

        SimpleCreatePoll pollDto = new SimpleCreatePoll();
        pollDto.setComment("Test comment");
        pollDto.setPollType(PollType.PROGRAM_POLL);
        pollDto.setFrequency(555);
        pollDto.setStartDate(Instant.now());
        pollDto.setEndDate(Instant.now().plus(1, ChronoUnit.DAYS));

        CreatePollResultDto createdPoll = getWebTargetExternal()
                .path("poll")
                .path("createPollForAsset")
                .path(asset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(pollDto), CreatePollResultDto.class);

        assertNotNull(createdPoll);

        assertEquals(1, createdPoll.getUnsentPolls().size());
        String pollId = createdPoll.getUnsentPolls().get(0);

        ProgramPoll retVal = getWebTargetExternal()
                .path("/poll/program/")
                .path(pollId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(ProgramPoll.class);

        assertNotNull(retVal.getId());
        assertEquals(asset.getId(), retVal.getAssetId());
        assertEquals(mt.getId(), retVal.getMobileterminal().getId());

        assertEquals(pollDto.getComment(), retVal.getComment());
        assertEquals(pollDto.getFrequency(), retVal.getFrequency());
        assertEquals(pollDto.getStartDate().truncatedTo(ChronoUnit.MILLIS), retVal.getStartDate());
        assertEquals(pollDto.getEndDate().truncatedTo(ChronoUnit.MILLIS), retVal.getStopDate());

    }

    @Test
    @OperateOnDeployment("normal")
    public void createConfigurationPollTest() {
        PollRequestType pollRequest = PollTestHelper.createPollRequestType(PollType.CONFIGURATION_POLL);

        Asset asset = createAndRestBasicAsset();

        MobileTerminal mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setAsset(asset);

        MobileTerminal createdMT = PollTestHelper.createMobileTerminalWithPluginAndCapabilities(mt, getWebTargetExternal(), getTokenExternal());

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequest, createdMT);

        PollTestHelper.createPollAttributesForRequest(pollRequest);

        CreatePollResultDto createdPoll = createPoll(pollRequest);

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
        PollRequestType input = PollTestHelper.createProgramPoll(createdMT);

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
        PollRequestType input = PollTestHelper.createProgramPoll(createdMT);

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

        assertEquals(ProgramPollStatus.ARCHIVED, retVal.getPollState());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollableChannelsTest() {
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);
        PollRequestType pollRequestType = PollTestHelper.createPollRequestType(PollType.MANUAL_POLL);

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

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

}
