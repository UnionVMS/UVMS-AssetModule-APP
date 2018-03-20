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
     * create assetGroup
     *
     * @param assetGroup
     * @param username
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws InputArgumentException {
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

    /**
     * update assetgroup
     *
     * @param assetGroup
     * @param username
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws InputArgumentException {

        if (assetGroup == null || assetGroup.getId() == null) {
            throw new InputArgumentException("Cannot update asset group because group or ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

        AssetGroup fetchedAssetGroup = assetGroupDao.getAssetGroupByGuid(assetGroup.getId());
        if (fetchedAssetGroup == null) {
            throw new InputArgumentException("No assetgroup found.");
        }
        assetGroup.setUpdatedBy(username);
        assetGroup.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
        AssetGroup changedAssetGroup = assetGroupDao.updateAssetGroup(assetGroup);
        return changedAssetGroup;
    }

    /**
     * get an assetgroup via its UUID
     *
     * @param guid
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup getAssetGroupById(UUID guid) throws InputArgumentException {

        if (guid == null) {
            throw new InputArgumentException("Cannot get asset group because ID is null.");
        }

        AssetGroup groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
        return groupEntity;
    }

    /**
     * delete assetGroup  (set it ti archived NO physical delete)
     *
     * @param guid
     * @param username
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup deleteAssetGroupById(UUID guid, String username) throws InputArgumentException {

        if (guid == null) {
            throw new InputArgumentException("Cannot delete asset group because the group ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

            AssetGroup groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
            if (groupEntity == null) {
                throw new InputArgumentException("No assetgroup found.");
            }
            groupEntity.setArchived(true);
            groupEntity.setUpdatedBy(username);
            groupEntity.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
            return groupEntity;
    }

    /**
     * get all assetgroups for an asset with specified User
     *
     * @param user
     * @return
     * @throws InputArgumentException
     */
    @Override
    public List<AssetGroup> getAssetGroupList(String user) throws InputArgumentException {

        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }

        List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupByUser(user);
        return filterGroupList;
    }


    /**
     * get all assetgroups for an asset with specified Id
     *
     * @param assetId
     * @return
     * @throws InputArgumentException
     */
    @Override
    public List<AssetGroup> getAssetGroupListByAssetId(UUID assetId) throws InputArgumentException {

        // TODO maybe this could be done more efficient if search is from the other side and joining . . . .

        if (assetId == null) {
            throw new InputArgumentException("Invalid asset");
        }

        List<AssetGroup> searchResultList = new ArrayList<>();
        List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupAll();
        for (AssetGroup group : filterGroupList) {
            List<AssetGroupField> fields = assetGroupFieldDao.retrieveFieldsForGroup(group);
            for (AssetGroupField field : fields) {
                if ("GUID".equals(field.getField()) && assetId.toString().equals(field.getValue())) {
                    searchResultList.add(group);
                }
            }
        }
        return searchResultList;
    }

    /**
     * create assetGroupField  assetGroup MUST exist before
     *
     * @param parentAssetgroup
     * @param assetGroupField
     * @param username
     * @return
     * @throws InputArgumentException
     */
    @Override
    public AssetGroupField createAssetGroupField(AssetGroup parentAssetgroup, AssetGroupField assetGroupField, String username) throws InputArgumentException {

        if (parentAssetgroup == null) {
            throw new InputArgumentException("Cannot create assetGroupField because the assetGroup is null.");
        }
        if (assetGroupField == null) {
            throw new InputArgumentException("Cannot create assetGroupField because the assetGroupField is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

        AssetGroup parentAssetGroup = assetGroupDao.getAssetGroupByGuid(parentAssetgroup.getId());
        if (parentAssetGroup == null) {
            throw new InputArgumentException("Assetgroup with id does not exist " + parentAssetgroup.toString());
        }

        assetGroupField.setAssetGroup(parentAssetgroup);
        assetGroupField.setUpdatedBy(username);
        assetGroupField.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
        AssetGroupField createdAssetGroupField = assetGroupFieldDao.create(assetGroupField);
        return createdAssetGroupField;
    }

    /**
     * update assetgroup
     *
     * @param assetGroupField
     * @param username
     * @return
     * @throws InputArgumentException
     */
    @Override
    public AssetGroupField updateAssetGroupField(AssetGroupField assetGroupField, String username) throws InputArgumentException {

        if (assetGroupField == null) {
            throw new InputArgumentException("Cannot update assetGroupField because assetField is invalid.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }
        AssetGroupField fetchedField = assetGroupFieldDao.get(assetGroupField.getId());
        if (fetchedField == null) {
            throw new InputArgumentException("AssetGroupField does not exist " + assetGroupField.getId().toString());
        }

        assetGroupField.setUpdatedBy(username);
        assetGroupField.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
        AssetGroupField updatedAssetGroupField = assetGroupFieldDao.update(assetGroupField);
        return updatedAssetGroupField;

    }

    /**
     * get an assetgroup via its Id
     *
     * @param id
     * @return
     * @throws InputArgumentException
     */
    @Override
    public AssetGroupField getAssetGroupField(UUID id) throws InputArgumentException {

        if (id == null) {
            throw new InputArgumentException("Cannot get assetGroupField because ID is null.");
        }

        AssetGroupField assetGroupField = assetGroupFieldDao.get(id);
        return assetGroupField;
    }

    /**
     * delete assetGroupField
     *
     * @param id
     * @param username
     * @return
     * @throws InputArgumentException
     */
    @Override
    public AssetGroupField deleteAssetGroupField(UUID id, String username) throws InputArgumentException {

        if (id == null) {
            throw new InputArgumentException("Cannot delete assetGroupId because ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new InputArgumentException("Username must be provided for selected operation");
        }

        AssetGroupField fetchedAssetGroupField = assetGroupFieldDao.get(id);
        if (fetchedAssetGroupField == null) {
            return null;
        }

        AssetGroupField groupField = assetGroupFieldDao.delete(fetchedAssetGroupField);
        return groupField;
    }

    @Override
    public List<AssetGroupField> retrieveFieldsForGroup(AssetGroup assetGroup) throws InputArgumentException {

        if (assetGroup == null) {
            throw new InputArgumentException("Cannot retrieve list for group because assetGroup is null.");
        }

        List<AssetGroupField> fetchedAssetGroupFieldList = assetGroupFieldDao.retrieveFieldsForGroup(assetGroup);
        if (fetchedAssetGroupFieldList == null) {
            return null;
        }

        return fetchedAssetGroupFieldList;
    }

    @Override
    public void removeFieldsForGroup(AssetGroup assetGroup) throws InputArgumentException {

        if (assetGroup == null) {
            throw new InputArgumentException("Cannot retrieve list for group because assetGroup is null.");
        }

        assetGroupFieldDao.removeFieldsForGroup(assetGroup);
    }


}