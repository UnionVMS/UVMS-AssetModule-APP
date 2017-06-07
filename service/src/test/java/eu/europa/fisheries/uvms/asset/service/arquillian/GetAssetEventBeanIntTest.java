package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetSuccessfulTestEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.SuccessfulTestEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetEventBean;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.TextMessage;


import java.util.UUID;

import static eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType.GUID;

@RunWith(Arquillian.class)
public class GetAssetEventBeanIntTest extends TransactionalTests {

    @EJB
    private GetAssetEventBean getAssetEventBean;

    @EJB
    private AssetService assetService;

    @Inject
    InterceptorForTest interceptorForTest;

    @After
    public void teardown() {
        interceptorForTest.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(getAssetEventBean);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testBadUUIDGetAsset() {
        TextMessage textMessage = null;
        AssetId assetId = new AssetId();
        assetId.setType(GUID);
        assetId.setValue("<BAD UUID>");

        getAssetEventBean.getAsset(textMessage, assetId);

        Assert.assertTrue(interceptorForTest.isFailed());
        Assert.assertTrue(interceptorForTest.getAssetFault().getFault().contains("Exception when getting asset from source : INTERNAL Error message: No asset found for <BAD UUID>"));

    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetAsset() throws AssetException {
        Asset createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");

        TextMessage textMessage = null;
        AssetId assetId = new AssetId();
        assetId.setType(GUID);
        assetId.setValue(createdAsset.getAssetId().getValue());

        getAssetEventBean.getAsset(textMessage, assetId);

        Assert.assertFalse(interceptorForTest.isFailed());
        Assert.assertTrue(interceptorForTest.getSuccessfulTestEvent().getMessage().contains(createdAsset.getAssetId().getValue()));

    }

}

@LocalBean
@Singleton
class InterceptorForTest {

    private boolean failed;
    private AssetFault assetFault;
    private SuccessfulTestEvent successfulTestEvent;

    public void listenForAssetMessageErrorEvent(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
        failed = true;
        assetFault = message.getFault();
    }

    public void listenForAssetSuccessfulTestEvent(@Observes @AssetSuccessfulTestEvent SuccessfulTestEvent successfulTestEvent) {
        this.successfulTestEvent = successfulTestEvent;
    }

    public boolean isFailed() {
        return failed;
    }

    public AssetFault getAssetFault() {
        return assetFault;
    }

    public SuccessfulTestEvent getSuccessfulTestEvent() {
        return successfulTestEvent;
    }

    public void recycle() {
        failed = false;
        assetFault = null;
        successfulTestEvent = null;
    }

}

