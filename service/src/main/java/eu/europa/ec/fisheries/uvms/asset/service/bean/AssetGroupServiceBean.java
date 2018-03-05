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

import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetGroupServiceBean implements AssetGroupService {

    private static  final String GROUP_QUALIFIER_PREFIX = "Group: ";

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetQueueConsumer receiver;


    @EJB
    private AssetGroupDao assetGroupDao;

    final static Logger LOG = LoggerFactory.getLogger(AssetGroupServiceBean.class);

    @Override
    public List<AssetGroupEntity> getAssetGroupList(String user) throws AssetException {

        LOG.info("Getting asset group list by user: {}.", user);
        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }

        try {
            List<AssetGroupEntity> filterGroupList = assetGroupDao.getAssetGroupByUser(user);
            return filterGroupList;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when getting asset group list by user. ] user: {} exception: {}",user, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }


    @Override
    public List<AssetGroupEntity> getAssetGroupListByAssetGuid(String assetGuid) throws AssetException {
        LOG.info("Getting asset group list by asset guid: {}.", assetGuid);
        if (assetGuid == null || assetGuid.isEmpty()) {
            throw new InputArgumentException("Invalid asset");
        }

        try {
            List<AssetGroupEntity> vesselGroupList = new ArrayList<>();
            List<AssetGroupEntity> filterGroupList = assetGroupDao.getAssetGroupAll();
            for (AssetGroupEntity group : filterGroupList) {
                List<AssetGroupField> fields = group.getFields();
                for (AssetGroupField field : fields) {
                    if ("GUID".equals(field.getField()) && assetGuid.equals(field.getValue())) {
                        vesselGroupList.add(group);
                    }
                }
            }
            return vesselGroupList;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when getting asset group list by assetGuid. ] assetGuid: {} exception: {}",assetGuid, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }



    @Override
    public AssetGroupEntity getAssetGroupById(String guid) throws AssetException {
        LOG.info("Getting asset group by id: {}.", guid);

        if (guid == null) {
            throw new InputArgumentException("Cannot get asset group because ID is null.");
        }

        try {
            AssetGroupEntity groupEntity = getAssetGroupByIdFROM_DOMAIN_MODEL(guid);
            return groupEntity;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when getting asset group. ] guid {} exception {}",guid, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity updateAssetGroup(AssetGroupEntity assetGroup, String username) throws AssetException {

        if (assetGroup == null || assetGroup.getGuid() == null) {
            throw new InputArgumentException("Cannot update asset group because group or ID is null.");
        }

        try {
            AssetGroupEntity groupEntity = getAssetGroupByIdFROM_DOMAIN_MODEL(assetGroup.getGuid());
            return groupEntity;
        } catch (AssetGroupDaoException  e) {
            LOG.error("[ Error when updating asset group. ] assetGroup: {} username: {} exception: {}", assetGroup, username, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity createAssetGroup(AssetGroupEntity assetGroup, String username) throws AssetException {
        if (assetGroup == null) {
            throw new InputArgumentException("Cannot create asset group because the group is null.");
        }

        try {
            AssetGroupEntity createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroup);
            return createdAssetGroupEntity;
        } catch (AssetGroupDaoException  e) {
            LOG.error("[ Error when creating asset group. ] assetGroup: {} username: {} exception: {}", assetGroup, username, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }


    @Override
    public AssetGroupEntity deleteAssetGroupById(String guid, String username) throws AssetException {
        LOG.info("Deleting asset group by id: {}.", guid);

        if (guid == null) {
            throw new InputArgumentException("Cannot delete asset group because the group ID is null.");
        }

        try {
            AssetGroupEntity groupEntity = getAssetGroupByIdFROM_DOMAIN_MODEL(guid);
            groupEntity.setArchived(true);
            groupEntity.setUpdatedBy(username);
            groupEntity.setUpdateTime(DateUtils.getNowDateUTC());
            return groupEntity;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when deleting asset group. ] guid: {} username: {} exception: {}",guid,username, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }


    private AssetGroupEntity getAssetGroupByIdFROM_DOMAIN_MODEL(String guid) throws AssetGroupDaoException {
        AssetGroupEntity filterGroup = assetGroupDao.getAssetGroupByGuid(guid);
        if (filterGroup == null) {
            throw new AssetGroupDaoException("No assetgroup found.");
        }

        return filterGroup;
    }




}