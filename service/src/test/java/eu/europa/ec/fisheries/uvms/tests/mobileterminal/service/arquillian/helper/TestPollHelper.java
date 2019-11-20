package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian.helper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;

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
        mobileTerminal.setActive(true);

        MobileTerminalPlugin mtp = new MobileTerminalPlugin();
        mtp.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        mtp.setName("Thrane&Thrane");
        mtp.setPluginSatelliteType("INMARSAT_C");
        mtp.setPluginInactive(false);
        mobileTerminal.setPlugin(mtp);

        serialNumber = generateARandomStringWithMaxLength(10);

        mobileTerminal.setSatelliteNumber("S" + generateARandomStringWithMaxLength(4));
        mobileTerminal.setAntenna("A");
        mobileTerminal.setTransceiverType("A");
        mobileTerminal.setSoftwareVersion("A");
        mobileTerminal.setSerialNo(serialNumber);
        mobileTerminal.setInstalledBy("Mike Great");

        Channel channel = new Channel();
        channel.setArchived(false);
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

    public MobileTerminal createAndPersistMobileTerminalOceanRegionSupport(Asset asset,boolean aor_e,boolean aor_w,boolean por,boolean ior)  {
        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(UUID.randomUUID().toString());
        mt.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);
        mt.setInstalledBy("Mike the not so Great");

        // only set if true so we can see if code defaults to false
        if(aor_e) mt.setEastAtlanticOceanRegion(aor_e);
        if(aor_w) mt.setWestAtlanticOceanRegion(aor_w);
        if(por) mt.setPacificOceanRegion(por);
        if(ior) mt.setIndianOceanRegion(ior);


        Set<MobileTerminalPluginCapability> capabilityList = new HashSet<>();
        MobileTerminalPluginCapability mtpc = new MobileTerminalPluginCapability();
        mtpc.setPlugin(mtp.getId());
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
        pollChannel.setDNID("5555");
        pollChannel.setMemberNumber("" + (int)(Math.random() * 100000));
        pollChannel.setLesDescription("Thrane&Thrane");
        pollChannel.setExpectedFrequency(Duration.ofSeconds(60));
        pollChannel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        pollChannel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        pollChannel.setPollChannel(true);

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setMobileTerminal(mt);
        channel.setDNID("555");
        channel.setMemberNumber("" + (int)(Math.random() * 100000));
        channel.setLesDescription("Thrane&Thrane");
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        channels.add(pollChannel);
        mt.getChannels().clear();
        mt.getChannels().addAll(channels);
        terminalDao.createMobileTerminal(mt);
        return mt;
    }

    public MobileTerminal createAndPersistMobileTerminal(Asset asset)  {
        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(UUID.randomUUID().toString());
        mt.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);
        mt.setInstalledBy("Mike the not so Great");

        Set<MobileTerminalPluginCapability> capabilityList = new HashSet<>();
        MobileTerminalPluginCapability mtpc = new MobileTerminalPluginCapability();
        mtpc.setPlugin(mtp.getId());
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

        pollChannel.setDNID("5555");
        pollChannel.setMemberNumber("" + (int)(Math.random() * 100000));
        pollChannel.setLesDescription("Thrane&Thrane");
        pollChannel.setExpectedFrequency(Duration.ofSeconds(60));
        pollChannel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        pollChannel.setExpectedFrequencyInPort(Duration.ofSeconds(60));
        pollChannel.setPollChannel(true);

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setMobileTerminal(mt);
        channel.setDNID("555");
        channel.setMemberNumber("" + (int)(Math.random() * 100000));
        channel.setLesDescription("Thrane&Thrane");
        channel.setExpectedFrequency(Duration.ofSeconds(60));
        channel.setFrequencyGracePeriod(Duration.ofSeconds(60));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(60));

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        channels.add(pollChannel);
        mt.getChannels().clear();
        mt.getChannels().addAll(channels);
        terminalDao.createMobileTerminal(mt);
        return mt;
    }

    public MobileTerminal createBasicMobileTerminal2(Asset  asset){
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mobileTerminal.setArchived(false);
        mobileTerminal.setActive(true);
        mobileTerminal.setAsset(asset);
        mobileTerminal.setInstalledBy("kanalbolaget");

        MobileTerminalPlugin mtp = new MobileTerminalPlugin();
        mtp.setPluginServiceName(UUID.randomUUID().toString());
        mtp.setName("Thrane&Thrane&Thrane");
        mtp.setPluginSatelliteType("INMARSAT_C");
        mtp.setPluginInactive(false);
        mobileTerminal.setPlugin(mtp);
        mobileTerminal.setTransceiverType("TRANSPONDERTYP_100");
        mobileTerminal.setSerialNo("SN1234567890");

        Channel channel = new Channel();
        channel.setArchived(false);
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

    public ProgramPoll createProgramPoll(String connectId, OffsetDateTime startDate, OffsetDateTime stopDate, OffsetDateTime latestRun) {

        ProgramPoll pp = new ProgramPoll();
        // create a valid mobileTerminal
        Asset asset = null;
        if (connectId != null) {
            asset = assetDao.getAssetById(UUID.fromString(connectId));
        }
        MobileTerminal mobileTerminal = createAndPersistMobileTerminal(asset);

        String terminalConnect = UUID.randomUUID().toString();
        pp.setMobileterminal(mobileTerminal);
        pp.setChannelId(UUID.randomUUID());
        pp.setTerminalConnect(terminalConnect);
        pp.setUpdatedBy("TEST");
        pp.setComment("Comment");
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollState(PollStateEnum.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setUpdateTime(latestRun);
        pp.setUpdatedBy("TEST");
        pp.setPollTypeEnum(PollTypeEnum.PROGRAM_POLL);
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
        cal.set(Calendar.YEAR, 2059);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    public String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
