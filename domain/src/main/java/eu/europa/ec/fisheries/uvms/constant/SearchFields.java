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

import eu.europa.ec.fisheries.uvms.mapper.SearchFieldType;


/**
 **/
public enum SearchFields {

    FLAG_STATE("countryOfRegistration", SearchTables.ASSET_HIST, "flagState"),
    EXTERNAL_MARKING("externalMarking", SearchTables.ASSET_HIST, "extMarking"),
    NAME("name", SearchTables.ASSET_HIST, "name"),
    IRCS("ircs", SearchTables.ASSET, "ircs"),
    CFR("cfr", SearchTables.ASSET, "cfr"),
    MMSI("mmsi", SearchTables.ASSET, "mmsi", SearchFieldType.NUMBER),
    IMO("imo", SearchTables.ASSET, "imo", SearchFieldType.NUMBER),
    HOMEPORT("portOfRegistration", SearchTables.ASSET_HIST, "homeport"),
    LICENSE("licenceType", SearchTables.ASSET_HIST, "license"),
    GUID("guid", SearchTables.ASSET, "guid"),
    HIST_GUID("guid", SearchTables.ASSET_HIST, "histGuid"),
    GEAR_TYPE("mainFishingGear.code", SearchTables.ASSET_HIST, "mainFishingGear", SearchFieldType.STRING),
    MAX_LENGTH("lengthOverAll", SearchTables.ASSET_HIST, "maxLength", SearchFieldType.MAX_DECIMAL),
    MIN_LENGTH("lengthOverAll", SearchTables.ASSET_HIST, "minLength", SearchFieldType.MIN_DECIMAL),
    MAX_POWER("powerOfMainEngine", SearchTables.ASSET_HIST, "maxPower", SearchFieldType.MAX_DECIMAL),
    MIN_POWER("powerOfMainEngine", SearchTables.ASSET_HIST, "minPower", SearchFieldType.MIN_DECIMAL),
//    PRODUCER_NAME("producerName", SearchTables.ASSET_HIST, "producerName"),

    // maybe in history instead history should be renamed to version
    ICCAT("iccat", SearchTables.ASSET, "iccat"),
    UVI("uvi", SearchTables.ASSET_HIST, "uvi"),
    GFCM("gfcm", SearchTables.ASSET, "gfcm");


    private final String fieldName;
    private final SearchTables searchTables;
    private String valueName;
    private SearchFieldType fieldType;
    
    private SearchFields(String fieldName, SearchTables searchTables, String valueName) {
        this.fieldName = fieldName;
        this.searchTables = searchTables;
        this.valueName = valueName;
        this.fieldType = SearchFieldType.LIST;
    }

    private SearchFields(String fieldName, SearchTables searchTables, String valueName, SearchFieldType fieldType) {
        this.fieldName = fieldName;
        this.searchTables = searchTables;
        this.valueName = valueName;
        this.fieldType = fieldType;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public SearchTables getSearchTable() {
        return searchTables;
    }
    
    public String getValueName() {
		return valueName;
	}
	
    public SearchFieldType getFieldType() {
    	return fieldType;
    }
}