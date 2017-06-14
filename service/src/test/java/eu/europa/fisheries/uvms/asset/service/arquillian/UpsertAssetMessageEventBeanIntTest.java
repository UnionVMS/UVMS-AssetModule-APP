package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.SuccessfulTestEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListEventBean;
import eu.europa.ec.fisheries.uvms.asset.service.bean.UpsertAssetMessageEventBean;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
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
 * Created by thofan on 2017-06-14.
 */

@RunWith(Arquillian.class)
public class UpsertAssetMessageEventBeanIntTest extends TransactionalTests {



    @EJB
    private UpsertAssetMessageEventBean upsertAssetMessageEventBean;

    @EJB
    private AssetService assetService;

    @Inject
    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(upsertAssetMessageEventBean);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertAsset_existing() throws AssetException {

        // create one first
        Asset createdAsset = null;
        // create an Asset
        createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
        em.flush();
        String createdAssetGuid = createdAsset.getAssetId().getGuid();

        // then update it
        TextMessage message = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(message);

        createdAsset.setName("UPSERTED_NAME");
        assetMessageEvent.setAsset(createdAsset);
        upsertAssetMessageEventBean.upsertAsset(assetMessageEvent);
        em.flush();

        // then get it and compare changed values
        Asset  fetchedAsset = assetService.getAssetByGuid(createdAssetGuid);
        String fetchedAssetGuid = fetchedAsset.getAssetId().getGuid();

        Assert.assertTrue(!interceptorForTests.isFailed() && createdAssetGuid.equals(fetchedAssetGuid));
    }


    @Test
    @OperateOnDeployment("normal")
    public void upsertAsset_nonexisting() throws AssetException {

        Asset anAsset = AssetHelper.helper_createAsset(AssetIdType.GUID);

        // then update it
        TextMessage message = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(message);

        assetMessageEvent.setAsset(anAsset);
        upsertAssetMessageEventBean.upsertAsset(assetMessageEvent);
        em.flush();

        Assert.assertTrue(!interceptorForTests.isFailed() );
    }





}
