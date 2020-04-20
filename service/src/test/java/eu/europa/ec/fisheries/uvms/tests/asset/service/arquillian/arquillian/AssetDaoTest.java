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
package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchLeaf;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.model.constants.UnitTonnage;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    public void createMTSettingActiveToNullShouldNotWork(){
        MobileTerminal mt = new MobileTerminal();
        assertTrue(mt.getActive());
        mt.setActive(null);
        assertNotNull(mt.getActive());
        assertTrue(mt.getActive());
        mt.setActive(false);
        assertFalse(mt.getActive());
        mt.setActive(null);
        assertNotNull(mt.getActive());
        assertFalse(mt.getActive());
    }

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

    @Test
    @OperateOnDeployment("normal")
    public void createSeveralAssetsWithEmptyIrcsTest() {
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        asset1.setIrcs("");
        asset1 = assetDao.createAsset(asset1);
        assertThat(asset1.getId(), is(notNullValue()));

        Asset asset2 = AssetTestsHelper.createBiggerAsset();
        asset2.setIrcs("");
        asset2 = assetDao.createAsset(asset2);
        assertThat(asset2.getId(), is(notNullValue()));
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
    public void getAssetByMmsiOrIrcsWithSeveralAssetsTest() {
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        asset1.setIrcs("");
        asset1 = assetDao.createAsset(asset1);
        assertThat(asset1.getId(), is(notNullValue()));

        Asset asset2 = AssetTestsHelper.createBiggerAsset();
        asset2.setIrcs("");
        asset2 = assetDao.createAsset(asset2);
        assertThat(asset2.getId(), is(notNullValue()));

        List<Asset> fetchedAsset = assetDao.getAssetByMmsiOrIrcs(asset1.getMmsi(), null);
        assertEquals(1, fetchedAsset.size());
        assertTrue(fetchedAsset.get(0).getId().equals(asset1.getId()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiOrIrcsWithIrcsMissingDashTest() {
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        asset1.setIrcs("SFB-" + AssetTestsHelper.getRandomIntegers(4));
        asset1 = assetDao.createAsset(asset1);
        assertThat(asset1.getId(), is(notNullValue()));

        List<Asset> fetchedAsset = assetDao.getAssetByMmsiOrIrcs(null, asset1.getIrcs().replace("-", ""));
        assertEquals(1, fetchedAsset.size());
        assertTrue(fetchedAsset.get(0).getId().equals(asset1.getId()));
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
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateSingleAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        Asset assetAtDate = assetDao.getAssetAtDate(asset, Instant.now());

        assertThat(assetAtDate.getId(), is(notNullValue()));

        assertThat(assetAtDate.getName(), is(asset.getName()));
        assertThat(assetAtDate.getCfr(), is(asset.getCfr()));
        assertThat(assetAtDate.getActive(), is(asset.getActive()));
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtFirstRevisionTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        String oldName = asset.getName();
        asset.setName(oldName + " updated");
        assetDao.updateAsset(asset);
        commit();

        Asset assetAtDate = assetDao.getAssetAtDate(asset, Instant.now().minus(1, ChronoUnit.DAYS));

        assertThat(assetAtDate.getId(), is(notNullValue()));

        assertThat(assetAtDate.getName(), is(oldName));
        assertThat(assetAtDate.getCfr(), is(asset.getCfr()));
        assertThat(assetAtDate.getActive(), is(asset.getActive()));
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateMultipleAssetsTest() throws Exception {
        Asset asset1 = AssetTestsHelper.createBasicAsset();
        asset1.setCfr(null);
        asset1.setIrcs(null);
        asset1.setMmsi(null);
        asset1.setGfcm(null);
        asset1.setImo(null);
        asset1.setUvi(null);
        asset1.setIccat(null);
        asset1 = assetDao.createAsset(asset1);
        String firstName = asset1.getName();
        commit();
        Instant firstDate = Instant.now();

        String newName = "NewName";
        asset1.setName(newName);
        Asset asset2 = assetDao.updateAsset(asset1);
        commit();
        Instant secondDate = Instant.now();

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

        // This is here since we no longer update HistId in closing the DB connection but rather update manually
        // in the middle. Reason for this is to stop it from first creating and then updating on create.
        UUID prevHistoryID = asset.getHistoryId();
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
    @OperateOnDeployment("normal")
    public void getAssetCountTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        Long count = assetDao.getAssetCount(trunk, false);
        assertEquals(Long.valueOf(1), count);

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetCountTestCB() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        Long count = assetDao.getAssetCountCB(trunk, false);
        assertEquals(Long.valueOf(1), count);

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetCountTwoRevisionsTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        asset.setName("NewName");
        assetDao.updateAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        Long count = assetDao.getAssetCount(trunk, false);
        assertEquals(Long.valueOf(1), count);

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
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

        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        Long count = assetDao.getAssetCount(trunk, false);
        assertEquals(Long.valueOf(1), count);

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetCountShouldNotFindAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, "TESTCFR"));

        Long count = assetDao.getAssetCount(trunk, false);

        assertEquals(Long.valueOf(0), count);

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertThat(assets.size(), is(1));
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestTwoAssets() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();

        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset2.getCfr()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(asset2.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestTwoAssetsLogicalOr() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();

        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, asset2.getIrcs()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, true);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(asset2.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestTwoAssestPageSizeOne() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        commit();

        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, asset2.getIrcs()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 1, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        
        assets = assetDao.getAssetListSearchPaginated(2, 1, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset2.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestFlagStateAndExtMarking() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.FLAG_STATE, asset.getFlagStateCode()));
        trunk.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, asset.getExternalMarking()));
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestGuid() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.GUID, asset.getId().toString()));


        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestHistoryGuid() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();
        
        Asset fetchedAsset = assetDao.getAssetById(asset.getId());
        String newName = "newName";
        fetchedAsset.setName(newName);
        Asset updatedAsset = assetDao.updateAsset(fetchedAsset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.HIST_GUID, asset.getHistoryId().toString()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);

        assertEquals(1, assets.size());
        assertThat(assets.get(0).getHistoryId(), is(asset.getHistoryId()));
        assertThat(assets.get(0).getName(), is(asset.getName()));
        
        trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.HIST_GUID, updatedAsset.getHistoryId().toString()));

        assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assets.get(0).getName(), is(updatedAsset.getName()));

        assetDao.deleteAsset(fetchedAsset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestMinLength() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        asset.setLengthOverAll(1d);
        assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf sLeaf = new SearchLeaf(SearchFields.LENGTH_OVER_ALL, asset.getLengthOverAll().toString());
        sLeaf.setOperator("<=");
        trunk.getFields().add(sLeaf);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestNumber() throws Exception {
        Asset asset = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.GEAR_TYPE, asset.getGearFishingType()));
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestWildcardSearch() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        String randomNumbers = AssetTestsHelper.getRandomIntegers(10);
        String searchName = "TestLikeSearchName" + randomNumbers;
        asset.setName(searchName);
        assetDao.createAsset(asset);
        commit();

        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.NAME, "*LikeSearch*"));
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getName(), is(searchName));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
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
        searchKey.setSearchValues(Collections.singletonList("*likeSearch*" + randomNumbers));
        searchKeyValues.add(searchKey);

        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.NAME, "*likeSearch*" + randomNumbers));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getName(), is(searchName));

        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestWildcardSearchCaseInsensitiveCB() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        String randomNumbers = AssetTestsHelper.getRandomIntegers(10);
        String searchName = "TestLikeSearchName" + randomNumbers;
        asset.setName(searchName);
        assetDao.createAsset(asset);
        commit();
        
        List<SearchKeyValue> searchKeyValues = new ArrayList<>();
        SearchKeyValue searchKey = new SearchKeyValue();
        searchKey.setSearchField(SearchFields.NAME);
        searchKey.setSearchValues(Collections.singletonList("*likeSearch*" + randomNumbers));
        searchKeyValues.add(searchKey);

        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.NAME, "*likeSearch*" + randomNumbers));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getName(), is(searchName));

        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchDeeperQueryTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        Asset asset2 = AssetTestsHelper.createBasicAsset();
        asset2.setFlagStateCode("DNK");
        assetDao.createAsset(asset2);
        commit();

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.IRCS, asset.getIrcs());
        trunk.getFields().add(leaf);

        SearchBranch branch = new SearchBranch(false);
        SearchLeaf subLeaf = new SearchLeaf(SearchFields.FLAG_STATE, "SWE");
        branch.getFields().add(subLeaf);
        subLeaf = new SearchLeaf(SearchFields.FLAG_STATE, "DNK");
        branch.getFields().add(subLeaf);

        trunk.getFields().add(branch);

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);

        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchQueryTestCB() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        Asset asset2 = AssetTestsHelper.createBasicAsset();
        asset2.setFlagStateCode("DNK");
        assetDao.createAsset(asset2);
        commit();

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.IRCS, asset.getIrcs());
        trunk.getFields().add(leaf);

        SearchBranch branch = new SearchBranch(false);
        SearchLeaf subLeaf = new SearchLeaf(SearchFields.FLAG_STATE, "SWE");
        branch.getFields().add(subLeaf);
        subLeaf = new SearchLeaf(SearchFields.FLAG_STATE, "DNK");
        branch.getFields().add(subLeaf);

        trunk.getFields().add(branch);

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchEmptyDepthQuery() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset);
        commit();

        Asset asset2 = AssetTestsHelper.createBasicAsset();
        asset2.setFlagStateCode("DNK");
        assetDao.createAsset(asset2);
        commit();

        SearchBranch trunk = new SearchBranch(true);
        SearchLeaf leaf = new SearchLeaf(SearchFields.CFR, asset.getCfr());
        trunk.getFields().add(leaf);
        leaf = new SearchLeaf(SearchFields.IRCS, asset.getIrcs());
        trunk.getFields().add(leaf);

        SearchBranch branch = new SearchBranch(false);
        trunk.getFields().add(branch);
        
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);

        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListSearchPaginatedTestNoInactivatedAssets() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset.setActive(true);
        assetDao.createAsset(asset);
        commit();
        
        Asset assetInactivated = AssetTestsHelper.createBasicAsset();
        assetInactivated.setActive(false);
        assetDao.createAsset(assetInactivated);
        commit();

        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.CFR, asset.getCfr()));
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, assetInactivated.getIrcs()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));

        assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, true);
        
        assertEquals(2, assets.size());
        assertThat(assets.get(0).getId(), is(asset.getId()));
        assertThat(assets.get(1).getId(), is(assetInactivated.getId()));
        
        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(assetInactivated);
        commit();
    }
 
    private void commit() throws Exception {
        userTransaction.commit();
        userTransaction.begin();
    }
}
