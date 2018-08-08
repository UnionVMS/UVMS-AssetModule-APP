package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalModelToEntityMapper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Stateless
@LocalBean
public class TestPollHelper {

    private Calendar cal = Calendar.getInstance();
    private Random rnd = new Random();

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    public PollRequestType createPollRequestType() {
        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(PollType.MANUAL_POLL);
        PollMobileTerminal pollMobileTerminal = createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);

        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        String startDate = LocalDateTime.now(ZoneOffset.UTC).toString();
        pollAttribute.setValue(startDate);

        prt.getAttributes().add(pollAttribute);
        return prt;
    }

    private PollMobileTerminal createPollMobileTerminal() {

        String connectId = UUID.randomUUID().toString();

        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(connectId);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(connectId);
        pmt.setMobileTerminalId(mobileTerminal.getId().toString());

        Iterator iterator = mobileTerminal.getChannels().iterator();
        Channel channel = (Channel) iterator.next();
        pmt.setComChannelId(channel.getId().toString());
        return pmt;
    }

    public MobileTerminal createAndPersistMobileTerminal(String connectId)  {

        String serialNo = UUID.randomUUID().toString();

        Channel channel = new Channel();
        channel.setArchived(false);

        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(LocalDateTime.now(ZoneOffset.UTC));
        mt.setUpdateuser("TEST");
        mt.setSource(MobileTerminalSource.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setInactivated(false);

        Set<MobileTerminalPluginCapability> capabilityList = new HashSet<>();
        MobileTerminalPluginCapability mtpc = new MobileTerminalPluginCapability();
        mtpc.setPlugin(mtp);
        mtpc.setName("test");
        mtpc.setValue("test");
        mtpc.setUpdatedBy("TEST_USER");
        mtpc.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        capabilityList.add(mtpc);

        mtp.getCapabilities().addAll(capabilityList);

        Set<MobileTerminalEvent> mobileTerminalEvents = new HashSet<>();
        MobileTerminalEvent mte = new MobileTerminalEvent();
        if(connectId != null && !connectId.trim().isEmpty())
            mte.setConnectId(connectId);
        mte.setActive(true);
        mte.setMobileterminal(mt);

        String attributes = PollAttributeType.START_DATE.value() + "=" + LocalDateTime.now(ZoneOffset.UTC).toString();
        attributes = attributes + ";";
        attributes = attributes + PollAttributeType.END_DATE.value() + "=" + LocalDateTime.now(ZoneOffset.UTC).toString();
        mte.setAttributes(attributes);

        Channel pollChannel = new Channel();
        pollChannel.setArchived(false);
        pollChannel.setMobileTerminal(mt);

        mte.setPollChannel(pollChannel);
        mobileTerminalEvents.add(mte);
        mt.getMobileTerminalEvents().addAll(mobileTerminalEvents);

        channel.setMobileTerminal(mt);

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        mt.getChannels().addAll(channels);
        terminalDao.createMobileTerminal(mt);
        return mt;
    }

    public MobileTerminalType createBasicMobileTerminalType() {
        MobileTerminalType mobileTerminal = new MobileTerminalType();
        mobileTerminal.setSource(MobileTerminalSource.INTERNAL);
        mobileTerminal.setType("INMARSAT_C");

        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(UUID.randomUUID().toString());
        mobileTerminal.setMobileTerminalId(terminalId);

        List<MobileTerminalAttribute> attributes = mobileTerminal.getAttributes();
        addAttribute(attributes, MobileTerminalConstants.SERIAL_NUMBER, generateARandomStringWithMaxLength(10));
        addAttribute(attributes, "SATELLITE_NUMBER", "S" + generateARandomStringWithMaxLength(4));
        addAttribute(attributes, "ANTENNA", "A");
        addAttribute(attributes, "TRANSCEIVER_TYPE", "A");
        addAttribute(attributes, "SOFTWARE_VERSION", "A");

        List<ComChannelType> channels = mobileTerminal.getChannels();
        ComChannelType comChannelType = new ComChannelType();
        channels.add(comChannelType);
        comChannelType.setGuid(UUID.randomUUID().toString());
        comChannelType.setName("VMS");

        addChannelAttribute(comChannelType, "FREQUENCY_GRACE_PERIOD", "54000");
        addChannelAttribute(comChannelType, "MEMBER_NUMBER", "100");
        addChannelAttribute(comChannelType, "FREQUENCY_EXPECTED", "7200");
        addChannelAttribute(comChannelType, "FREQUENCY_IN_PORT", "10800");
        addChannelAttribute(comChannelType, "LES_DESCRIPTION", "Thrane&Thrane");
        addChannelAttribute(comChannelType, "DNID", "1" + generateARandomStringWithMaxLength(3));
        addChannelAttribute(comChannelType, "INSTALLED_BY", "Mike Great");

        addChannelCapability(comChannelType, "POLLABLE", true);
        addChannelCapability(comChannelType, "CONFIGURABLE", true);
        addChannelCapability(comChannelType, "DEFAULT_REPORTING", true);

        Plugin plugin = new Plugin();
        plugin.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        plugin.setLabelName("Thrane&Thrane");
        plugin.setSatelliteType("INMARSAT_C");
        plugin.setInactive(false);

        mobileTerminal.setPlugin(plugin);

        return mobileTerminal;
    }

    public MobileTerminal createBasicMobileTerminal(){
        MobileTerminalType mobileTerminalType = createBasicMobileTerminalType();
        MobileTerminalPlugin mtp = new MobileTerminalPlugin();
        mtp.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        mtp.setName("Thrane&Thrane");
        mtp.setPluginSatelliteType("INMARSAT_C");
        mtp.setPluginInactive(false);
        MobileTerminal mobileTerminal = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminalType,mobileTerminalType.getAttributes().get(0).getValue(), mtp, "TEST_USERNAME");
        return mobileTerminal;
    }

    private String generateARandomStringWithMaxLength(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            builder.append(String.valueOf(val));
        }
        return builder.toString();
    }

    private void addChannelCapability(ComChannelType comChannelType, String type, boolean value) {
        ComChannelCapability channelCapability = new ComChannelCapability();

        channelCapability.setType(type);
        channelCapability.setValue(value);
        comChannelType.getCapabilities().add(channelCapability);
    }

    private void addChannelAttribute(ComChannelType comChannelType, String type, String value) {
        ComChannelAttribute channelAttribute = new ComChannelAttribute();
        channelAttribute.setType(type);
        channelAttribute.setValue(value);
        comChannelType.getAttributes().add(channelAttribute);
    }

    private void addAttribute(List<MobileTerminalAttribute> attributes, String type, String value) {
        MobileTerminalAttribute attribute = new MobileTerminalAttribute();
        attribute.setType(type);
        attribute.setValue(value);
        attributes.add(attribute);
    }

    public PollProgram createPollProgramHelper(String mobileTerminalSerialNo, LocalDateTime startDate, LocalDateTime stopDate, LocalDateTime latestRun) {

        PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(mobileTerminalSerialNo);

        PollBase pb = new PollBase();
        String terminalConnect = UUID.randomUUID().toString();
        pb.setMobileterminal(mobileTerminal);
        pb.setChannelId(UUID.randomUUID());
        pb.setTerminalConnect(terminalConnect);
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollBase(pb);
        pp.setPollState(PollStateEnum.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setUpdateTime(latestRun);
        pp.setUpdatedBy("TEST");

        return pp;
    }

    public LocalDateTime getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public LocalDateTime getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public LocalDateTime getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
