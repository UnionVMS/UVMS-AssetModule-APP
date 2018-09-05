package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PollMapper;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian.AssetTestsHelper;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

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
    AssetDao assetDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Calendar cal = Calendar.getInstance();

    @Test
    public void createPoll()  {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(pollRequestType, "TEST");
        assertNotNull(createPollResultDto);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPoll_FromMPSBIT() throws Exception {   //MPSBIT = Mapped Poll Service Bean Int Test, a test class for a, now removed, middle layer

        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);

        // create a poll
        CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(pollRequestType, "TEST");
        em.flush();

        if(createPollResultDto.getSentPolls().size() == 0 && createPollResultDto.getUnsentPolls().size() == 0) {
            Assert.fail();
        }
    }

    @Test
    public void createPollWithBrokenJMS_WillFail() {

        try {
            System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "true");
            PollRequestType pollRequestType = testPollHelper.createPollRequestType();
            pollServiceBean.createPoll(pollRequestType, "TEST");
            Assert.fail();
        }
        catch(Throwable t){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void getRunningProgramPolls() throws Exception {
        OffsetDateTime startDate = testPollHelper.getStartDate();
        OffsetDateTime latestRun = testPollHelper.getLatestRunDate();
        OffsetDateTime stopDate = testPollHelper.getStopDate();

        int numberOfProgramB4 = pollServiceBean.getRunningProgramPolls().size();

        PollProgram pollProgram = testPollHelper.createPollProgramHelper(null, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        List<PollDto> runningProgramPolls = pollServiceBean.getRunningProgramPolls();
        assertNotNull(runningProgramPolls);
        assertEquals(numberOfProgramB4 + 1, runningProgramPolls.size());
    }

    @Test
    public void startProgramPoll() {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        OffsetDateTime startDate = testPollHelper.getStartDate();
        OffsetDateTime latestRun = testPollHelper.getLatestRunDate();
        OffsetDateTime stopDate = testPollHelper.getStopDate();

        PollProgram pollProgram = testPollHelper.createPollProgramHelper(null, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

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
    public void startProgramPoll_FromMPSBIT() throws Exception {

        // we want to be able to tamper with the dates for proper test coverage
        OffsetDateTime startDate = getStartDate();
        OffsetDateTime latestRun = getLatestRunDate();
        OffsetDateTime stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();

        //PollDto startedProgramPoll = mappedPollService.startProgramPoll(guid.toString(), username);
        PollResponseType pollResponse = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollDto startedProgramPoll = PollMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValue();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(found);
    }

    @Test
    public void stopProgramPoll() {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        OffsetDateTime startDate = testPollHelper.getStartDate();
        OffsetDateTime latestRun = testPollHelper.getLatestRunDate();
        OffsetDateTime stopDate = testPollHelper.getStopDate();

        PollProgram pollProgram = testPollHelper.createPollProgramHelper(null, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

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
    public void stopProgramPoll_FromMPSBIT() throws Exception {

//        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        // we want to be able to tamper with the dates for proper test coverage
        OffsetDateTime startDate = getStartDate();
        OffsetDateTime latestRun = getLatestRunDate();
        OffsetDateTime stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();

        //PollDto startedProgramPoll = mappedPollService.startProgramPoll(String.valueOf(guid), username);
        PollResponseType pollResponse = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollDto startedProgramPoll = PollMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        //PollDto stoppedProgramPoll = mappedPollService.stopProgramPoll(String.valueOf(guid), username);
        pollResponse = pollServiceBean.stopProgramPoll(String.valueOf(guid), username);
        PollDto stoppedProgramPoll = PollMapper.mapPoll(pollResponse);
        assertNotNull(stoppedProgramPoll);

        List<PollValue> values = stoppedProgramPoll.getValue();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(found);
    }

    @Test
    public void inactivateProgramPoll() {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        OffsetDateTime startDate = testPollHelper.getStartDate();
        OffsetDateTime latestRun = testPollHelper.getLatestRunDate();
        OffsetDateTime stopDate = testPollHelper.getStopDate();

        PollProgram pollProgram = testPollHelper.createPollProgramHelper(null, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

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
    @OperateOnDeployment("normal")   //TODO: Move to PollServiceBeanTest
    public void inactivateProgramPoll_FromMPSBIT() throws Exception {

//        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        // we want to be able to tamper with the dates for proper test coverage
        OffsetDateTime startDate = getStartDate();
        OffsetDateTime latestRun = getLatestRunDate();
        OffsetDateTime stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();

        //PollDto startedProgramPoll = mappedPollService.startProgramPoll(String.valueOf(guid), username);
        PollResponseType pollResponse = pollServiceBean.startProgramPoll(guid.toString(), username);
        PollDto startedProgramPoll = PollMapper.mapPoll(pollResponse);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValue();
        boolean isRunning = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(isRunning);

        //PollDto inactivatedProgramPoll = mappedPollService.inactivateProgramPoll(String.valueOf(guid), username);
        pollResponse = pollServiceBean.inactivateProgramPoll(String.valueOf(guid), username);
        PollDto inactivatedProgramPoll = PollMapper.mapPoll(pollResponse);
        assertNotNull(inactivatedProgramPoll);

        List<PollValue> values1 = inactivatedProgramPoll.getValue();
        boolean isStopped = validatePollKeyValue(values1, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(isStopped);
    }

    @Test
    public void getPollProgramRunningAndStarted() {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        OffsetDateTime startDate = testPollHelper.getStartDate();
        OffsetDateTime latestRun = testPollHelper.getLatestRunDate();
        OffsetDateTime stopDate = testPollHelper.getStopDate();

        PollProgram pollProgram = testPollHelper.createPollProgramHelper(null, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

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
    @OperateOnDeployment("normal")    //TODO: Move to PollServiceBeanTest
    public void startProgramPoll_ShouldFailWithNullAsPollId() throws Exception {

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
    public void stopProgramPoll_ShouldFailWithNullAsPollId() throws Exception {

        thrown.expect(EJBTransactionRolledbackException.class);
//        thrown.expectMessage("No poll id given");

        pollServiceBean.stopProgramPoll(null, "TEST");
    }


    private PollRequestType helper_createPollRequestType(PollType pollType) throws Exception {

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

    private PollMobileTerminal helper_createPollMobileTerminal() throws Exception {

        UUID connectId = assetDao.createAsset(AssetTestsHelper.createBasicAsset()).getId();

        MobileTerminal mobileTerminal = testPollHelper.createAndPersistMobileTerminal(connectId.toString());
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(connectId.toString());
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

    private OffsetDateTime getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    private OffsetDateTime getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }

    private OffsetDateTime getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return OffsetDateTime.ofInstant(cal.toInstant(), ZoneOffset.UTC);
    }
}
