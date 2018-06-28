package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageProducerBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MappedPollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceMapperException;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class MappedPollServiceBeanIntTest extends TransactionalTests {

    @EJB
    private MappedPollServiceBean mappedPollService;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @EJB
    private TestPollHelper testPollHelper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Calendar cal = Calendar.getInstance();

    @Test
    @OperateOnDeployment("normal")
    public void createPoll() throws Exception {

        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);

        // create a poll
        CreatePollResultDto createPollResultDto = mappedPollService.createPoll(pollRequestType, "TEST");
        em.flush();
        List<String> sendPolls = createPollResultDto.getSentPolls();
        String pollGuid = sendPolls.get(0);

        assertNotNull(pollGuid);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRunningProgramPolls() {
        // This is already tested in PollProgramDaoBeanIT class.
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll() throws Exception {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();

        PollDto startedProgramPoll = mappedPollService.startProgramPoll(guid.toString(), username);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValue();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll() throws Exception {

//        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();

        PollDto startedProgramPoll = mappedPollService.startProgramPoll(String.valueOf(guid), username);
        assertNotNull(startedProgramPoll);

        PollDto stoppedProgramPoll = mappedPollService.stopProgramPoll(String.valueOf(guid), username);
        assertNotNull(stoppedProgramPoll);

        List<PollValue> values = stoppedProgramPoll.getValue();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll() throws Exception {

//        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();

        PollDto startedProgramPoll = mappedPollService.startProgramPoll(String.valueOf(guid), username);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValue();
        boolean isRunning = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(isRunning);

        PollDto inactivatedProgramPoll = mappedPollService.inactivateProgramPoll(String.valueOf(guid), username);
        assertNotNull(inactivatedProgramPoll);

        List<PollValue> values1 = inactivatedProgramPoll.getValue();
        boolean isStopped = validatePollKeyValue(values1, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(isStopped);
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll_ShouldFailWithNullAsPollId() throws MobileTerminalServiceException, MobileTerminalServiceMapperException {

        try {
            mappedPollService.startProgramPoll(null, "TEST");
            Assert.fail();
        }
        catch(EJBTransactionRolledbackException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll_ShouldFailWithNullAsPollId() throws MobileTerminalServiceException, MobileTerminalServiceMapperException {

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("No poll id given");

        mappedPollService.stopProgramPoll(null, "TEST");
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

        UUID connectId = UUID.randomUUID();

        MobileTerminal mobileTerminal = testPollHelper.createMobileTerminal(connectId.toString());
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

    private Date getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return cal.getTime();
    }

    private Date getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.getTime();
    }

    private Date getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.getTime();
    }
}
