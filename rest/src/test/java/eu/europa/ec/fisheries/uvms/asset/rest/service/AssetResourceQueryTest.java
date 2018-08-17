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
        Asset createdAsset = getWebTarget()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
        
        AssetQuery query = new AssetQuery();

        AssetListResponse listResponse = getWebTarget()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        int sizeBefore = listResponse.getAssetList().size();
        
        getWebTarget()
                .path("asset")
                .path("archive")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(createdAsset), Asset.class);
        
        AssetListResponse listResponseAfter = getWebTarget()
                .path("asset")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
        
        assertEquals(sizeBefore - 1, listResponseAfter.getAssetList().size());
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
}