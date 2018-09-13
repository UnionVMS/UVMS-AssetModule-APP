package eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.UUID;


/**
 * Main focus for this testclass is to verify that the keyhandling is ok
 * since it is divided with different columns for different keytypes
 */

@RunWith(Arquillian.class)
public class AssetKeyTestsIT extends TransactionalTests {

    private AssetTestsHelper assetTestsHelper = new AssetTestsHelper();

    @Inject
    private AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IRCS() {
        create(AssetIdentifier.IRCS, "IRCSVAL");
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_MMSI() {
        create(AssetIdentifier.MMSI, "123456789"); // MUST be 9 in length
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_CFR() {
        create(AssetIdentifier.CFR, "CFR_VAL" + UUID.randomUUID().toString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IMO() {
        create(AssetIdentifier.IMO, "IMO_VAL");
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_UVI() {
        String val = UUID.randomUUID().toString();
        create(AssetIdentifier.UVI, val);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_ICCAT() {
        String val = UUID.randomUUID().toString();
        create(AssetIdentifier.ICCAT, val);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_GFCM() {
        String val = UUID.randomUUID().toString();
        create(AssetIdentifier.GFCM, val);
    }


    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IRCS() {

        AssetIdentifier keyType = AssetIdentifier.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val);
        String createdIRCS = theCreatedAsset.getIrcs();
        try {
            Asset fetchedEntity = get(keyType, createdIRCS);
            String fetchedIRCS = fetchedEntity.getIrcs();
            Assert.assertEquals(createdIRCS, fetchedIRCS);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_MMSI() {

        AssetIdentifier keyType = AssetIdentifier.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val);
        String createdMMSI = theCreatedAsset.getMmsi();
        try {
            Asset fetchedEntity = get(keyType, createdMMSI);
            String fetchedMMSI = fetchedEntity.getMmsi();
            Assert.assertEquals(createdMMSI, fetchedMMSI);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_CFR() {

        AssetIdentifier keyType = AssetIdentifier.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val);
        String createdCFR = theCreatedAsset.getCfr();
        try {
            Asset fetchedEntity = get(keyType, createdCFR);
            String fetchedCFR = fetchedEntity.getCfr();
            Assert.assertEquals(createdCFR, fetchedCFR);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IMO() {

        AssetIdentifier keyType = AssetIdentifier.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val);
        String createdIMO = theCreatedAsset.getImo();
        try {
            Asset fetchedEntity = get(keyType, createdIMO);
            String fetchedIMO = fetchedEntity.getImo();
            Assert.assertEquals(createdIMO, fetchedIMO);
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_ICCAT() {

        AssetIdentifier keyType = AssetIdentifier.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdICCAT = theCreatedAsset.getIccat();
        try {
            Asset fetchedEntity = get(keyType, createdICCAT);
            String fetchedUUID = fetchedEntity.getIccat();
            Assert.assertEquals(createdICCAT, fetchedUUID);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_UVI() {

        AssetIdentifier keyType = AssetIdentifier.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdUvi = theCreatedAsset.getUvi();
        try {
            Asset fetchedEntity = get(keyType, createdUvi);
            String fetchedUvi = fetchedEntity.getUvi();
            Assert.assertEquals(createdUvi, fetchedUvi);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_GFCM() {

        AssetIdentifier keyType = AssetIdentifier.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdGfcm = theCreatedAsset.getGfcm();
        try {
            Asset fetchedEntity = get(keyType, createdGfcm);
            String fetchedGfcm = fetchedEntity.getGfcm();
            Assert.assertEquals(fetchedGfcm, createdGfcm);
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IRCS() {

        AssetIdentifier keyType = AssetIdentifier.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val);
        String createdIRCS = theCreatedAsset.getIrcs();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdIRCS);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_MMSI() {

        AssetIdentifier keyType = AssetIdentifier.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val);
        String createdMMSI = theCreatedAsset.getMmsi();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdMMSI);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_CFR() {

        AssetIdentifier keyType = AssetIdentifier.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val);
        String createdCFR = theCreatedAsset.getCfr();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdCFR);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IMO() {

        AssetIdentifier keyType = AssetIdentifier.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val);
        String createdIMO = theCreatedAsset.getImo();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdIMO);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_ICCAT() {

        AssetIdentifier keyType = AssetIdentifier.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdIccat = theCreatedAsset.getIccat();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdIccat);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_UVI() {

        AssetIdentifier keyType = AssetIdentifier.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdUvi = theCreatedAsset.getUvi();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdUvi);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_GFCM() {

        AssetIdentifier keyType = AssetIdentifier.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdGFCM = theCreatedAsset.getGfcm();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdGFCM);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_IRCS() {

        AssetIdentifier keyType = AssetIdentifier.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val);
        String createdIRCS = theCreatedAsset.getIrcs();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdIRCS);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_MMSI() {

        AssetIdentifier keyType = AssetIdentifier.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val);
        String createdMMSI = theCreatedAsset.getMmsi();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdMMSI);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_CFR() {

        AssetIdentifier keyType = AssetIdentifier.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val);
        String createdCFR = theCreatedAsset.getCfr();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdCFR);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_IMO() {

        AssetIdentifier keyType = AssetIdentifier.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val);
        String createdIMO = theCreatedAsset.getImo();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdIMO);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_ICCAT() {

        AssetIdentifier keyType = AssetIdentifier.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdICCAT = theCreatedAsset.getIccat();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdICCAT);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_UVI() {

        AssetIdentifier keyType = AssetIdentifier.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdUVI = theCreatedAsset.getUvi();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdUVI);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_GFCM() {

        AssetIdentifier keyType = AssetIdentifier.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val);
        String createdGFCM = theCreatedAsset.getGfcm();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdGFCM);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (Exception e) {
            Assert.fail();
        }
    }


    private Asset get(AssetIdentifier assetIdType, String value) {
        Asset fetchedEntity = getAssetHelper(assetIdType, value);
        return fetchedEntity;

    }

    private Asset getAssetHelper(AssetIdentifier assetIdType, String value) {

        Asset fetchedAsset = null;
        switch (assetIdType) {
            case CFR:
                fetchedAsset = assetDao.getAssetByCfr(value);
                break;
            case IMO:
                fetchedAsset = assetDao.getAssetByImo(value);
                break;
            case IRCS:
                fetchedAsset = assetDao.getAssetByIrcs(value);
                break;
            case MMSI:
                fetchedAsset = assetDao.getAssetByMmsi(value);
                break;
            case ICCAT:
                fetchedAsset = assetDao.getAssetByIccat(value);
                break;
            case UVI:
                fetchedAsset = assetDao.getAssetByUvi(value);
                break;
            case GFCM:
                fetchedAsset = assetDao.getAssetByGfcm(value);
                break;
        }
        return fetchedAsset;
    }


    private Asset create(AssetIdentifier key, String value) {

        Asset assetEntity = assetTestsHelper.createAssetHelper(key, value);
        Asset createdAsset = assetDao.createAsset(assetEntity);
        em.flush();
        UUID guid = createdAsset.getId();
        Asset fetchedAsset = assetDao.getAssetById(guid);
        Assert.assertEquals(createdAsset.getId(), fetchedAsset.getId());
        return fetchedAsset;
    }


}
