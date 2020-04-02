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
package eu.europa.ec.fisheries.uvms.asset.client.model.search;

public enum SearchFields {

    FLAG_STATE("flagStateCode"),
    EXTERNAL_MARKING("externalMarking", true),
    NAME("name"),
    IRCS("ircs", true),
    CFR("cfr"),
    MMSI("mmsi"),
    IMO("imo"),
    ICCAT("iccat"),
    UVI("uvi"),
    GFCM("gfcm"),
    HOMEPORT("portOfRegistration"),
    LICENSE("licenceType"),
    VESSEL_TYPE("vesselType"),
    GUID("id", SearchFieldType.ID),
    HIST_GUID("historyId", SearchFieldType.ID),
    GEAR_TYPE("gearFishingType", SearchFieldType.STRING),
    MAX_LENGTH("lengthOverAll", SearchFieldType.MAX_DECIMAL),
    MIN_LENGTH("lengthOverAll", SearchFieldType.MIN_DECIMAL),
    MAX_POWER("powerOfMainEngine", SearchFieldType.MAX_DECIMAL),
    MIN_POWER("powerOfMainEngine", SearchFieldType.MIN_DECIMAL),
    PRODUCER_NAME("producerName"),
    DATE(null, SearchFieldType.DATE);

    private String fieldName;
    private SearchFieldType fieldType;
    private boolean fuzzySearch;
    
    private SearchFields(String fieldName) {
        this.fieldName = fieldName;
        this.fieldType = SearchFieldType.LIST;
        this.fuzzySearch = false;
    }

    private SearchFields(String fieldName, SearchFieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fuzzySearch = false;
    }
    
    private SearchFields(String fieldName, boolean fuzzySearch) {
        this.fieldName = fieldName;
        this.fuzzySearch = fuzzySearch;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public SearchFieldType getFieldType() {
    	return fieldType;
    }

	public boolean isFuzzySearch() {
		return fuzzySearch;
	}
}