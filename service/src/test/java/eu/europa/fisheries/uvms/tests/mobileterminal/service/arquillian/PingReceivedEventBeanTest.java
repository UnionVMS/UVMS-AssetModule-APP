//package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;
//
//import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
//import eu.europa.ec.fisheries.uvms.mobileterminal.message.PingReceivedEventBean;
//import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
//import eu.europa.fisheries.uvms.tests.TransactionalTests;
//import org.jboss.arquillian.container.test.api.OperateOnDeployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import javax.ejb.EJB;
//import javax.jms.*;
//
//import static org.junit.Assert.fail;
//
//@RunWith(Arquillian.class)
//public class PingReceivedEventBeanTest extends TransactionalTests {
//
//    @EJB
//    private PingReceivedEventBean pingReceivedEventBean;
//
//    @Test
//    @OperateOnDeployment("normal")
//    public void ping() {
//        try{
//            EventMessage message = createEventMessage();
//            pingReceivedEventBean.ping(message);
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    private EventMessage createEventMessage() {
//        ConnectionFactory connectionFactory = JMSUtils.lookupConnectionFactory();
//        try (Connection connection = connectionFactory.createConnection()) {
//            Session session = JMSUtils.connectToQueue(connection);
//            TextMessage textMessage = session.createTextMessage();
//            textMessage.setText("TEST_MESSAGE");
//            EventMessage message = new EventMessage(textMessage, "TEST_ERROR");
//            message.getJmsMessage().setJMSReplyTo(new Queue() {
//                @Override
//                public String getQueueName() {
//                    return "UVMSMobileTerminalEvent";
//                }
//            });
//            return message;
//        } catch (JMSException e) {
//            fail("FAILED: " + e);
//        }
//        return null;
//    }
//}
