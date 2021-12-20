package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.dto;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.MTListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalDtoMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.assertNotNull;

public class PollTestHelper {

    static MTListResponse createMTListResponse() {
        MTListResponse response = new MTListResponse();
        response.setCurrentPage(1);
        response.setTotalNumberOfPages(1);
        MobileTerminal created = createMobileTerminal();
        response.getMobileTerminalList().add(MobileTerminalDtoMapper.mapToMobileTerminalDto(created));
        return response;
    }

    private static MobileTerminal createMobileTerminal()  {
        String serialNo = UUID.randomUUID().toString();
        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(Instant.now());
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);
        mt.setInstalledBy("Mike the not so Great");

        Channel pollChannel = new Channel();
        pollChannel.setArchived(false);
        pollChannel.setMobileTerminal(mt);
        pollChannel.setDnid(5555);
        pollChannel.setMemberNumber(666);
        pollChannel.setLesDescription("Thrane&Thrane");
        pollChannel.setExpectedFrequency(Duration.ofSeconds(60));
        pollChannel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        pollChannel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        pollChannel.setPollChannel(true);

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setMobileTerminal(mt);
        channel.setDnid(555);
        channel.setMemberNumber(666);
        channel.setLesDescription("Thrane&Thrane");
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        channels.add(pollChannel);
        mt.getChannels().clear();
        mt.getChannels().addAll(channels);
        return mt;
    }

    public static PollListQuery createPollListQueryWithPagination() {
        PollListQuery input = new PollListQuery();
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        input.setPagination(pagination);
        return input;
    }

    public static ListCriteria createListCriteria(SearchKey key, String value) {
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(key);
        listCriteria.setValue(value);
        return listCriteria;
    }

    public static PollRequestType createProgramPoll(MobileTerminal mobileTerminal){
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

    public static PollRequestType createPollRequestType(PollType type) {
        PollRequestType pollRequest = new PollRequestType();
        pollRequest.setPollType(type);
        pollRequest.setComment("Test Comment");
        pollRequest.setUserName("user");
        return pollRequest;
    }

    public static void constructPollMobileTerminalAndAddToRequest(PollRequestType request, MobileTerminal terminal) {
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(terminal.getChannels().iterator().next().getId().toString());
        pmt.setMobileTerminalId(terminal.getId().toString());
        request.getMobileTerminals().add(pmt);
    }

    public static MobileTerminal createMobileTerminal(MobileTerminal mt, WebTarget webTarget, String token) {
        return webTarget
            .path("mobileterminal")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, token)
            .post(Entity.json(mt), MobileTerminal.class);
    }

    public static void createPollAttributesForRequest(PollRequestType pollRequest) {
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
}
