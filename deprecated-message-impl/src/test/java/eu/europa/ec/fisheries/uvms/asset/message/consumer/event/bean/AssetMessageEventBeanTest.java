package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.message.AbstractMessageTest;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;

@RunWith(Arquillian.class)
public class AssetMessageEventBeanTest extends AbstractMessageTest {
    
    @Test
    @RunAsClient
    public void testTest() {
        
    }

    @Test
    @Ignore
    public void testBadUUIDGetAsset() {
//        TextMessage textMessage = null;
//        AssetId assetId = new AssetId();
//        assetId.setType(AssetIdType.GUID);
//        assetId.setValue("<BAD UUID>");
//
//        getAssetEventBean.getAsset(textMessage, assetId);
//
//        Assert.assertTrue(interceptorForTest.isFailed());
        //Assert.assertTrue(interceptorForTest.getAssetFault().getFault().contains("Exception when getting asset from source : INTERNAL Error message: No asset found for <BAD UUID>"));

    }
    
    @Ignore
    @Test
    public void testGetAsset() throws AssetException {


        /*
        AssetDTO createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");

        TextMessage textMessage = null;
        AssetId assetId = new AssetId();
        assetId.setType(GUID);
        assetId.setValue(createdAsset.getAsset().getValue());

        getAssetEventBean.getAsset(textMessage, assetId);

        Assert.assertFalse(interceptorForTest.isFailed());
        Assert.assertTrue(interceptorForTest.getSuccessfulTestEvent().getMessage().contains(createdAsset.getAsset().getValue()));

        */


    }
    
    @Ignore
    @Test
    public void upsertAsset_existing() throws AssetException {


        /*

        // create one first
        AssetDTO createdAsset = null;
        // create an Asset
        createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
        em.flush();
        String createdAssetGuid = createdAsset.getAsset().getGuid();

        // then update it
        TextMessage message = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(message);

        createdAsset.setName("UPSERTED_NAME");
        assetMessageEvent.setAsset(createdAsset);
        upsertAssetMessageEventBean.upsertAsset(assetMessageEvent);
        em.flush();

        // then get it and compare changed values
        AssetDTO  fetchedAsset = assetService.getAssetByGuid(createdAssetGuid);
        String fetchedAssetGuid = fetchedAsset.getAsset().getGuid();

        Assert.assertTrue(!interceptorForTests.isFailed() && createdAssetGuid.equals(fetchedAssetGuid));

        */

    }

    @Ignore
    @Test
    public void upsertAsset_nonexisting() throws AssetException {

        /*

        AssetDTO anAsset = AssetHelper.helper_createAsset(AssetIdType.GUID);

        // then update it
        TextMessage message = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(message);

        assetMessageEvent.setAsset(anAsset);
        upsertAssetMessageEventBean.upsertAsset(assetMessageEvent);
        em.flush();

        Assert.assertTrue(!interceptorForTests.isFailed() );

        */

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
    
    @Ignore
    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupListByAssetEvent() throws AssetException {

        /*

        // create 2 asset with homeport   TEST_GOT
        Asset a1 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID, "1"), "test1");
        Asset a2 = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID,"2"), "test2");


        // create 1 asset with homeport   STHLM
        Asset wa3 = AssetHelper.helper_createAsset(AssetIdType.GUID,"3");
        wa3.setHomePort("STHLM");
        Asset a3 = assetService.createAsset(wa3, "test3");


        // create n assetgroup with searchfield GUID value an Assets GUID
        // result should contain the group id for given asset


        AssetGroupWSDL ag = AssetHelper.create_asset_group();
        List<AssetGroupSearchField> searchFields =  ag.getSearchFields();
        AssetGroupSearchField assetGroupSearchField = new AssetGroupSearchField();
        assetGroupSearchField.setKey(ConfigSearchField.GUID);
        assetGroupSearchField.setValue(a1.getAsset().getGuid());
        searchFields.add(assetGroupSearchField);
        AssetGroupWSDL  assetGroup = assetGroupService.createAssetGroup(ag, "TEST");
        String assetGroupGUID = assetGroup.getGuid();
        em.flush();

        TextMessage textMessage = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage);

        assetMessageEvent.setAssetGuid(a1.getAsset().getGuid());
        getAssetGroupListByAssetGuidEventBean.getAssetGroupListByAssetEvent(assetMessageEvent);

        SuccessfulTestEvent successfulTestEvent = interceptorForTest.getSuccessfulTestEvent();
        String result = successfulTestEvent.getMessage();

        Assert.assertTrue(result.contains(assetGroupGUID));

        */

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
        Boolean ok = message.contains(asset1.getAsset().getValue()) &&
                message.contains(asset2.getAsset().getValue()) &&
                message.contains(asset3.getAsset().getValue()) ;

        Assert.assertTrue(ok);
        */

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
        Boolean ok = message.contains(asset1.getAsset().getValue()) &&
                message.contains(asset2.getAsset().getValue()) &&
                message.contains(asset3.getAsset().getValue()) ;

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
        Boolean ok = message.contains(asset1.getAsset().getValue()) &&
                message.contains(asset2.getAsset().getValue()) &&
                message.contains(asset3.getAsset().getValue()) ;

        Assert.assertTrue(!ok);

        */

    }
}
