package eu.europa.ec.fisheries.uvms.asset.service.bean;
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

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.FishingGearService;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.dao.FishingGearDao;
import eu.europa.ec.fisheries.uvms.dao.FishingGearTypeDao;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearType;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

@Stateless
@Local
public class FishingGearServiceBean implements FishingGearService {

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetQueueConsumer reciever;


    @EJB
    private FishingGearDao fishingGearBean;

    @EJB
    private FishingGearTypeDao fishingGearTypeBean;

    private static final Logger LOG = LoggerFactory.getLogger(FishingGearServiceBean.class);






    @Override
    public FishingGearResponse upsertFishingGears(FishingGearDTO fishingGear, String username) throws AssetMessageException, AssetModelMapperException {
        String request = AssetDataSourceRequestMapper.mapUpsertFishingGearRequest(fishingGear, username);
        String messageId = messageProducer.sendDataSourceMessage(request, AssetDataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        FishingGearResponse fishingGearResponse = AssetDataSourceResponseMapper.mapToUpsertFishingGearResponse(response, messageId);
        //FishingGearListResponse fishingGearListResponse = AssetDataSourceResponseMapper.mapToFishingGearResponse(response, messageId);
        return fishingGearResponse;
    }



    @Override
    public eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO upsertFishingGear(eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO gear, String username) {
        //FishingGearEntity fishingGearEntity = updateFishinGear(gear, username);
        //eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO fishingGear = EntityToModelMapper.mapEntityToFishingGear(fishingGearEntity);
        //return fishingGear;
        return null;
    }

    @Override
    public FishingGearEntity updateFishinGear(eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO fishingGear, String username){

        /*

        FishingGearEntity fishingGearByExternalIdEntity = null;
        try {
            fishingGearByExternalIdEntity = fishingGearBean.getFishingGearByExternalId(fishingGear.getExternalId());
            if(fishingGearByExternalIdEntity == null){
                FishingGearEntity fishingGearEntity;
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
            LOG.error("Could not creae or update the fishing gear with external id: " + fishingGear.getExternalId());
        }

        return fishingGearByExternalIdEntity;
        */
        return null;
    }

    @Override
    public void updateFishingGearTypeProperties(eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO fishingGear, String username, FishingGearType fishingGearTypeByCodeEntity) {
        fishingGearTypeByCodeEntity.setUpdateUser(username);
        fishingGearTypeByCodeEntity.setDescription(fishingGear.getFishingGearType().getName());
        fishingGearTypeByCodeEntity.setCode(fishingGear.getFishingGearType().getCode());
        fishingGearTypeByCodeEntity.setName(fishingGear.getFishingGearType().getName());
        fishingGearTypeByCodeEntity.setUpdateDateTime(DateUtils.getNowDateUTC());
    }

    @Override
    public void updateFishingGearProperties(eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO fishingGear, String username, FishingGearEntity fishingGearByExternalIdEntity, FishingGearType fishingGearType) {
        fishingGearByExternalIdEntity.setFishingGearType(fishingGearType);
        fishingGearByExternalIdEntity.setCode(fishingGear.getCode());
        fishingGearByExternalIdEntity.setUpdatedBy(username);
        fishingGearByExternalIdEntity.setDescription(fishingGear.getDescription());
        fishingGearByExternalIdEntity.setExternalId(fishingGear.getExternalId());
        fishingGearByExternalIdEntity.setUpdateTime(DateUtils.getNowDateUTC());
    }




}
