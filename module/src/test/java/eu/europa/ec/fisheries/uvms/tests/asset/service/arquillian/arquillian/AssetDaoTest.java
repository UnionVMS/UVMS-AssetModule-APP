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

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.model.constants.UnitTonnage;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchLeaf;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
        assertEquals(fetchedAsset.get(0).getId(), asset1.getId());
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
        assertEquals(fetchedAsset.get(0).getId(), asset1.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiOrIrcsWithIrcsContainingSpaceTest() {
        String randomSuffix = AssetTestsHelper.getRandomIntegers(6);
        String ircs = "I" + randomSuffix;
        String testIrcs = "I " + randomSuffix;
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        asset1.setIrcs(ircs);
        asset1 = assetDao.createAsset(asset1);
        assertThat(asset1.getId(), is(notNullValue()));

        List<Asset> fetchedAsset = assetDao.getAssetByMmsiOrIrcs(null, testIrcs);
        assertEquals(1, fetchedAsset.size());
        assertEquals(fetchedAsset.get(0).getId(), asset1.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiOrIrcsWithIrcsFormatNLLNNNN() {
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        asset1.setIrcs("3FB" + AssetTestsHelper.getRandomIntegers(4));
        asset1 = assetDao.createAsset(asset1);
        assertThat(asset1.getId(), is(notNullValue()));

        List<Asset> fetchedAsset = assetDao.getAssetByMmsiOrIrcs(null, asset1.getIrcs());
        assertEquals(1, fetchedAsset.size());
        assertEquals(fetchedAsset.get(0).getId(), asset1.getId());
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
    public void getAssetsAtDateSingleAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        List<Asset> assetsAtDate = assetDao.getAssetsAtDate(Arrays.asList(asset.getId()), Instant.now());
        Asset assetAtDate = assetsAtDate.get(0);

        assertThat(assetAtDate.getId(), is(notNullValue()));

        assertThat(assetAtDate.getName(), is(asset.getName()));
        assertThat(assetAtDate.getCfr(), is(asset.getCfr()));
        assertThat(assetAtDate.getActive(), is(asset.getActive()));
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetsAtDateTwoAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        asset2 = assetDao.createAsset(asset2);
        commit();

        List<Asset> assetsAtDate = assetDao.getAssetsAtDate(Arrays.asList(asset.getId(), asset2.getId()), Instant.now());

        assertThat(assetsAtDate.size(), CoreMatchers.is(2));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetAtDateSingleAssetTest() throws Exception {
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        commit();

        Asset assetAtDate = assetDao.getAssetAtDate(asset.getId(), Instant.now());

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

        Asset assetAtDate = assetDao.getAssetAtDate(asset.getId(), Instant.now().minus(1, ChronoUnit.DAYS));

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

        Asset assetAtFirstDate = assetDao.getAssetAtDate(asset2.getId(), firstDate);
        assertThat(assetAtFirstDate.getName(), is(firstName));

        Asset assetAtSecondDate = assetDao.getAssetAtDate(asset2.getId(), secondDate);
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
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(asset.getId())));
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(asset.getId())));

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
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset.getId())));
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset2.getId())));

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
        
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(asset.getId()) || 
                		fetchedAsset.getId().equals(asset2.getId())   ));
        
        assets = assetDao.getAssetListSearchPaginated(2, 1, trunk, false);
        
        assertEquals(1, assets.size());
        
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(asset.getId()) || 
                		fetchedAsset.getId().equals(asset2.getId())   ));

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

        List<Asset> assetListWithInactivated = assetDao.getAssetListSearchPaginated(1, 10, trunk, true);
        
        assertEquals(2, assetListWithInactivated.size());
        assertTrue(assetListWithInactivated.stream().anyMatch(a -> a.getId().equals(asset.getId())));
        assertTrue(assetListWithInactivated.stream().anyMatch(a -> a.getId().equals(assetInactivated.getId())));

        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(assetInactivated);
        commit();
    }
 
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearch() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        Asset asset = AssetTestsHelper.createBasicAsset();
		asset.setIrcs("I "+ randomIntegersGen);
        assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        asset2.setIrcs("I-" + randomIntegersGen);
        assetDao.createAsset(asset2);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, asset2.getIrcs()));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        assertEquals(2, assets.size());
        
        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        commit();
    }
 
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchAndInActive() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        Asset asset = AssetTestsHelper.createBasicAsset();
		asset.setIrcs("Q "+ randomIntegersGen);
        asset = assetDao.createAsset(asset);
        commit();
        
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        asset2.setIrcs("Q-" + randomIntegersGen);
        asset2 = assetDao.createAsset(asset2);
        commit();
        
        Asset asset3 = AssetTestsHelper.createBasicAsset();
        asset3.setIrcs("Q" + randomIntegersGen);
        asset3.setActive(false);
        asset3 = assetDao.createAsset(asset3);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, "Q" + randomIntegersGen));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        assertEquals(2, assets.size());
        
        trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, "Q" + randomIntegersGen));

        assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, true);
        assertEquals(3, assets.size());
        
        assetDao.deleteAsset(asset);
        assetDao.deleteAsset(asset2);
        assetDao.deleteAsset(asset3);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchExternalMarking() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        String eMarking = "E Marking"+randomIntegersGen;
        Asset asset = AssetTestsHelper.createBasicAsset();
		asset.setExternalMarking(eMarking);
        asset = assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, "E-MaR-king-"+randomIntegersGen));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        assertEquals(1, assets.size());
        assertEquals(asset.getExternalMarking(), eMarking);
        
        assetDao.deleteAsset(asset);
        commit();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchExternalMarkingWithInactivated() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        String eMarking = "E Marking"+randomIntegersGen;
        Asset asset = AssetTestsHelper.createBasicAsset();
		asset.setExternalMarking(eMarking);
		asset.setActive(false);
        asset = assetDao.createAsset(asset);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, "E-MaR-king-"+randomIntegersGen));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, true);
        assertEquals(1, assets.size());
        assertEquals(asset.getExternalMarking(), eMarking);
        
        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchExternalMarkingOrIrcs() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        String eMarkingString = "E Marking"+randomIntegersGen;
        String ircsString = "IQ-"+randomIntegersGen;
        
        Asset assetEM = AssetTestsHelper.createBasicAsset();
        assetEM.setExternalMarking(eMarkingString);
        assetDao.createAsset(assetEM);
        commit();
        
        Asset assetIrcs = AssetTestsHelper.createBasicAsset();
        assetIrcs.setIrcs(ircsString);
        assetDao.createAsset(assetIrcs);
        commit();
        
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, "E-MaR-king-"+randomIntegersGen));
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, "I -Q-"+randomIntegersGen));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(2, assets.size());
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(assetIrcs.getId())));
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(assetEM.getId())));
        
        assertEquals(assetEM.getExternalMarking(), eMarkingString);
        assertEquals(assetIrcs.getIrcs(), ircsString);
        
        assetDao.deleteAsset(assetEM);
        assetDao.deleteAsset(assetIrcs);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchExternalMarkingOrIrcsNoInactiveated() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        String eMarkingString = "E Marking"+randomIntegersGen;
        String ircsString = "IQ-"+randomIntegersGen;
        
        Asset assetEM = AssetTestsHelper.createBasicAsset();
        assetEM.setExternalMarking(eMarkingString);
        assetDao.createAsset(assetEM);
        commit();
        
        Asset assetIrcs = AssetTestsHelper.createBasicAsset();
        assetIrcs.setIrcs(ircsString);
        assetIrcs.setActive(false);
        assetDao.createAsset(assetIrcs);
        commit();
        
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, "E-MaR-king-"+randomIntegersGen));
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, "I -Q-"+randomIntegersGen));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(assetEM.getId())));
        
        assertEquals(assetEM.getExternalMarking(), eMarkingString);
        
        assetDao.deleteAsset(assetEM);
        assetDao.deleteAsset(assetIrcs);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchExternalMarkingAndIrcsNested() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        String eMarkingString = "E Marking"+randomIntegersGen;
        String ircsString = "IQ-"+randomIntegersGen;
        
        Asset assetEM = AssetTestsHelper.createBasicAsset();
        assetEM.setExternalMarking(eMarkingString);
        assetDao.createAsset(assetEM);
        commit();
        
        Asset assetIrcs = AssetTestsHelper.createBasicAsset();
        assetIrcs.setIrcs(ircsString);
        assetDao.createAsset(assetIrcs);
        commit();
        
        
        SearchBranch trunk = new SearchBranch(true);
        SearchBranch trunk2 = new SearchBranch(false);
        trunk.getFields().add(trunk2);
        SearchBranch trunk3 = new SearchBranch(false);
        trunk3.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, "E-MaR-king-"+randomIntegersGen));
        trunk3.getFields().add(new SearchLeaf(SearchFields.IRCS, "I -Q-"+randomIntegersGen));
        trunk2.getFields().add(trunk3);
        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(2, assets.size());
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(assetIrcs.getId())));
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(assetEM.getId())));
        
        assertEquals(assetEM.getExternalMarking(), eMarkingString);
        assertEquals(assetIrcs.getIrcs(), ircsString);
        
        assetDao.deleteAsset(assetEM);
        assetDao.deleteAsset(assetIrcs);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchExternalMarkingAndIrcs() throws Exception {

        String randomIntegersGen = AssetTestsHelper.getRandomIntegers(5);
        
        String eMarkingString = "E Marking"+randomIntegersGen;
        String ircsString = "IQ-"+randomIntegersGen;
        
        Asset asset = AssetTestsHelper.createBasicAsset();
        asset.setExternalMarking(eMarkingString);
        asset.setIrcs(ircsString);
        assetDao.createAsset(asset);
        commit();
        
        
        SearchBranch trunk = new SearchBranch(true);
        trunk.getFields().add(new SearchLeaf(SearchFields.EXTERNAL_MARKING, "E-MaR-king-"+randomIntegersGen));
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, "I -Q-"+randomIntegersGen));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        
        assertEquals(1, assets.size());
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(asset.getId())));
        
        assertEquals(asset.getExternalMarking(), eMarkingString);
        assertEquals(asset.getIrcs(), ircsString);
        
        assetDao.deleteAsset(asset);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchEmpty() throws Exception {
        
        Asset asset1 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset1);
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        Asset asset3 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset3);
        Asset asset4 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset4);
        Asset asset5 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset5);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        assertTrue(assets.size() >= 5 );
        
        assetDao.deleteAsset(asset1);
        assetDao.deleteAsset(asset2);
        assetDao.deleteAsset(asset3);
        assetDao.deleteAsset(asset4);
        assetDao.deleteAsset(asset5);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchEmptyNoInactivated() throws Exception {
        
        Asset asset1 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset1);
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        Asset asset3 = AssetTestsHelper.createBasicAsset();
        asset3.setActive(false);
        assetDao.createAsset(asset3);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 10, trunk, false);
        assertTrue(assets.size() >= 2 );
        assertFalse(assets.contains(asset3));
        
        assetDao.deleteAsset(asset1);
        assetDao.deleteAsset(asset2);
        assetDao.deleteAsset(asset3);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListFuzzySearchEmptyWithInactivated() throws Exception {
        
        Asset asset1 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset1);
        Asset asset2 = AssetTestsHelper.createBasicAsset();
        assetDao.createAsset(asset2);
        Asset asset3 = AssetTestsHelper.createBasicAsset();
        asset3.setActive(false);
        assetDao.createAsset(asset3);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 100, trunk, true);
        assertTrue(assets.size() >= 3 );
        assertTrue(assets.stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(asset3.getId())));
        
        assetDao.deleteAsset(asset1);
        assetDao.deleteAsset(asset2);
        assetDao.deleteAsset(asset3);
        commit();
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void orderByDescTestEmptySearch() throws Exception {
        
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset1);
        commit();
        Asset asset2 = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset2);
        commit();
        Asset asset3 = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset3);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 100, trunk, true);
        assertTrue(assets.size() >= 3 );
        
        List<UUID> idList  = assets.stream()
        	.map(a -> a.getId())
        	.collect(Collectors.toList());
        
        assertTrue(idList.indexOf(asset1.getId()) > idList.indexOf(asset2.getId()) );
        assertTrue(idList.indexOf(asset2.getId()) > idList.indexOf(asset3.getId()) );
        
        assetDao.deleteAsset(asset1);
        assetDao.deleteAsset(asset2);
        assetDao.deleteAsset(asset3);
        commit();
    }
   
    @Test
    @OperateOnDeployment("normal")
    public void orderByDescTestWithIrcs() throws Exception {
        
        Asset asset1 = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset1);
        commit();
        Asset asset2 = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset2);
        commit();
        Asset asset3 = AssetTestsHelper.createBiggerAsset();
        assetDao.createAsset(asset3);
        commit();
        
        SearchBranch trunk = new SearchBranch(false);
        trunk.getFields().add(new SearchLeaf(SearchFields.IRCS, "F*"));

        List<Asset> assets = assetDao.getAssetListSearchPaginated(1, 100, trunk, false);
        assertTrue(assets.size() >= 3 );
        
        List<UUID> idList  = assets.stream()
        	.map(a -> a.getId())
        	.collect(Collectors.toList());
        
        assertTrue(idList.indexOf(asset1.getId()) > idList.indexOf(asset2.getId()) );
        assertTrue(idList.indexOf(asset2.getId()) > idList.indexOf(asset3.getId()) );
        
        assetDao.deleteAsset(asset1);
        assetDao.deleteAsset(asset2);
        assetDao.deleteAsset(asset3);
        commit();
    }

    private void commit() throws Exception {
        userTransaction.commit();
        userTransaction.begin();
    }
}
