package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PollServiceBeanIntTest extends TransactionalTests {

    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";


    @EJB
    private PollServiceBean pollService;

    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createPoll() throws MobileTerminalServiceException {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        CreatePollResultDto createPollResultDto = pollService.createPoll(pollRequestType, "TEST");
        assertNotNull(createPollResultDto);
    }

    @Test
    public void createPollWithBrokenJMS_WillFail() throws  MobileTerminalServiceException {

        try {
            System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "true");
            PollRequestType pollRequestType = testPollHelper.createPollRequestType();
            pollService.createPoll(pollRequestType, "TEST");
            Assert.fail();
        }
        catch(Throwable t){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void getRunningProgramPolls()  {
        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        List<PollResponseType> runningProgramPolls = pollService.getRunningProgramPolls();
        assertNotNull(runningProgramPolls);
        assertEquals(1, runningProgramPolls.size());
    }

    @Test
    public void startProgramPoll() throws MobileTerminalServiceException {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        PollResponseType responseType = pollService.startProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
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
    public void stopProgramPoll() throws  MobileTerminalServiceException {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        PollResponseType responseType = pollService.stopProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
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
    public void inactivateProgramPoll() throws MobileTerminalServiceException {
        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        PollResponseType responseType = pollService.inactivateProgramPoll(pollProgram.getId().toString(), pollProgram.getUpdatedBy());
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
    public void getPollProgramRunningAndStarted() throws MobileTerminalModelException {

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        List<PollResponseType> responseTypeList = pollService.timer();

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
}
