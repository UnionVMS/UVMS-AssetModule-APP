package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.types.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.Asset;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


/**
 * Main focus for this testclass is to verify that the keyhandling is ok
 * since it is divided with different columns for different keytypes
 */

@RunWith(Arquillian.class)
public class AssetKeyTestsIT extends TransactionalTests {


    private AssetTestsHelper assetTestsHelper = new AssetTestsHelper();

    private Random rnd = new Random();

    @EJB
    private AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IRCS() {
        Date date = new Date();
        create(AssetIdTypeEnum.IRCS, "IRCSVAL", date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_MMSI() {
        Date date = new Date();
        create(AssetIdTypeEnum.MMSI, "123456789", date); // MUST be 9 in length
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_CFR() {
        Date date = new Date();
        create(AssetIdTypeEnum.CFR, "CFR_VAL" + UUID.randomUUID().toString(), date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IMO() {
        Date date = new Date();
        create(AssetIdTypeEnum.IMO, "IMO_VAL", date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_UVI() {
        Date date = new Date();
        String val = UUID.randomUUID().toString();
        create(AssetIdTypeEnum.UVI, val, date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_ICCAT() {
        Date date = new Date();
        String val = UUID.randomUUID().toString();
        create(AssetIdTypeEnum.ICCAT, val, date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_GFCM() {
        Date date = new Date();
        String val = UUID.randomUUID().toString();
        create(AssetIdTypeEnum.GFCM, val, date);
    }


    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IRCS() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIRCS = theCreatedAsset.getIrcs();
        try {
            Asset fetchedEntity = get(keyType, createdIRCS);
            String fetchedIRCS = fetchedEntity.getIrcs();
            Assert.assertEquals(createdIRCS, fetchedIRCS);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_MMSI() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdMMSI = theCreatedAsset.getMmsi();
        try {
            Asset fetchedEntity = get(keyType, createdMMSI);
            String fetchedMMSI = fetchedEntity.getMmsi();
            Assert.assertEquals(createdMMSI, fetchedMMSI);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_CFR() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdCFR = theCreatedAsset.getCfr();
        try {
            Asset fetchedEntity = get(keyType, createdCFR);
            String fetchedCFR = fetchedEntity.getCfr();
            Assert.assertEquals(createdCFR, fetchedCFR);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IMO() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIMO = theCreatedAsset.getImo();
        try {
            Asset fetchedEntity = get(keyType, createdIMO);
            String fetchedIMO = fetchedEntity.getImo();
            Assert.assertEquals(createdIMO, fetchedIMO);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_ICCAT() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdICCAT = theCreatedAsset.getIccat();
        try {
            Asset fetchedEntity = get(keyType, createdICCAT);
            String fetchedUUID = fetchedEntity.getIccat();
            Assert.assertEquals(createdICCAT, fetchedUUID);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_UVI() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdUvi = theCreatedAsset.getUvi();
        try {
            Asset fetchedEntity = get(keyType, createdUvi);
            String fetchedUvi = fetchedEntity.getUvi();
            Assert.assertEquals(createdUvi, fetchedUvi);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_GFCM() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdGfcm = theCreatedAsset.getGfcm();
        try {
            Asset fetchedEntity = get(keyType, createdGfcm);
            String fetchedGfcm = fetchedEntity.getGfcm();
            Assert.assertEquals(fetchedGfcm, createdGfcm);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IRCS() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIRCS = theCreatedAsset.getIrcs();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdIRCS);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_MMSI() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdMMSI = theCreatedAsset.getMmsi();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdMMSI);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_CFR() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdCFR = theCreatedAsset.getCfr();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdCFR);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IMO() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIMO = theCreatedAsset.getImo();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdIMO);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_ICCAT() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIccat = theCreatedAsset.getIccat();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdIccat);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_UVI() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdUvi = theCreatedAsset.getUvi();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdUvi);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_GFCM() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdGFCM = theCreatedAsset.getGfcm();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            Asset fetchedAsset = get(keyType, createdGFCM);
            if (fetchedAsset != null) {
                Assert.fail();
            }
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_IRCS() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.IRCS;
        String val = UUID.randomUUID().toString();
        if (val.length() > 8) val = val.substring(0, 8);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIRCS = theCreatedAsset.getIrcs();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdIRCS);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_MMSI() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.MMSI;
        String val = UUID.randomUUID().toString();
        if (val.length() > 9) val = val.substring(0, 9);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdMMSI = theCreatedAsset.getMmsi();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdMMSI);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_CFR() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.CFR;
        String val = UUID.randomUUID().toString();
        if (val.length() > 12) val = val.substring(0, 12);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdCFR = theCreatedAsset.getCfr();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdCFR);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_IMO() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.IMO;
        String val = UUID.randomUUID().toString();
        if (val.length() > 7) val = val.substring(0, 7);
        Asset theCreatedAsset = create(keyType, val, date);
        String createdIMO = theCreatedAsset.getImo();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdIMO);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_ICCAT() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.ICCAT;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdICCAT = theCreatedAsset.getIccat();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdICCAT);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_UVI() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.UVI;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdUVI = theCreatedAsset.getUvi();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdUVI);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_GFCM() {

        Date date = new Date();
        AssetIdTypeEnum keyType = AssetIdTypeEnum.GFCM;
        String val = UUID.randomUUID().toString();
        Asset theCreatedAsset = create(keyType, val, date);
        String createdGFCM = theCreatedAsset.getGfcm();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            Asset fetchedAsset = get(keyType, createdGFCM);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    private Asset get(AssetIdTypeEnum assetIdType, String value) throws AssetDaoException {
        Asset fetchedEntity = getAssetHelper(assetIdType, value);
        return fetchedEntity;

    }

    private Asset getAssetHelper(AssetIdTypeEnum assetIdType, String value) throws AssetDaoException {

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


    private Asset create(AssetIdTypeEnum key, String value, Date date) {

        Asset assetEntity = assetTestsHelper.createAssetHelper(key, value, date);
        Asset createdAsset = assetDao.createAsset(assetEntity);
        em.flush();
        UUID guid = createdAsset.getId();
        Asset fetchedAsset = assetDao.getAssetById(guid);
        Assert.assertEquals(createdAsset.getId(), fetchedAsset.getId());
        return fetchedAsset;
    }


}
