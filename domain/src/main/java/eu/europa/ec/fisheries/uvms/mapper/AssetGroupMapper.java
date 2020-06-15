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
package eu.europa.ec.fisheries.uvms.mapper;

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssetGroupMapper {

    public static AssetGroup toGroupEntity(eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup group, String username) throws AssetDaoMappingException {
    	AssetGroup groupEntity = new AssetGroup();
        toGroupEntity(groupEntity, group, username);
        return groupEntity;
    }

    private static AssetGroupField toFilterEntity(AssetGroup parent, eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField searchField, String username) {
    	AssetGroupField filter = new AssetGroupField();
        filter.setAssetGroup(parent);

        filter.setField(searchField.getKey().name());
        filter.setValue(searchField.getValue());

        filter.setUpdateTime(DateUtils.getNowDateUTC());
        filter.setUpdatedBy(username);
        return filter;
    }

    public static eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup toAssetGroup(AssetGroup groupEntity) {
        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup group = new eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup();
        group.setGuid(groupEntity.getGuid());
        group.setDynamic(groupEntity.getDynamic());
        group.setName(groupEntity.getName());
        group.setUser(groupEntity.getOwner());

        List<AssetGroupField> filterList = groupEntity.getFields();
        for (AssetGroupField filter : filterList) {
            eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField searchField = toSearchField(filter);
            group.getSearchFields().add(searchField);
        }
        return group;
    }

    public static List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField> generateSearchFields(AssetGroup groupEntity){
        return groupEntity.getFields().stream().map(f -> toSearchField(f)).collect(Collectors.toList());
    }

    private static eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField toSearchField(AssetGroupField field) {
        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField searchField = new eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField();
        searchField.setKey(ConfigSearchField.fromValue(field.getField()));
        searchField.setValue(field.getValue());
        return searchField;
    }

    public static AssetGroup toGroupEntity(AssetGroup groupEntity, eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup assetGroup, String username) throws AssetDaoMappingException {
        groupEntity.setGlobal(false);
        groupEntity.setArchived(false);
        groupEntity.setDynamic(assetGroup.isDynamic());

        groupEntity.setUpdateTime(DateUtils.getNowDateUTC());
        groupEntity.setUpdatedBy(username);

        if (assetGroup.getName() == null) {
            throw new AssetDaoMappingException("VesselGroupName cannot be null");
        }
        groupEntity.setName(assetGroup.getName());

        groupEntity.setOwner(assetGroup.getUser());

        //Remove old filters
        for(AssetGroupField oldField : groupEntity.getFields()) {
        	oldField.setAssetGroup(null);
        }
        groupEntity.getFields().clear();

        //Add new filters
        List<AssetGroupField> newFilters = new ArrayList<>();
        List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField> searchFieldList = assetGroup.getSearchFields();
        if (searchFieldList != null) {
            for (AssetGroupSearchField searchField : searchFieldList) {
                newFilters.add(toFilterEntity(groupEntity, searchField, username));
            }
        }
        groupEntity.getFields().addAll(newFilters);

        return groupEntity;
    }
}