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
package eu.europa.ec.fisheries.uvms.constant;

public class UvmsConstants {

    public static final String ASSET_FIND_ALL = "Asset.findAll";
    public static final String ASSET_FIND_BY_ID = "Asset.findById";
    public static final String ASSET_FIND_BY_CFR = "Asset.findByCfr";
    public static final String ASSET_FIND_BY_IRCS = "Asset.findByIrcs";
    public static final String ASSET_FIND_BY_GUID = "Asset.findByGuid";
    public static final String ASSET_FIND_BY_IMO = "Asset.findByImo";
	public static final String ASSET_FIND_BY_MMSI = "Asset.findByMMSI";
    public static final String ASSETHISTORY_FIND_BY_GUIDS = "Asset.findByGuids";
    public static final String ASSET_FIND_BY_ICCAT = "Asset.findByIccat";
    public static final String ASSET_FIND_BY_UVI = "Asset.findByUvi";
    public static final String ASSET_FIND_BY_GFCM = "Asset.findByGfcm";

    public static final String ASSETHISTORY_FIND_BY_GUID = "Assethistory.findByGuid";
    public static final String ASSETHISTORY_FIND_BY_CRITERIA = "Assethistory.findByCriteria";

    public static final String GROUP_ASSET_FIND_ALL = "AssetGroup.findAll";
    public static final String GROUP_ASSET_BY_USER = "AssetGroup.findByUser";
    public static final String GROUP_ASSET_BY_GUID = "AssetGroup.findByGuid";
    public static final String GROUP_ASSET_BY_GUID_LIST = "AssetGroup.findByGuidList";
    
    public static final String LICENSE_TYPE_LIST = "LicenseType.findAll";
	public static final String FLAG_STATE_LIST = "FlagState.findAll";
	public static final String SETTING_LIST = "Setting.findAll";
	public static final String SETTING_BY_FIELD = "Setting.findByField";
    
    public static final String QUEUE_DOMAIN_MODEL = "jms/queue/UVMSAssetModel";
    public static final String QUEUE_NAME_DOMAIN_MODEL = "UVMSAssetModel";

    public static final String VESSEL_CONNECTION_FACTORY = "java:jboss/DefaultJMSConnectionFactory";
    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";
    public static final String CONNECTION_FACTORY = "ConnectionFactory";

    public static final String ASSET_FIND_BY_CFR_EXCLUDE_ARCHIVED = "Asset.findByCfrExcludeArchived";
    public static final String ASSET_FIND_BY_IRCS_EXCLUDE_ARCHIVED = "Asset.findByIrcsExcludeArchived";
    public static final String ASSET_FIND_BY_IMO_EXCLUDE_ARCHIVED = "Asset.findByImoExcludeArchived";
    public static final String ASSET_FIND_BY_MMSI_EXCLUDE_ARCHIVED = "Asset.findByMMSIExcludeArchived";

    public static final String SPACE = " ";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    
	public static final String UPDATE_USER = "JPA";

    public static final String FISHING_GEAR_TYPE_FIND_ALL="FishingGearType.findAll";
    public static final String FISHING_GEAR_TYPE_FIND_BY_CODE="FishingGearType.findByCode";

    public static final String FISHING_GEAR_FIND_ALL = "Fishinggear.findAll";
    public static final String FISHING_GEAR_FIND_BY_ID = "Fishinggear.findById";
    public static final String FISHING_GEAR_FIND_BY_EXT_ID = "Fishinggear.findByExternalId";

    public static final String ASSET_NOTE_ACTIVITY_CODE_FIND_ALL = "AssetNoteActivity.findAll";

    public static final String FLAGSTATE_GET_BY_CODE = "FlagState.getByCode";
    public static final String ASSETHISTORY_FIND_BY_GUID_AND_DATE = "Assethistory.findByGuidAndDate";
}