package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetGroupEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetGroupListByAssetGuidEventBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;

/**
 * Created by thofan on 2017-06-16.
 */

@RunWith(Arquillian.class)
public class GetAssetGroupListByAssetGuidEventBeanIntTest extends TransactionalTests {



    @EJB
    private GetAssetGroupListByAssetGuidEventBean getAssetGroupListByAssetGuidEventBean;

    @EJB
    private AssetGroupService assetGroupService;

    @Inject
    InterceptorForTest interceptorForTest;

    @After
    public void teardown() {
        interceptorForTest.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(getAssetGroupListByAssetGuidEventBean);
    }









}
