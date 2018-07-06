package eu.europa.fisheries.uvms.asset.service.arquillian;

import java.util.List;
import java.util.Random;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

/**
 * Created by thofan on 2017-06-01.
 */

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

    Random rnd = new Random();


    @EJB
    AssetService assetService;


    @Test
    @OperateOnDeployment("normal")
    public void crtAssert() {

        // this test is to ensure that create actually works
        Asset createdAsset = null;
        try {
            // create an Asset
            createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();
            Assert.assertTrue(createdAsset != null);
        } catch (AssetException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetById_INTERNAL_TYPE_GUID() {

        Asset createdAsset = null;
        Asset fetched_asset = null;
        try {
            // create an Asset
            createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();
            // fetch it and compare guid to verify
            fetched_asset = assetService.getAssetById(createdAsset.getAssetId(), AssetDataSourceQueue.INTERNAL);
// @formatter:off
            boolean ok = fetched_asset != null &&
                    fetched_asset.getAssetId() != null &&
                    fetched_asset.getAssetId().getGuid() != null;
// @formatter:on
            if (ok) {
                Assert.assertTrue(createdAsset.getAssetId().getGuid().equals(fetched_asset.getAssetId().getGuid()));
            } else {
                Assert.fail();
            }
        } catch (AssetException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetById_INTERNAL_TYPE_INTERNAL_ID() {

        try {
            // create an Asset
            Asset createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.INTERNAL_ID), "test");
            em.flush();
            // fetch it and compare guid to verify
            Asset fetched_asset = assetService.getAssetById(createdAsset.getAssetId(), AssetDataSourceQueue.INTERNAL);
// @formatter:off
            boolean ok = fetched_asset != null &&
                    fetched_asset.getAssetId() != null &&
                    fetched_asset.getAssetId().getGuid() != null;
// @formatter:on
            if (ok) {
                Assert.assertTrue(createdAsset.getAssetId().getGuid().equals(fetched_asset.getAssetId().getGuid()));
            } else {
                Assert.fail();
            }
        } catch (AssetException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void upsert_createVersion() {

        Asset createdAsset = null;
        Asset fetched_asset = null;
        try {
            // create an Asset
            createdAsset = assetService.upsertAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();
            // fetch it and compare guid to verify
            fetched_asset = assetService.getAssetById(createdAsset.getAssetId(), AssetDataSourceQueue.INTERNAL);
// @formatter:off
            boolean ok = fetched_asset != null &&
                    fetched_asset.getAssetId() != null &&
                    fetched_asset.getAssetId().getGuid() != null;
// @formatter:on
            if (ok) {
                Assert.assertTrue(createdAsset.getAssetId().getGuid().equals(fetched_asset.getAssetId().getGuid()));
            } else {
                Assert.fail();
            }
        } catch (AssetException e) {
            Assert.fail();
        }
    }

    @Test
    public void getAssetListByGUID() throws Exception {
        Asset asset = AssetHelper.helper_createAsset(AssetIdType.INTERNAL_ID);
        Asset createdAsset = assetService.createAsset(asset, "Test");

        AssetListQuery query = getBasicAssetQuery();
        AssetListCriteriaPair criteria = new AssetListCriteriaPair();
        criteria.setKey(ConfigSearchField.GUID);
        criteria.setValue(createdAsset.getAssetId().getGuid());
        query.getAssetSearchCriteria().getCriterias().add(criteria);

        ListAssetResponse assetsResponse = assetService.getAssetList(query);
        List<Asset> assets = assetsResponse.getAsset();
        
        assertEquals(1, assets.size());
        assertEquals(createdAsset.getAssetId().getGuid(), assets.get(0).getAssetId().getGuid());
    }

    public AssetListQuery getBasicAssetQuery() {
        AssetListQuery query = new AssetListQuery();
        AssetListPagination pagination = new AssetListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);
        query.setPagination(pagination);
        AssetListCriteria listCriteria = new AssetListCriteria();
        listCriteria.setIsDynamic(true);
        query.setAssetSearchCriteria(listCriteria);
        return query;
    }

}
