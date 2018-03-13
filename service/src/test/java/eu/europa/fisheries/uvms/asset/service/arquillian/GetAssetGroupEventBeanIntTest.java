package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;

/**
 * Created by thofan on 2017-06-13.
 */

@RunWith(Arquillian.class)
public class GetAssetGroupEventBeanIntTest extends TransactionalTests {



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
    @Ignore
    public void testGetAssetGroup() throws AssetException {

        /*

        AssetGroupWSDL assetGroup = AssetHelper.create_asset_group();
        AssetGroupWSDL  createdAssetGroup = assetGroupService.createAssetGroup(assetGroup, "TEST");
        em.flush();

        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(null);

        AssetGroupListByUserRequest assetGroupListByUserRequest = new AssetGroupListByUserRequest();
        assetGroupListByUserRequest.setUser("TEST");
        assetMessageEvent.setRequest(assetGroupListByUserRequest);

        getAssetGroupEventBean.getAssetGroupByUserName(assetMessageEvent);


        Assert.assertFalse(interceptorForTest.isFailed());
        String message = interceptorForTest.getSuccessfulTestEvent().getMessage();

        String createdAssetGroupUUID = createdAssetGroup.getGuid();
        Assert.assertTrue(message.contains(createdAssetGroupUUID));

        */
    }




}
