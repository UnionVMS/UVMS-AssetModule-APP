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
package eu.europa.ec.fisheries.uvms.asset.service.sync;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetSyncException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselCoreDataResponse;
import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselExtendedDataResponse;

@ApplicationScoped
public class AssetSyncClient {

    private static final long CACHE_MAX_ENTRIES = 1000;
    private static final long CACHE_TTL = 30;

    private LoadingCache<String, AssetHistory> extendedAssetDataCache;

    @Inject
    private AssetWsClient assetWsClient;

    @Inject
    private AssetSyncDataConverter mapper;

    public AssetSyncClient() {
        extendedAssetDataCache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_MAX_ENTRIES)
                .expireAfterAccess(CACHE_TTL, TimeUnit.SECONDS)
                .build(
                        new CacheLoader<String, AssetHistory>() {
                            public AssetHistory load(String cfr) {
                                final AssetHistory assetHistory = getAssetExtendedData(cfr);
                                return assetHistory;
                            }
                        }
                );
    }

    public List<AssetHistory> getAssetsPage(Integer pageNumber, Integer pageSize) {
        GetVesselCoreDataResponse assetsFromPage = assetWsClient.getAssetPage(pageNumber, pageSize);

        return assetsFromPage.getVesselCoreDataPage()
                .getVesselEvent()
                .stream()
                .map(mapper::convert)
                .collect(Collectors.toList());
    }

    public AssetHistory getAssetExtendedDataCached(String cfr) {
        try {
            return extendedAssetDataCache.get(cfr);
        } catch (ExecutionException ex) {
            throw new AssetSyncException("Error getting page extended data for: " + cfr + " from fleet server", ex);
        }
    }

    private AssetHistory getAssetExtendedData(String cfr) {
        GetVesselExtendedDataResponse response = assetWsClient.getExtendedDataForAssetByCfr(cfr);
        return response.getVesselExtendedDataPage().getVesselEvent().stream()
                .map(mapper::convertFromExtendedData)
                .findFirst().orElse(null);
    }
}
