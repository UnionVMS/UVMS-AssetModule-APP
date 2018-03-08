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
package eu.europa.ec.fisheries.uvms.asset.model;

import eu.europa.ec.fisheries.asset.types.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;

import java.util.List;
import java.util.Map;

/**
 **/
public interface AssetModel {

    public Long createAsset(AssetDTO assetDto) throws AssetModelException;

    public List<AssetDTO> getAssetList(Map<String, String> criteria) throws AssetModelException;

    public AssetDTO getAssetById(Long id) throws AssetModelException;

    public AssetDTO getAssetByCfr(String cfr) throws AssetModelException;

    public AssetDTO getAssetByIrcs(String ircs) throws AssetModelException;

    public Long updateAsset(AssetDTO assetDto) throws AssetModelException;

    public void deleteAsset(Long id) throws AssetModelException;

}