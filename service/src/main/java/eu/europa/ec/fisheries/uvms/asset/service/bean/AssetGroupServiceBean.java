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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.AssetGroupField;

@Stateless
public class AssetGroupServiceBean implements AssetGroupService {

    @Inject
    private AssetGroupDao assetGroupDao;

    @Inject
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
    public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) {
        if (assetGroup == null) {
            throw new IllegalArgumentException("Cannot create asset group because the group is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be provided for selected operation");
        }

        assetGroup.setOwner(username);
        assetGroup.setUpdatedBy(username);
        assetGroup.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return assetGroupDao.createAssetGroup(assetGroup);
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
    public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) {

        if (assetGroup == null || assetGroup.getId() == null) {
            throw new IllegalArgumentException("Cannot update asset group because group or ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be provided for selected operation");
        }

        AssetGroup fetchedAssetGroup = assetGroupDao.getAssetGroupByGuid(assetGroup.getId());
        if (fetchedAssetGroup == null) {
            throw new IllegalArgumentException("No assetgroup found.");
        }
        assetGroup.setUpdatedBy(username);
        assetGroup.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return assetGroupDao.updateAssetGroup(assetGroup);
    }

    /**
     * get an assetgroup via its UUID
     *
     * @param guid
     * @return
     * @throws AssetException
     */
    @Override
    public AssetGroup getAssetGroupById(UUID guid)  {

        if (guid == null) {
            throw new IllegalArgumentException("Cannot get asset group because ID is null.");
        }

        return assetGroupDao.getAssetGroupByGuid(guid);
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
    public AssetGroup deleteAssetGroupById(UUID guid, String username)  {

        if (guid == null) {
            throw new IllegalArgumentException("Cannot delete asset group because the group ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be provided for selected operation");
        }

            AssetGroup groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
            if (groupEntity == null) {
                throw new IllegalArgumentException("No assetgroup found.");
            }
            groupEntity.setArchived(true);
            groupEntity.setUpdatedBy(username);
            groupEntity.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
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
    public List<AssetGroup> getAssetGroupList(String user)  {

        if (user == null || user.isEmpty()) {
            throw new IllegalArgumentException("Invalid user");
        }

        return assetGroupDao.getAssetGroupByUser(user);
    }


    /**
     * get all assetgroups for an asset with specified Id
     *
     * @param assetId
     * @return
     * @throws InputArgumentException
     */
    @Override
    public List<AssetGroup> getAssetGroupListByAssetId(UUID assetId)  {

        // TODO maybe this could be done more efficient if search is from the other side and joining . . . .

        if (assetId == null) {
            throw new IllegalArgumentException("Invalid asset");
        }

        List<AssetGroup> searchResultList = new ArrayList<>();
        List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupAll();
        for (AssetGroup group : filterGroupList) {
            List<AssetGroupField> fields = assetGroupFieldDao.retrieveFieldsForGroup(group.getId());
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
     * @param parentAssetGroupId
     * @param assetGroupField
     * @param username
     * @return
     * @throws InputArgumentException
     */
    @Override
    public AssetGroupField createAssetGroupField(UUID parentAssetGroupId, AssetGroupField assetGroupField, String username)  {

        if (parentAssetGroupId == null) {
            throw new IllegalArgumentException("Cannot create assetGroupField because the assetGroup is null.");
        }
        if (assetGroupField == null) {
            throw new IllegalArgumentException("Cannot create assetGroupField because the assetGroupField is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be provided for selected operation");
        }

        AssetGroup parentAssetGroup = assetGroupDao.getAssetGroupByGuid(parentAssetGroupId);
        if (parentAssetGroup == null) {
            throw new IllegalArgumentException("Assetgroup with id does not exist " + parentAssetGroupId);
        }

        assetGroupField.setAssetGroup(parentAssetGroup.getId());
        assetGroupField.setUpdatedBy(username);
        assetGroupField.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return assetGroupFieldDao.create(assetGroupField);
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
    public AssetGroupField updateAssetGroupField(AssetGroupField assetGroupField, String username)  {

        if (assetGroupField == null) {
            throw new IllegalArgumentException("Cannot update assetGroupField because assetField is invalid.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be provided for selected operation");
        }
        AssetGroupField fetchedField = assetGroupFieldDao.get(assetGroupField.getId());
        if (fetchedField == null) {
            throw new IllegalArgumentException("AssetGroupField does not exist " + assetGroupField.getId().toString());
        }

        assetGroupField.setUpdatedBy(username);
        assetGroupField.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return assetGroupFieldDao.update(assetGroupField);
    }

    /**
     * get an assetgroup via its Id
     *
     * @param id
     * @return
     * @throws InputArgumentException
     */
    @Override
    public AssetGroupField getAssetGroupField(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("Cannot get assetGroupField because ID is null.");
        }

        return assetGroupFieldDao.get(id);
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
    public AssetGroupField deleteAssetGroupField(UUID id, String username)  {

        if (id == null) {
            throw new IllegalArgumentException("Cannot delete assetGroupId because ID is null.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be provided for selected operation");
        }

        AssetGroupField fetchedAssetGroupField = assetGroupFieldDao.get(id);
        if (fetchedAssetGroupField == null) {
            return null;
        }

        return assetGroupFieldDao.delete(fetchedAssetGroupField);
    }

    @Override
    public List<AssetGroupField> retrieveFieldsForGroup(UUID assetGroupId)  {

        if (assetGroupId == null) {
            throw new IllegalArgumentException("Cannot retrieve list for group because assetGroup is null.");
        }

        AssetGroup assetGroup = assetGroupDao.getAssetGroupByGuid(assetGroupId);
        if (assetGroup == null) {
            throw new IllegalArgumentException("Cannot retrieve list for group because assetGroup does not exist.");
        }

        return assetGroupFieldDao.retrieveFieldsForGroup(assetGroup.getId());
    }

    @Override
    public void removeFieldsForGroup(UUID assetGroupId)  {

        if (assetGroupId == null) {
            throw new IllegalArgumentException("Cannot retrieve list for group because assetGroup is null.");
        }
        AssetGroup assetGroup = assetGroupDao.getAssetGroupByGuid(assetGroupId);
        if (assetGroup == null) {
            throw new IllegalArgumentException("Cannot retrieve list for group because assetGroup does not exist.");
        }

        assetGroupFieldDao.removeFieldsForGroup(assetGroup.getId());
    }


}