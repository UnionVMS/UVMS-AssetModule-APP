package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;
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
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-05-29.
 */
@RunWith(Arquillian.class)
public class MobileTerminalServiceIntTest extends TransactionalTests {

    // TODO we do test on those transactions that are wrong in construction
    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";


    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    private MobileTerminalPluginDaoBean pluginDao;

    @Inject
    private AssetDao assetDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String USERNAME = "TEST_USERNAME";
    private static final String NEW_MOBILETERMINAL_TYPE = "IRIDIUM";
    private static final String TEST_COMMENT = "TEST_COMMENT";

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalById() {

        UUID createdMobileTerminalId;
        UUID fetchedMobileTerminalGuid;

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        MobileTerminal createdMobileTerminal = testPollHelper.createAndPersistMobileTerminal(null);
        createdMobileTerminalId = createdMobileTerminal.getId();

        MobileTerminal fetchedMobileTerminal = mobileTerminalService.getMobileTerminalEntityById(createdMobileTerminalId);
        assertNotNull(fetchedMobileTerminal);

        fetchedMobileTerminalGuid = fetchedMobileTerminal.getId();
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal() {

        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);

        MobileTerminal updated = upsertMobileTerminalEntity(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().name());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalByUpsert() {

        MobileTerminal created = testPollHelper.createBasicMobileTerminal();
        MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(created.getPlugin().getPluginServiceName());
        if(plugin == null){
            plugin = pluginDao.createMobileTerminalPlugin(created.getPlugin());
        }
        created.setPlugin(plugin);
        assertNotNull(created);

        created.setId(null);
        MobileTerminal updated = upsertMobileTerminalEntity(created);

        assertNotNull(updated);
        assertNotNull(updated.getId());
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().name());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {

        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);

        MobileTerminal updated = updateMobileTerminal(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().toString());
        assertEquals(TerminalSourceEnum.INTERNAL, updated.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminal() {
        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        Asset asset = createAndPersistAsset();
        assertNotNull(created);

        UUID guid = created.getId();

        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(asset.getId(), guid, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unAssignMobileTerminalFromCarrier() {

        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        Asset asset = createAndPersistAsset();
        assertNotNull(created);

        UUID guid = created.getId();

        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(asset.getId(), guid, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);

        em.flush();

        mobileTerminal.setAsset(asset);
        mobileTerminal = mobileTerminalService.unAssignMobileTerminal(asset.getId(), guid, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_Plugin() {

        thrown.expect(EJBTransactionRolledbackException.class);

        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        mobileTerminal.setPlugin(null);
        mobileTerminalService.createMobileTerminal(mobileTerminal, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_SerialNumber() {
        thrown.expect(EJBTransactionRolledbackException.class);

        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        mobileTerminal.setSerialNo(null);
        mobileTerminalService.createMobileTerminal(mobileTerminal, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal_WillFail_Null_TerminalId() {

        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);
        created.setId(null);
        try {
            upsertMobileTerminalEntity(created);
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFail_Null_TerminalId() {

        MobileTerminal created = testPollHelper.createAndPersistMobileTerminal(null);
        assertNotNull(created);

        created.setId(null);

        try {
            updateMobileTerminal(created);
            fail();
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    private Asset createAndPersistAsset(){
        Asset asset = AssetTestsHelper.createBasicAsset();
        return assetDao.createAsset(asset);
    }
    private MobileTerminal updateMobileTerminal(MobileTerminal created) {
        created.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        created.setSource(TerminalSourceEnum.INTERNAL);
        return mobileTerminalService.updateMobileTerminal(created, TEST_COMMENT, USERNAME);
    }

    private MobileTerminal upsertMobileTerminalEntity(MobileTerminal created) {
        created.setMobileTerminalType(MobileTerminalTypeEnum.getType(NEW_MOBILETERMINAL_TYPE));
        MobileTerminal mobileTerminal = mobileTerminalService.upsertMobileTerminal(created, TerminalSourceEnum.INTERNAL, USERNAME);
        return mobileTerminal;
    }
}
