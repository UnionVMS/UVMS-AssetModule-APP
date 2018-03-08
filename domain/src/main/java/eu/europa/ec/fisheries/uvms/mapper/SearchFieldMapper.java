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

import eu.europa.ec.fisheries.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.asset.enums.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.constant.SearchTables;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetSearchMapperException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 **/
public class SearchFieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(SearchFieldMapper.class);

    private static SearchKeyValue getSearchKeyValue(SearchFields field, Map<SearchFields, SearchKeyValue> searchKeys) {
        SearchKeyValue searchKeyValue = searchKeys.get(field);
        if (searchKeyValue == null) {
            searchKeyValue = new SearchKeyValue();
        }
        searchKeyValue.setSearchField(field);
        return searchKeyValue;
    }

    private static SearchFields getSearchFields(ConfigSearchFieldEnum field) throws AssetSearchMapperException {
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
            case MAX_LENGTH:
                return SearchFields.MAX_LENGTH;
            case MAX_POWER:
                return SearchFields.MAX_POWER;
            case MIN_POWER:
                return SearchFields.MIN_POWER;
            case MIN_LENGTH:
                return SearchFields.MIN_LENGTH;
            case PRODUCER_NAME:
            	return SearchFields.PRODUCER_NAME;
            case ASSET_TYPE:
            default:
                throw new AssetSearchMapperException("No field found: " + field.name());
        }
    }

    public static SearchKeyValue mapSearchFieldForAssetListCriteria(AssetListCriteriaPair pair, Map<SearchFields, SearchKeyValue> searchKeys) throws AssetDaoMappingException {
        if (pair == null || pair.getKey() == null || pair.getValue() == null) {
            throw new AssetSearchMapperException("Non valid search criteria");
        }

        SearchKeyValue searchKeyValue = getSearchKeyValue(getSearchFields(pair.getKey()), searchKeys);
        String value = prepareSearchValue(searchKeyValue.getSearchField(), pair.getValue());
        searchKeyValue.getSearchValues().add(value);
        return searchKeyValue;
    }

    private static String prepareSearchValue(SearchFields searchField, String searchValue) throws AssetDaoMappingException {
    	String value = searchValue;
        if(SearchFields.GEAR_TYPE.equals(searchField)) {
        	try {
				value = GearFishingTypeEnum.getValue(searchValue);
			} catch (AssetModelValidationException e) {
				throw new AssetDaoMappingException("GearType couldn't be transformed to valid search value");
			}
        }
        value = value.replace("-", "");
        value = value.toUpperCase();
        return value;
    }

    /*
    public static SearchKeyValue mapSearchFieldForAssetGroupField(AssetGroupSearchField pair, Map<SearchFields, SearchKeyValue> searchKeys) throws AssetDaoMappingException {
        if (pair == null || pair.getKey() == null || pair.getValue() == null) {
            throw new AssetSearchMapperException("Non valid search criteria");
        }

        SearchKeyValue searchKeyValue = getSearchKeyValue(getSearchFields(pair.getKey()), searchKeys);
        String value = prepareSearchValue(searchKeyValue.getSearchField(), pair.getValue());
        searchKeyValue.getSearchValues().add(value);
        return searchKeyValue;
    }
    */

    public static List<SearchKeyValue> createSearchFields(List<AssetListCriteriaPair> criterias) throws AssetDaoMappingException {
        Map<SearchFields, SearchKeyValue> searchKeyValues = new HashMap<>();
        for (AssetListCriteriaPair criteria : criterias) {
            SearchKeyValue searchField = mapSearchFieldForAssetListCriteria(criteria, searchKeyValues);
            searchKeyValues.put(searchField.getSearchField(), searchField);
        }
        return new ArrayList<>(searchKeyValues.values());
    }

    /*
    public static List<SearchKeyValue> createSearchFieldsFromGroupCriterias(List<AssetGroupSearchField> criterias) throws AssetDaoMappingException {

        Map<SearchFields, SearchKeyValue> searchKeyValues = new HashMap<>();
        for (AssetGroupSearchField criteria : criterias) {
            SearchKeyValue searchField = mapSearchFieldForAssetGroupField(criteria, searchKeyValues);
            searchKeyValues.put(searchField.getSearchField(), searchField);
        }
        return new ArrayList<>(searchKeyValues.values());

    }
    */

    public static boolean useLike(SearchKeyValue entry) {
        for (String searchValue : entry.getSearchValues()) {
            if (searchValue.contains("*")) {
                return true;
            }
        }
        return false;
    }

    public enum JoinType {

        INNER,
        LEFT;
    }

    private static String getJoin(boolean fetch, JoinType type) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ").append(type.name()).append(" ");
        builder.append("JOIN ");
        if (fetch) {
            builder.append("FETCH ");
        }
        return builder.toString();
    }

    private static String getHistoryCriterias(List<SearchKeyValue> criterias) {
        StringBuilder builder = new StringBuilder();
        builder.append(true);
        for (SearchKeyValue searchKeyValue : criterias) {
            if (searchKeyValue.getSearchField().equals(SearchFields.HIST_GUID)) {
                builder.append(",").append(false);
            }
        }
        return builder.toString();
    }

    public static String createSearchSql(List<SearchKeyValue> criterias, boolean dynamic, boolean fetch) {

        String OPERATOR = " OR ";
        if (dynamic) {
            OPERATOR = " AND ";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(getJoin(fetch, JoinType.INNER)).append(SearchTables.ASSET_HIST.getTableAlias()).append(".asset ").append(SearchTables.ASSET.getTableAlias());
        builder.append(getJoin(fetch, JoinType.INNER)).append(SearchTables.ASSET.getTableAlias()).append(".carrier ").append(SearchTables.CARRIER.getTableAlias());

        builder.append(" WHERE ").append(SearchTables.ASSET_HIST.getTableAlias()).append(".active IN (").append(getHistoryCriterias(criterias)).append(") ");

        if (!criterias.isEmpty()) {

            builder.append("AND ( ");

            //Add the filters
            boolean first = true;
            for (SearchKeyValue entry : criterias) {
                if (first) {
                    first = false;
                } else {
                    builder.append(OPERATOR);
                }
                if(entry.getSearchField().getFieldType()== SearchFieldType.LIST){
                    builder.append(" UPPER(REPLACE(");
                    builder.append(entry.getSearchField().getSearchTable().getTableAlias()).append(".").append(entry.getSearchField().getFieldName());
                    builder.append(",'-','')) ");
                }else{
                    builder.append(entry.getSearchField().getSearchTable().getTableAlias()).append(".").append(entry.getSearchField().getFieldName());
                }

                if (useLike(entry)) {
                    int containsCount = 0;
                    boolean containsFirst = true;
                    for (String searchValue : entry.getSearchValues()) {
                        containsCount++;
                        if (containsFirst) {
                            containsFirst = false;
                        } else {
                            builder.append(" OR ");
                            builder.append(" UPPER(REPLACE(");
                            builder.append(entry.getSearchField().getSearchTable().getTableAlias()).append(".").append(entry.getSearchField().getFieldName());
                            builder.append(",'-','')) ");
                        }
                        builder.append(" LIKE :").append(entry.getSearchField().getValueName()).append(containsCount);
                    }
                } else if (entry.getSearchField().getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                    builder.append(" >= :").append(entry.getSearchField().getValueName());
                } else if (entry.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                    builder.append(" <= :").append(entry.getSearchField().getValueName());
                } else if (entry.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) {
                	builder.append(" = :").append(entry.getSearchField().getValueName());
                } else {
                    builder.append(" IN (:").append(entry.getSearchField().getValueName()).append(") ");
                }
            }

            builder.append(" ) ");

        }

        return builder.toString();
    }

    public static String createSelectSearchSql(List<SearchKeyValue> searchFields, boolean isDynamic) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT DISTINCT vh FROM AssetHistory vh ");
        buffer.append(createSearchSql(searchFields, isDynamic, true));
        LOG.debug("Primary SQL full: {}", buffer.toString());
        return buffer.toString();
    }

    public static String createCountSearchSql(List<SearchKeyValue> searchFields, boolean isDynamic) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT COUNT( DISTINCT v.guid ) FROM AssetHistory vh ");
        buffer.append(createSearchSql(searchFields, isDynamic, false));
        LOG.debug("Count SQL full: {}", buffer.toString());
        return buffer.toString();
    }

}