package eu.europa.fisheries.uvms.asset.service.arquillian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.constant.AssetIdentity;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.entity.Note;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

    Random rnd = new Random();

    @Inject
    AssetService assetService;

    @Test
    @OperateOnDeployment("normal")
    public void createAssert() {

        // this test is to ensure that create actually works
        Asset createdAsset = null;
        try {
            // create an Asset
            Asset asset = AssetHelper.createBiggerAsset();
            createdAsset = assetService.createAsset(asset, "test");
            commit();
            Assert.assertTrue(createdAsset != null);
        } catch (AssetException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAsset() throws AssetException {

        // create an asset
        Asset asset = AssetHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();
        // change it and store it
        createdAsset.setName("ÄNDRAD");
        Asset changedAsset = assetService.updateAsset(createdAsset, "CHG_USER", "En changekommentar");
        commit();

        // fetch it and check name
        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        Assert.assertEquals(createdAsset.getName(), fetchedAsset.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset() throws AssetException {

        // create an asset
        Asset asset = AssetHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        commit();

        // change it to get an audit
        createdAsset.setName("ÄNDRAD_1");
        Asset changedAsset1 = assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");
        commit();

        assetService.deleteAsset(AssetIdentity.GUID, createdAsset.getId().toString());
        commit();

        // fetch it and it should be null
        Asset fetchedAsset = assetService.getAssetById(createdAsset.getId());
        Assert.assertEquals(fetchedAsset, null);
    }



    @Test
    @OperateOnDeployment("normal")
    public void updateAssetThreeTimesAndCheckRevisionsAndValues() throws AssetException {

        // create an asset
        Asset asset = AssetHelper.createBiggerAsset();
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

    }

    @Test
    public void getAssetListTestIdQuery() throws Exception {
        Asset asset = AssetHelper.createBiggerAsset();
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
    }
    
    @Test
    public void getAssetListTestNameQuery() throws Exception {
        Asset asset = AssetHelper.createBiggerAsset();
        asset = assetService.createAsset(asset, "test");
        commit();
        
        List<SearchKeyValue> searchValues = new ArrayList<>();
        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.NAME);
        searchValue.setSearchValues(Arrays.asList(asset.getName()));
        searchValues.add(searchValue);
        
        List<Asset> assets = assetService.getAssetList(searchValues, 1, 100, true).getAssetList();
        
        assertTrue(!assets.isEmpty());
    }

    @Test
    public void createNotesTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");
        
        Note note = AssetHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");
        
        List<Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }
    
    @Test
    public void addNoteTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");
        
        Note note = AssetHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note, "test");

        Note note2 = AssetHelper.createBasicNote();
        assetService.createNoteForAsset(asset.getId(), note2, "test");

        List<Note> notes = assetService.getNotesForAsset(asset.getId());
        
        assertEquals(2, notes.size());
    }
    
    @Test
    public void deleteNoteTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createNoteForAsset(asset.getId(), AssetHelper.createBasicNote(), "test");
        assetService.createNoteForAsset(asset.getId(), AssetHelper.createBasicNote(), "test");

        List<Note> notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(2, notes.size());
        
        assetService.deleteNote(notes.get(0).getId());

        notes = assetService.getNotesForAsset(asset.getId());
        assertEquals(1, notes.size());
    }
    
    @Test
    public void createContactInfoTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");
        
        ContactInfo contactInfo = AssetHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");
        
        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }
    
    @Test
    public void addContactInfoTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");
        
        ContactInfo contactInfo = AssetHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo, "test");

        ContactInfo contactInfo2 = AssetHelper.createBasicContactInfo();
        assetService.createContactInfoForAsset(asset.getId(), contactInfo2, "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        
        assertEquals(2, contacts.size());
    }
    
    @Test
    public void deleteContactInfoTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        asset = assetService.createAsset(asset, "test");

        assetService.createContactInfoForAsset(asset.getId(), AssetHelper.createBasicContactInfo(), "test");
        assetService.createContactInfoForAsset(asset.getId(), AssetHelper.createBasicContactInfo(), "test");

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(2, contacts.size());
        
        assetService.deleteContactInfo(contacts.get(0).getId());

        contacts = assetService.getContactInfoForAsset(asset.getId());
        assertEquals(1, contacts.size());
    }

    private void commit() throws AssetException {

        try {
            userTransaction.commit();
            userTransaction.begin();
        } catch (RollbackException |HeuristicMixedException | HeuristicRollbackException |SystemException |  NotSupportedException e) {
            throw new AssetException(e);
        }
    }



}
