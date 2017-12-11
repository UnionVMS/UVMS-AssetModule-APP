package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(Arquillian.class)
public class AssetTestsIT extends TransactionalTests {


    private Random rnd = new Random();


    @EJB
    private AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void createAsset() {

        Date date = new Date();

        AssetEntity assetEntity = createAssetHelper(AssetIdType.IRCS, "IRCSVAL", date);

        try {
            assetDao.createAsset(assetEntity);
            Assert.assertTrue(true);
        } catch (AssetDaoException e) {
            e.printStackTrace();
            Assert.fail();
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
                assetEntity.setCFR(value);
                break;
            case GUID:
                assetEntity.setGuid(value);
                break;
            case IMO:
                assetEntity.setIMO(value);
                break;
            case INTERNAL_ID:
                // ??????
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


        FishingGear fishingGear = new FishingGear();
        ah.setMainFishingGear(fishingGear);
        
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
        if(regnbr.length() > 14) regnbr = regnbr.substring(0, 14);
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

        fishingGear.setCode("OTB");

        FishingGearType fishingGearType = createFishingGearTypeHelper();
        fishingGear.setFishingGearType(fishingGearType);

        fishingGear.setDescription("BESKR KOD1");
        fishingGear.setExternalId(4242L);

        return fishingGear;
    }


    public FishingGearType createFishingGearTypeHelper() {


        FishingGearType fishingGearType = new FishingGearType();
        fishingGearType.setCode(4l);

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
