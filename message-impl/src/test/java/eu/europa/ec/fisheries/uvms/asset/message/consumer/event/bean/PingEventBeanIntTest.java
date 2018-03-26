package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import javax.ejb.EJB;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.message.AbstractMessageTest;

/**
 * Created by thofan on 2017-06-13.
 */

@RunWith(Arquillian.class)
public class PingEventBeanIntTest extends AbstractMessageTest{



    @EJB
    private PingEventBean pingEventBean;



//    @Inject
//    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
//        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(pingEventBean);
    }

}
