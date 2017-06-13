package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListByAssetGroupEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.PingEventBean;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.TextMessage;

/**
 * Created by thofan on 2017-06-13.
 */

@RunWith(Arquillian.class)
public class PingEventBeanIntTest extends TransactionalTests{



    @EJB
    private PingEventBean pingEventBean;



    @Inject
    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(pingEventBean);
    }


    @Test
    @OperateOnDeployment("normal")
    public void testGetAssetListByAssetGroups() throws AssetException {


        AssetMessageEvent assertMessageEvent = new AssetMessageEvent(null );
        assertMessageEvent.setUsername("TEST");

        pingEventBean.ping(assertMessageEvent);


        Assert.assertFalse(interceptorForTests.isFailed());
        String message = interceptorForTests.getSuccessfulTestEvent().getMessage();


        Assert.assertTrue(message.contains("pong"));

    }



}
