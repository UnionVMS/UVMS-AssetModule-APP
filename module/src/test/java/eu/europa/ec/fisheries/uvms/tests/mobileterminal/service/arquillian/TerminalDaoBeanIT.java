package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.transaction.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class TerminalDaoBeanIT extends TransactionalTests {

    private static final Logger LOG = LoggerFactory.getLogger(TerminalDaoBeanIT.class);

    private Random rnd = new Random();

    @EJB
    private TerminalDaoBean terminalDaoBean;

    @EJB
    private MobileTerminalPluginDaoBean testDaoBean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);

        assertNotNull(created.getId());
        assertEquals(serialNo, created.getSerialNo());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createChannelWoName() throws SystemException, NotSupportedException {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        Channel channel = new Channel();
        channel.setMobileTerminal(mobileTerminal);
        channel.setConfigChannel(true);
        channel.setPollChannel(true);
        channel.setDefaultChannel(true);
        channel.setDnid(555);
        channel.setLesDescription("description");
        channel.setArchived(false);
        channel.setMemberNumber(5555);
        channel.setUpdateUser("tester");
        channel.setExpectedFrequency(Duration.ZERO);
        channel.setFrequencyGracePeriod(Duration.ZERO);
        channel.setExpectedFrequencyInPort(Duration.ZERO);

        channel.setName(null);

        mobileTerminal.getChannels().add(channel);
        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);
        try {
            userTransaction.commit();
            fail("There should be a not null constraint on name");
        }catch (Exception e){
            assertTrue(true); //should be here
        }
        userTransaction.begin();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFailWithUpdateUserConstraintViolation() {

        try {
            String serialNo = createSerialNumber();
            MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

            char[] chars = new char[61];
            Arrays.fill(chars, 'x');
            mobileTerminal.setUpdateuser(new String(chars));

            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            fail("Should've thrown RuntimeException");
        } catch(RuntimeException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);
        MobileTerminal fetched = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo_NON_EXISTING_SERIAL_NO() {

        MobileTerminal does_not_exist = terminalDaoBean.getMobileTerminalBySerialNo("does_not_exist");
        assertNull(does_not_exist);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_VERIFY_THAT_SETGUID_DOES_NOT_WORK_AT_CREATE() {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        MobileTerminal created = terminalDaoBean.createMobileTerminal(mobileTerminal);
        String uuid = created.getId().toString();

        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getId() != null) &&
                (fetchedBySerialNo.getId().toString().equals(uuid)));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid() {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
        UUID id = fetchedBySerialNo.getId();
        assertNotNull(id);
        MobileTerminal fetchedByGUID = terminalDaoBean.getMobileTerminalById(id);
// @formatter:off
        boolean ok = (
            (fetchedBySerialNo.getSerialNo() != null) &&
            (fetchedBySerialNo.getId() != null) &&
            (fetchedBySerialNo.getSerialNo().equals(serialNo)) &&
            (fetchedByGUID != null)) &&
            (fetchedByGUID.getId() != null) &&
            (fetchedByGUID.getId().toString().equals(fetchedBySerialNo.getId().toString())
        );
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid_NON_EXISTING_GUID() {
        MobileTerminal mt =  terminalDaoBean.getMobileTerminalById(UUID.randomUUID());
        assertNull(mt);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        String updateUser = UUID.randomUUID().toString();
        mobileTerminal.setUpdateuser(updateUser);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = '" + updateUser + "'";

        List<MobileTerminal> mobileTerminals = terminalDaoBean.getMobileTerminalsByQuery(sql);
// @formatter:off
        boolean ok = (
                (mobileTerminals != null) &&
                (mobileTerminals.size() > 0));
// @formatter:on
        boolean found = false;
        if (ok) {
            for (MobileTerminal mt : mobileTerminals) {
                String wrkUpdateUser = mt.getUpdateuser();
                if (wrkUpdateUser.equals(updateUser)) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery_ShouldFailWithInvalidSqlQuery() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = 'test'"; // lower cases

        List<MobileTerminal> mobileTerminals = terminalDaoBean.getMobileTerminalsByQuery(sql);
// @formatter:off
        boolean nullOrEmpty = (
                (mobileTerminals == null) ||
                (mobileTerminals.size() <= 0));
// @formatter:on

        assertTrue(nullOrEmpty);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        mobileTerminal.setUpdateuser("NEW_TEST_USER");
        MobileTerminal updated = terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();

        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getId() != null) &&
                (fetchedBySerialNo.getUpdateuser().equalsIgnoreCase(updated.getUpdateuser())));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithGuidConstraintViolation() {

        thrown.expect(IllegalArgumentException.class);

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        String uuid = UUID.randomUUID().toString() + "length-violation";
        fetchedBySerialNo.setId(UUID.fromString(uuid));

        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithNoPersistedEntity() {

        thrown.expect(EJBTransactionRolledbackException.class);

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();
    }

    private MobileTerminal createMobileTerminalHelper(String serialNo) {

        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs;

            plugs = testDaoBean.getPluginList();
            mtp = plugs.get(0);
            mt.setSerialNo(serialNo);
            mt.setUpdatetime(Instant.now());
            mt.setUpdateuser("TEST");
            mt.setSource(TerminalSourceEnum.INTERNAL);
            mt.setPlugin(mtp);
            mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
            mt.setArchived(false);
            mt.setActive(true);
        return mt;
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
