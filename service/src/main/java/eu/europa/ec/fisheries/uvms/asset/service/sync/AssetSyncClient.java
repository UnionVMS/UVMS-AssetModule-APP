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

import eu.europa.ec.fisheries.uvms.asset.service.sync.collector.AssetSyncRawDataConverter;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselAggregatedDataResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AssetSyncClient {

    @Inject
    private AssetWsClient assetWsClient;

    @Inject
    private AssetSyncDataConverter mapper;

    @Inject
    private AssetSyncRawDataConverter rawMapper;

    public AssetSyncClient() { }

    public List<AssetHistory> getAssetsPage(Integer pageNumber, Integer pageSize) {
        GetVesselAggregatedDataResponse assetsFromPage = assetWsClient.getAssetPage(pageNumber, pageSize);

        return assetsFromPage.getVesselAggregatedDataPageType()
                .getVesselEvent()
                .stream()
                .map(mapper::convert)
                .collect(Collectors.toList());
    }

    public List<AssetRawHistory> getRawAssetsPage(Integer pageNumber, Integer pageSize) {
        GetVesselAggregatedDataResponse assetsFromPage = assetWsClient.getAssetPage(pageNumber, pageSize);

        return assetsFromPage.getVesselAggregatedDataPageType()
                .getVesselEvent()
                .stream()
                .map(rawMapper::rawConvert)
                .collect(Collectors.toList());
    }
}