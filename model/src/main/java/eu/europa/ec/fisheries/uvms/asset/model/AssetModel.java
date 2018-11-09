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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import java.util.List;
import java.util.Map;

/**
 **/
public interface AssetModel {

     Long createAsset(Asset assetDto) throws AssetModelException;

     List<Asset> getAssetList(Map<String, String> criteria) throws AssetModelException;

     Asset getAssetById(Long id) throws AssetModelException;

     Asset getAssetByCfr(String cfr) throws AssetModelException;

     Asset getAssetByIrcs(String ircs) throws AssetModelException;

     Long updateAsset(Asset assetDto) throws AssetModelException;

     void deleteAsset(Long id) throws AssetModelException;

}