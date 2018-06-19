package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.TerminalDaoException;
import eu.europa.fisheries.uvms.TransactionalTests;
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
import java.util.*;

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

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getSerialNo() != null) &&
                (fetchedBySerialNo.getSerialNo().equals(serialNo)));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFailWithUpdateUserConstraintViolation() {

        try {
            String serialNo = createSerialNumber();
            MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

            char[] chars = new char[61];
            Arrays.fill(chars, 'x');
            mobileTerminal.setUpdatedBy(new String(chars));

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

        // this is the same as create since they both use getMobileTerminalBySerialNo to verify functionality
        createMobileTerminal();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo_NON_EXISTING_SERIAL_NO() {

        thrown.expect(NoEntityFoundException.class);
        thrown.expectMessage("No entity found with serial no does_not_exist");
        terminalDaoBean.getMobileTerminalBySerialNo("does_not_exist");
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_VERIFY_THAT_SETGUID_DOES_NOT_WORK_AT_CREATE() {

        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        mobileTerminal.setGuid(uuid);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getGuid() != null) &&
                (fetchedBySerialNo.getGuid().equals(uuid)));
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
        String fetchedGUID = fetchedBySerialNo.getGuid();
        assertNotNull(fetchedGUID);
        MobileTerminal fetchedByGUID = terminalDaoBean.getMobileTerminalByGuid(fetchedGUID);
// @formatter:off
        boolean ok = (
            (fetchedBySerialNo.getSerialNo() != null) &&
            (fetchedBySerialNo.getGuid() != null) &&
            (fetchedBySerialNo.getSerialNo().equals(serialNo)) &&
            (fetchedByGUID != null)) &&
            (fetchedByGUID.getGuid() != null) &&
            (fetchedByGUID.getGuid().equals(fetchedBySerialNo.getGuid())
        );
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid_NON_EXISTING_GUID() {


        String aNonExistingGuid = UUID.randomUUID().toString();
        MobileTerminal mt =  terminalDaoBean.getMobileTerminalByGuid(aNonExistingGuid);
        Assert.assertTrue(mt == null);

    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        String updateUser = UUID.randomUUID().toString();
        mobileTerminal.setUpdatedBy(updateUser);

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
                String wrkUpdateUser = mt.getUpdatedBy();
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

        mobileTerminal.setGuid("UPDATED");
        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();

        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getGuid() != null) &&
                (fetchedBySerialNo.getGuid().equals("UPDATED")));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithGuidConstraintViolation() {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage("[ Error when updating. ]");

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        String uuid = UUID.randomUUID().toString() + "length-violation";
        fetchedBySerialNo.setGuid(uuid);

        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithNoPersistedEntity() {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage(" [ There is no such persisted entity to update ] ");

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
            mt.setUpdateTime(new Date());
            mt.setUpdatedBy("TEST");
            mt.setSource(MobileTerminalSourceEnum.INTERNAL);
            mt.setPlugin(mtp);
            mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
            mt.setArchived(false);
            mt.setInactivated(false);
        return mt;
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
