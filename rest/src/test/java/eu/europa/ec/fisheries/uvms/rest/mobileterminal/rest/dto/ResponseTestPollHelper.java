package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.dto;

import eu.europa.ec.fisheries.uvms.mobileterminal.dto.MTListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalDtoMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResponseTestPollHelper {

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
        pollChannel.setDNID("5555");
        pollChannel.setMemberNumber("666");
        pollChannel.setLesDescription("Thrane&Thrane");
        pollChannel.setExpectedFrequency(Duration.ofSeconds(60));
        pollChannel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        pollChannel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        pollChannel.setPollChannel(true);

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setMobileTerminal(mt);
        channel.setDNID("555");
        channel.setMemberNumber("666");
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
}
