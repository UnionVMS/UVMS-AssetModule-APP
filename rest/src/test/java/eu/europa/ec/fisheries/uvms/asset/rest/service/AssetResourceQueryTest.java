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
package eu.europa.ec.fisheries.uvms.asset.rest.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.rest.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.asset.rest.AssetHelper;
import eu.europa.ec.fisheries.uvms.asset.rest.AssetMatcher;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;

@RunWith(Arquillian.class)
public class AssetResourceQueryTest extends AbstractAssetRestTest {

    @Test
    @RunAsClient
    public void getAssetListQueryTest() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList(createdAsset.getCfr()));
        
        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        assertTrue(listResponse != null);
        assertThat(listResponse.getAssetList().size(), is(1));
        assertThat(listResponse.getAssetList().get(0), is(AssetMatcher.assetEquals(createdAsset)));
    }

    @Test
    @RunAsClient
    public void getAssetListQueryTestEmptyResult() {
        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList("APA"));

        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);

        assertTrue(listResponse != null);
        assertThat(listResponse.getAssetList().size(), is(0));
    }

    @Test
    @RunAsClient
    public void testCaseSensitiveiness() {

        String cfrValue = UUID.randomUUID().toString().substring(0,11).toUpperCase();
        Asset asset = AssetHelper.createBasicAsset();
        asset.setCfr(cfrValue);
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList(cfrValue));
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(cfrValue.toLowerCase()));

        AssetListResponse listResponse1 = getWebTarget()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTarget()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query2), AssetListResponse.class);

        assertTrue(listResponse1 != null);
        assertTrue(listResponse2 != null);
        assertTrue(listResponse1.getAssetList().size() > 0);
        assertTrue(listResponse2.getAssetList().size() > 0);


        Asset asset1 = listResponse1.getAssetList().get(0);
        Asset asset2 = listResponse2.getAssetList().get(0);

        assertTrue(asset1.getCfr().equals(asset2.getCfr()));

    }

    @Test
    @RunAsClient
    public void testCaseSensitiveinessTwoEntities() {

        String cfrValue = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        String cfrValue2 = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setCfr(cfrValue);
        Asset createdAsset1 = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset1), Asset.class);
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setCfr(cfrValue2);
        Asset createdAsset2 = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset2), Asset.class);


        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList(cfrValue));
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(cfrValue.toLowerCase(), cfrValue2.toLowerCase()));

        AssetListResponse listResponse1 = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query2), AssetListResponse.class);

        assertTrue(listResponse1 != null);
        assertTrue(listResponse2 != null);
        assertTrue(listResponse1.getAssetList().size() == 1);
        assertTrue(listResponse2.getAssetList().size() == 2);


        Asset fetched_asset1 = listResponse1.getAssetList().get(0);
        Asset fetched_asset2 = listResponse2.getAssetList().get(0);

        assertTrue(fetched_asset1.getCfr().equals(fetched_asset2.getCfr()));
        assertTrue(listResponse2.getAssetList().get(1).getCfr().equals(cfrValue2));

    }

    @Test
    @RunAsClient
    public void testCaseIncompleteCFR() {

        String cfrValue = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        String cfrValue2 = UUID.randomUUID().toString().substring(0,11).toUpperCase();

        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setCfr(cfrValue);
        Asset createdAsset1 = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset1), Asset.class);
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setCfr(cfrValue2);
        Asset createdAsset2 = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset2), Asset.class);


        AssetQuery query = new AssetQuery();
        query.setCfr(Arrays.asList(cfrValue));
        AssetQuery query2 = new AssetQuery();
        query2.setCfr(Arrays.asList(cfrValue.toLowerCase().substring(5), cfrValue2.toLowerCase().substring(3,8)));

        AssetListResponse listResponse1 = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);

        AssetListResponse listResponse2 = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query2), AssetListResponse.class);

        assertTrue(listResponse1 != null);
        assertTrue(listResponse2 != null);
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
    public void getAssetListEmptyCriteriasShouldReturnAllAssets() {
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();

        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        assertTrue(listResponse != null);
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    public void getAssetListEmptyCriteriasShouldReturnAllAssetsOR() {

        // create an asset
        Asset asset = AssetHelper.createBasicAsset();
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);

        // aempty query
        AssetQuery query = new AssetQuery();

        // ask for everything since query is empty
        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("dynamic","false")
                .queryParam("size", "1000")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);

        assertTrue(listResponse != null);

        
        assertTrue(listResponse.getAssetList().stream()
                .anyMatch(fetchedAsset -> fetchedAsset.getId().equals(createdAsset.getId())));
    }

    @Test
    @RunAsClient
    public void getAssetListEmptyCriteriasShouldNotReturnInactivatedAssets() {
        Asset asset = AssetHelper.createBasicAsset();
        // create an Asset
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();

        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        int sizeBefore = listResponse.getAssetList().size();


        // Archive the asset
        getWebTarget()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);

        // ask for it
        AssetListResponse listResponseAfter = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);


        assertEquals(sizeBefore  - 1, listResponseAfter.getAssetList().size());
    }
    
    @Test
    @RunAsClient
    public void getAssetListPaginationTest() {
        String customFlagState = AssetHelper.getRandomIntegers(3);
        
        Asset asset1 = AssetHelper.createBasicAsset();
        asset1.setFlagStateCode(customFlagState);
        Asset createdAsset1 = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset1), Asset.class);
        
        Asset asset2 = AssetHelper.createBasicAsset();
        asset2.setFlagStateCode(customFlagState);
        Asset createdAsset2 = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset2), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setFlagState(Arrays.asList(customFlagState));
        
        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("page", 1)
                .queryParam("size", 1)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        assertThat(listResponse.getCurrentPage(), is(1));
        assertThat(listResponse.getTotalNumberOfPages(), is(2));
        assertThat(listResponse.getAssetList().size(), is(1));
        
        AssetListResponse listResponse2 = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("page", 2)
                .queryParam("size", 1)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        assertThat(listResponse2.getCurrentPage(), is(2));
        assertThat(listResponse2.getTotalNumberOfPages(), is(2));
        assertThat(listResponse2.getAssetList().size(), is(1));
        
        assertThat(listResponse.getAssetList().get(0).getId(), is(not(listResponse2.getAssetList().get(0).getId())));
    }
    
    @Test
    @RunAsClient
    public void getAssetListWildcardSearchCaseInsensitive() {
        Asset asset = AssetHelper.createBasicAsset();
        String randomNumbers = AssetHelper.getRandomIntegers(10);
        asset.setName("ShipName" + randomNumbers);
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();
        query.setName(Arrays.asList("shipn*me" + randomNumbers));
        
        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .queryParam("size","1000")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        List<Asset> assetList = listResponse.getAssetList();
        assertThat(assetList.size(), is(1));
        assertThat(assetList.get(0).getId(), is(createdAsset.getId()));
    }
}