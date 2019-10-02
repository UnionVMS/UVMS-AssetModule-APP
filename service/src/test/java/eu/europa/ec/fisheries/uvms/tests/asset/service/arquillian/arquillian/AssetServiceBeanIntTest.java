package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.*;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentResponse;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

    @Inject
    private AssetService assetService;

    @Inject
    private TestPollHelper testPollHelper;

    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    private AssetGroupService assetGroupService;

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
            assertNotNull(createdAsset);
            assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
            commit();
        } catch (AssetServiceException e) {
            fail();
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
        assertEquals(createdAsset.getName(), fetchedAsset.getName());
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
        assertNull(fetchedAsset);
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
        assertEquals(assetVersions.size(), 4);
        commit();

        Asset fetchedAssetAtRevision = assetService.getAssetRevisionForRevisionId(historyId2);
        assertEquals(historyId2, fetchedAssetAtRevision.getHistoryId());

        assetService.deleteAsset(AssetIdentifier.GUID, createdAsset.getId().toString());
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
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
    @OperateOnDeployment("normal")
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
    @OperateOnDeployment("normal")
    public void archiveAssetTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        assetService.archiveAsset(createdAsset, "test", "archived");

        Asset assetByCfr = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());

        assertNull(assetByCfr);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unarchiveAssetTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");

        Asset archived = assetService.archiveAsset(createdAsset, "test", "archived");
        assertFalse(archived.getActive());
        Asset assetByCfr = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());
        assertNull(assetByCfr);

        Asset unarchived = assetService.unarchiveAsset(archived.getId(), "test", "archived");
        assertTrue(unarchived.getActive());
        Asset assetByCfr2 = assetService.getAssetById(AssetIdentifier.CFR, createdAsset.getCfr());
        assertNotNull(assetByCfr2);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListTestIdQuery() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetService.createAsset(asset, "test");
        commit();

        List<SearchKeyValue> searchValues = new ArrayList<>();
        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.GUID);
        searchValue.setSearchValues(Collections.singletonList(asset.getId().toString()));
        searchValues.add(searchValue);

        List<Asset> assets = assetService.getAssetList(searchValues, 1, 100, true, false).getAssetList();

        assertEquals(1, assets.size());
        assertEquals(asset.getCfr(), assets.get(0).getCfr());
        assetService.deleteAsset(AssetIdentifier.GUID, asset.getId().toString());
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListTestNameQuery() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetService.createAsset(asset, "test");
        commit();

        List<SearchKeyValue> searchValues = new ArrayList<>();
        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.NAME);
        searchValue.setSearchValues(Collections.singletonList(asset.getName()));
        searchValues.add(searchValue);

        List<Asset> assets = assetService.getAssetList(searchValues, 1, 100, true, false).getAssetList();

        assertTrue(!assets.isEmpty());
        assertEquals(asset.getCfr(), assets.get(0).getCfr());
        assetService.deleteAsset(AssetIdentifier.GUID, asset.getId().toString());
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createNotesTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        Note note = AssetTestsHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");

        List<Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }

    @Test
    @OperateOnDeployment("normal")
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
    @OperateOnDeployment("normal")
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
    @OperateOnDeployment("normal")
    public void createContactInfoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        ContactInfo contactInfo = AssetTestsHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }

    @Test
    @OperateOnDeployment("normal")
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
    @OperateOnDeployment("normal")
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
    @OperateOnDeployment("normal")
    public void testGetMobileTerminalByConnectId() {
        Asset asset = createAsset();
        MobileTerminal mobileTerminal = createMobileTerminal(asset);
        asset.getMobileTerminals().add(mobileTerminal);
        assetService.updateAsset(asset, "TEST", "TEST_COMMENT");
        MobileTerminal fetchedTerminal = mobileTerminalService.findMobileTerminalByAsset(asset.getId());
        assertNotNull(fetchedTerminal);
        assertNotNull(fetchedTerminal.getId());
        UUID fetchedUUID = fetchedTerminal.getId();
        assertEquals(mobileTerminal.getId(), fetchedUUID);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRequiredEnrichment() {
        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        AssetGroup createdAssetGroup = createAssetGroup(asset);
        UUID createdAssetGroupId = createdAssetGroup.getId();
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal2(asset);

        mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");

        AssetMTEnrichmentRequest request = createRequest(asset);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertNotNull(response.getAssetId());
        assertNotNull(response.getMobileTerminalType());
        String assetUUID = response.getAssetUUID();
        assertEquals(asset.getId(), UUID.fromString(assetUUID));

        List<String> fetchedAssetGroups = response.getAssetGroupList();
        assertNotNull(fetchedAssetGroups);
        assertTrue(fetchedAssetGroups.size() > 0);
        assertTrue(fetchedAssetGroups.contains(createdAssetGroupId.toString()));
        assertEquals(request.getSerialNumberValue(), response.getSerialNumber());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRequiredEnrichmentOnlyMT_InmarsatSpecific() {

        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        AssetGroup createdAssetGroup = createAssetGroup(asset);
        UUID createdAssetGroupId = createdAssetGroup.getId();
        MobileTerminal mobileTerminal = createMobileTerminal(asset);
        mobileTerminal.setArchived(false);

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();

        // put membernumber into request
        // put dnid into request
        // shall give Asset and Mobileterminal in response

        Set<Channel> channels = mobileTerminal.getChannels();
        Channel channel = channels.iterator().next();
        String dnid = channel.getDNID();
        String memberNumber = channel.getMemberNumber();

        request.setMemberNumberValue(memberNumber);
        request.setDnidValue(dnid);
        request.setIdValue(asset.getId());
        request.setTranspondertypeValue(mobileTerminal.getMobileTerminalType().toString());

        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertNotNull(response.getAssetId());
        assertNotNull(response.getMobileTerminalType());
        String assetUUID = response.getAssetUUID();
        assertEquals(asset.getId(), UUID.fromString(assetUUID));

        List<String> fetchedAssetGroups = response.getAssetGroupList();
        assertNotNull(fetchedAssetGroups);
        assertTrue(fetchedAssetGroups.size() > 0);
        assertTrue(fetchedAssetGroups.contains(createdAssetGroupId.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRequiredEnrichment_NO_MOBILETERMINAL() {

        // create stuff so we can create a valid rawMovement
        Asset asset = createAsset();
        AssetGroup createdAssetGroup = createAssetGroup(asset);
        UUID createdAssetGroupId = createdAssetGroup.getId();

        em.flush();

        AssetMTEnrichmentRequest request = createRequest(asset);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertNotNull(response.getAssetId());
        String assetUUID = response.getAssetUUID();
        assertEquals(asset.getId(), UUID.fromString(assetUUID));

        List<String> fetchedAssetGroups = response.getAssetGroupList();
        assertNotNull(fetchedAssetGroups);
        assertTrue(fetchedAssetGroups.size() > 0);
        assertTrue(fetchedAssetGroups.contains(createdAssetGroupId.toString()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTAssingCorrectAssetFromMTInformation() {
        String dnid = "1234";
        
        Asset asset1 = createAsset();
        MobileTerminal mobileTerminal1 = testPollHelper.createBasicMobileTerminal();
        mobileTerminal1.getChannels().iterator().next().setDNID(dnid);
        mobileTerminal1.setAsset(asset1);
        mobileTerminal1 = mobileTerminalService.createMobileTerminal(mobileTerminal1, "TEST");
        MobileTerminalPlugin plugin = mobileTerminal1.getPlugin();
        
        Asset asset2 = createAsset();
        MobileTerminal mobileTerminal2 = testPollHelper.createBasicMobileTerminal();
        mobileTerminal2.setPlugin(plugin);
        mobileTerminal2.getChannels().iterator().next().setDNID(dnid);
        mobileTerminal2.setAsset(asset2);
        mobileTerminal2 = mobileTerminalService.createMobileTerminal(mobileTerminal2, "TEST");
        
        Asset asset3 = createAsset();
        MobileTerminal mobileTerminal3 = testPollHelper.createBasicMobileTerminal();
        mobileTerminal3.setPlugin(plugin);
        mobileTerminal3.getChannels().iterator().next().setDNID(dnid);
        mobileTerminal3.setAsset(asset3);
        mobileTerminal3 = mobileTerminalService.createMobileTerminal(mobileTerminal3, "TEST");

        AssetMTEnrichmentRequest request = createRequest(mobileTerminal1);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetUUID(), CoreMatchers.is(asset1.getId().toString()));
        
        AssetMTEnrichmentRequest request2 = createRequest(mobileTerminal2);
        AssetMTEnrichmentResponse response2 = assetService.collectAssetMT(request2);
        assertThat(response2.getAssetUUID(), CoreMatchers.is(asset2.getId().toString()));
        
        AssetMTEnrichmentRequest request3 = createRequest(mobileTerminal3);
        AssetMTEnrichmentResponse response3 = assetService.collectAssetMT(request3);
        assertThat(response3.getAssetUUID(), CoreMatchers.is(asset3.getId().toString()));

    }
    
    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTCreateUnknownAsset() {
        Asset nonExisting = AssetTestsHelper.createBasicAsset();
        nonExisting.setName(null);
        
        AssetMTEnrichmentRequest request = createRequest(nonExisting);
        request.setDnidValue(null);
        request.setMemberNumberValue(null);
        request.setTranspondertypeValue(null);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertTrue(response.getAssetName().startsWith("Unknown"));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTDontCreateUnknownAssetForInmarsat() {
        MobileTerminal mobileTerminalNonExisting = testPollHelper.createBasicMobileTerminal();
        
        AssetMTEnrichmentRequest request = createRequest(mobileTerminalNonExisting);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetId(), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(response.getAssetName(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void collectAssetMTRequestOnUnlinkedMTInmarsat() {
        MobileTerminal mobileTerminalUnlinked = testPollHelper.createBasicMobileTerminal();
        mobileTerminalUnlinked = mobileTerminalService.createMobileTerminal(mobileTerminalUnlinked, "TEST");

        AssetMTEnrichmentRequest request = createRequest(mobileTerminalUnlinked);
        AssetMTEnrichmentResponse response = assetService.collectAssetMT(request);
        assertThat(response.getAssetId(), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(response.getAssetName(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    private AssetMTEnrichmentRequest createRequest(MobileTerminal mobileTerminal) {

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();

        Channel channel = mobileTerminal.getChannels().iterator().next();
        
        // for mobileTerminal
        request.setMemberNumberValue(channel.getMemberNumber());
        request.setDnidValue(channel.getDNID());
        request.setTranspondertypeValue(mobileTerminal.getMobileTerminalType().toString());
        
        return request;
    }
    
    private AssetMTEnrichmentRequest createRequest(Asset asset) {

        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();

        // for mobileTerminal
        request.setMemberNumberValue("MEMBER1234567890");
        request.setSerialNumberValue("SN1234567890");
        request.setDnidValue("DNID1234567890");
        request.setLesValue("LES1234567890");

        // for asset
        if (asset != null) {
            request.setIdValue(asset.getId());
            request.setCfrValue(asset.getCfr());
            request.setImoValue(asset.getImo());
            request.setIrcsValue(asset.getIrcs());
            request.setMmsiValue(asset.getMmsi());
            request.setGfcmValue(asset.getGfcm());
            request.setUviValue(asset.getUvi());
            request.setIccatValue(asset.getIccat());
        }
        request.setPluginType(PluginType.NAF.value());

        request.setTranspondertypeValue(MobileTerminalTypeEnum.INMARSAT_C.name());
        return request;
    }

    private AssetGroup createAssetGroup(Asset asset) {

        AssetGroup ag = new AssetGroup();
        ag.setUpdatedBy("test");
        ag.setUpdateTime(OffsetDateTime.now(Clock.systemUTC()));
        ag.setArchived(false);
        ag.setName("The Name");
        ag.setOwner("test");
        ag.setDynamic(false);
        ag.setGlobal(true);

        AssetGroup createdAssetGroup = assetGroupService.createAssetGroup(ag, "test");
        AssetGroupField assetGroupField = new AssetGroupField();
        assetGroupField.setAssetGroup(createdAssetGroup);
        assetGroupField.setKey("GUID");
        assetGroupField.setValue(asset.getId().toString());
        assetGroupField.setUpdateTime(OffsetDateTime.now(Clock.systemUTC()));
        assetGroupService.createAssetGroupField(createdAssetGroup.getId(), assetGroupField, "TEST");

        return createdAssetGroup;
    }

    private Asset createAsset() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset createdAsset = assetService.createAsset(asset, "TEST");
        return createdAsset;
    }

    private MobileTerminal createMobileTerminal(Asset asset) {
        MobileTerminal mobileTerminal = testPollHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(asset);
        MobileTerminal created = mobileTerminalService.createMobileTerminal(mobileTerminal, "TEST");
        return created;
    }
}
