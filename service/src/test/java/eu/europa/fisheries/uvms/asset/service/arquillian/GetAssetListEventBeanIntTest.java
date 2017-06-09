package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListEventBean;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.TextMessage;

import static eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType.GUID;

/**
 * NOT READY
 */
public class GetAssetListEventBeanIntTest {


    @EJB
    private GetAssetListEventBean getAssetEventBean;

    @EJB
    private AssetService assetService;

    @Inject
    InterceptorForTests interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(getAssetEventBean);
    }

    @Test
    @Ignore
    @OperateOnDeployment("normal")
    public void testBadUUIDGetAsset() {
        TextMessage textMessage = null;
        AssetId assetId = new AssetId();
        assetId.setType(GUID);
        assetId.setValue("<BAD UUID>");

        getAssetEventBean.getAssetList(null);

        Assert.assertTrue(interceptorForTests.isFailed());
        Assert.assertTrue(interceptorForTests.getAssetFault().getFault().contains("Exception when getting asset from source : INTERNAL Error message: No asset found for <BAD UUID>"));

    }


}












