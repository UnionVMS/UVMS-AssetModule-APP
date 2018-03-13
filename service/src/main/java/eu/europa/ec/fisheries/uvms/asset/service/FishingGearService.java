package eu.europa.ec.fisheries.uvms.asset.service;
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

import eu.europa.ec.fisheries.uvms.asset.types.FishingGearDTO;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearType;

import javax.ejb.Local;
import java.util.List;

@Local
public interface FishingGearService {

    List<FishingGearEntity> upsertFishingGears(List<FishingGearEntity> fishingGear, String username) throws AssetMessageException, AssetModelMapperException;

    FishingGearEntity upsertFishingGear(FishingGearEntity gear, String username);

    FishingGearEntity updateFishingGear(FishingGearEntity fishingGear, String username);

    void updateFishingGearTypeProperties(FishingGearEntity fishingGear, String username, FishingGearType fishingGearTypeByCodeEntity);

    void updateFishingGearProperties(FishingGearEntity fishingGear, String username, FishingGearEntity fishingGearByExternalIdEntity, FishingGearType fishingGearType);
}
