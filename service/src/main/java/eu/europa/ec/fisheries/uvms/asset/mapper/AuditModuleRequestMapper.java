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
package eu.europa.ec.fisheries.uvms.asset.mapper;

import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogModelMapper;

public class AuditModuleRequestMapper {

    private static final String ASSET = "Asset";
    private static final String ASSET_GROUP = "Asset Group";
    private static final String CREATE = "Create";
    private static final String UPDATE = "Update";
    private static final String ARCHIVE = "Archive";
    private static final String UNARCHIVE = "Unarchive";

    private AuditModuleRequestMapper() {}
    
    public static String mapAuditLogAssetCreated(String guid, String username)  {
        return mapToAuditLog(ASSET, CREATE, guid, username);
    }

    public static String mapAuditLogAssetUpdated(String guid, String comment, String username)  {
        return AuditLogModelMapper.mapToAuditLog(ASSET, UPDATE, guid, comment, username);
    }

    public static String mapAuditLogAssetArchived(String guid, String comment, String username)  {
        return AuditLogModelMapper.mapToAuditLog(ASSET, ARCHIVE, guid, comment, username);
    }

    public static String mapAuditLogAssetUnarchived(String guid, String comment, String username) {
        return AuditLogModelMapper.mapToAuditLog(ASSET, UNARCHIVE, guid, comment, username);
    }

    public static String mapAuditLogAssetGroupCreated(String guid, String username,String name)  {
        return mapToAuditLog(ASSET_GROUP, CREATE, guid, username, name);
    }

    public static String mapAuditLogAssetGroupUpdated(String guid, String username, String name)  {
        return mapToAuditLog(ASSET_GROUP, UPDATE, guid, username,name);
    }

    public static String mapAuditLogAssetGroupDeleted(String guid, String username, String name)  {
        return mapToAuditLog(ASSET_GROUP, ARCHIVE, guid, username, name);
    }

    private static String mapToAuditLog(String objectType, String operation, String affectedObject, String username)  {
        return AuditLogModelMapper.mapToAuditLog(objectType, operation, affectedObject, username);
    }
    private static String mapToAuditLog(String objectType, String operation, String affectedObject, String username,String name) {
        return AuditLogModelMapper.mapToAuditLog(objectType, operation, affectedObject, name, username);
    }
}
