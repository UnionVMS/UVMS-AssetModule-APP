package eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;


import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.asset.bean.AssetMTBean;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

    @Inject
    private AssetService assetService;

    @Inject
    private TestPollHelper testPollHelper;

    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    private MobileTerminalPluginDaoBean pluginDao;

    @Inject
    private AssetMTBean assetMtBean;

    @PersistenceContext
    private EntityManager em;


    @Test
    @OperateOnDeployment("normal")
    public void createAssert() {

        // this test is to ensure that create actually works
        try {
            // create an Asset
            Asset asset = AssetTestsHelper.createBiggerAsset();
            Asset createdAsset = assetService.createAsset(asset, "test");
            commit();
            Assert.assertTrue(createdAsset != null);
            assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
            commit();
        } catch (AssetServiceException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAsset() throws AssetServiceException {

        // create an asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();
        // change it and store it
        createdAsset.setName("ÄNDRAD");
        Asset changedAsset = assetService.updateAsset(createdAsset, "CHG_USER", "En changekommentar");
        commit();

        // fetch it and check name
        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        Assert.assertEquals(createdAsset.getName(), fetchedAsset.getName());
        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset() throws AssetServiceException {

        // create an asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();

        // change it to get an audit
        createdAsset.setName("ÄNDRAD_1");
        Asset changedAsset1 = assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");
        commit();

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
        commit();

        // fetch it and it should be null
        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        Assert.assertEquals(fetchedAsset, null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetThreeTimesAndCheckRevisionsAndValues() throws AssetServiceException {

        // create an asset
        Asset asset = AssetTestsHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();
        // change it and store it
        createdAsset.setName("ÄNDRAD_1");
        Asset changedAsset1 = assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");
        commit();
        UUID historyId1 = changedAsset1.getHistoryId();

        // change it and store it
        createdAsset.setName("ÄNDRAD_2");
        Asset changedAsset2 = assetService.updateAsset(createdAsset, "CHG_USER_2", "En changekommentar");
        commit();
        UUID historyId2 = changedAsset2.getHistoryId();

        // change it and store it
        createdAsset.setName("ÄNDRAD_3");
        Asset changedAsset3 = assetService.updateAsset(createdAsset, "CHG_USER_3", "En changekommentar");
        commit();
        UUID historyId3 = changedAsset3.getHistoryId();

        List<Asset> assetVersions = assetService.getRevisionsForAsset(asset.getId());
        Assert.assertEquals(assetVersions.size(), 4);
        commit();

        Asset fetchedAssetAtRevision = assetService.getAssetRevisionForRevisionId(historyId2);
        Assert.assertEquals(historyId2, fetchedAssetAtRevision.getHistoryId());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
        commit();


    }

    @Test
    public void getRevisionsForAssetLimitedTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();

        createdAsset.setName("NewName");
        assetService.updateAsset(asset, "test", "comment");
        commit();

        List<Asset> revisions = assetService.getRevisionsForAsset(createdAsset.getId());
        assertEquals(2, revisions.size());

        List<Asset> revisions2 = assetService.getRevisionsForAssetLimited(createdAsset.getId(), 10);
        assertEquals(2, revisions2.size());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
        commit();

    }

    @Test
    public void getRevisionsForAssetLimitedMaxNumberTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();

        createdAsset.setName("NewName");
        assetService.updateAsset(asset, "test", "comment");
        commit();

        List<Asset> revisions = assetService.getRevisionsForAsset(createdAsset.getId());
        assertEquals(2, revisions.size());

        List<Asset> revisions2 = assetService.getRevisionsForAssetLimited(createdAsset.getId(), 1);
        assertEquals(1, revisions2.size());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
        commit();

    }

    @Test
    public void archiveAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        assetService.archiveAsset(createdAsset, "test", "archived");

        Asset assetByCfr = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());

        assertNull(assetByCfr);
    }

    @Test
    public void getAssetListTestIdQuery() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetService.createAsset(asset, "test");
        commit();

        List<SearchKeyValue> searchValues = new ArrayList<>();
        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.GUID);
        searchValue.setSearchValues(Arrays.asList(asset.getId().toString()));
        searchValues.add(searchValue);

        List<Asset> assets = assetService.getAssetList(searchValues, 1, 100, true).getAssetList();

        assertEquals(1, assets.size());
        assertEquals(asset.getCfr(), assets.get(0).getCfr());
        assetService.deleteAsset(AssetIdentifier.GUID, asset.getId().toString());
        commit();
    }

    @Test
    public void getAssetListTestNameQuery() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetService.createAsset(asset, "test");
        commit();

        List<SearchKeyValue> searchValues = new ArrayList<>();
        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.NAME);
        searchValue.setSearchValues(Arrays.asList(asset.getName()));
        searchValues.add(searchValue);

        List<Asset> assets = assetService.getAssetList(searchValues, 1, 100, true).getAssetList();

        assertTrue(!assets.isEmpty());
        assertEquals(asset.getCfr(), assets.get(0).getCfr());
        assetService.deleteAsset(AssetIdentifier.GUID, asset.getId().toString());
        commit();

    }

    @Test
    public void createNotesTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        Note note = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");

        List<Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }

    @Test
    public void addNoteTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        Note note = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");

        Note note2 = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note2, "test");

        List<Note> notes = assetService.getNotesForAsset(asset.getId());

        assertEquals(2, notes.size());
    }

    @Test
    public void deleteNoteTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createNoteForAsset(asset.getId(), AssetTestsHelper.createBasicNote(), "test");
        assetService.createNoteForAsset(asset.getId(), AssetTestsHelper.createBasicNote(), "test");

        List<Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(2, notes.size());

        assetService.deleteNote(notes.get(0).getId());

        notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }

    @Test
    public void createContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        ContactInfo contactInfo = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }

    @Test
    public void addContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        ContactInfo contactInfo = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");

        ContactInfo contactInfo2 = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo2, "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());

        assertEquals(2, contacts.size());
    }

    @Test
    public void deleteContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createContactInfoForAsset(asset.getId(), AssetTestsHelper.createBasicContactInfo(), "test");
        assetService.createContactInfoForAsset(asset.getId(), AssetTestsHelper.createBasicContactInfo(), "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(2, contacts.size());

        assetService.deleteContactInfo(contacts.get(0).getId());

        contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }

    private void commit() throws AssetServiceException {
        try {
            userTransaction.commit();
            userTransaction.begin();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SystemException | NotSupportedException e) {
            throw new AssetServiceException(e);
        }
    }


    @Test
    public void testGetMobileTerminalByConnectId() {
        Asset asset = createAsset();
        MobileTerminal mobileTerminal = createMobileterminal();
        mobileTerminal.getCurrentEvent().setActive(false);
        MobileTerminalEvent event = new MobileTerminalEvent();
        event.setActive(true);
        event.setAsset(asset);
        event.setEventCodeType(EventCodeEnum.CREATE);
        event.setMobileterminal(mobileTerminal);
        mobileTerminal.getMobileTerminalEvents().add(event);
        mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");

        MobileTerminalType fetchedTerminal = mobileTerminalService.findMobileTerminalByAsset(asset.getId());
        Assert.assertNotNull(fetchedTerminal);
        Assert.assertNotNull(fetchedTerminal.getMobileTerminalId());
        Assert.assertNotNull(fetchedTerminal.getMobileTerminalId().getGuid());
        UUID fetchedUUID = UUID.fromString(fetchedTerminal.getMobileTerminalId().getGuid());
        Assert.assertEquals(mobileTerminal.getId(), fetchedUUID);
    }

    @Test
    public void testGetAssetByConnectId() {
        Asset asset = createAsset();
        MobileTerminal mobileTerminal = createMobileterminal();
        mobileTerminal.getCurrentEvent().setActive(false);
        MobileTerminalEvent event = new MobileTerminalEvent();
        event.setActive(true);
        event.setAsset(asset);
        event.setEventCodeType(EventCodeEnum.CREATE);
        event.setMobileterminal(mobileTerminal);
        mobileTerminal.getMobileTerminalEvents().add(event);
        mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");
        UUID mobileTerminalId = mobileTerminal.getId();
        Asset fetchedAsset = assetService.getAssetByConnectId(mobileTerminalId);
        Assert.assertNotNull(fetchedAsset);
        Assert.assertEquals(asset.getId(), fetchedAsset.getId());
    }

    @Test
    public void testGetRequiredEnrichment() {

        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        MobileTerminal mobileTerminal = createMobileterminal();
        mobileTerminal.getCurrentEvent().setActive(false);
        MobileTerminalEvent event = new MobileTerminalEvent();
        event.setActive(true);
        event.setAsset(asset);
        event.setEventCodeType(EventCodeEnum.CREATE);
        event.setMobileterminal(mobileTerminal);
        mobileTerminal.getMobileTerminalEvents().add(event);

        IdList DNID = new IdList();
        DNID.setType(IdType.DNID);
        DNID.setValue("DNID1234567890");
        IdList MEMBER_NUMBER = new IdList();
        MEMBER_NUMBER.setType(IdType.MEMBER_NUMBER);
        MEMBER_NUMBER.setValue("MEMBER1234567890");
        IdList SERIAL_NUMBER = new IdList();
        SERIAL_NUMBER.setType(IdType.SERIAL_NUMBER);
        SERIAL_NUMBER.setValue("SN1234567890");

        mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");

        eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.MobileTerminalType mtType = new eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.MobileTerminalType();
        mtType.setConnectId(asset.getId().toString());
        mtType.getMobileTerminalIdList().add(DNID);
        mtType.getMobileTerminalIdList().add(MEMBER_NUMBER);
        mtType.getMobileTerminalIdList().add(SERIAL_NUMBER);
        mtType.setConnectId(asset.getId().toString());

        RawMovementType rawMomenet = new RawMovementType();

        AssetId assetId = createAssetId(asset);
        rawMomenet.setAssetId(assetId);
        rawMomenet.setMobileTerminal(mtType);
        AssetMTEnrichmentResponse response = assetService.setMovementReportReceived(rawMomenet, null, "test");

        Assert.assertNotNull(response.getAsset());
        Assert.assertNotNull(response.getMobileTerminalType());

        Asset fetchedAsset = response.getAsset();
        MobileTerminalType mobileTerminalType = response.getMobileTerminalType();

        Assert.assertEquals(asset.getId(), fetchedAsset.getId());
        Assert.assertEquals(mobileTerminal.getId().toString(), mobileTerminalType.getMobileTerminalId().getGuid());

    }



    private AssetId createAssetId(Asset asset) {

        AssetId i = new AssetId();

        String mmsiValue = asset.getMmsi();
        if(mmsiValue != null && mmsiValue.length() > 0 ){
            AssetIdList line = new AssetIdList();
            line.setIdType(eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdType.MMSI);
            line.setValue(mmsiValue);
            i.getAssetIdList().add(line);
        }
        String cfrValue = asset.getCfr();
        if(cfrValue != null && cfrValue.length() > 0 ){
            AssetIdList line = new AssetIdList();
            line.setIdType(eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdType.CFR);
            line.setValue(cfrValue);
            i.getAssetIdList().add(line);
        }
        String ircsValue = asset.getIrcs();
        if(ircsValue != null && ircsValue.length() > 0 ){
            AssetIdList line = new AssetIdList();
            line.setIdType(eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdType.IRCS);
            line.setValue(ircsValue);
            i.getAssetIdList().add(line);
        }
        return i;
    }


    private Asset createAsset() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "TEST");
        return createdAsset;
    }

    private MobileTerminal createMobileterminal() {

        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal2();
        MobileTerminal created = mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");
        return created;
    }
}
