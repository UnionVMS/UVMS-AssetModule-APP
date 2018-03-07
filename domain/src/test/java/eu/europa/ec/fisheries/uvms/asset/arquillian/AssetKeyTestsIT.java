package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.uvms.entity.model.*;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.*;


/** Main focus for this testclass is to verify that the keyhandling is ok
 *  since it is divided with different columns for different keytypes
 */

@RunWith(Arquillian.class)
public class AssetKeyTestsIT extends TransactionalTests {

    /*


    private AssetTestsHelper assetTestsHelper = new AssetTestsHelper();

    private Random rnd = new Random();

    @EJB
    private AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IRCS() {
        Date date = new Date();
        create(AssetIdType.IRCS, "IRCSVAL", date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_MMSI() {
        Date date = new Date();
        create(AssetIdType.MMSI, "123456789", date); // MUST be 9 in length
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_CFR() {
        Date date = new Date();
        create(AssetIdType.CFR, "CFR_VAL" + UUID.randomUUID().toString(), date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_IMO() {
        Date date = new Date();
        create(AssetIdType.IMO, "IMO_VAL", date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_GUID() {

        // OBS since guid is always overwritten at before persist
        // the seted guid can never be the same as the retrieved one
        // the method shoud be private or nonexsisting
        // since it is pointless

        Date date = new Date();
        String theGuid = UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(AssetIdType.GUID, theGuid, date);
        String theCreatedAssetsUUID = theCreatedAsset.getGuid();
        Assert.assertNotEquals(theGuid, theCreatedAssetsUUID);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_UVI() {
        Date date = new Date();
        String val = UUID.randomUUID().toString();
        create(AssetIdType.UVI, val, date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_ICCAT() {
        Date date = new Date();
        String val = UUID.randomUUID().toString();
        create(AssetIdType.ICCAT, val, date);
    }

    @Test
    @OperateOnDeployment("normal")
    public void create_Asset_GFCM() {
        Date date = new Date();
        String val = UUID.randomUUID().toString();
        create(AssetIdType.GFCM, val, date);
    }



    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IRCS() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.IRCS;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 8) val = val.substring(0, 8);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIRCS = theCreatedAsset.getIRCS();
        try {
            AssetEntity fetchedEntity = get(keyType, createdIRCS);
            String fetchedIRCS = fetchedEntity.getIRCS();
            Assert.assertEquals(createdIRCS, fetchedIRCS);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_MMSI() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.MMSI;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 9) val = val.substring(0, 9);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdMMSI = theCreatedAsset.getMMSI();
        try {
            AssetEntity fetchedEntity = get(keyType, createdMMSI);
            String fetchedMMSI = fetchedEntity.getMMSI();
            Assert.assertEquals(createdMMSI, fetchedMMSI);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_CFR() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.CFR;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 12) val = val.substring(0, 12);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdCFR = theCreatedAsset.getCFR();
        try {
            AssetEntity fetchedEntity = get(keyType, createdCFR);
            String fetchedCFR = fetchedEntity.getCFR();
            Assert.assertEquals(createdCFR, fetchedCFR);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IMO() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.IMO;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 7) val = val.substring(0, 7);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIMO = theCreatedAsset.getIMO();
        try {
            AssetEntity fetchedEntity = get(keyType, createdIMO);
            String fetchedIMO = fetchedEntity.getIMO();
            Assert.assertEquals(createdIMO, fetchedIMO);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_GUID() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.GUID;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 7) val = val.substring(0, 7);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdUUID = theCreatedAsset.getGuid();
        try {
            AssetEntity fetchedEntity = get(keyType, createdUUID);
            String fetchedUUID = fetchedEntity.getGuid();
            Assert.assertEquals(createdUUID, fetchedUUID);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }



    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_ICCAT() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.ICCAT;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdICCAT = theCreatedAsset.getIccat();
        try {
            AssetEntity fetchedEntity = get(keyType, createdICCAT);
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
        AssetIdType keyType = AssetIdType.UVI;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdUvi = theCreatedAsset.getUvi();
        try {
            AssetEntity fetchedEntity = get(keyType, createdUvi);
            String fetchedUvi= fetchedEntity.getUvi();
            Assert.assertEquals(createdUvi, fetchedUvi);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_GFCM() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.GFCM;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdGfcm = theCreatedAsset.getGfcm();
        try {
            AssetEntity fetchedEntity = get(keyType, createdGfcm);
            String fetchedGfcm= fetchedEntity.getGfcm();
            Assert.assertEquals(fetchedGfcm, createdGfcm);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }




    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IRCS() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.IRCS;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 8) val = val.substring(0, 8);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIRCS = theCreatedAsset.getIRCS();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdIRCS);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.MMSI;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 9) val = val.substring(0, 9);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdMMSI = theCreatedAsset.getMMSI();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdMMSI);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.CFR;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 12) val = val.substring(0, 12);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdCFR = theCreatedAsset.getCFR();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdCFR);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.IMO;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 7) val = val.substring(0, 7);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIMO = theCreatedAsset.getIMO();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdIMO);
            Assert.fail();
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_GUID() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.GUID;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 7) val = val.substring(0, 7);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdUUID = theCreatedAsset.getGuid();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdUUID);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.ICCAT;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIccat = theCreatedAsset.getIccat();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdIccat);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.UVI;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdUvi = theCreatedAsset.getUvi();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdUvi);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.GFCM;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdGFCM = theCreatedAsset.getGfcm();
        try {
            assetDao.deleteAsset(theCreatedAsset);
            get(keyType, createdGFCM);
            Assert.fail();
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
        AssetIdType keyType = AssetIdType.IRCS;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 8) val = val.substring(0, 8);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIRCS = theCreatedAsset.getIRCS();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdIRCS);
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
        AssetIdType keyType = AssetIdType.MMSI;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 9) val = val.substring(0, 9);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdMMSI = theCreatedAsset.getMMSI();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdMMSI);
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
        AssetIdType keyType = AssetIdType.CFR;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 12) val = val.substring(0, 12);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdCFR = theCreatedAsset.getCFR();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdCFR);
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
        AssetIdType keyType = AssetIdType.IMO;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 7) val = val.substring(0, 7);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdIMO = theCreatedAsset.getIMO();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdIMO);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void update_Asset_GUID() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.GUID;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 7) val = val.substring(0, 7);
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdUUID = theCreatedAsset.getGuid();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdUUID);
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
        AssetIdType keyType = AssetIdType.ICCAT;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdICCAT = theCreatedAsset.getIccat();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdICCAT);
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
        AssetIdType keyType = AssetIdType.UVI;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdUVI = theCreatedAsset.getUvi();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdUVI);
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
        AssetIdType keyType = AssetIdType.GFCM;
        String val =  UUID.randomUUID().toString();
        AssetEntity theCreatedAsset = create(keyType, val, date);
        String createdGFCM = theCreatedAsset.getGfcm();
        try {
            theCreatedAsset.setUpdatedBy("CHANGED");
            assetDao.updateAsset(theCreatedAsset);
            em.flush();
            AssetEntity fetchedAsset = get(keyType, createdGFCM);
            Assert.assertEquals("CHANGED", fetchedAsset.getUpdatedBy());
        } catch (NoAssetEntityFoundException e) {
            Assert.fail();
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }







    private AssetEntity get(AssetIdType assetIdType, String value) throws AssetDaoException {
        AssetEntity fetchedEntity = getAssetHelper(assetIdType, value);
        return fetchedEntity;

    }

    private AssetEntity getAssetHelper(AssetIdType assetIdType, String value) throws AssetDaoException {

        AssetEntity fetchedAsset = null;
        switch (assetIdType) {
            case CFR:
                fetchedAsset = assetDao.getAssetByCfr(value);
                break;
            case GUID:
                fetchedAsset = assetDao.getAssetByGuid(value);
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


    private AssetEntity create(AssetIdType key, String value, Date date) {

        AssetEntity assetEntity = assetTestsHelper.createAssetHelper(key, value, date);
        try {
            AssetEntity createdAsset = assetDao.createAsset(assetEntity);
            em.flush();
            String guid = createdAsset.getGuid();
            AssetEntity fetchedAsset = assetDao.getAssetByGuid(guid);
            Assert.assertEquals(createdAsset.getId(), fetchedAsset.getId());
            return fetchedAsset;
        } catch (AssetDaoException e) {
            Assert.fail();
            return null;
        }
    }


    */



}
