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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 **/
@Local
public interface AssetHistoryService {

    public List<Asset> getAssetHistoryListByAssetId(String assetId, Integer maxNbr) throws AssetException;

    public Asset getAssetHistoryByAssetHistGuid(String assetHistId) throws AssetException;

    Map<String, Object > getFlagStateByIdAndDate(String assetGuid, Date date) throws AssetException;

    Asset getAssetByIdAndDate(String type, String value, Date date) throws AssetException ;

    }