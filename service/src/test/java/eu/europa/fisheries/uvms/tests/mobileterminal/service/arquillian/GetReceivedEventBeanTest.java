package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.GetReceivedEventBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PluginMapper;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class GetReceivedEventBeanTest extends TransactionalTests {

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Inject
    TestPollHelper testPollHelper;

    @Inject
    MobileTerminalServiceBean mobileTerminalService;

    @Inject
    MobileTerminalPluginDaoBean pluginDao;

    @Inject
    GetReceivedEventBean getReceivedEvent;


    @Test
    public void getMobileTerminalTest() throws Exception{
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getPluginServiceName());
        if(plugin == null){
            plugin = PluginMapper.mapModelToEntity(testPollHelper.createPluginService());
        }
        mobileTerminal.setPlugin(plugin);
        mobileTerminal = mobileTerminalService.createMobileTerminal(mobileTerminal, "Super Tester");
        assertNotNull(mobileTerminal);

        MobileTerminalId id = new MobileTerminalId();
        id.setGuid(mobileTerminal.getId().toString());
        String requestString = MobileTerminalModuleRequestMapper.createGetMobileTerminalRequest(id);

        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        TextMessage requestMessage = session.createTextMessage(requestString);
        EventMessage message = new EventMessage(requestMessage);

        MobileTerminalType output = getReceivedEvent.getMobileTerminal(message);
        assertNotNull(output);
        assertEquals(mobileTerminal.getId().toString(), output.getMobileTerminalId().getGuid());
    }
}
