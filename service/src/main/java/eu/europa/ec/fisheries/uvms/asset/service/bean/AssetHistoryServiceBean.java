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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;

/***/
@Stateless
public class AssetHistoryServiceBean implements AssetHistoryService {

    final static Logger LOG = LoggerFactory.getLogger(AssetHistoryServiceBean.class);
    @EJB
    MessageProducer messageProducer;
    @EJB
    AssetQueueConsumer reciever;
    @EJB
    private AssetDomainModelBean assetDomainModel;

    @Override
    public List<Asset> getAssetHistoryListByAssetId(String assetId, Integer maxNbr) throws AssetException {
        LOG.info("Getting AssetHistoryList by AssetId: {}.", assetId);
        AssetId assetIdData = new AssetId();
        assetIdData.setValue(assetId);
        assetIdData.setType(AssetIdType.GUID);
        List<Asset> assetHistoryList = assetDomainModel.getAssetHistoryListByAssetId(assetIdData, maxNbr);
        return assetHistoryList;
    }

    @Override
    public Asset getAssetHistoryByAssetHistGuid(String assetHistId) throws AssetException {
        LOG.info("Getting AssetHistory by AssetHistoryGuid: {}.", assetHistId);
        AssetHistoryId assetHistoryId = new AssetHistoryId();
        assetHistoryId.setEventId(assetHistId);
        Asset assetHistory = assetDomainModel.getAssetHistory(assetHistoryId);
        return assetHistory;
    }


    @Override
    public FlagStateType getFlagStateByIdAndDate(String assetGuid, Date date) throws AssetException {


        FlagState flagState = assetDomainModel.getFlagStateByIdAndDate(assetGuid, date);
        FlagStateType flagStateType = new FlagStateType();
        if (flagState != null) {
            flagStateType.setCode(flagState.getCode());
            flagStateType.setName(flagState.getName());
            flagStateType.setId(flagState.getId());
            flagStateType.setUpdatedBy(flagState.getUpdatedBy());
            flagStateType.setUpdateTime(DateUtils.dateToString(flagState.getUpdateTime()));
            return flagStateType;
        }
        throw new AssetException("FlagSate not found. Check you setup");
    }


    @Override
    public Asset getAssetByIdAndDate(String type, String value, Date date) throws AssetException {

        if (type == null) {
            throw new AssetException("not a valid type");
        }
        AssetIdType assetType = AssetIdType.fromValue(type);
        if (assetType == null) {
            throw new AssetException("not a valid type " + type);
        }
        if (value == null) {
            throw new AssetException("not a valid value");
        }
        if (date == null) {
            throw new AssetException("not a valid date");
        }

        AssetId assetId = new AssetId();
        assetId.setType(assetType);
        assetId.setValue(value);
        Asset asset = assetDomainModel.getAssetByIdAndDate(assetId, date);
        return asset;
    }


}