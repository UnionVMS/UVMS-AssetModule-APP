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
package eu.europa.ec.fisheries.uvms.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.uvms.mapper.AssetGroupMapper;
import eu.europa.ec.fisheries.wsdl.asset.group.ZeroBasedIndexListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class AssetGroupDomainModelBean  {

    @EJB
    private AssetGroupDao assetGroupDao;

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupDomainModelBean.class);

    public eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup getAssetGroup(String guid) throws AssetModelException {
        if (guid == null) {
            throw new InputArgumentException("Cannot get asset group because ID is null.");
        }

        try {
            AssetGroup groupEntity = getAssetGroupById(guid);
            return AssetGroupMapper.toAssetGroup(groupEntity);
        } catch (AssetGroupDaoException e) {
            throw new AssetModelException("Error when getting asset group guid: "+guid+" "+ e.getMessage(),e);
        }
    }

    private AssetGroup getAssetGroupById(String guid) throws AssetGroupDaoException {
    	AssetGroup filterGroup = assetGroupDao.getAssetGroupByGuid(guid);
        if (filterGroup == null) {
            throw new AssetGroupDaoException("No assetgroup found.");
        }

        return filterGroup;
    }

    public eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup createAssetGroup(eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup assetGroup, String username) throws AssetModelException {
        if (assetGroup == null) {
            throw new InputArgumentException("Cannot create asset group because the group is null.");
        }

        try {
            AssetGroup groupEntity = AssetGroupMapper.toGroupEntity(assetGroup, username);
            assetGroupDao.createAssetGroup(groupEntity);
            return AssetGroupMapper.toAssetGroup(groupEntity);
        } catch (AssetGroupDaoException | AssetDaoMappingException e) {
            throw new AssetModelException("Error when creating asset group assetGroup: "+assetGroup +"username: "+ username +e.getMessage(),e);
        }
    }

    public eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup updateAssetGroup(eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup assetGroup, String username) throws AssetModelException {
        if (assetGroup == null || assetGroup.getGuid() == null) {
            throw new InputArgumentException("Cannot update asset group because group or ID is null.");
        }

        try {
        	AssetGroup groupEntity = getAssetGroupById(assetGroup.getGuid());
            groupEntity = AssetGroupMapper.toGroupEntity(groupEntity, assetGroup, username);
            return AssetGroupMapper.toAssetGroup(groupEntity);
        } catch (AssetGroupDaoException | AssetDaoMappingException e) {
            throw new AssetModelException("Error when updating asset group assetGroup:"+assetGroup+" username: "+username +e.getMessage(),e);
        }
    }

    public List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> getAssetGroupsByAssetGuid(String assetGuid) throws AssetModelException {
        if (assetGuid == null) {
            throw new InputArgumentException("Cannot get asset group list because the vesselGuid is null.");
        }

        try {
            List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> vesselGroupList = new ArrayList<>();
            List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupAll();
            for (AssetGroup group : filterGroupList) {
                List<AssetGroupField> fields = group.getFields();
                for (AssetGroupField field : fields) {
                    if ("GUID".equals(field.getField()) && assetGuid.equals(field.getValue())) {
                        vesselGroupList.add(AssetGroupMapper.toAssetGroup(group));
                    }
                }
            }
            return vesselGroupList;
        } catch (AssetGroupDaoException e) {
            throw new AssetModelException("Error when getting asset group list by assetGuid: "+assetGuid+e.getMessage(),e);
        }
    }

    public ZeroBasedIndexListAssetGroupResponse getZeroBasedAssetGroupListByUser(String user, AssetListQuery assetQuery) throws AssetGroupDaoException, InputArgumentException {
        if (user == null) {
            throw new InputArgumentException("Cannot get asset group list because the user is null.");
        }

        int page = assetQuery.getPagination().getPage();
        int listSize = assetQuery.getPagination().getListSize();
        List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupByUserPaginated(user,page,listSize);
        Long numberOfAssets = assetGroupDao.getAssetGroupByUserCount(user);
        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int)(numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }
        ZeroBasedIndexListAssetGroupResponse assetGroupResponse = new ZeroBasedIndexListAssetGroupResponse();
        assetGroupResponse.setTotalNumberOfPages(numberOfPages);
        assetGroupResponse.setCurrentPage(page);
        assetGroupResponse.setTotalResults(numberOfAssets);

        filterGroupList.stream()
                .map(AssetGroupMapper::toAssetGroup)
                .forEach(assetGroupResponse.getAssetGroup()::add);

        return assetGroupResponse;
    }

    public List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> getAssetGroupListByUser(String user) throws AssetModelException {
        if (user == null) {
            throw new InputArgumentException("Cannot get asset group list because the user is null.");
        }

        try {
            List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> assetGroupList = new ArrayList<>();
            List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupByUser(user);
            for (AssetGroup group : filterGroupList) {
                assetGroupList.add(AssetGroupMapper.toAssetGroup(group));
            }

            return assetGroupList;
        } catch (AssetGroupDaoException e) {
            throw new AssetModelException("Error when getting asset group list by user: "+user +e.getMessage(),e);
        }
    }

    public eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup deleteAssetGroup(String guid, String username) throws AssetModelException {
        if (guid == null) {
            throw new InputArgumentException("Cannot delete asset group because the group ID is null.");
        }

        try {
            AssetGroup groupEntity = getAssetGroupById(guid);
            groupEntity.setArchived(true);
            groupEntity.setUpdatedBy(username);
            groupEntity.setUpdateTime(DateUtils.getNowDateUTC());
            return AssetGroupMapper.toAssetGroup(groupEntity);
        } catch (AssetGroupDaoException e) {
            throw new AssetModelException("Error when deleting asset group guid: "+ guid+" username: "+username + e.getMessage(),e);
        }
    }

	public List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> getAssetGroupsByGroupList(List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> groups) throws AssetModelException {
		if (groups == null) {
            throw new InputArgumentException("Cannot get asset group list because the input is null.");
        }

		List<String> guidList = new ArrayList<>();
		for(eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup group : groups) {
			guidList.add(group.getGuid());
		}
		
		if(guidList.isEmpty()) {
			throw new InputArgumentException("Cannot get asset group list because the input missing guid.");
		}
		
        try {
            List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> vesselGroupList = new ArrayList<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup>();
            List<AssetGroup> filterGroupList = assetGroupDao.getAssetGroupsByGroupGuidList(guidList);
            for (AssetGroup group : filterGroupList) {
                vesselGroupList.add(AssetGroupMapper.toAssetGroup(group));
            }

            return vesselGroupList;
        } catch (AssetGroupDaoException e) {
            throw new AssetModelException("Error when getting asset group list by List.groups: "+ groups+e.getMessage(),e);
        }
	}

}

