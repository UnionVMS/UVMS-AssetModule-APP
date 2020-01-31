package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.ProgramPoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollDtoMapper;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PollServiceBeanIntTest extends TransactionalTests {

    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";

    @Inject
    private PollServiceBean pollServiceBean;

    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @Inject
    private AssetDao assetDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Calendar cal = Calendar.getInstance();

    @Test
    @OperateOnDeployment("normal")
    public void createPoll()  {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(pollRequestType);
        assertNotNull(createPollResultDto);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPoll_FromMPSBIT() {   //MPSBIT = Mapped Poll Service Bean Int Test, a test class for a, now removed, middle layer

        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);

        // create a poll
        CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(pollRequestType);
        em.flush();

        if(createPollResultDto.getSentPolls().size() == 0 && createPollResultDto.getUnsentPolls().size() == 0) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollWithBrokenJMS_WillFail() {
        try {
            System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "true");
            PollRequestType pollRequestType = testPollHelper.createPollRequestType();
            pollServiceBean.createPoll(pollRequestType);
            Assert.fail();
        }
        catch(Throwable t){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRunningProgramPolls() {
        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        int numberOfProgramB4 = pollServiceBean.getRunningProgramPolls().size();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        List<PollDto> runningProgramPolls = pollServiceBean.getRunningProgramPolls();
        assertNotNull(runningProgramPolls);
        assertEquals(numberOfProgramB4 + 1, runningProgramPolls.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll() {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        PollResponseType responseType = pollServiceBean.startProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
        List<PollAttribute> attributes = responseType.getAttributes();

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "TRUE");
                ok = true;
            }
        }
        assertTrue(ok);
        assertNotNull(responseType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll_FromMPSBIT() {

        // we want to be able to tamper with the dates for proper test coverage
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        ProgramPoll pollProgram = testPollHelper.createProgramPoll(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();

        PollResponseType pollResponse = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollDto startedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValues();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll() {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        PollResponseType responseType = pollServiceBean.stopProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
        List<PollAttribute> attributes = responseType.getAttributes();

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "FALSE");
                ok = true;
            }
        }
        assertTrue(ok);
        assertNotNull(responseType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll_FromMPSBIT() {

        // we want to be able to tamper with the dates for proper test coverage
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        ProgramPoll pollProgram = testPollHelper.createProgramPoll(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();

        PollResponseType pollResponse = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollDto startedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        pollResponse = pollServiceBean.stopProgramPoll(String.valueOf(guid), username);
        PollDto stoppedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(stoppedProgramPoll);

        List<PollValue> values = stoppedProgramPoll.getValues();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll() {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        PollResponseType responseType = pollServiceBean.inactivateProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
        List<PollAttribute> attributes = responseType.getAttributes();

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "FALSE");
                ok = true;
            }
        }
        assertTrue(ok);
        assertNotNull(responseType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll_FromMPSBIT() {

        // we want to be able to tamper with the dates for proper test coverage
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        ProgramPoll pollProgram = testPollHelper.createProgramPoll(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();

        PollResponseType pollResponse = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollDto startedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValues();
        boolean isRunning = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(isRunning);

        pollResponse = pollServiceBean.inactivateProgramPoll(String.valueOf(guid), username);
        PollDto inactivatedProgramPoll = PollDtoMapper.mapPoll(pollResponse);
        assertNotNull(inactivatedProgramPoll);

        List<PollValue> values1 = inactivatedProgramPoll.getValues();
        boolean isStopped = validatePollKeyValue(values1, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(isStopped);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Instant startDate = testPollHelper.getStartDate();
        Instant latestRun = testPollHelper.getLatestRunDate();
        Instant stopDate = testPollHelper.getStopDate();

        ProgramPoll pollProgram = testPollHelper.createProgramPoll(null, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);

        List<PollResponseType> responseTypeList = pollServiceBean.timer();

        assertNotNull(responseTypeList);
        assertEquals(1, responseTypeList.size());

        List<PollAttribute> attributes = responseTypeList.get(0).getAttributes();
        assertNotNull(attributes);

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "TRUE");
                ok = true;
            }
        }
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll_ShouldFailWithNullAsPollId() {
        try {
            pollServiceBean.startProgramPoll(null, "TEST");
            Assert.fail();
        }
        catch(EJBTransactionRolledbackException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll_ShouldFailWithNullAsPollId() {
        thrown.expect(EJBTransactionRolledbackException.class);

        pollServiceBean.stopProgramPoll(null, "TEST");
    }

    private PollRequestType helper_createPollRequestType(PollType pollType) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String startDate = format.format(cal.getTime());
        cal.set(Calendar.YEAR, 2020);
        String endDate = format.format(cal.getTime());

        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(pollType);
        PollMobileTerminal pollMobileTerminal = helper_createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);

        PollAttribute psStart = new PollAttribute();
        PollAttribute psEnd = new PollAttribute();
        PollAttribute psFreq = new PollAttribute();

        psStart.setKey(PollAttributeType.START_DATE);
        psStart.setValue(startDate);
        prt.getAttributes().add(psStart);

        psEnd.setKey(PollAttributeType.END_DATE);
        psEnd.setValue(endDate);
        prt.getAttributes().add(psEnd);

        psFreq.setKey(PollAttributeType.FREQUENCY);
        psFreq.setValue("300000");
        prt.getAttributes().add(psFreq);

        return prt;
    }

    private PollMobileTerminal helper_createPollMobileTerminal() {

        Asset asset = assetDao.createAsset(AssetTestsHelper.createBasicAsset());

        MobileTerminal mobileTerminal = testPollHelper.createAndPersistMobileTerminal(asset);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(asset.getId().toString());
        pmt.setMobileTerminalId(mobileTerminal.getId().toString());

        Set<Channel> channels = mobileTerminal.getChannels();
        Channel channel = channels.iterator().next();
        UUID channelId = channel.getId();
        pmt.setComChannelId(channelId.toString());
        return pmt;
    }

    private boolean validatePollKeyValue(List<PollValue> values, PollKey key, String value) {
        for(PollValue v : values) {
            PollKey pollKey = v.getKey();
            if(pollKey.equals(key) && v.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private Instant getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return cal.toInstant();
    }

    private Instant getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.toInstant();
    }

    private Instant getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.toInstant();
    }
}
