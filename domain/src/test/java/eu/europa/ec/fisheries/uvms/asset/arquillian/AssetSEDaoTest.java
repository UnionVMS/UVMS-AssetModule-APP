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
package eu.europa.ec.fisheries.uvms.asset.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetSEDao;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.SegmentFUP;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@RunWith(Arquillian.class)
public class AssetSEDaoTest extends TransactionalTests {

    // Envers requires a commit for auditing
    // Set this to true to clean up test data 
    private boolean cleanUpDB = true;
    private static Random rnd = new Random();


    @Inject
    AssetSEDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void createAssetTest() throws AssetDaoException {
        //AssetSE asset = AssetTestsHelper.createBasicAsset();
        AssetSE asset = AssetTestsHelper.createBiggerAsset();
        asset = assetDao.createAsset(asset);

        assertThat(asset.getId(), is(notNullValue()));

        AssetSE fetchedAsset = assetDao.getAssetById(asset.getId());

        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void createAssetNullInputShouldThrowExceptionTest() throws AssetDaoException {
        assetDao.createAsset(null);
    }

    // TODO should test history GUID
    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckHistoryGuid() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);


        assertThat(asset.getHistoryId(), is(notNullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByCfr(asset.getCfr());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTestNonExistingCrf() throws AssetDaoException {
        String randomCrf = UUID.randomUUID().toString();
        AssetSE fetchedAsset = assetDao.getAssetByCfr(randomCrf);
        assertThat(fetchedAsset, is(nullValue()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIrcsTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByIrcs(asset.getIrcs());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIrcs(), is(asset.getIrcs()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByImoTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByImo(asset.getImo());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getImo(), is(asset.getImo()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByMmsi(asset.getMmsi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIccatTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByIccat(asset.getIccat());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIccat(), is(asset.getIccat()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByUviTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByUvi(asset.getUvi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getUvi(), is(asset.getUvi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByGfcmTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);

        AssetSE fetchedAsset = assetDao.getAssetByGfcm(asset.getGfcm());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getGfcm(), is(asset.getGfcm()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBiggerAsset();
        asset = assetDao.createAsset(asset);
        commit();

        String newName = "UpdatedName";
        asset.setName(newName);
        asset = assetDao.updateAsset(asset);
        commit();
        assertThat(asset.getName(), is(newName));

        AssetSE updatedAsset = assetDao.getAssetById(asset.getId());
        assertThat(updatedAsset.getId(), is(asset.getId()));
        assertThat(updatedAsset.getName(), is(newName));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListAllTest() throws AssetDaoException {
        List<AssetSE> assetListBefore = assetDao.getAssetListAll();
        assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        List<AssetSE> assetListAfter = assetDao.getAssetListAll();

        assertThat(assetListAfter.size(), is(assetListBefore.size() + 1));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetSingleRevisionTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        List<AssetSE> assetRevisions = assetDao.getRevisionsForAsset(asset);

        assertEquals(1, assetRevisions.size());

        if (cleanUpDB) {
            cleanUpDB(asset);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetTwoVersionsCheckSizeTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        AssetSE fetchedAsset = assetDao.getAssetById(asset.getId());
        String newName1 = "NewName1";
        fetchedAsset.setName(newName1);
        assetDao.updateAsset(fetchedAsset);
        commit();

        List<AssetSE> assetRevisions = assetDao.getRevisionsForAsset(fetchedAsset);

        assertEquals(2, assetRevisions.size());

        if (cleanUpDB) {
            cleanUpDB(fetchedAsset);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRevisionsForAssetCompareRevisionsTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        AssetSE assetVersion1 = assetDao.createAsset(asset);
        commit();

        String newName1 = "NewName1";
        assetVersion1.setName(newName1);
        AssetSE assetVersion2 = assetDao.updateAsset(assetVersion1);
        commit();

        String newName2 = "NewName2";
        assetVersion2.setName(newName2);
        AssetSE assetVersion3 = assetDao.updateAsset(assetVersion2);
        commit();

        assertThat(assetVersion3.getId(), is(notNullValue()));
        List<AssetSE> assetRevisions = assetDao.getRevisionsForAsset(assetVersion3);

        assertEquals(3, assetRevisions.size());

        // TODO 
//        AssetSE rev1 = assetRevisions.get(0);
//        assertThat(rev1.getName(), is(asset.getName()));
//        assertThat(rev1.getCfr(), is(asset.getCfr()));
//        assertThat(rev1.getActive(), is(asset.getActive()));
//        
//        AssetSE rev2 = assetRevisions.get(1);
//        assertThat(rev2.getName(), is(assetVersion1.getName()));
//        assertThat(rev2.getCfr(), is(assetVersion1.getCfr()));
//        assertThat(rev2.getActive(), is(assetVersion1.getActive()));
//        
//        AssetSE rev3 = assetRevisions.get(2);
//        assertThat(rev3.getName(), is(assetVersion2.getName()));
//        assertThat(rev3.getCfr(), is(assetVersion2.getCfr()));
//        assertThat(rev3.getActive(), is(assetVersion2.getActive()));

        if (cleanUpDB) {
            cleanUpDB(assetVersion3);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateSingleAssetTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        AssetSE assetAtDate = assetDao.getAssetAtDate(asset, LocalDateTime.now(ZoneOffset.UTC));

        assertThat(assetAtDate.getId(), is(notNullValue()));

        assertThat(assetAtDate.getName(), is(asset.getName()));
        assertThat(assetAtDate.getCfr(), is(asset.getCfr()));
        assertThat(assetAtDate.getActive(), is(asset.getActive()));

        if (cleanUpDB) {
            cleanUpDB(asset);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateMultipleAssetsTest() throws Exception {
        AssetSE asset1 = AssetTestsHelper.createBasicAsset();
        asset1 = assetDao.createAsset(asset1);
        String firstName = asset1.getName();
        commit();
        LocalDateTime firstDate = LocalDateTime.now(ZoneOffset.UTC);

        String newName = "NewName";
        asset1.setName(newName);
        AssetSE asset2 = assetDao.updateAsset(asset1);
        commit();
        LocalDateTime secondDate = LocalDateTime.now(ZoneOffset.UTC);

        AssetSE assetAtFirstDate = assetDao.getAssetAtDate(asset2, firstDate);
        assertThat(assetAtFirstDate.getName(), is(firstName));

        AssetSE assetAtSecondDate = assetDao.getAssetAtDate(asset2, secondDate);
        assertThat(assetAtSecondDate.getName(), is(newName));

        if (cleanUpDB) {
            cleanUpDB(asset1);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void verifyAssetHistoryUpdatesCorrectlyTest() throws Exception {

        AssetSE asset = AssetTestsHelper.createBiggerAsset();
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
        asset.setLicenceType(GearFishingTypeEnum.PELAGIC.toString());
        asset.setSegment(SegmentFUP.CA2);
        asset.setConstructionYear("1924");
        asset.setConstructionPlace("BEJ");




        AssetSE updatedAsset = assetDao.updateAsset(asset);
        commit();

        assertThat(updatedAsset.getHistoryId(), is(notNullValue()));
        assertThat(asset.getHistoryId(), is(not(updatedAsset.getHistoryId())));
        assertThat(newOrgCode, is(updatedAsset.getProdOrgCode()));
        assertThat(newOrgName, is(updatedAsset.getProdOrgName()));

    }

    @Test
    public void getAssetCountTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(1), count);
    }

    @Test
    public void getAssetCountTwoRevisionsTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        asset.setName("NewName");
        assetDao.updateAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(1), count);
    }
    
    @Test
    public void getAssetCountTwoRevisionsAndTwoAssetsTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        asset.setName("NewName");
        assetDao.updateAsset(asset);
        commit();
        
        assetDao.createAsset(AssetTestsHelper.createBasicAsset());
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(1), count);
    }
    
    @Test
    public void getAssetCountShouldNotFindAssetTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList("TESTCFR"));
        searchKeyValues.add(searchKey);
        Long count = assetDao.getAssetCount(searchKeyValues, false);
        assertEquals(new Long(0), count);
    }
    
    @Test
    public void getAssetListSearchPaginatedTest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(0, 10, searchKeyValues, true);
        
        assertThat(assets.size(), is(1));
        assertThat(assets.get(0).getId(), is(asset.getId()));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestTwoAssest() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        AssetSE asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr(), asset2.getCfr()));
        searchKeyValues.add(searchKey);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(asset2.getId()));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestTwoAssestNotDynamic() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        AssetSE asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.IRCS);
        searchKey2.setSearchValues(Arrays.asList(asset2.getIrcs()));
        searchKeyValues.add(searchKey2);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, false);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(asset2.getId()));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestTwoAssestPageSizeOne() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        AssetSE asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.CFR);
        searchKey.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.IRCS);
        searchKey2.setSearchValues(Arrays.asList(asset2.getIrcs()));
        searchKeyValues.add(searchKey2);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 1, searchKeyValues, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        
        assets = assetDao.getAssetListSearchPaginated(2, 1, searchKeyValues, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset2.getId()));
    }

