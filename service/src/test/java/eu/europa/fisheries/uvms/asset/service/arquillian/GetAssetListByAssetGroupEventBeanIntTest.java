package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetListByAssetGroupEventBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.TextMessage;

/**
 * Created by thofan on 2017-06-12.
 */

@RunWith(Arquillian.class)
public class GetAssetListByAssetGroupEventBeanIntTest extends TransactionalTests {



    @EJB
    private GetAssetListByAssetGroupEventBean getAssetListByAssetGroupEventBean;

    @EJB
    private AssetService assetService;

    @EJB
    private AssetGroupService assetGroupService;

    @Inject
    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(getAssetListByAssetGroupEventBean);
    }


    @Test
    @OperateOnDeployment("normal")
    @Ignore
    public void testGetAssetListByAssetGroups() throws AssetException {

        /*

        AssetGroupWSDL assetGroup = AssetHelper.create_asset_group();
        assetGroup = assetGroupService.createAssetGroup(assetGroup, "TEST");
        em.flush();

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

        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage, assetListQuery);
        GetAssetListByAssetGroupsRequest assetListByGroup = new GetAssetListByAssetGroupsRequest();

        AssetGroupSearchField assetGroupSearchField = new AssetGroupSearchField();
        assetGroupSearchField.setKey(ConfigSearchField.HOMEPORT);
        assetGroupSearchField.setValue("TEST_GOT");
        assetGroup.getSearchFields().add(assetGroupSearchField);
        assetListByGroup.getGroups().add(assetGroup);

        assetMessageEvent.setAssetListByGroup(assetListByGroup);
        getAssetListByAssetGroupEventBean.getAssetListByAssetGroups(assetMessageEvent);

        Assert.assertFalse(interceptorForTests.isFailed());
        String message = interceptorForTests.getSuccessfulTestEvent().getMessage();

        // ALL 3 ID:s MUST exist
        Boolean ok = message.contains(asset1.getAssetId().getValue()) &&
                message.contains(asset2.getAssetId().getValue()) &&
                message.contains(asset3.getAssetId().getValue()) ;

        Assert.assertTrue(ok);
        */

    }



}
