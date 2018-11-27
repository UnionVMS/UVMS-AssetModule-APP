package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;
import eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
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

    @Inject
    private AssetDao assetDao;

    private String serialNumber;

    public MobileTerminal createBasicMobileTerminal() {
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mobileTerminal.setArchived(false);
        mobileTerminal.setInactivated(false);

        MobileTerminalPlugin mtp = new MobileTerminalPlugin();
        mtp.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        mtp.setName("Thrane&Thrane");
        mtp.setPluginSatelliteType("INMARSAT_C");
        mtp.setPluginInactive(false);
        mobileTerminal.setPlugin(mtp);

        Set<MobileTerminalAttributes> attributes = mobileTerminal.getMobileTerminalAttributes();
        serialNumber = generateARandomStringWithMaxLength(10);
        addAttribute(attributes, MobileTerminalConstants.SERIAL_NUMBER, serialNumber, mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.SATELLITE_NUMBER, "S" + generateARandomStringWithMaxLength(4), mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.ANTENNA, "A", mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.TRANSCEIVER_TYPE, "A", mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.SOFTWARE_VERSION, "A", mobileTerminal);
        mobileTerminal.setSerialNo(serialNumber);

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setInstalledBy("Mike Great");
        channel.setMemberNumber(generateARandomStringWithMaxLength(3));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setLesDescription("Thrane&Thrane");
        channel.setMobileTerminal(mobileTerminal);
        channel.setDNID("1" + generateARandomStringWithMaxLength(3));
        channel.setName("VMS");
        channel.setConfigChannel(true);
        channel.setDefaultChannel(true);
        channel.setPollChannel(true);

        mobileTerminal.setConfigChannel(channel);
        mobileTerminal.setDefaultChannel(channel);
        mobileTerminal.setPollChannel(channel);

        mobileTerminal.getChannels().clear();
        mobileTerminal.getChannels().add(channel);

        return mobileTerminal;
    }

    private String generateARandomStringWithMaxLength(int len) {
        Random random = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = random.nextInt(10);
            ret.append(String.valueOf(val));
        }
        return ret.toString();
    }

    private void addAttribute(Set<MobileTerminalAttributes> attributes, String type, String value, MobileTerminal terminal) {
        MobileTerminalAttributes attribute = new MobileTerminalAttributes();
        attribute.setAttribute(type);
        attribute.setValue(value);
        attribute.setMobileTerminal(terminal);
        attributes.add(attribute);
    }

    public PollRequestType createPollRequestType() {
        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(PollType.MANUAL_POLL);
        PollMobileTerminal pollMobileTerminal = createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);

        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        String startDate = OffsetDateTime.now(ZoneOffset.UTC).toString();
        pollAttribute.setValue(startDate);

        prt.getAttributes().add(pollAttribute);
        return prt;
    }

    private PollMobileTerminal createPollMobileTerminal() {

        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());

        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(asset);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(asset.getId().toString());
        pmt.setMobileTerminalId(mobileTerminal.getId().toString());
        Channel channel = mobileTerminal.getChannels().iterator().next();
        pmt.setComChannelId(channel.getId().toString());
        return pmt;
    }

    public MobileTerminal createAndPersistMobileTerminal(Asset asset)  {

        String serialNo = UUID.randomUUID().toString();

        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
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
        mtpc.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        capabilityList.add(mtpc);

        mtp.getCapabilities().addAll(capabilityList);

        if(asset != null) {
            mt.setAsset(asset);
        }

        Channel pollChannel = new Channel();
        pollChannel.setArchived(false);
        pollChannel.setMobileTerminal(mt);
        pollChannel.setInstalledBy("Mike the not so Great");
        pollChannel.setDNID("5555");
        pollChannel.setMemberNumber("666");
        pollChannel.setLesDescription("Thrane&Thrane");
        pollChannel.setExpectedFrequency(Duration.ofSeconds(60));
        pollChannel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        pollChannel.setExpectedFrequencyInPort(Duration.ofSeconds(60));

        mt.setPollChannel(pollChannel);

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setMobileTerminal(mt);
        channel.setInstalledBy("Mike the not so Great");
        channel.setDNID("555");
        channel.setMemberNumber("666");
        channel.setLesDescription("Thrane&Thrane");
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        mt.getChannels().addAll(channels);
        terminalDao.createMobileTerminal(mt);
        return mt;
    }

    public MobileTerminal createBasicMobileTerminal2(Asset  asset){
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mobileTerminal.setArchived(false);
        mobileTerminal.setInactivated(false);
        mobileTerminal.setAsset(asset);

        MobileTerminalPlugin mtp = new MobileTerminalPlugin();
        mtp.setPluginServiceName(UUID.randomUUID().toString());
        mtp.setName("Thrane&Thrane&Thrane");
        mtp.setPluginSatelliteType("INMARSAT_C");
        mtp.setPluginInactive(false);
        mobileTerminal.setPlugin(mtp);

        MobileTerminalAttributes attr = new MobileTerminalAttributes();
        attr.setAttribute("TRANSPONDER_TYPE");
        attr.setValue("TRANSPONDERTYP_100");
        attr.setMobileTerminal(mobileTerminal);
        mobileTerminal.getMobileTerminalAttributes().add(attr);
        mobileTerminal.setSerialNo("SN1234567890");

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setInstalledBy("kanalbolaget");
        channel.setMemberNumber("MEMBER1234567890");
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setLesDescription("LESDESCRIPTION");
        channel.setMobileTerminal(mobileTerminal);
        channel.setDNID("DNID1234567890");
        mobileTerminal.getChannels().clear();
        mobileTerminal.getChannels().add(channel);
        return mobileTerminal;
    }

    public MobileTerminalPlugin createMobileTerminalPlugin() {
        MobileTerminalPlugin plugin = new MobileTerminalPlugin();
        plugin.setPluginInactive(false);
        plugin.setName("Thrane&Thrane");
        plugin.setPluginSatelliteType("INMARSAT_C");
        plugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        return plugin;
    }

    public PollProgram createPollProgramHelper(String connectId, OffsetDateTime startDate, OffsetDateTime stopDate, OffsetDateTime latestRun) {

        PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        Asset asset = null;
        if (connectId != null) {
            asset = assetDao.getAssetById(UUID.fromString(connectId));
        }
        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(asset);

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

    public OffsetDateTime getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public OffsetDateTime getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public OffsetDateTime getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
