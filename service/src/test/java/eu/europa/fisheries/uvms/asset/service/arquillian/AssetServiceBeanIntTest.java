package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetGroupDaoBean;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thofan on 2017-06-01.
 */

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

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
    @Ignore
    public void shouldFindAssetGroupsForAssetsWithConnectId()  {
        AssetEntity asset = new AssetEntity();
        asset.setMMSI("227632840");

        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setGuid("8d8fe897-6a01-4059-96d5-382dfd2bbae8");
        assetHistory.setCountryOfRegistration("GRC");
        assetHistory.setType(GearFishingTypeEnum.DEMERSAL_AND_PELAGIC);

        AssetGroup assetGroup = new AssetGroup();
        assetGroup.setName("Greek Vessel");
        assetGroup.setGuid("7f620b3a-1e29-4afc-85bd-14f1a3a10a5e");
        assetGroup.setUpdatedBy("test");
        assetGroup.setUpdateTime(new Date());
        assetGroup.setDynamic(true);
        assetGroup.setArchived(false);
        assetGroup.setGlobal(false);
        assetGroup.setOwner("test");
        assetGroup.setId(1L);

        AssetGroupField assetGroupSearchField1 = new AssetGroupField();
        assetGroupSearchField1.setId(1L);
        assetGroupSearchField1.setField(ConfigSearchField.FLAG_STATE.toString());
        assetGroupSearchField1.setValue("GRC1");
        assetGroupSearchField1.setUpdatedBy("test");
        assetGroupSearchField1.setUpdateTime(new Date());
        assetGroupSearchField1.setAssetGroup(assetGroup);
        assetGroup.getFields().add(assetGroupSearchField1);
        AssetGroupField assetGroupSearchField2 = new AssetGroupField();
        assetGroupSearchField2.setId(2L);
        assetGroupSearchField2.setField(ConfigSearchField.MMSI.toString());
        assetGroupSearchField2.setValue("227632841");
        assetGroupSearchField2.setUpdatedBy("test");
        assetGroupSearchField2.setUpdateTime(new Date());
        assetGroupSearchField2.setAssetGroup(assetGroup);
        assetGroup.getFields().add(assetGroupSearchField2);
        AssetGroupField assetGroupSearchField3 = new AssetGroupField();
        assetGroupSearchField3.setId(3L);
        assetGroupSearchField3.setField(ConfigSearchField.GEAR_TYPE.toString());
        assetGroupSearchField3.setValue("DEMERSAL_AND_PELAGIC1");
        assetGroupSearchField3.setUpdatedBy("test");
        assetGroupSearchField3.setUpdateTime(new Date());
        assetGroupSearchField3.setAssetGroup(assetGroup);
        assetGroup.getFields().add(assetGroupSearchField3);

        try
        {

            em.getTransaction().begin();
            AssetGroupDaoBean assetGroupDaoBean = new AssetGroupDaoBean();
            assetGroupDaoBean.createAssetGroup(assetGroup);
            List<String> assetGroupForAssetAndHistory = assetGroupDaoBean.getAssetGroupForAssetAndHistory(asset, assetHistory);
            em.getTransaction().rollback();
            Assert.assertEquals(assetGroup.getGuid(),assetGroupForAssetAndHistory.get(0));
            // insert groups
            // test with non persisted Asset, AssetHistory
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Test
    @Ignore
    public void shouldFindAssetGroupsForAssetsWithAssetIdList() throws AssetException {
        List<AssetGroupsForAssetQueryElement> assetIdentificationList = new ArrayList<>();
        AssetGroupsForAssetQueryElement assetIdentification = new AssetGroupsForAssetQueryElement();
        assetIdentification.setRefUuid("AK1234");
        List<AssetId> assetIdList = new ArrayList<>();
        AssetId assetId1 = new AssetId();
        assetId1.setType(AssetIdType.CFR);
        assetId1.setValue("BEL031341966");
        AssetId assetId2 = new AssetId();
        assetId2.setType(AssetIdType.IRCS);
        assetId2.setValue("OPFD");
        assetIdList.add(assetId1);
        assetIdList.add(assetId2);
        assetIdentification.getAssetId().addAll(assetIdList);
        Date date = new Date();
        date.setYear(1990);
        assetIdentification.setOccurrenceDate(date);
        assetIdentificationList.add(assetIdentification);


        List<AssetGroupsForAssetResponseElement> assetGroupsForAssetsList = assetService.findAssetGroupsForAssets(assetIdentificationList);
        for(AssetGroupsForAssetResponseElement assetGroupsForAssetResponseElement:assetGroupsForAssetsList){
            Assert.assertEquals("01f5bbc1-e159-46a5-b678-20d224b2369a",assetGroupsForAssetResponseElement.getGroupUuid().get(0));
        }
    }

    @Test
    @Ignore
    public void shouldFindAssetGroupsForAssetsWithNumericValues() throws AssetException {
        List<AssetGroupsForAssetQueryElement> assetIdentificationList = new ArrayList<>();
        AssetGroupsForAssetQueryElement assetIdentification = new AssetGroupsForAssetQueryElement();
        assetIdentification.setRefUuid("AK1234");
        List<AssetId> assetIdList = new ArrayList<>();
        AssetId assetId1 = new AssetId();
//        assetId1.setType(AssetIdType.CFR);
//        assetId1.setValue("LUX123456789");
        assetId1.setType(AssetIdType.GUID);
        assetId1.setValue("97a40604-45ea-11e7-bec7-4c32759615eb");
        assetIdList.add(assetId1);
        assetIdentification.getAssetId().addAll(assetIdList);
        Date date = new Date();
        assetIdentification.setOccurrenceDate(date);
        assetIdentificationList.add(assetIdentification);

        List<AssetGroupsForAssetResponseElement> assetGroupsForAssetsList = assetService.findAssetGroupsForAssets(assetIdentificationList);
        for(AssetGroupsForAssetResponseElement assetGroupsForAssetResponseElement:assetGroupsForAssetsList){
            Assert.assertEquals("2dad7434-9aba-4768-9fcf-52cb7eb783bd",assetGroupsForAssetResponseElement.getGroupUuid().get(0));
        }
    }


}
