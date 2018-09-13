package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.PluginServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.DNIDListDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.DNIDList;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class PluginServiceBeanIntTest extends TransactionalTests {

    @EJB
    private PluginServiceBean pluginService;
    @EJB
    private TestPollHelper testPollHelper;
    @EJB
    private DNIDListDaoBean dnidListDao;

    private final String USERNAME = "TEST_USERNAME";

    @Test
    @OperateOnDeployment("normal")
    public void sendPoll() {

        PollResponseType pollResponseType = createPollResponseType();

        AcknowledgeTypeType ack = pluginService.sendPoll(pollResponseType, USERNAME);
        assertNotNull(ack);
        //assertEquals(AcknowledgeTypeType.OK, ack);    //this sends a message to exchange, since we dont have exchange running we mock the response and to run the MT tests we need said mocker to return a plugin instead of the ack required by this test. Thus ignore until we have changed the mocker class.
    }

    @Test
    @OperateOnDeployment("normal")
    public void processUpdatedDNIDList() {

        try {
            DNIDList dnidList = createDnidList();
            String pluginName = dnidList.getPluginName();

            dnidList = dnidListDao.create(dnidList);
            assertNotNull(dnidList.getId());

            pluginService.processUpdatedDNIDList(pluginName);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private PollResponseType createPollResponseType() {

        // PollID
        PollId pollId  = new PollId();
        pollId.setGuid(UUID.randomUUID().toString());

        // MobileTerminalType
        MobileTerminalType terminalType = testPollHelper.createBasicMobileTerminalType();

        // PollType
        PollType pollType = PollType.PROGRAM_POLL;

        String comment = "TEST_COMMENT";

        // PollAttribute List
        List<PollAttribute> attributes = new ArrayList<>();
        PollAttribute attribute = new PollAttribute();
        attribute.setKey(PollAttributeType.USER);
        attribute.setValue(USERNAME);
        attributes.add(attribute);

        // PollResponseType
        PollResponseType pollResponseType = new PollResponseType();
        pollResponseType.setPollId(pollId);
        pollResponseType.setMobileTerminal(terminalType);
        pollResponseType.setPollType(pollType);
        pollResponseType.setUserName(USERNAME);
        pollResponseType.setComment(comment);
        pollResponseType.getAttributes().addAll(attributes);

        return pollResponseType;
    }

    private DNIDList createDnidList() {
        DNIDList dnidList = new DNIDList();
        dnidList.setDnid("TEST_DN_ID");
        dnidList.setPluginName("TEST_PLUGIN_NAME");
        dnidList.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        dnidList.setUpdatedBy(USERNAME);
        return dnidList;
    }
}
