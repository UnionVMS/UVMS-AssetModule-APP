package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
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

    private Random rnd = new Random();

    @EJB
    private AssetDao assetDao;

    /*--------------------------------------
     *  create tests
     ---------------------------------------*/

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

    /*--------------------------------------
     *  get tests
     ---------------------------------------*/

    @Test
    @OperateOnDeployment("normal")
    public void get_Asset_IRCS() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.IRCS;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 8) val = val.substring(0, 8);
        AssetEntity createsEntity = create(keyType, val, date);
        String createdIRCS = createsEntity.getIRCS();
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdMMSI = createsEntity.getMMSI();
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdCFR = createsEntity.getCFR();
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdIMO = createsEntity.getIMO();
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdUUID = createsEntity.getGuid();
        try {
            AssetEntity fetchedEntity = get(keyType, createdUUID);
            String fetchedUUID = fetchedEntity.getGuid();
            Assert.assertEquals(createdUUID, fetchedUUID);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }


    /*--------------------------------------
     *  delete tests
     ---------------------------------------*/

    @Test
    @OperateOnDeployment("normal")
    public void delete_Asset_IRCS() {

        Date date = new Date();
        AssetIdType keyType = AssetIdType.IRCS;
        String val =  UUID.randomUUID().toString();
        if(val.length() > 8) val = val.substring(0, 8);
        AssetEntity createsEntity = create(keyType, val, date);
        String createdIRCS = createsEntity.getIRCS();
        try {
            assetDao.deleteAsset(createsEntity);
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdMMSI = createsEntity.getMMSI();
        try {
            assetDao.deleteAsset(createsEntity);
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdCFR = createsEntity.getCFR();
        try {
            assetDao.deleteAsset(createsEntity);
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdIMO = createsEntity.getIMO();
        try {
            assetDao.deleteAsset(createsEntity);
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
        AssetEntity createsEntity = create(keyType, val, date);
        String createdUUID = createsEntity.getGuid();
        try {
            assetDao.deleteAsset(createsEntity);
            get(keyType, createdUUID);
            Assert.fail();
        } catch (NoAssetEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            Assert.fail();
        }
    }



    /*--------------------------------------
     *  helper/convinience mehods
     ---------------------------------------*/

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
        }
        return fetchedAsset;
    }


    private AssetEntity create(AssetIdType key, String value, Date date) {

        AssetEntity assetEntity = createAssetHelper(key, value, date);
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


    public AssetEntity createAssetHelper(AssetIdType assetIdType, String value, Date date) {

        AssetEntity assetEntity = new AssetEntity();

        Carrier carrier = createCarrierHelper(date);
        assetEntity.setCarrier(carrier);

        assetEntity.setCFR(null);
        assetEntity.setGuid(null);
        assetEntity.setIMO(null);
        assetEntity.setIRCS(null);
        assetEntity.setMMSI(null);
        assetEntity.setIrcsIndicator(null);

        switch (assetIdType) {
            case CFR:
                if (value.length() > 12) value = value.substring(0, 12);
                assetEntity.setCFR(value);
                break;
            case GUID:
                assetEntity.setGuid(value);
                break;
            case IMO:
                assetEntity.setIMO(value);
                break;
            case IRCS:
                assetEntity.setIRCS(value);
                assetEntity.setIrcsIndicator("I");
                break;
            case MMSI:
                assetEntity.setMMSI(value);
                break;
        }

        assetEntity.setCommissionDay("10");
        assetEntity.setCommissionMonth("10");
        assetEntity.setCommissionYear("1961");

        assetEntity.setConstructionYear("1914");
        assetEntity.setConstructionPlace("GBG");

        assetEntity.setHullMaterial(HullMaterialEnum.GLAS_PLASTIC_FIBER);
        assetEntity.setUpdateTime(date);
        assetEntity.setUpdatedBy("TEST");


        List<Notes> notes = createNotesHelper(assetEntity, date);
        assetEntity.setNotes(notes);

        List<AssetHistory> assetHistories = createHistoriesHelper(assetEntity, date);
        assetEntity.setHistories(assetHistories);


        return assetEntity;
    }

    public List<AssetHistory> createHistoriesHelper(AssetEntity ae, Date date) {

        List<AssetHistory> assetHistories = new ArrayList<>();
        AssetHistory ah = new AssetHistory();
        ah.setActive(true);
        ah.setAsset(ae);
        ah.setCfr(ae.getCFR());
        ah.setIrcs(ae.getIRCS());
        ah.setImo(ae.getIMO());
        assetHistories.add(ah);
        ah.setMmsi(ae.getMMSI());

        List<ContactInfo> contacts = new ArrayList<>();
        ContactInfo ci = new ContactInfo();
        ci.setAsset(ah);
        ci.setName("contactInfoName");
        ci.setSource(ContactInfoSourceEnum.INTERNAL);
        contacts.add(ci);
        ah.setContactInfo(contacts);

        ah.setGrossTonnageUnit(UnitTonnage.LONDON);
        ah.setType(GearFishingTypeEnum.DEMERSAL_AND_PELAGIC);
        ah.setSegment(SegmentFUP.CA3);


        // all fields
        ah.setOwnerAddress("owneradress_" + rnd.nextInt());
        ah.setAssetAgentAddress("assetagentadress_" + rnd.nextInt());
        ah.setCountryOfImportOrExport("SWE");
        ah.setCountryOfRegistration("SWE");
        ah.setDateOfEvent(new Date());
        ah.setExternalMarking("EXTMARK");

        ah.setAdministrativeDecisionDate("19431139");

        ah.setSegmentOfAdministrativeDecision(SegmentFUP.CA3);
        ah.setEventCode(EventCodeEnum.UNK);
        ah.setAssetAgentIsAlsoOwner(true);
        ah.setLengthBetweenPerpendiculars(new BigDecimal(17));


        // ?????
        //FishingGear fishingGear =createFishingGearHelper();
        //ah.setMainFishingGear(fishingGear);

        AssetProdOrg assetProdOrg = new AssetProdOrg();
        assetProdOrg.setAddress("prodorgaddress");
        assetProdOrg.setCity("prodorgcity");
        assetProdOrg.setCode("prodorgcode");
        assetProdOrg.setFax("fax");
        assetProdOrg.setName("prodorg name");
        assetProdOrg.setPhone("0091-1-123-456");
        assetProdOrg.setMobile("004631112233");
        assetProdOrg.setZipCode(41523);

        ah.setAssetProdOrg(assetProdOrg);

        ah.setHasLicence(true);
        ah.setLicenceType("AllFish");

        ah.setLengthOverAll(new BigDecimal(25));
        ah.setName("Name_" + rnd.nextInt());
        ah.setOwnerName("Ownername_" + rnd.nextInt());
        ah.setPortOfRegistration("GBG");

        ah.setPowerOfAuxEngine(new BigDecimal(1000));
        ah.setPowerOfMainEngine(new BigDecimal(7000));
        ah.setPublicAid(PublicAidEnum.EG);
        String regnbr = "THOFAN" + rnd.nextInt();
        if (regnbr.length() > 14) regnbr = regnbr.substring(0, 14);
        ah.setRegistrationNumber(regnbr);

        ah.setGrossTonnage(new BigDecimal(25000));
        ah.setSafteyGrossTonnage(new BigDecimal(24000));
        ah.setOtherTonnage(new BigDecimal(23000));

        ah.setTypeOfExport(TypeOfExportEnum.SM);
        ah.setUpdateTime(ae.getUpdateTime());
        ah.setUpdatedBy(ae.getUpdatedBy());
        ah.setHasVms(false);

        return assetHistories;
    }


    public FishingGear createFishingGearHelper() {

        FishingGear fishingGear = new FishingGear();

        fishingGear.setCode("NK");

        FishingGearType fishingGearType = createFishingGearTypeHelper();
        fishingGear.setFishingGearType(fishingGearType);

        fishingGear.setDescription("BESKR KOD1");
        fishingGear.setExternalId(4242L);

        return fishingGear;
    }


    public FishingGearType createFishingGearTypeHelper() {


        FishingGearType fishingGearType = new FishingGearType();
        fishingGearType.setCode(7l);

        return fishingGearType;
    }


    public Carrier createCarrierHelper(Date date) {

        Carrier carrier = new Carrier();
        carrier.setActive(true);
        carrier.setSource(CarrierSourceEnum.INTERNAL);
        carrier.setUpdatedBy("TEST");
        carrier.setUpdatetime(date);
        return carrier;
    }

    public List<Notes> createNotesHelper(AssetEntity assetEntity, Date date) {

        List<Notes> notes = new ArrayList<>();
        Notes note = new Notes();
        note.setActivity("EL3");
        note.setAsset(assetEntity);
        note.setContact("TESTContact");
        note.setDate(date);
        note.setDocument("this is a document text");
        note.setLicenseHolder("verisign licenseholder");
        note.setNotes("this is a note in a document");
        note.setReadyDate(date);
        note.setSheetNumber("1");
        note.setSource(NotesSourceEnum.INTERNAL);
        note.setUpdatedBy("TEST");
        note.setUser("A USER");
        note.setUpdateTime(date);
        notes.add(note);
        return notes;
    }


}
