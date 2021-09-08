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
package eu.europa.ec.fisheries.uvms.asset.message.event;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

public class SearchFieldMapper {
    
    private SearchFieldMapper() {}

    private static SearchKeyValue getSearchKeyValue(SearchFields field, Map<SearchFields, SearchKeyValue> searchKeys) {
        SearchKeyValue searchKeyValue = searchKeys.get(field);
        if (searchKeyValue == null) {
            searchKeyValue = new SearchKeyValue();
        }
        searchKeyValue.setSearchField(field);
        return searchKeyValue;
    }

    private static SearchFields getSearchFields(ConfigSearchField field) {
        switch (field) {
            case CFR:
                return SearchFields.CFR;
            case EXTERNAL_MARKING:
                return SearchFields.EXTERNAL_MARKING;
            case FLAG_STATE:
                return SearchFields.FLAG_STATE;
            case HOMEPORT:
                return SearchFields.HOMEPORT;
            case ICCAT:
                return SearchFields.ICCAT;
            case UVI:
                return SearchFields.UVI;
            case GFCM:
                return SearchFields.GFCM;
            case IRCS:
                return SearchFields.IRCS;
            case LICENSE_TYPE:
                return SearchFields.LICENSE;
            case MMSI:
                return SearchFields.MMSI;
            case NAME:
                return SearchFields.NAME;
            case GEAR_TYPE:
                return SearchFields.GEAR_TYPE;
            case GUID:
                return SearchFields.GUID;
            case HIST_GUID:
            	return SearchFields.HIST_GUID;
            case IMO:
                return SearchFields.IMO;
            case LENGTH_OVER_ALL:
                return SearchFields.LENGTH_OVER_ALL;
            case ENGINE_POWER:
                return SearchFields.ENGINE_POWER;
            case PRODUCER_NAME:
            	return SearchFields.PRODUCER_NAME;
            case ASSET_TYPE:
            default:
                throw new IllegalArgumentException("No field found: " + field.name());
        }
    }

    public static SearchKeyValue mapSearchFieldForAssetListCriteria(AssetListCriteriaPair pair, Map<SearchFields, SearchKeyValue> searchKeys) {
        if (pair == null || pair.getKey() == null || pair.getValue() == null) {
            throw new IllegalArgumentException("Non valid search criteria");
        }

        SearchKeyValue searchKeyValue = getSearchKeyValue(getSearchFields(pair.getKey()), searchKeys);
        String value = pair.getValue();
        searchKeyValue.getSearchValues().add(value);
        return searchKeyValue;
    }

    public static List<SearchKeyValue> createSearchFields(List<AssetListCriteriaPair> criterias) {
        Map<SearchFields, SearchKeyValue> searchKeyValues = new EnumMap<>(SearchFields.class);
        for (AssetListCriteriaPair criteria : criterias) {
            SearchKeyValue searchField = mapSearchFieldForAssetListCriteria(criteria, searchKeyValues);
            searchKeyValues.put(searchField.getSearchField(), searchField);
        }
        return new ArrayList<>(searchKeyValues.values());
    }
}