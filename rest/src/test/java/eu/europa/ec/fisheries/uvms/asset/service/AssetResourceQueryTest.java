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
package eu.europa.ec.fisheries.uvms.asset.service;

import static org.hamcrest.CoreMatchers.is;
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
import eu.europa.ec.fisheries.uvms.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.asset.AssetMatcher;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;

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
}
