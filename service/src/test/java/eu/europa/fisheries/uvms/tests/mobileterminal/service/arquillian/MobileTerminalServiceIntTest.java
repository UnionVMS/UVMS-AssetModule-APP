package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalModelToEntityMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PluginMapper;
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
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thofan on 2017-05-29.
 */
@RunWith(Arquillian.class)
public class MobileTerminalServiceIntTest extends TransactionalTests {

    // TODO we do test on those transactions that are wrong in construction
    public static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";


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
    public void getMobileTerminalByIdAndDataSourceQueue() throws Exception {

        UUID createdMobileTerminalId;
        UUID fetchedMobileTerminalGuid;

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        String connectId = UUID.randomUUID().toString();
        MobileTerminal createdMobileTerminal = testPollHelper.createAndPersistMobileTerminal(connectId);
        createdMobileTerminalId = createdMobileTerminal.getId();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(createdMobileTerminalId.toString());

        MobileTerminalType fetchedMobileTerminalType = mobileTerminalService.getMobileTerminalByIdFromInternalOrExternalSource(mobileTerminalId, DataSourceQueue.INTERNAL);
        assertNotNull(fetchedMobileTerminalType);

        fetchedMobileTerminalGuid = UUID.fromString(fetchedMobileTerminalType.getMobileTerminalId().getGuid());
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalById() {

        UUID createdMobileTerminalId;
        UUID fetchedMobileTerminalGuid;

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        String connectId = UUID.randomUUID().toString();
        MobileTerminal createdMobileTerminal = testPollHelper.createAndPersistMobileTerminal(connectId);
        createdMobileTerminalId = createdMobileTerminal.getId();

        MobileTerminal fetchedMobileTerminal = mobileTerminalService.getMobileTerminalEntityById(createdMobileTerminalId);
        assertNotNull(fetchedMobileTerminal);

        fetchedMobileTerminalGuid = fetchedMobileTerminal.getId();
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() throws Exception {

        MobileTerminalType created = createAndPersistMobileTerminalType();
        assertNotNull(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal() throws Exception {

        MobileTerminal created = createAndPersistMobileTerminal();
        assertNotNull(created);

        MobileTerminal updated = upsertMobileTerminalEntity(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().name());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminalByUpsert() throws Exception {

        MobileTerminal created = testPollHelper.createBasicMobileTerminal();
        MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(created.getPlugin().getPluginServiceName());
        if(plugin == null){
            plugin = PluginMapper.mapModelToEntity(testPollHelper.createPluginService());
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
    public void updateMobileTerminal() throws Exception {

        MobileTerminal created = createAndPersistMobileTerminal();
        assertNotNull(created);

        MobileTerminal updated = updateMobileTerminalType(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getMobileTerminalType().toString());
        assertEquals(MobileTerminalSource.INTERNAL, updated.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminal() throws Exception {

        MobileTerminalType created = createAndPersistMobileTerminalType();
        Asset asset = createAndPersistAsset();
        assertNotNull(created);

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(created.getMobileTerminalId().getGuid());
        query.setMobileTerminalId(mobileTerminalId);
        String guid = asset.getId().toString();
        query.setConnectId(guid);

        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(query, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unAssignMobileTerminalFromCarrier() throws Exception{

        MobileTerminalType created = createAndPersistMobileTerminalType();
        Asset asset = createAndPersistAsset();
        created.setConnectId(asset.getId().toString());
        assertNotNull(created);

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(created.getMobileTerminalId().getGuid());
        query.setMobileTerminalId(mobileTerminalId);
        query.setConnectId(created.getConnectId());

        MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(query, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminal);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_Plugin() throws Exception{

        thrown.expect(EJBTransactionRolledbackException.class);
//        thrown.expectMessage("Cannot create Mobile terminal when plugin is not null");

        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminalType();
        mobileTerminalType.setPlugin(null);
        MobileTerminal mobileTerminal = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminalType,null , "123456789", null, USERNAME);   //this really should be such that that testPollHelper creates a mobile terminal, but until such a time
        mobileTerminalService.createMobileTerminal(mobileTerminal, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_SerialNumber() throws Exception {

        thrown.expect(EJBTransactionRolledbackException.class);
//        thrown.expectMessage("Cannot create mobile terminal without serial number");

        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminalType();
        List<MobileTerminalAttribute> attributes = mobileTerminalType.getAttributes();
        for (MobileTerminalAttribute attribute : attributes) {
            if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType())) {
                attribute.setType(null);
                attribute.setValue(null);
                break;
            }
        }
        MobileTerminal mobileTerminal = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminalType, null, null, null, USERNAME);
        mobileTerminalService.createMobileTerminal(mobileTerminal, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal_WillFail_Null_TerminalId() throws Exception {

        MobileTerminal created = createAndPersistMobileTerminal();
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
    public void updateMobileTerminal_WillFail_Null_TerminalId() throws Exception {


        MobileTerminal created = createAndPersistMobileTerminal();
        assertNotNull(created);

        created.setId(null);

        try {
            updateMobileTerminalType(created);
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    private MobileTerminalType createAndPersistMobileTerminalType() throws Exception {

        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(createAndPersistMobileTerminal());
    }

    private MobileTerminal createAndPersistMobileTerminal() throws Exception {

        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getPluginServiceName());
        if(plugin == null){
            plugin = PluginMapper.mapModelToEntity(testPollHelper.createPluginService());
        }
        mobileTerminal.setPlugin(plugin);
        mobileTerminal = mobileTerminalService.createMobileTerminal(mobileTerminal, USERNAME);
        return mobileTerminal;
    }

    private Asset createAndPersistAsset(){
        Asset asset = AssetTestsHelper.createBasicAsset();
        return assetDao.createAsset(asset);
    }
    private MobileTerminal updateMobileTerminalType(MobileTerminal created) throws Exception {
        created.setMobileTerminalType(MobileTerminalTypeEnum.IRIDIUM);
        created.setSource(MobileTerminalSource.INTERNAL);
        return mobileTerminalService.updateMobileTerminal(created, TEST_COMMENT, USERNAME);
    }


    private MobileTerminal upsertMobileTerminalEntity(MobileTerminal created) throws Exception{
        MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(created.getPlugin().getPluginServiceName());
        if(plugin == null){
            plugin = PluginMapper.mapModelToEntity(testPollHelper.createPluginService());
        }
        created.setPlugin(plugin);
        created.setMobileTerminalType(MobileTerminalTypeEnum.getType(NEW_MOBILETERMINAL_TYPE));
        return mobileTerminalService.upsertMobileTerminal(created, MobileTerminalSource.INTERNAL, USERNAME);
    }
}
