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
package eu.europa.ec.fisheries.uvms.asset.bean;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;

@Stateless
public class AssetGroupServiceBean implements AssetGroupService {

    @Inject
    private AssetGroupDao assetGroupDao;

    @Inject
    private AssetGroupFieldDao assetGroupFieldDao;

    @Override
    public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) {
        if (assetGroup == null) {
            throw new NullPointerException("Cannot create asset group because the group is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }
        assetGroup.setOwner(username);
        assetGroup.setUpdatedBy(username);
        assetGroup.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return assetGroupDao.createAssetGroup(assetGroup);
    }

    @Override
    public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) {
        if (assetGroup == null || assetGroup.getId() == null) {
            throw new NullPointerException("Cannot update asset group because group or ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }

        AssetGroup fetchedAssetGroup = assetGroupDao.getAssetGroupByGuid(assetGroup.getId());
        if (fetchedAssetGroup == null) {
            throw new NullPointerException("No assetGroup found.");
        }
        assetGroup.setUpdatedBy(username);
        assetGroup.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return assetGroupDao.updateAssetGroup(assetGroup);
    }

    @Override
    public AssetGroup getAssetGroupById(UUID guid)  {
        if (guid == null) {
            throw new NullPointerException("Cannot get asset group because ID is null.");
        }
        return assetGroupDao.getAssetGroupByGuid(guid);
    }

    /**
     * delete assetGroup  (set it to archived NO physical delete)
     *
     * @param guid
     * @param username
     * @return AssetGroup
     */
    @Override
    public AssetGroup deleteAssetGroupById(UUID guid, String username)  {
        if (guid == null) {
            throw new NullPointerException("Cannot delete asset group because the group ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }
        AssetGroup groupEntity = assetGroupDao.getAssetGroupByGuid(guid);
        if (groupEntity == null) {
            throw new NullPointerException("No assetgroup found.");
        }
        groupEntity.setArchived(true);
        groupEntity.setUpdatedBy(username);
        groupEntity.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return groupEntity;
    }

    @Override
    public List<AssetGroup> getAssetGroupList(String user)  {
        if (user == null || user.trim().isEmpty()) {
            throw new NullPointerException("Invalid user");
        }
        return assetGroupDao.getAssetGroupByUser(user);
    }

    @Override
    public List<AssetGroup> getAssetGroupListByAssetId(UUID assetId)  {
        // TODO maybe this could be done more efficient if search is from the other side and joining . . . .
        if (assetId == null) {
            throw new NullPointerException("Invalid asset");
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
     * @return AssetGroupField
     */
    @Override
    public AssetGroupField createAssetGroupField(UUID parentAssetGroupId, AssetGroupField assetGroupField, String username)  {
        if (parentAssetGroupId == null) {
            throw new NullPointerException("Cannot create AssetGroupField because the AssetGroup ID is Null");
        }
        if (assetGroupField == null) {
            throw new NullPointerException("Cannot create AssetGroupField because the AssetGroupField is Null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }
        AssetGroup parentAssetGroup = assetGroupDao.getAssetGroupByGuid(parentAssetGroupId);
        if (parentAssetGroup == null) {
            throw new NullPointerException("AssetGroup with ID: " + parentAssetGroupId + " does not exist");
        }

        assetGroupField.setAssetGroup(parentAssetGroup);
        assetGroupField.setUpdatedBy(username);
        assetGroupField.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return assetGroupFieldDao.create(assetGroupField);
    }

    @Override
    public AssetGroupField updateAssetGroupField(AssetGroupField assetGroupField, String username)  {
        if (assetGroupField == null) {
            throw new NullPointerException("Cannot update assetGroupField because assetField is invalid.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }
        AssetGroupField fetchedField = assetGroupFieldDao.get(assetGroupField.getId());
        if (fetchedField == null) {
            throw new NullPointerException("AssetGroupField does not exist " + assetGroupField.getId().toString());
        }
        assetGroupField.setUpdatedBy(username);
        assetGroupField.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return assetGroupFieldDao.update(assetGroupField);
    }

    @Override
    public AssetGroupField getAssetGroupField(UUID id) {
        if (id == null) {
            throw new NullPointerException("Cannot get assetGroupField because ID is null.");
        }
        return assetGroupFieldDao.get(id);
    }

    @Override
    public AssetGroupField deleteAssetGroupField(UUID id, String username)  {
        if (id == null) {
            throw new NullPointerException("Cannot delete assetGroupId because ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
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
            throw new NullPointerException("Cannot retrieve list for group because assetGroup is null.");
        }
        AssetGroup assetGroup = assetGroupDao.getAssetGroupByGuid(assetGroupId);
        if (assetGroup == null) {
            throw new NullPointerException("Cannot retrieve list for group because assetGroup does not exist.");
        }
        return assetGroupFieldDao.retrieveFieldsForGroup(assetGroup.getId());
    }

    @Override
    public void removeFieldsForGroup(UUID assetGroupId)  {
        if (assetGroupId == null) {
            throw new NullPointerException("Cannot retrieve list for group because assetGroup is null.");
        }
        AssetGroup assetGroup = assetGroupDao.getAssetGroupByGuid(assetGroupId);
        if (assetGroup == null) {
            throw new NullPointerException("Cannot retrieve list for group because assetGroup does not exist.");
        }
        assetGroupFieldDao.removeFieldsForGroup(assetGroup.getId());
    }
}
