package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.dto.PollTestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@RunAsClient
public class PollRestResourceQueryTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void createConfigurationPoll_ThenGetPollListByMultipleSearchCriteriaTest() {
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

        PollListQuery query = PollTestHelper.createPollListQueryWithPagination();
        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollTypeListCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_TYPE, PollTypeEnum.CONFIGURATION_POLL.name());
        pollSearchCriteria.getCriterias().add(pollTypeListCriteria);

        ListCriteria connectIdListCriteria = PollTestHelper.createListCriteria(SearchKey.CONNECT_ID, asset.getId().toString());
        pollSearchCriteria.getCriterias().add(connectIdListCriteria);

        ListCriteria pollIdListCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_ID, createdPoll.getSentPolls().get(0));
        pollSearchCriteria.getCriterias().add(pollIdListCriteria);

        query.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(query);

        assertNotNull(pollChannelListDto.getPollableChannels());
    }


    @Test
    @OperateOnDeployment("normal")
    public void getTwoPollsBySearchCriteria() {
        PollRequestType pollRequestType = PollTestHelper.createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollRequestType pollRequestType2 = PollTestHelper.createPollRequestType(PollType.MANUAL_POLL);
        Asset asset2 = createAndRestBasicAsset();
        MobileTerminal createdMT2 = createAndRestMobileTerminal(asset2);

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType2, createdMT2);

        CreatePollResultDto createdPoll2 = createPoll(pollRequestType2);

        assertNotNull(createdPoll2);

        String pollGuid2;
        if(createdPoll2.isUnsentPoll()){
            pollGuid2 = createdPoll2.getUnsentPolls().get(0);
        } else {
            pollGuid2 = createdPoll2.getSentPolls().get(0);
        }

        PollListQuery input = PollTestHelper.createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        ListCriteria pollIdCriteria2 = PollTestHelper.createListCriteria(SearchKey.POLL_ID, pollGuid2);
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
    public void getPollByTwoSearchCriteria() {
        PollRequestType pollRequestType = PollTestHelper.createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = PollTestHelper.createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        ListCriteria userCriteria = PollTestHelper.createListCriteria(SearchKey.USER, "user");
        pollSearchCriteria.getCriterias().add(userCriteria);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);
        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValues().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollByIdAndCreatorCriteria() {

        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);
        PollRequestType pollRequestType = PollTestHelper.createProgramPoll(createdMT);
        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = PollTestHelper.createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        ListCriteria userCriteria = PollTestHelper.createListCriteria(SearchKey.USER, "user");
        pollSearchCriteria.getCriterias().add(userCriteria);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);
        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValues().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollByTypeAndAssetIdCriteria() {

        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);
        PollRequestType pollRequestType = PollTestHelper.createProgramPoll(createdMT);
        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = PollTestHelper.createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_TYPE, PollType.PROGRAM_POLL.value());
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        ListCriteria userCriteria = PollTestHelper.createListCriteria(SearchKey.CONNECT_ID, asset.getId().toString());
        pollSearchCriteria.getCriterias().add(userCriteria);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);
        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValues().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollBySearchCriteria() {
        PollRequestType pollRequestType = PollTestHelper.createPollRequestType(PollType.MANUAL_POLL);
        Asset asset = createAndRestBasicAsset();
        MobileTerminal createdMT = createAndRestMobileTerminal(asset);

        PollTestHelper.constructPollMobileTerminalAndAddToRequest(pollRequestType, createdMT);

        CreatePollResultDto createdPoll = createPoll(pollRequestType);

        assertNotNull(createdPoll);

        String pollGuid;
        if(createdPoll.isUnsentPoll()){
            pollGuid = createdPoll.getUnsentPolls().get(0);
        } else {
            pollGuid = createdPoll.getSentPolls().get(0);
        }

        PollListQuery input = PollTestHelper.createPollListQueryWithPagination();

        PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
        pollSearchCriteria.setIsDynamic(true);

        ListCriteria pollIdCriteria = PollTestHelper.createListCriteria(SearchKey.POLL_ID, pollGuid);
        pollSearchCriteria.getCriterias().add(pollIdCriteria);

        input.setPollSearchCriteria(pollSearchCriteria);

        PollChannelListDto pollChannelListDto = getPollList(input);
        assertNotNull(pollChannelListDto);

        assertEquals(pollGuid, pollChannelListDto.getPollableChannels().get(0).getPoll().getValues().get(2).getValue());
        assertEquals(createdMT.getId().toString(), pollChannelListDto.getPollableChannels().get(0).getMobileTerminalId());
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
}
