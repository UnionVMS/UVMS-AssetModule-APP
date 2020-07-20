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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.constant.SearchTables;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetSearchMapperException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory_;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
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

    private static SearchFields getSearchFields(ConfigSearchField field) throws AssetSearchMapperException {
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
//            case PRODUCER_NAME:
//            	return SearchFields.PRODUCER_NAME;
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
    
    public static SearchKeyValue mapSearchFieldForAssetGroupField(AssetGroupSearchField pair, Map<SearchFields, SearchKeyValue> searchKeys) throws AssetDaoMappingException {
        if (pair == null || pair.getKey() == null || pair.getValue() == null) {
            throw new AssetSearchMapperException("Non valid search criteria");
        }

        SearchKeyValue searchKeyValue = getSearchKeyValue(getSearchFields(pair.getKey()), searchKeys);
        String value = prepareSearchValue(searchKeyValue.getSearchField(), pair.getValue());
        searchKeyValue.getSearchValues().add(value);
        return searchKeyValue;
    }

    public static List<SearchKeyValue> createSearchFields(List<AssetListCriteriaPair> criterias) throws AssetDaoMappingException {
        Map<SearchFields, SearchKeyValue> searchKeyValues = new HashMap<>();
        for (AssetListCriteriaPair criteria : criterias) {
            SearchKeyValue searchField = mapSearchFieldForAssetListCriteria(criteria, searchKeyValues);
            searchKeyValues.put(searchField.getSearchField(), searchField);
        }
        return new ArrayList<>(searchKeyValues.values());
    }

    public static List<SearchKeyValue> createSearchFieldsFromGroupCriterias(List<AssetGroupSearchField> criterias) throws AssetDaoMappingException {

        Map<SearchFields, SearchKeyValue> searchKeyValues = new HashMap<>();
        for (AssetGroupSearchField criteria : criterias) {
            SearchKeyValue searchField = mapSearchFieldForAssetGroupField(criteria, searchKeyValues);
            searchKeyValues.put(searchField.getSearchField(), searchField);
        }
        return new ArrayList<>(searchKeyValues.values());

    }

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

    private static String createSearchSql(List<SearchKeyValue> criterias, boolean fetch, Date dateOfEvent) {

        StringBuilder builder = new StringBuilder();
        builder.append(" FROM AssetHistory {asset_history_alias} ");
        builder.append(getJoin(fetch, JoinType.INNER)).append("{asset_history_alias}.asset {asset_alias}");
        builder.append(getJoin(fetch, JoinType.INNER)).append("{asset_alias}.carrier {carrier_alias}");
        builder.append(" WHERE {asset_history_alias}.active IN (").append(getHistoryCriterias(criterias)).append(") ");

        if (!criterias.isEmpty()) {

            builder.append("AND ( ");
            //Add the filters
            for (SearchKeyValue entry : criterias) {
                SearchFields entrySF = entry.getSearchField();
                if(entrySF.getFieldType()== SearchFieldType.LIST){
                    builder.append(" UPPER(REPLACE(");
                    builder.append(entrySF.getSearchTable().getTableAlias()).append(".").append(entrySF.getFieldName());
                    builder.append(",'-','')) ");
                }else{
                    builder.append(entrySF.getSearchTable().getTableAlias()).append(".").append(entrySF.getFieldName());
                }

                if (useLike(entry)) {

                    builder.append(" LIKE :").append(entrySF.getValueName()).append(1);

                    if(entry.getSearchValues().size() > 1) {
                        for (int i = 1; i < entry.getSearchValues().size() ; i++) {
                            builder.append(" OR ");
                            builder.append(" UPPER(REPLACE(");
                            builder.append(entrySF.getSearchTable().getTableAlias()).append(".").append(entrySF.getFieldName());
                            builder.append(",'-','')) ");
                            builder.append(" LIKE :").append(entrySF.getValueName()).append(i+1);
                        }
                    }

                } else if (entrySF.getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                    builder.append(" >= :").append(entrySF.getValueName());
                } else if (entrySF.getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                    builder.append(" <= :").append(entrySF.getValueName());
                } else if (entrySF.getFieldType().equals(SearchFieldType.BOOLEAN)) {
                    builder.append(" = :").append(entrySF.getValueName());
                } else {
                    builder.append(" IN (:").append(entrySF.getValueName()).append(") ");
                }
                builder.append(" AND ");
            }
            // remove last AND
            int lastAndIdx = builder.lastIndexOf(" AND ");
            builder.replace(lastAndIdx,lastAndIdx+5,"");
            builder.append(" ) ");
        }

        if(dateOfEvent != null) {
            builder.append(" AND {asset_history_alias}.{date_of_event} =");
            builder.append(" (SELECT MAX(ah2.{date_of_event}) FROM AssetHistory ah2");
            builder.append(" WHERE ah2.asset.id = {asset_alias}.id AND ah2.{date_of_event} <= '").append(dateOfEvent).append("')");
        }
        return builder.toString();
    }

    public static String createSelectSearchSql(List<SearchKeyValue> searchFields) {
        return createSelectSearchSql(searchFields, null);
    }

    /**
     * Create JPQL query for distinct AssetHistory entities
     * @param searchFields List of SearchKeyValue
     * @param dateOfEvent if exists appends where criteria for first AssetHistory with dateOfEvent occurred before that time
     * @return generated select assets jpql query
     */
    public static String createSelectSearchSql(List<SearchKeyValue> searchFields, Date dateOfEvent) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT DISTINCT {asset_history_alias}");
        buffer.append(createSearchSql(searchFields, true, dateOfEvent));
        buffer.append(" ORDER BY {asset_history_alias}.").append(AssetHistory_.ID).append(" DESC");
        String query = replaceParams(buffer);
        LOG.debug("Primary SQL full: {}",query);
        return query;
    }

    public static String createCountSearchSql(List<SearchKeyValue> searchFields) {
        return createCountSearchSql(searchFields,null);
    }
    /**
     * Create JPQL query for count distinct Asset GUID
     * @param searchFields List of SearchKeyValue
     * @param dateOfEvent if exists appends where criteria for first AssetHistory with dateOfEvent occurred before that time
     * @return generated count assets jpql query
     */
    public static String createCountSearchSql(List<SearchKeyValue> searchFields, Date dateOfEvent) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT COUNT( DISTINCT {asset_alias}.guid )");
        buffer.append(createSearchSql(searchFields, false , dateOfEvent));
        String query = replaceParams(buffer);
        LOG.debug("Count SQL full: {}", query);
        return query;
    }

    private static String replaceParams(StringBuilder builder) {
        return builder.toString()
                .replace("{asset_history_alias}", SearchTables.ASSET_HIST.getTableAlias())
                .replace("{asset_alias}"        , SearchTables.ASSET.getTableAlias())
                .replace("{carrier_alias}"      , SearchTables.CARRIER.getTableAlias())
                .replace("{date_of_event}"      , AssetHistory_.DATE_OF_EVENT);
    }
}