    @Test
    public void getAssetListSearchPaginatedTestFlagStateAndExtMarking() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.FLAG_STATE);
        searchKey.setSearchValues(Arrays.asList(asset.getFlagStateCode()));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.EXTERNAL_MARKING);
        searchKey2.setSearchValues(Arrays.asList(asset.getExternalMarking()));
        searchKeyValues.add(searchKey2);
        SearchKeyValue searchKey3 = new SearchKeyValue();
        searchKey3.setSearchField(SearchFields.CFR);
        searchKey3.setSearchValues(Arrays.asList(asset.getCfr()));
        searchKeyValues.add(searchKey3);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestGuid() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.GUID);
        searchKey.setSearchValues(Arrays.asList(asset.getId().toString()));
        searchKeyValues.add(searchKey);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestHistoryGuid() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        
        AssetSE fetchedAsset = assetDao.getAssetById(asset.getId());
        String newName = "newName";
        fetchedAsset.setName(newName);
        AssetSE updatedAsset = assetDao.updateAsset(fetchedAsset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.HIST_GUID);
        searchKey.setSearchValues(Arrays.asList(asset.getHistoryId().toString()));
        searchKeyValues.add(searchKey);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getHistoryId(), is(asset.getHistoryId()));
        assertThat(assets.get(0).getName(), is(asset.getName()));
        
        searchKey.setSearchValues(Arrays.asList(updatedAsset.getHistoryId().toString()));
        assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assets.get(0).getName(), is(updatedAsset.getName()));
    }
    
    @Test
    public void getAssetListSearchPaginatedTestMinLength() throws Exception {
        AssetSE asset = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.MIN_LENGTH);
        searchKey.setSearchValues(Arrays.asList((asset.getLengthOverAll().toString())));
        searchKeyValues.add(searchKey);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertTrue(!assets.isEmpty());
    }
    
    @Test
    public void getAssetListSearchPaginatedTestNumber() throws Exception {
        AssetSE asset = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.GEAR_TYPE);
        searchKey.setSearchValues(Arrays.asList((String.valueOf(asset.getGearFishingType()))));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.CFR);
        searchKey2.setSearchValues(Arrays.asList((asset.getCfr())));
        searchKeyValues.add(searchKey2);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
    }
    
    @Test
    public void getAssetListSearchPaginatedTestWildcardSearch() throws Exception {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        String searchName = "TestLikeSearchName";
        asset.setName(searchName);
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.NAME);
        searchKey.setSearchValues(Arrays.asList("*LikeSearch*", "*Name*"));
        searchKeyValues.add(searchKey);
        SearchKeyValue searchKey2 = new SearchKeyValue();
        searchKey2.setSearchField(SearchFields.NAME);
        searchKey2.setSearchValues(Arrays.asList("*Name*"));
        searchKeyValues.add(searchKey2);
        List<AssetSE> assets = assetDao.getAssetListSearchPaginated(1, 10, searchKeyValues, true);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getName(), is(searchName));
    }


    private void commit() throws Exception {
        userTransaction.commit();
        userTransaction.begin();
    }

    private void cleanUpDB(AssetSE asset) throws Exception {
        String sql = "delete from assetse where id = '" + asset.getId() + "'";
        em.createNativeQuery(sql).executeUpdate();
        sql = "delete from assetse_aud where id = '" + asset.getId() + "'";
        em.createNativeQuery(sql).executeUpdate();
        sql = "delete from revinfo where rev not in (select rev from assetse_aud)";
        em.createNativeQuery(sql).executeUpdate();
        commit();
    }
}
