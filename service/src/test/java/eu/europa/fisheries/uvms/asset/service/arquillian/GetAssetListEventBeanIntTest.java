package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
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
 * NOT READY
 */

@RunWith(Arquillian.class)
public class GetAssetListEventBeanIntTest  extends TransactionalTests {



    @EJB
    private AssetService assetService;

    @Inject
    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }




    @Test
    @Ignore
    @OperateOnDeployment("normal")
    public void testGetAssetList() throws AssetException {

        /*

        // create a tiny list
        AssetDTO asset1 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID, "1"), "test1");
        AssetDTO asset2 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"2"), "test2");
        AssetDTO asset3 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"3"), "test3");

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

        */

    }

    @Test
    @Ignore
    @OperateOnDeployment("normal")
    public void testGetAssetList_FAIL_ON_LENGTH() throws AssetException {

        /*

        // create a tiny list
        AssetDTO asset1 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID, "4"), "test1");
        AssetDTO asset2 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"5"), "test2");
        AssetDTO asset3 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"6"), "test3");

        TextMessage textMessage = null;

        AssetListPagination pagination = new AssetListPagination();
        pagination.setListSize(20);
        pagination.setPage(1);
        AssetListQuery assetListQuery = new AssetListQuery();
        assetListQuery.setPagination(pagination);

        AssetListCriteria assetListCriteria = new AssetListCriteria();
        assetListCriteria.setIsDynamic(false);

        AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
        assetListCriteriaPair.setKey(ConfigSearchField.MAX_LENGTH);
        assetListCriteriaPair.setValue("10");

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

        Assert.assertTrue(!ok);

        */

    }


}












