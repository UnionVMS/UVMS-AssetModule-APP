/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetDaoTest extends TransactionalTests {

    private static Random rnd = new Random();

    @Inject
    private AssetDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void createAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetDao.createAsset(asset);

        assertThat(asset.getId(), is(notNullValue()));

        Asset fetchedAsset = assetDao.getAssetById(asset.getId());

        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void createAssetNullInputShouldThrowExceptionTest() {
        assetDao.createAsset(null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckHistoryGuid() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        assertThat(asset.getHistoryId(), is(notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        Asset fetchedAsset = assetDao.getAssetByCfr(asset.getCfr());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTestNonExistingCrf() {
        String randomCrf = UUID.randomUUID().toString();
        Asset fetchedAsset = assetDao.getAssetByCfr(randomCrf);
        assertThat(fetchedAsset, is(nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIrcsTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        Asset fetchedAsset = assetDao.getAssetByIrcs(asset.getIrcs());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIrcs(), is(asset.getIrcs()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByImoTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        Asset fetchedAsset = assetDao.getAssetByImo(asset.getImo());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getImo(), is(asset.getImo()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        Asset fetchedAsset = assetDao.getAssetByMmsi(asset.getMmsi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIccatTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        Asset fetchedAsset = assetDao.getAssetByIccat(asset.getIccat());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIccat(), is(asset.getIccat()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByUviTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        Asset fetchedAsset = assetDao.getAssetByUvi(asset.getUvi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getUvi(), is(asset.getUvi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByGfcmTest() {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        Asset fetchedAsset = assetDao.getAssetByGfcm(asset.getGfcm());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getGfcm(), is(asset.getGfcm()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetDao.createAsset(asset);
        commit();

        String newName = "UpdatedName";
        asset.setName(newName);
        asset = assetDao.updateAsset(asset);
        commit();
        assertThat(asset.getName(), is(newName));

        Asset updatedAsset = assetDao.getAssetById(asset.getId());
        assertThat(updatedAsset.getId(), is(asset.getId()));
        assertThat(updatedAsset.getName(), is(newName));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListAllTest() throws Exception {
        List<Asset> assetListBefore = assetDao.getAssetListAll();
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        List<Asset> assetListAfter = assetDao.getAssetListAll();
        assertThat(assetListAfter.size(), is(assetListBefore.size() + 1));
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetSingleRevisionTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        List<Asset> assetRevisions = assetDao.getRevisionsForAsset(asset.getId());
        assertEquals(1, assetRevisions.size());
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetTwoVersionsCheckSizeTest() throws Exception {
        // TODO: Audited with UUID problem should be fixed first.
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        Asset fetchedAsset = assetDao.getAssetById(asset.getId());
        String newName1 = "NewName1";
        fetchedAsset.setName(newName1);
        assetDao.updateAsset(fetchedAsset);
        commit();

        List<Asset> assetRevisions = assetDao.getRevisionsForAsset(fetchedAsset.getId());

        assertEquals(2, assetRevisions.size());
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetCompareRevisionsTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        Asset assetVersion1 = assetDao.createAsset(asset);
        commit();

        String newName1 = "NewName1";
        assetVersion1.setName(newName1);
        Asset assetVersion2 = assetDao.updateAsset(assetVersion1);
        commit();

        String newName2 = "NewName2";
        assetVersion2.setName(newName2);
        Asset assetVersion3 = assetDao.updateAsset(assetVersion2);
        commit();

        assertThat(assetVersion3.getId(), is(notNullValue()));
        List<Asset> assetRevisions = assetDao.getRevisionsForAsset(assetVersion3.getId());

        assertEquals(3, assetRevisions.size());
        assetDao.deleteAsset(asset);

        commit();


//        Asset rev1 = assetRevisions.get(0);
//        assertEquals(rev1.getName(), asset.getName());
//        
//        Asset rev2 = assetRevisions.get(1);
//        assertEquals(rev2.getName(), newName1);
//        
//        Asset rev3 = assetRevisions.get(2);
//        assertEquals(rev3.getName(), newName2);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateSingleAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        Asset assetAtDate = assetDao.getAssetAtDate(asset, OffsetDateTime.now(ZoneOffset.UTC));

        assertThat(assetAtDate.getId(), is(notNullValue()));

        assertThat(assetAtDate.getName(), is(asset.getName()));
        assertThat(assetAtDate.getCfr(), is(asset.getCfr()));
        assertThat(assetAtDate.getActive(), is(asset.getActive()));
        assetDao.deleteAsset(asset);
        commit();
    }

    // TODO redo this test when there is time it fails on gfcm unique contraint
    @Test
    @Ignore
    @OperateOnDeployment("normal")
    public void getAssetAtDateMultipleAssetsTest() throws Exception {
        Asset asset1 = AssetTestsHelper.createBasicAsset();
        asset1 = assetDao.createAsset(asset1);
        String firstName = asset1.getName();
        commit();
        OffsetDateTime firstDate = OffsetDateTime.now(ZoneOffset.UTC);

        String newName = "NewName";
        asset1.setName(newName);
        Asset asset2 = assetDao.updateAsset(asset1);
        commit();
        OffsetDateTime secondDate = OffsetDateTime.now(ZoneOffset.UTC);

        Asset assetAtFirstDate = assetDao.getAssetAtDate(asset2, firstDate);
        assertThat(assetAtFirstDate.getName(), is(firstName));

        Asset assetAtSecondDate = assetDao.getAssetAtDate(asset2, secondDate);
        assertThat(assetAtSecondDate.getName(), is(newName));

        assetDao.deleteAsset(asset1);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void verifyAssetHistoryUpdatesCorrectlyTest() throws Exception {

        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset = assetDao.createAsset(asset);
        commit();
        assertThat(asset.getHistoryId(), is(notNullValue()));

        String newName = "UpdatedName";
        asset.setName(newName);

        String newOrgCode = "ORGCODE" + rnd.nextInt();
        String newOrgName = "ORGNAME" + rnd.nextInt();

        asset.setProdOrgCode(newOrgCode);
        asset.setProdOrgName(newOrgName);
        asset.setGrossTonnageUnit(UnitTonnage.OSLO);
        asset.setLicenceType("PELAGIC");
        asset.setSegment("3");
        asset.setConstructionYear("1924");
        asset.setConstructionPlace("BEJ");

        UUID prevHistoryID = asset.getHistoryId();  //this is here since we no longer update HistId in closing the DB connection but rather update manually in the middle. Reason for this is to stop it from first creating and then updating on create.
        Asset updatedAsset = assetDao.updateAsset(asset);
        commit();

        assertThat(updatedAsset.getHistoryId(), is(notNullValue()));
        assertThat(prevHistoryID, is(not(updatedAsset.getHistoryId())));
        assertThat(newOrgCode, is(updatedAsset.getProdOrgCode()));
        assertThat(newOrgName, is(updatedAsset.getProdOrgName()));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    public void getAssetCountTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(1), count);

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    public void getAssetCountTwoRevisionsTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        asset.setName("NewName");
        assetDao.updateAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(1), count);

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetCountTwoRevisionsAndTwoAssetsTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        asset.setName("NewName");
        assetDao.updateAsset(asset);
        commit();

        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();

        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(1), count);

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }
    
    @Test
    public void getAssetCountShouldNotFindAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList("TESTCFR"));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(0), count);

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertThat(assets.size(), is(1));
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestTwoAssets() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr(), asset2.getCfr()));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, false);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(asset2.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestTwoAssetsNotDynamic() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.IRCS);
        searchKey2.setSearchValues(Arrays.<String>asList(asset2.getIrcs()));
        searchKeyValues.add(searchKey2);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, false);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(asset2.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestTwoAssestPageSizeOne() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.IRCS);
        searchKey2.setSearchValues(Collections.singletonList(asset2.getIrcs()));
        searchKeyValues.add(searchKey2);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 1, searchKeyValues, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        
        assets = assetDao.getAssetListSearchPaginated(2, 1, searchKeyValues, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset2.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    public void getAssetListSearchPaginatedTestFlagStateAndExtMarking() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.FLAG_STATE);
        searchKey.setSearchValues(Collections.singletonList(asset.getFlagStateCode()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.EXTERNAL_MARKING);
        searchKey2.setSearchValues(Collections.singletonList(asset.getExternalMarking()));
        searchKeyValues.add(searchKey2);
        SearchKeyValue searchKey3 = new SearchKeyValue();
        searchKey3.setSearchField(SearchFields.CFR);
        searchKey3.setSearchValues(Collections.singletonList(asset.getCfr()));
        searchKeyValues.add(searchKey3);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestGuid() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.GUID);
        searchKey.setSearchValues(Collections.singletonList(asset.getId().toString()));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestHistoryGuid() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        
        Asset fetchedAsset = assetDao.getAssetById(asset.getId());
        String newName = "newName";
        fetchedAsset.setName(newName);
        Asset updatedAsset = assetDao.updateAsset(fetchedAsset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.HIST_GUID);
        searchKey.setSearchValues(Collections.singletonList(asset.getHistoryId().toString()));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getHistoryId(), is(asset.getHistoryId()));
        assertThat(assets.get(0).getName(), is(asset.getName()));
        
        searchKey.setSearchValues(Collections.singletonList(updatedAsset.getHistoryId().toString()));
        assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assets.get(0).getName(), is(updatedAsset.getName()));

        assetDao.deleteAsset(fetchedAsset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestMinLength() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.MIN_LENGTH);
        searchKey.setSearchValues(Collections.singletonList((asset.getLengthOverAll().toString())));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestNumber() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.GEAR_TYPE);
        searchKey.setSearchValues(Arrays.asList(asset.getGearFishingType()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.CFR);
        searchKey2.setSearchValues(Collections.singletonList((asset.getCfr())));
        searchKeyValues.add(searchKey2);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    public void getAssetListSearchPaginatedTestWildcardSearch() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        String randomNumbers = AssetTestsHelper.getRandomIntegers(10);
        String searchName = "TestLikeSearchName" + randomNumbers;
        asset.setName(searchName);
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.NAME);
        searchKey.setSearchValues(Arrays.asList("*LikeSearch*" + randomNumbers));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getName(), is(searchName));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestWildcardSearchCaseInsensitive() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        String randomNumbers = AssetTestsHelper.getRandomIntegers(10);
        String searchName = "TestLikeSearchName" + randomNumbers;
        asset.setName(searchName);
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.NAME);
        searchKey.setSearchValues(Arrays.asList("*likeSearch*" + randomNumbers));
        searchKeyValues.add(searchKey);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getName(), is(searchName));

        assetDao.deleteAsset(asset);
        commit();
    }

    private void commit() throws Exception {
        userTransaction.commit();
        userTransaction.begin();
    }
}
