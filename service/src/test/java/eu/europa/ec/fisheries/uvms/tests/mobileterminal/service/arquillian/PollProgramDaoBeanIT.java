package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.ProgramPoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-05-02.
 */

@RunWith(Arquillian.class)
public class PollProgramDaoBeanIT extends TransactionalTests {

    private Calendar cal = Calendar.getInstance();

    private int startYear = 1999;
    private int latestRunYear = 2017;

    private Random rnd = new Random();

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @EJB
    private TerminalDaoBean terminalDaoBean;

    @EJB
    private MobileTerminalPluginDaoBean testDaoBean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        assertNotNull(pollProgram.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid() {
        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        UUID guid = pollProgram.getId();
        ProgramPoll fetchedPollProgram = pollProgramDao.getProgramPollById(guid);

        assertEquals(guid, fetchedPollProgram.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_updateUserConstraintViolation() {
        thrown.expect(ConstraintViolationException.class);

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        pollProgram.setUpdatedBy(new String(updatedBy));

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_withNullWillFail() {
        try {
            pollProgramDao.createProgramPoll(null);
            Assert.fail();
        }catch(RuntimeException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();

        pollProgram.setUpdatedBy("update");
        pollProgramDao.updateProgramPoll(pollProgram);
        em.flush();

        ProgramPoll fetchedPollProgram = pollProgramDao.getProgramPollById(pollProgram.getId());

        assertNotNull(fetchedPollProgram.getId());
        assertEquals("update", fetchedPollProgram.getUpdatedBy());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram_WithNonePersistedEntityWillFail()  {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.updateProgramPoll(pollProgram);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();
        List<ProgramPoll> pollsAlive = pollProgramDao.getProgramPollsAlive();

        boolean found = pollsAlive.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithCurrentDateBiggerThenStopDate() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();

        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, startYear - 1);
        Instant stopDate = Instant.now();


        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();
        List<ProgramPoll> pollsAlive = pollProgramDao.getProgramPollsAlive();

        boolean found = pollsAlive.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithPollStateArchived() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(PollStateEnum.ARCHIVED);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();
        List<ProgramPoll> pollsAlive = pollProgramDao.getProgramPollsAlive();

        boolean found = pollsAlive.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();
        boolean found = pollPrograms.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenLatestRunBiggerThenNow() {

        Date now = new Date();
        cal.setTime(now);

        Instant startDate = getStartDate();

        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear + 3);
        Instant latestRun = Instant.now();

        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();

        assertTrue(pollPrograms.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenStartDateBiggerThenNow() {

        cal.setTime(new Date(System.currentTimeMillis()));

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        Instant startDate = ZonedDateTime.now(ZoneOffset.UTC).plusYears(1).toInstant();  //starting the poll one year in the future should mean that it is not running now

        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();

        boolean found = pollPrograms.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
        assertTrue(pollPrograms.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenPollStateIsNotStarted() {

        cal.setTime(new Date(System.currentTimeMillis()));

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(PollStateEnum.STOPPED);

        pollProgramDao.createProgramPoll(pollProgram);
        em.flush();

        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollRunningAndStarted();

        boolean found = pollPrograms.stream().anyMatch(pp -> pollProgram.getId().equals(pp.getId()));

        assertFalse(found);
        assertTrue(pollPrograms.isEmpty());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid_ShouldFailWithInvalidGuid() {

        Instant startDate = getStartDate();
        Instant latestRun = getLatestRunDate();
        Instant stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        ProgramPoll pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createProgramPoll(pollProgram);
        ProgramPoll fetchedPollProgram = pollProgramDao.getProgramPollById(UUID.randomUUID());

        assertNull(fetchedPollProgram);
    }

    private ProgramPoll createPollProgramHelper(String mobileTerminalSerialNo, Instant startDate,
                                                Instant stopDate, Instant latestRun) {

        ProgramPoll pp = new ProgramPoll();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(mobileTerminalSerialNo);

        UUID terminalConnect = UUID.randomUUID();
        pp.setChannelId(UUID.randomUUID());
        pp.setMobileterminal(mobileTerminal);
        pp.setAssetId(terminalConnect);
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollState(PollStateEnum.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setUpdateTime(latestRun);
        pp.setUpdatedBy("TEST");

        return pp;
    }

    private MobileTerminal createMobileTerminalHelper(String serialNo) {

        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs = testDaoBean.getPluginList();
        mtp = plugs.get(0);
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(Instant.now());
        mt.setUpdateuser("TEST");
        mt.setSource(TerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setActive(true);

        MobileTerminal mobileTerminal = terminalDaoBean.createMobileTerminal(mt);
        if (mobileTerminal != null) return mobileTerminal;
        else return null;
    }

    // we want to be able to tamper with the dates for proper test coverage
    private Instant getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        return cal.toInstant();
    }

    private Instant getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.toInstant();
    }

    private Instant getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2059);
        return cal.toInstant();
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
