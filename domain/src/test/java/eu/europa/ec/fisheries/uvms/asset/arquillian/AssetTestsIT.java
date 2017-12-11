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


        // TODO
        // all fields
        ah.setOwnerAddress("owneradress_" + rnd.nextInt());
        ah.setAssetAgentAddress("assetagentadress_" + rnd.nextInt());
        ah.setCountryOfImportOrExport("SWE");
        ah.setCountryOfRegistration("SWE");

        ah.setAdministrativeDecisionDate(null);
        ah.setDateOfEvent(null);
        ah.setSegmentOfAdministrativeDecision(null);
        ah.setEventCode(null);

        ah.setExternalMarking(null);
        ah.setSafteyGrossTonnage(null);
        ah.setAssetAgentIsAlsoOwner(null);
        ah.setLengthBetweenPerpendiculars(null);
        ah.setHasLicence(null);
        ah.setLicenceType(null);
        ah.setLengthOverAll(null);
        ah.setMainFishingGear(null);
        ah.setName("Name_" + rnd.nextInt());
        ah.setOwnerName("Ownername_" + rnd.nextInt());
        ah.setOtherTonnage(null);
        ah.setPortOfRegistration("GBG");
        ah.setPowerOfAuxEngine(null);
        ah.setPowerOfMainEngine(null);
        ah.setPublicAid(null);
        ah.setRegistrationNumber(null);
        ah.setSubFishingGear(null);
        ah.setGrossTonnage(null);

        ah.setTypeOfExport(null);
        ah.setUpdateTime(ae.getUpdateTime());
        ah.setUpdatedBy(ae.getUpdatedBy());
        ah.setHasVms(false);
        ah.setAssetProdOrg(null);

        return assetHistories;
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
