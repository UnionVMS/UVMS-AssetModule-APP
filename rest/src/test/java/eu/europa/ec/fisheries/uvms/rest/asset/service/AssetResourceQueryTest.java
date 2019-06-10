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
package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetMatcher;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.AssetQuery;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetResourceQueryTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setCfr(Collections.singletonList(createdAsset.getCfr()));
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListQueryTestEmptyResult() {
        AssetQuery query = new AssetQuery();
        query.setCfr(Collections.singletonList("APA"));

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(0));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void testCaseSensitiveiness() {

        String cfrValue = UUID.randomUUID().toString().substring(0,11).toUpperCase();
        Asset asset = AssetHelper.createBasicAsset();
        asset.setCfr(cfrValue);
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        AssetQuery query = new AssetQuery();
        query.setCfr(Collections.singletonList(cfrValue));
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Collections.singletonList(cfrValue.toLowerCase()));

        AssetListResponse listResponse1 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query2), AssetListResponse.class);

        assertNotNull(listResponse1);
        assertNotNull(listResponse2);
        assertTrue(listResponse1.getAssetList().size() > 0);
        assertTrue(listResponse2.getAssetList().size() > 0);

        Asset asset1 = listResponse1.getAssetList().get(0);
        Asset asset2 = listResponse2.getAssetList().get(0);

        assertEquals(asset1.getCfr(), asset2.getCfr());
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void testCaseSensitiveinessTwoEntities() {

        String cfrValue = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        String cfrValue2 = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setCfr(cfrValue);
        Asset createdAsset1 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset1), Asset.class);
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setCfr(cfrValue2);
        Asset createdAsset2 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);


        AssetQuery query = new AssetQuery();
        query.setCfr(Collections.singletonList(cfrValue));
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(cfrValue.toLowerCase(), cfrValue2.toLowerCase()));

        AssetListResponse listResponse1 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query2), AssetListResponse.class);

        assertNotNull(listResponse1);
        assertNotNull(listResponse2);
        assertEquals(1, listResponse1.getAssetList().size());
        assertEquals(2, listResponse2.getAssetList().size());


        Asset fetched_asset1 = listResponse1.getAssetList().get(0);
        Asset fetched_asset2 = listResponse2.getAssetList().get(0);

        assertEquals(fetched_asset1.getCfr(), fetched_asset2.getCfr());
        assertEquals(listResponse2.getAssetList().get(1).getCfr(), cfrValue2);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void testCaseIncompleteCFR() {

        String cfrValue = UUID.randomUUID().toString().substring(0,11).toUpperCase();
        String cfrValue2 = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setCfr(cfrValue);
        Asset createdAsset1 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset1), Asset.class);
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setCfr(cfrValue2);
        Asset createdAsset2 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);

        AssetQuery query = new AssetQuery();
        query.setCfr(Collections.singletonList(cfrValue));
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(cfrValue.toLowerCase().substring(5), cfrValue2.toLowerCase().substring(3,8)));

        AssetListResponse listResponse1 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query2), AssetListResponse.class);

        assertNotNull(listResponse1);
        assertNotNull(listResponse2);
        assertTrue(listResponse1.getAssetList().size() >= 1);
        assertTrue(listResponse2.getAssetList().size() >= 2);

        boolean found = false;
        for(Asset asset  :listResponse2.getAssetList() ){
            if(asset.getCfr().equals(cfrValue)){
                found = true;
            }
        }

        assertTrue(found);

        found = false;
        for(Asset asset  :listResponse2.getAssetList() ){
            if(asset.getCfr().equals(cfrValue2)){
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListEmptyCriteriasShouldReturnAllAssets() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertNotNull(listResponse);
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListEmptyCriteriasShouldReturnAllAssetsOR() {

        // create an asset
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        // aempty query
        AssetQuery query = new AssetQuery();

        // ask for everything since query is empty
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertNotNull(listResponse);
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListEmptyCriteriasShouldNotReturnInactivatedAssets() {
        Asset asset = AssetHelper.createBasicAsset();
        // create an Asset
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();

        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        int sizeBefore = listResponse.getAssetList().size();

        // Archive the asset
        Asset archived = getWebTargetExternal()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);

        assertFalse(archived.getActive());

        // ask for it
        AssetListResponse listResponseAfter = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertEquals(sizeBefore - 1, listResponseAfter.getAssetList().size());
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListPaginationTest() {
        String customFlagState = AssetHelper.getRandomIntegers(3);
        
        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setFlagStateCode(customFlagState);
        Asset createdAsset1 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset1), Asset.class);
        
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setFlagStateCode(customFlagState);
        Asset createdAsset2 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setFlagState(Collections.singletonList(customFlagState));
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("page", 1)
                .queryParam("size", 1)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        assertThat(listResponse.getCurrentPage(), is(1));
        assertThat(listResponse.getTotalNumberOfPages(), is(2));
        assertThat(listResponse.getAssetList().size(), is(1));
        
        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("page", 2)
                .queryParam("size", 1)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        assertThat(listResponse2.getCurrentPage(), is(2));
        assertThat(listResponse2.getTotalNumberOfPages(), is(2));
        assertThat(listResponse2.getAssetList().size(), is(1));
        
        assertThat(listResponse.getAssetList().get(0).getId(), is(not(listResponse2.getAssetList().get(0).getId())));
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetListWildcardSearchCaseInsensitive() {
        Asset asset = AssetHelper.createBasicAsset();
        String randomNumbers = AssetHelper.getRandomIntegers(10);
        asset.setName("ShipName" + randomNumbers);
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setName(Collections.singletonList("shipn*me" + randomNumbers));
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithDateSearch() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        Instant timestamp = Instant.now();
        
        createdAsset.setName(createdAsset.getName() + "UPDATE");
        Asset updatedAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);
        
        
        // Get history
        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList(asset.getCfr()));
        query.setDate(timestamp);
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
        assertThat(assetList.get(0).getHistoryId(), is(createdAsset.getHistoryId()));
        assertThat(assetList.get(0).getName(), is(asset.getName()));
        
        // Get current
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(asset.getCfr()));
        query2.setDate(Instant.now());
        
        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query2), AssetListResponse.class);
        
        List<Asset> assetList2 = listResponse2.getAssetList();
        assertThat(assetList2.size(), is(1));
        assertThat(assetList2.get(0).getId(), is(updatedAsset.getId()));
        assertThat(assetList2.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assetList2.get(0).getName(), is(updatedAsset.getName()));
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetWithDateSearchThreeRevisions() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        Instant timestamp = Instant.now();
        
        String updatedName1 = createdAsset.getName() + "UPDATE";
        createdAsset.setName(updatedName1);
        Asset updatedAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);
        
        Instant timestamp2 = Instant.now();
        
        String updatedName2 = createdAsset.getName() + "UPDATE2";
        updatedAsset.setName(updatedName2);
        Asset updatedAsset2 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);
        
        // Get history1
        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList(asset.getCfr()));
        query.setDate(timestamp);
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
        assertThat(assetList.get(0).getHistoryId(), is(createdAsset.getHistoryId()));
        assertThat(assetList.get(0).getName(), is(asset.getName()));
        
        // Get history2
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(asset.getCfr()));
        query2.setDate(timestamp2);
        
        AssetListResponse listResponse2 = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query2), AssetListResponse.class);
        
        List<Asset> assetList2 = listResponse2.getAssetList();
        assertThat(assetList2.size(), is(1));
        assertThat(assetList2.get(0).getId(), is(updatedAsset.getId()));
        assertThat(assetList2.get(0).getHistoryId(), is(updatedAsset.getHistoryId()));
        assertThat(assetList2.get(0).getName(), is(updatedName1));
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetsAtDate() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset asset2 = AssetHelper.createBasicAsset();
        Asset createdAsset2 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);
        
        AssetQuery query2 = new AssetQuery();
        query2.setDate(Instant.now());
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query2), AssetListResponse.class);
        
        List<Asset> assetList2 = listResponse.getAssetList();
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset2.getId())));
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetsAtDateAndIrcsAndFs() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        Asset asset2 = AssetHelper.createBasicAsset();
        Asset createdAsset2 = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);
        
        Instant timestamp = Instant.now();
        
        String newIrcs = "F" + AssetHelper.getRandomIntegers(7);
        createdAsset.setIrcs(newIrcs);
        Asset updatedAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(createdAsset), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setIrcs(Arrays.asList(asset.getIrcs()));
        query.setFlagState(Arrays.asList(asset.getFlagStateCode()));
        query.setDate(timestamp);
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
        assertThat(assetList.get(0).getHistoryId(), is(createdAsset.getHistoryId()));
        assertThat(assetList.get(0).getIrcs(), is(asset.getIrcs()));
    }
    
    @Test
    @RunAsClient
    @OperateOnDeployment("normal")
    public void getAssetAtInvalidDate() {
        Asset asset = AssetHelper.createBasicAsset();
        getWebTargetExternal()
            .path("asset")
            .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .post(Entity.json(asset), Asset.class);
        
        Instant pastDate = OffsetDateTime.now().minus(100, ChronoUnit.YEARS).toInstant();
        
        AssetQuery query = new AssetQuery();
        query.setIrcs(Arrays.asList(asset.getIrcs()));
        query.setFlagState(Arrays.asList(asset.getFlagStateCode()));
        query.setDate(pastDate);
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);
        
        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(0));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryGearTypeTest() {
        String gearType = "tempGearType" + AssetHelper.getRandomIntegers(10);
        Asset asset = AssetHelper.createBasicAsset();
        asset.setGearFishingType(gearType);
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setGearType(gearType);
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetListQueryGearTypeNoWildcardSearch() {
        String gearType1 = "tempGearType" + AssetHelper.getRandomIntegers(10);
        Asset asset = AssetHelper.createBasicAsset();
        asset.setGearFishingType(gearType1);
        Asset createdAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);
        
        String gearType2 = gearType1 + AssetHelper.getRandomIntegers(5);
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setGearFishingType(gearType2);
        getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset2), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setGearType(gearType1);
        
        AssetListResponse listResponse = getWebTargetExternal()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(query), AssetListResponse.class);

        assertNotNull(listResponse);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }
}
