package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.util.DateUtils;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.validation.ConstraintViolationException;
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

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        UUID guid = pollProgram.getId();
        PollProgram fetchedPollProgram = pollProgramDao.getPollProgramById(guid);

//@formatter:off
        boolean ok = ((fetchedPollProgram != null) &&
                (fetchedPollProgram.getId() != null) &&
                (fetchedPollProgram.getId().equals(guid)));
//@formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_updateUserConstraintViolation() {

        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("Validation failed for classes [eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram] during persist time for groups [javax.validation.groups.Default, ]");

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        pollProgram.setUpdatedBy(new String(updatedBy));

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_withNullWillFail() {

        try {
            pollProgramDao.createPollProgram(null);
            Assert.fail();
        }catch(RuntimeException e){
            Assert.assertTrue(true);

        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram() {

        // we want to be able to tamper with the dates for proper test  coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        // change Username
        pollProgram.setUpdatedBy("update");
        // store
        pollProgramDao.updatePollProgram(pollProgram);
        em.flush();

        PollProgram fetchedPollProgram = pollProgramDao.getPollProgramById(pollProgram.getId());

// @formatter:off
        boolean ok = ((fetchedPollProgram != null) &&
                (fetchedPollProgram.getId() != null) &&
                (fetchedPollProgram.getUpdatedBy() != null) &&
                (fetchedPollProgram.getUpdatedBy().equals("update")));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram_WithNonePersistedEntityWillFail()  {


        // we want to be able to tamper with the dates for proper test  coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.updatePollProgram(pollProgram);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive() {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
        List<PollProgram> pollsAlive = pollProgramDao.getProgramPollsAlive();
        boolean found = false;
        for (PollProgram pp : pollsAlive) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithCurrentDateBiggerThenStopDate() {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();

        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, startYear - 1);
        Date stopDate = cal.getTime();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
        List<PollProgram> pollsAlive = pollProgramDao.getProgramPollsAlive();
        boolean found = false;
        for (PollProgram pp : pollsAlive) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithPollStateArchived() {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(PollStateEnum.ARCHIVED);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
        List<PollProgram> pollsAlive = pollProgramDao.getProgramPollsAlive();
        boolean found = false;
        for (PollProgram pp : pollsAlive) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertTrue(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenLatestRunBiggerThenNow() {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();

        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear + 3);
        Date latestRun = cal.getTime();

        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
        assertFalse(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenStartDateBiggerThenNow() {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        Date startDate = cal.getTime();

        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
        assertFalse(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenPollStateIsNotStarted() {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(PollStateEnum.STOPPED);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            UUID tmpGuid = pp.getId();
            if (tmpGuid.equals(pollProgram.getId())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
        assertFalse(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid() {
        // same as create since it uses the same methods to validate itself
        createPollProgram();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid_ShouldFailWithInvalidGuid() {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        PollProgram fetchedPollProgram = pollProgramDao.getPollProgramById(UUID.randomUUID());

        assertNull(fetchedPollProgram);
    }

    private PollProgram createPollProgramHelper(String mobileTerminalSerialNo, Date startDate, Date stopDate, Date latestRun) {

        PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        MobileTerminal mobileTerminal = createMobileTerminalHelper(mobileTerminalSerialNo);

        PollBase pb = new PollBase();
        String terminalConnect = UUID.randomUUID().toString();
        pb.setChannelId(UUID.randomUUID());
        pb.setMobileterminal(mobileTerminal);
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

    private MobileTerminal createMobileTerminalHelper(String serialNo) {

        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs = null;

            plugs = testDaoBean.getPluginList();

        mtp = plugs.get(0);
        mt.setSerialNo(serialNo);
        mt.setUpdatetime(new Date());
        mt.setUpdateuser("TEST");
        mt.setSource(MobileTerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setInactivated(false);

        MobileTerminal mobileTerminal = terminalDaoBean.createMobileTerminal(mt);
        if (mobileTerminal != null) return mobileTerminal;
        else return null;
    }

    private Date getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        return cal.getTime();
    }

    private Date getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.getTime();
    }

    private Date getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.getTime();
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
