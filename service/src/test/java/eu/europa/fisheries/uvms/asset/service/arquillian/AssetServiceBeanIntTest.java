package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by thofan on 2017-06-01.
 */
@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

    @EJB
    AssetService assetService;


    @Test
    @OperateOnDeployment("normal")
    public void getAssetById_INTERNAL_TYPE_GUID() {

        try {
            // create an Asset
            Asset createdAsset = assetService.createAsset(helper_createAsset(AssetIdType.GUID), "test");
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
    public void getAssetById_INTERNAL_TYPE_INTERNAL_ID() {

        try {
            // create an Asset
            Asset createdAsset = assetService.createAsset(helper_createAsset(AssetIdType.INTERNAL_ID), "test");
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


    private Asset helper_createAsset(AssetIdType assetIdType) {

        Asset asset = new Asset();
        AssetId assetId = new AssetId();
        assetId.setType(assetIdType);
        switch (assetIdType) {
            case GUID:
                assetId.setGuid(UUID.randomUUID().toString());
                break;
            case INTERNAL_ID:
                assetId.setValue("INTERNALID_" + UUID.randomUUID().toString());
                break;
        }

        asset.setActive(true);
        asset.setAssetId(assetId);
        asset.setActive(true);

        asset.setSource(CarrierSource.INTERNAL);
        //asset.setEventHistory();
        asset.setName("TEST_NAME");
        asset.setCountryCode("3");
        asset.setGearType(GearFishingTypeEnum.UNKNOWN.name());
        asset.setHasIrcs("1");

        asset.setIrcs("1");
        asset.setExternalMarking("13");
        asset.setCfr("TEST_CFR");
        asset.setImo("2");
        asset.setMmsiNo("3");
        asset.setHasLicense(true);
        asset.setLicenseType("MOCK-license-DB");
        asset.setHomePort("TEST_GOT");
        asset.setLengthOverAll(new BigDecimal(15l));
        asset.setLengthBetweenPerpendiculars(new BigDecimal(3l));
        asset.setGrossTonnage(new BigDecimal(200));


        asset.setGrossTonnageUnit(UnitTonnage.OSLO.name());
        asset.setOtherGrossTonnage(new BigDecimal(200));
        asset.setSafetyGrossTonnage(new BigDecimal(80));
        asset.setPowerMain(new BigDecimal(10));
        asset.setPowerAux(new BigDecimal(10));

        AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
        assetProdOrgModel.setName("NAME");
        assetProdOrgModel.setCity("CITY");
        assetProdOrgModel.setAddress("ADDRESS");
        assetProdOrgModel.setCode("CODE");
        assetProdOrgModel.setPhone("070 111 222");
        asset.getContact();
        asset.getNotes();


        return asset;

    }





}
