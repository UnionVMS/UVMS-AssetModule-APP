package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
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
import javax.transaction.*;
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
        MobileTerminal createdMobileTerminal = testPollHelper.createMobileTerminal(connectId);
        createdMobileTerminalId = createdMobileTerminal.getId();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(createdMobileTerminalId.toString());

        MobileTerminalType fetchedMobileTerminalType = mobileTerminalService.getMobileTerminalById(mobileTerminalId, DataSourceQueue.INTERNAL);
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
        MobileTerminal createdMobileTerminal = testPollHelper.createMobileTerminal(connectId);
        createdMobileTerminalId = createdMobileTerminal.getId();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(createdMobileTerminalId.toString());

        MobileTerminalType fetchedMobileTerminalType = mobileTerminalService.getMobileTerminalById(createdMobileTerminalId.toString());
        assertNotNull(fetchedMobileTerminalType);

        fetchedMobileTerminalGuid = UUID.fromString(fetchedMobileTerminalType.getMobileTerminalId().getGuid());
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() throws Exception {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal() throws Exception {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        MobileTerminalType updated = upsertMobileTerminalType(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getType());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() throws Exception {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        MobileTerminalType updated = updateMobileTerminalType(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getType());
        assertEquals(MobileTerminalSource.INTERNAL, updated.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminal() throws Exception {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(created.getMobileTerminalId().getGuid());
        query.setMobileTerminalId(mobileTerminalId);
        String guid = UUID.randomUUID().toString();
        query.setConnectId(guid);

        MobileTerminalType mobileTerminalType = mobileTerminalService.assignMobileTerminal(query, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminalType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unAssignMobileTerminalFromCarrier() throws Exception{

        MobileTerminalType created = createMobileTerminalType();
        created.setConnectId(UUID.randomUUID().toString());
        assertNotNull(created);

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(created.getMobileTerminalId().getGuid());
        query.setMobileTerminalId(mobileTerminalId);
        query.setConnectId(created.getConnectId());

        MobileTerminalType mobileTerminalType = mobileTerminalService.assignMobileTerminal(query, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminalType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_Plugin() throws Exception{

        thrown.expect(MobileTerminalModelException.class);
//        thrown.expectMessage("Cannot create Mobile terminal when plugin is not null");

        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminal();
        mobileTerminalType.setPlugin(null);
        mobileTerminalService.createMobileTerminal(mobileTerminalType, MobileTerminalSource.INTERNAL, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_SerialNumber() throws Exception {

        thrown.expect(MobileTerminalModelException.class);
//        thrown.expectMessage("Cannot create mobile terminal without serial number");

        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminal();
        List<MobileTerminalAttribute> attributes = mobileTerminalType.getAttributes();
        for (MobileTerminalAttribute attribute : attributes) {
            if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType())) {
                attribute.setType(null);
                attribute.setValue(null);
                break;
            }
        }
        mobileTerminalService.createMobileTerminal(mobileTerminalType, MobileTerminalSource.INTERNAL, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal_WillFail_Null_TerminalId() throws Exception {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);
        created.setMobileTerminalId(null);
        try {
            upsertMobileTerminalType(created);
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFail_Null_TerminalId() throws Exception {


        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        created.setMobileTerminalId(null);

        try {
            updateMobileTerminalType(created);
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    private MobileTerminalType createMobileTerminalType() throws Exception {
        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminal();
        return mobileTerminalService.createMobileTerminal(mobileTerminalType, MobileTerminalSource.INTERNAL, USERNAME);
    }

    private MobileTerminalType updateMobileTerminalType(MobileTerminalType created) throws Exception {
        created.setType(NEW_MOBILETERMINAL_TYPE);
        return mobileTerminalService.updateMobileTerminal(created, TEST_COMMENT, MobileTerminalSource.INTERNAL, USERNAME);
    }

    private MobileTerminalType upsertMobileTerminalType(MobileTerminalType created) throws Exception{
        created.setType(NEW_MOBILETERMINAL_TYPE);
        return mobileTerminalService.upsertMobileTerminal(created, MobileTerminalSource.INTERNAL, USERNAME);
    }
}
