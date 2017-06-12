package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListEventBean;
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
 * NOT READY
 */

@RunWith(Arquillian.class)
public class GetAssetListEventBeanIntTest  extends TransactionalTests {


    @EJB
    private GetAssetListEventBean getAssetListEventBean;

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
        Assert.assertNotNull(getAssetListEventBean);
    }


    @Test
    @OperateOnDeployment("normal")
    public void testGetAssetList() throws AssetException {

        // create a tiny list
        Asset asset1 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID, "1"), "test1");
        Asset asset2 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"2"), "test2");
        Asset asset3 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"3"), "test3");

        TextMessage textMessage = null;

        AssetListPagination pagination = new AssetListPagination();
        pagination.setListSize(20);
        pagination.setPage(1);
        AssetListQuery assetListQuery = new AssetListQuery();
        assetListQuery.setPagination(pagination);

        AssetListCriteria assetListCriteria = new AssetListCriteria();
        assetListCriteria.setIsDynamic(false);

        AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
        assetListCriteriaPair.setKey(ConfigSearchField.MIN_LENGTH);
        assetListCriteriaPair.setValue("12");

        assetListCriteria.getCriterias().add(assetListCriteriaPair);
        assetListQuery.setAssetSearchCriteria(assetListCriteria);

        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage, assetListQuery);
        getAssetListEventBean.getAssetList(assetMessageEvent);

        Assert.assertFalse(interceptorForTests.isFailed());
        String message = interceptorForTests.getSuccessfulTestEvent().getMessage();

        // ALL 3 ID:s MUST exist
        Boolean ok = message.contains(asset1.getAssetId().getValue()) &&
                message.contains(asset2.getAssetId().getValue()) &&
                message.contains(asset3.getAssetId().getValue()) ;

        Assert.assertTrue(ok);

    }


}












