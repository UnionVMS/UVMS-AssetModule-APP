package eu.europa.ec.fisheries.uvms.bean;
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

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.dao.FishingGearDao;
import eu.europa.ec.fisheries.uvms.dao.FishingGearTypeDao;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearType;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mapper.ModelToEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class FishingGearDomainModelBean  {

    private static final Logger LOG = LoggerFactory.getLogger(FishingGearDomainModelBean.class);

    @EJB
    private FishingGearDao fishingGearBean;

    @EJB
    private FishingGearTypeDao fishingGearTypeBean;

    public eu.europa.ec.fisheries.wsdl.asset.types.FishingGear upsertFishingGear(eu.europa.ec.fisheries.wsdl.asset.types.FishingGear gear, String username) {
        FishingGear fishingGearEntity = updateFishinGear(gear, username);
        eu.europa.ec.fisheries.wsdl.asset.types.FishingGear fishingGear = EntityToModelMapper.mapEntityToFishingGear(fishingGearEntity);
        return fishingGear;
    }

    private FishingGear updateFishinGear(eu.europa.ec.fisheries.wsdl.asset.types.FishingGear fishingGear, String username){
        FishingGear fishingGearByExternalIdEntity = null;
        try {
            fishingGearByExternalIdEntity = fishingGearBean.getFishingGearByExternalId(fishingGear.getExternalId());
            if(fishingGearByExternalIdEntity == null){
                FishingGear fishingGearEntity;
                FishingGearType fishingGearTypeByCodeEntity = fishingGearTypeBean.getFishingGearByCode(fishingGear.getFishingGearType().getCode());
                if(fishingGearTypeByCodeEntity == null){
                    FishingGearType fishingGearTypeEntity = ModelToEntityMapper.mapFishingGearTypeToEntity(fishingGear.getFishingGearType(), username);
                    fishingGearEntity = ModelToEntityMapper.mapFishingGearToEntity(fishingGear, username);
                    fishingGearEntity.setFishingGearType(fishingGearTypeEntity);
                }else{
                    fishingGearEntity = ModelToEntityMapper.mapFishingGearToEntity(fishingGear, username);
                    fishingGearEntity.setFishingGearType(fishingGearTypeByCodeEntity);
                    updateFishingGearTypeProperties(fishingGear, username, fishingGearTypeByCodeEntity);

                }
                fishingGearBean.create(fishingGearEntity);
                return fishingGearEntity;
            }else{
                FishingGearType fishingGearTypeByCodeEntity = fishingGearTypeBean.getFishingGearByCode(fishingGear.getFishingGearType().getCode());
                if(fishingGearTypeByCodeEntity == null){
                    FishingGearType fishingGearType = ModelToEntityMapper.mapFishingGearTypeToEntity(fishingGear.getFishingGearType(), username);
                    updateFishingGearProperties(fishingGear, username, fishingGearByExternalIdEntity, fishingGearType);
                } else {
                    updateFishingGearProperties(fishingGear, username, fishingGearByExternalIdEntity, fishingGearTypeByCodeEntity);
                    updateFishingGearTypeProperties(fishingGear, username, fishingGearTypeByCodeEntity);
                    fishingGearByExternalIdEntity.setFishingGearType(fishingGearTypeByCodeEntity);
                }
                fishingGearBean.update(fishingGearByExternalIdEntity);
            }
        } catch (Exception e) {
            LOG.error("Could not create or update the fishing gear with external id: " + fishingGear.getExternalId(),e);
        }

        return fishingGearByExternalIdEntity;
    }

    private void updateFishingGearTypeProperties(eu.europa.ec.fisheries.wsdl.asset.types.FishingGear fishingGear, String username, FishingGearType fishingGearTypeByCodeEntity) {
        fishingGearTypeByCodeEntity.setUpdateUser(username);
        fishingGearTypeByCodeEntity.setDescription(fishingGear.getFishingGearType().getName());
        fishingGearTypeByCodeEntity.setCode(fishingGear.getFishingGearType().getCode());
        fishingGearTypeByCodeEntity.setName(fishingGear.getFishingGearType().getName());
        fishingGearTypeByCodeEntity.setUpdateDateTime(DateUtils.getNowDateUTC());
    }

    private void updateFishingGearProperties(eu.europa.ec.fisheries.wsdl.asset.types.FishingGear fishingGear, String username, FishingGear fishingGearByExternalIdEntity, FishingGearType fishingGearType) {
        fishingGearByExternalIdEntity.setFishingGearType(fishingGearType);
        fishingGearByExternalIdEntity.setCode(fishingGear.getCode());
        fishingGearByExternalIdEntity.setUpdatedBy(username);
        fishingGearByExternalIdEntity.setDescription(fishingGear.getDescription());
        fishingGearByExternalIdEntity.setExternalId(fishingGear.getExternalId());
        fishingGearByExternalIdEntity.setUpdateTime(DateUtils.getNowDateUTC());
    }
}