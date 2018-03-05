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

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateType;

@Stateless
public class AssetHistoryServiceBean implements AssetHistoryService {

	final static Logger LOG = LoggerFactory.getLogger(AssetHistoryServiceBean.class);
	@EJB
	MessageProducer messageProducer;
	@EJB
	AssetQueueConsumer reciever;

	// @EJB
	// AssetDomainModelBean assetDomainModel;
	@Inject
	private AssetDao assetDao;

	@Override
	public List<Asset> getAssetHistoryListByAssetId(String assetId, Integer maxNbr) throws AssetServiceException {
		try {
			AssetEntity asset = assetDao.getAssetByGuid(assetId);
			return EntityToModelMapper.toAssetHistoryList(asset, maxNbr);
		} catch (AssetDaoException e) {
			throw new AssetServiceException("Could not find asset histories from id " + assetId, e);
		}
	}

	@Override
	public Asset getAssetHistoryByAssetHistGuid(String assetHistId) throws AssetServiceException {
		try {
			AssetHistory assetHistory = assetDao.getAssetHistoryByGuid(assetHistId);
			return EntityToModelMapper.toAssetFromAssetHistory(assetHistory);
		} catch (AssetDaoException e) {
			throw new AssetServiceException("Could not find asset history by id " + assetHistId, e);
		}
	}

	@Override
	public FlagStateType getFlagStateByIdAndDate(String assetGuid, Date date) throws AssetServiceException {
		if (assetGuid == null) {
			throw new InputArgumentException("Cannot get asset because asset ID is null.");
		}
		if (date == null) {
			throw new InputArgumentException("Cannot get asset because date is null.");
		}

		try {
			FlagState flagState = assetDao.getAssetFlagStateByIdAndDate(assetGuid, date);

			FlagStateType flagStateType = new FlagStateType();
			flagStateType.setCode(flagState.getCode());
			flagStateType.setName(flagState.getName());
			flagStateType.setId(flagState.getId());
			flagStateType.setUpdatedBy(flagState.getUpdatedBy());
			flagStateType.setUpdateTime(DateUtils.dateToString(flagState.getUpdateTime()));
			return flagStateType;
		} catch (AssetDaoException e) {
			throw new AssetServiceException("Could not get flag state by id " + assetGuid, e);
		}
	}

	@Override
	public Asset getAssetByIdAndDate(String type, String value, Date date) throws AssetServiceException {

		if (type == null) {
			throw new InputArgumentException("Type is null");
		}
		AssetIdType assetType = AssetIdType.fromValue(type);
		if (assetType == null) {
			throw new InputArgumentException("Not a valid type: " + type);
		}
		if (value == null) {
			throw new InputArgumentException("Value is null");
		}
		if (date == null) {
			throw new InputArgumentException("Date is null");
		}

		try {
			AssetId assetId = new AssetId();
			assetId.setType(assetType);
			assetId.setValue(value);
			AssetEntity assetEntity = assetDao.getAssetFromAssetIdAndDate(assetId, date);
			return EntityToModelMapper.toAssetFromEntity(assetEntity);
		} catch (AssetDaoException e) {
			throw new AssetServiceException("Could not get asset by id and date", e);
		}
	}
}
