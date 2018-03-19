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
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroup;
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

    final static Logger LOG = LoggerFactory.getLogger(AssetGroupServiceBean.class);
    private static final String GROUP_QUALIFIER_PREFIX = "Group: ";

    @EJB
    private AssetGroupDao assetGroupDao;

    @EJB
    private AssetGroupFieldDao assetGroupFieldDao;


    /**
     *
     * @param user
     * @return
     * @throws AssetException
     */
    @Override
    public List<AssetGroup> getAssetGroupList(String user) throws AssetException {

        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }

        List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupByUser(user);
        return filterGroupList;
    }


    /** get all assetgroups for an asset with specified UUID
     *
     * @param assetGuid
     * @return
     * @throws AssetException
     */
    @Override
    public List<AssetGroup> getAssetGroupListByAssetGuid(UUID assetGuid) throws AssetException {

        // TODO maybe this could be done more efficient if search is from the other side and joining . . . .

        if (assetGuid == null) {
            throw new InputArgumentException("Invalid asset");
        }

        List<AssetGroup> searchResultList = new ArrayList<>();
        List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupAll();
            for (AssetGroup group : filterGroupList) {
                List<AssetGroupField> fields =  assetGroupFieldDao.retrieveFieldsForGroup(group);
                for (AssetGroupField field : fields) {
                    if ("GUID".equals(field.getField()) && assetGuid.equals(field.getValue())) {
                        searchResultList.add(group);
                    }
                }
            }
        return searchResultList;
    }


    /** get an assetgroup via its UUID
     *
     * @param guid
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup getAssetGroupById(UUID guid) throws AssetException {

        if (guid == null) {
            throw new InputArgumentException("Cannot get asset group because ID is null.");
        }

        try {
            AssetGroup groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
            if (groupEntity == null) {
                throw new AssetGroupDaoException("No assetgroup found.");
            }

            return groupEntity;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when getting asset group. ] guid {} exception {}", guid, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }


    /** update assetgroup OBS not the childlist (that goes with
     *
     * @param assetGroup
     * @param username
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws AssetException {

        if (assetGroup == null || assetGroup.getId() == null) {
            throw new InputArgumentException("Cannot update asset group because group or ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

        try {
            AssetGroup fetchedAssetGroup = assetGroupDao.getAssetGroupByGuid(assetGroup.getId());
            if (fetchedAssetGroup == null) {
                throw new AssetGroupDaoException("No assetgroup found.");
            }
            assetGroup.setUpdatedBy(username);
            assetGroup.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
            AssetGroup changedAssetGroup = assetGroupDao.updateAssetGroup(assetGroup);
            return changedAssetGroup;
        } catch (AssetGroupDaoException e) {
            LOG.error("[ Error when updating asset group. ] assetGroup: {} username: {} exception: {}", assetGroup, username, e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }

    @Override
    public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws AssetException {
        if (assetGroup == null) {
            throw new InputArgumentException("Cannot create asset group because the group is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

        assetGroup.setName(username);
        AssetGroup createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroup);
        return createdAssetGroupEntity;
    }


    @Override
    public AssetGroup deleteAssetGroupById(UUID guid, String username) throws AssetException {

        if (guid == null) {
            throw new InputArgumentException("Cannot delete asset group because the group ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

        try {
            AssetGroup groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
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