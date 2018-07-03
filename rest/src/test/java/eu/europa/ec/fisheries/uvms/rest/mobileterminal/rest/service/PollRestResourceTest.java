package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.util.DateUtils;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.util.Date;


import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class PollRestResourceTest extends AbstractAssetRestTest {

    /*@Inject
    PollDaoBean pollDaoBean;

    @Inject
    PollProgramDaoBean pollProgramDaoBean;

    @Inject
    TerminalDaoBean terminalDaoBean;*/

    @Test
    public void getRunningProgramPollsTest() {

        String response = getWebTarget()
                .path("/poll/running")
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();

        assertThat(jsonObject.getInt("code"), CoreMatchers.is(MTResponseCode.OK.getCode()));
    }


    @Test
    public void createPollTest() throws Exception {
        PollRequestType input = new PollRequestType();
        MobileTerminalType createdMT = createAndRestMobileTerminal("Test Boat");

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().get(0).getGuid());
        pmt.setConnectId(createdMT.getConnectId());
        pmt.setMobileTerminalId(createdMT.getMobileTerminalId().getGuid());
        input.getMobileTerminals().add(pmt);

        input.setPollType(PollType.MANUAL_POLL);
        input.setComment("Test Comment");
        input.setUserName("Test User");

        String response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto output = deserializeResponseDto(response, CreatePollResultDto.class);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, output.getSentPolls().size() + output.getUnsentPolls().size());
        //assertFalse(output.isUnsentPoll());
        //assertTrue(output.getUnsentPolls().isEmpty());

        //cleanup
        String pollGuid;
        if(output.isUnsentPoll()){
            pollGuid = output.getUnsentPolls().get(0);
        }else{
            pollGuid = output.getSentPolls().get(0);
        }

        //pollDaoBean.removePollAfterTests(pollGuid);
        //terminalDaoBean.removeMobileTerminalAfterTests(mt.getMobileTerminalId().getGuid());


    }

    @Test
    public void stopAndStartProgramPollTest() throws Exception{
        MobileTerminalType createdMT = createAndRestMobileTerminal("Test Boat");

        //Create program poll
        PollRequestType input = createProgramPoll(createdMT);

        String response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto output = deserializeResponseDto(response, CreatePollResultDto.class);

        //Stopping since autostart
        String pollGuid;
        if(output.isUnsentPoll()){
            pollGuid = output.getUnsentPolls().get(0);
        }else{
            pollGuid = output.getSentPolls().get(0);
        }
        response = getWebTarget()
                .path("/poll/stop/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        PollDto startStopOutput = deserializeResponseDto(response, PollDto.class);

        assertFalse(startStopOutput.getValue().isEmpty());
        assertEquals(pollGuid, startStopOutput.getValue().get(2).getValue());       //pray to god that theses are fixed......
        assertEquals("FALSE", startStopOutput.getValue().get(8).getValue());


        //and starting again
        response = getWebTarget()
                .path("/poll/start/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        startStopOutput = deserializeResponseDto(response, PollDto.class);

        assertFalse(startStopOutput.getValue().isEmpty());
        assertEquals(pollGuid, startStopOutput.getValue().get(2).getValue());       //pray to god that theses are fixed in position......
        assertEquals("TRUE", startStopOutput.getValue().get(8).getValue());

        //cleanup
        //pollProgramDaoBean.removePollProgrameAfterTests(pollGuid);
        //terminalDaoBean.removeMobileTerminalAfterTests(mt.getMobileTerminalId().getGuid());
    }

    @Test
    public void archiveProgramPollTest() throws Exception{
        MobileTerminalType createdMT = createAndRestMobileTerminal("Test Boat");

        //Create program poll
        PollRequestType input = createProgramPoll(createdMT);

        String response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto createdPoll = deserializeResponseDto(response, CreatePollResultDto.class);

        //Archiving
        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }
        response = getWebTarget()
                .path("/poll/inactivate/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        PollDto output = deserializeResponseDto(response, PollDto.class);

        assertFalse(output.getValue().isEmpty());
        assertEquals(pollGuid, output.getValue().get(2).getValue());       //pray to god that theses are fixed in position......
        assertEquals("FALSE", output.getValue().get(8).getValue());

        //PollProgram checkThatThePollIsArchived = pollProgramDaoBean.getPollProgramById(UUID.fromString(pollGuid));
        response = getWebTarget()
                .path("/poll/program/" + pollGuid)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        String  letsHopeThisWorks = deserializeResponseDto(response, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //PollProgram checkThatThePollIsArchived = deserializeResponseDto(response, PollProgram.class);
        PollProgram checkThatThePollIsArchived = objectMapper.readValue(letsHopeThisWorks, PollProgram.class);
        //PollProgram checkThatThePollIsArchived = deserializeResponseDto(letsHopeThisWorks, PollProgram.class);
        assertEquals(PollStateEnum.ARCHIVED, checkThatThePollIsArchived.getPollState());

    }

    @Test
    public void getPollBySearchCriteria() throws Exception {
        PollRequestType pollRequestType = new PollRequestType();
        MobileTerminalType createdMT = createAndRestMobileTerminal("Test Boat");

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().get(0).getGuid());
        pmt.setConnectId(createdMT.getConnectId());
        pmt.setMobileTerminalId(createdMT.getMobileTerminalId().getGuid());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        String response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(pollRequestType), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto createdPoll = deserializeResponseDto(response, CreatePollResultDto.class);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();   //I absolutely detest pagination
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);
        eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria listCriteria = new eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria();
        listCriteria.setKey(SearchKey.POLL_ID);
        listCriteria.setValue(pollGuid);
        pollSearchCriteria.getCriterias().add(listCriteria);
        input.setPollSearchCriteria(pollSearchCriteria);

        response = getWebTarget()
                .path("/poll/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        PollChannelListDto outputPollList = deserializeResponseDto(response, PollChannelListDto.class);

        assertEquals(pollGuid, outputPollList.getPollableChannels().get(0).getPoll().getValue().get(2).getValue());
        assertEquals(createdMT.getMobileTerminalId().getGuid(), outputPollList.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    public void getPollByTwoSearchCriteria() throws Exception {
        PollRequestType pollRequestType = new PollRequestType();
        MobileTerminalType createdMT = createAndRestMobileTerminal("Test Boat");

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().get(0).getGuid());
        pmt.setConnectId(createdMT.getConnectId());
        pmt.setMobileTerminalId(createdMT.getMobileTerminalId().getGuid());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        String response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(pollRequestType), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto createdPoll = deserializeResponseDto(response, CreatePollResultDto.class);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        }else{
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();   //I absolutely detest pagination
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);
        eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria listCriteria = new eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria();
        listCriteria.setKey(SearchKey.POLL_ID);
        listCriteria.setValue(pollGuid);
        pollSearchCriteria.getCriterias().add(listCriteria);

        listCriteria = new eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria();
        listCriteria.setKey(SearchKey.USER);
        listCriteria.setValue("Test User");
        pollSearchCriteria.getCriterias().add(listCriteria);
        input.setPollSearchCriteria(pollSearchCriteria);


        response = getWebTarget()
                .path("/poll/list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        PollChannelListDto outputPollList = deserializeResponseDto(response, PollChannelListDto.class);

        assertEquals(pollGuid, outputPollList.getPollableChannels().get(0).getPoll().getValue().get(2).getValue());
        assertEquals(createdMT.getMobileTerminalId().getGuid(), outputPollList.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    //@Ignore             //throws a 404 for some reason
    public void getPollableChannelsTest() throws Exception {
        MobileTerminalType createdMT = createAndRestMobileTerminal("Special Test Boat");
        PollRequestType pollRequestType = new PollRequestType();

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().get(0).getGuid());
        pmt.setConnectId(createdMT.getConnectId());
        pmt.setMobileTerminalId(createdMT.getMobileTerminalId().getGuid());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.MANUAL_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        String response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(pollRequestType), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto createdPoll = deserializeResponseDto(response, CreatePollResultDto.class);

        PollableQuery input = new PollableQuery();
        ListPagination hate = new ListPagination();
        hate.setPage(1);
        hate.setListSize(100);
        input.setPagination(hate);
        input.getConnectIdList().add("Special Test Boat");

        response = getWebTarget()
                .path("/poll/pollable")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        PollChannelListDto output = deserializeResponseDto(response, PollChannelListDto.class);

        output.getPollableChannels();
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

    private PollRequestType createProgramPoll(MobileTerminalType mobileTerminal){
        PollRequestType pollRequestType = new PollRequestType();
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(mobileTerminal.getChannels().get(0).getGuid());
        pmt.setConnectId(mobileTerminal.getConnectId());
        pmt.setMobileTerminalId(mobileTerminal.getMobileTerminalId().getGuid());
        pollRequestType.getMobileTerminals().add(pmt);

        pollRequestType.setPollType(PollType.PROGRAM_POLL);
        pollRequestType.setComment("Test Comment");
        pollRequestType.setUserName("Test User");

        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.USER);
        pollAttribute.setValue("Test User");
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.FREQUENCY);          //this is probably in minutes
        pollAttribute.setValue("20");
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        pollAttribute.setValue(DateUtils.parseUTCDateToString(new Date(System.currentTimeMillis())));
        pollRequestType.getAttributes().add(pollAttribute);

        pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.END_DATE);
        pollAttribute.setValue(DateUtils.parseUTCDateToString(new Date(System.currentTimeMillis() + 24*60*60*1000 )));
        pollRequestType.getAttributes().add(pollAttribute);

        return pollRequestType;
    }


}
