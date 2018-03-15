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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class AssetGroupServiceBean implements AssetGroupService {

    private static final String GROUP_QUALIFIER_PREFIX = "Group: ";


    @EJB
    private AssetGroupDao assetGroupDao;

    @EJB
    private AssetGroupFieldDao assetGroupFieldDao;

    final static Logger LOG = LoggerFactory.getLogger(AssetGroupServiceBean.class);

    @Override
    public List<AssetGroupEntity> getAssetGroupList(String user) throws AssetException {

        LOG.info("Getting asset group list by user: {}.", user);
        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }

        List<AssetGroupEntity> filterGroupList = assetGroupDao.getAssetGroupByUser(user);
        return filterGroupList;
    }


    @Override
    public List<AssetGroupEntity> getAssetGroupListByAssetGuid(UUID assetGuid) throws AssetException {
        LOG.info("Getting asset group list by asset guid: {}.", assetGuid);
        if (assetGuid == null) {
            throw new InputArgumentException("Invalid asset");
        }

            List<AssetGroupEntity> vesselGroupList = new ArrayList<>();

            // TODO DO a join instead
            List<AssetGroupEntity> filterGroupList = assetGroupDao.getAssetGroupAll();
            /*
            for (AssetGroupEntity group : filterGroupList) {
                List<AssetGroupField> fields = group.getFields();
                for (AssetGroupField field : fields) {
                    if ("GUID".equals(field.getField()) && assetGuid.equals(field.getValue())) {
                        vesselGroupList.add(group);
                    }
                }
            }
            */
            return filterGroupList;
    }


    @Override
    public AssetGroupEntity getAssetGroupById(UUID guid) throws AssetException {

        if (guid == null) {
            throw new InputArgumentException("Cannot get asset group because ID is null.");
        }

        try {
            AssetGroupEntity groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
            if (groupEntity == null) {
                throw new AssetGroupDaoException("No assetgroup found.");
            }

            return groupEntity;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when getting asset group. ] guid {} exception {}", guid, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity updateAssetGroup(AssetGroupEntity assetGroup, String username) throws AssetException {

        if (assetGroup == null || assetGroup.getId() == null) {
            throw new InputArgumentException("Cannot update asset group because group or ID is null.");
        }

        try {
            AssetGroupEntity groupEntity = assetGroupDao.getAssetGroupByGuid(assetGroup.getId());
            if (groupEntity == null) {
                throw new AssetGroupDaoException("No assetgroup found.");
            }
            return groupEntity;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when updating asset group. ] assetGroup: {} username: {} exception: {}", assetGroup, username, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity createAssetGroup(AssetGroupEntity assetGroup, String username) throws AssetException {
        if (assetGroup == null) {
            throw new InputArgumentException("Cannot create asset group because the group is null.");
        }

            AssetGroupEntity createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroup);
            List<AssetGroupField> fields = assetGroup.getFields();
            if(fields != null){
                for(AssetGroupField field : fields ){
                    field.setAssetGroup(assetGroup);
                    field.setUpdateTime(createdAssetGroupEntity.getUpdateTime());
                    field.setUpdatedBy(createdAssetGroupEntity.getUpdatedBy());
                    assetGroupFieldDao.create(field);
                }
            }
            return createdAssetGroupEntity;
    }


    @Override
    public AssetGroupEntity deleteAssetGroupById(UUID guid, String username) throws AssetException {

        if (guid == null) {
            throw new InputArgumentException("Cannot delete asset group because the group ID is null.");
        }

        try {
            AssetGroupEntity groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
            if (groupEntity == null) {
                throw new AssetGroupDaoException("No assetgroup found.");
            }
            groupEntity.setArchived(true);
            groupEntity.setUpdatedBy(username);
            groupEntity.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
            return groupEntity;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when deleting asset group. ] guid: {} username: {} exception: {}", guid, username, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }
}