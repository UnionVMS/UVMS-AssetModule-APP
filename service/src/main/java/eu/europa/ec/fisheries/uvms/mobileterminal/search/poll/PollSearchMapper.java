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
package eu.europa.ec.fisheries.uvms.mobileterminal.search.poll;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchField;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollSearchMapper {

	public static List<PollSearchKeyValue> createSearchFields(List<ListCriteria> criterias) {
		Map<PollSearchField, PollSearchKeyValue> searchKeyValues = new HashMap<>();
		for (ListCriteria criteria : criterias) {
			PollSearchKeyValue keyValue = mapSearchKey(criteria, searchKeyValues);
			searchKeyValues.put(keyValue.getSearchField(), keyValue);
		}
		return new ArrayList<>(searchKeyValues.values());
	}

	private static PollSearchKeyValue mapSearchKey(ListCriteria criteria, Map<PollSearchField, PollSearchKeyValue> searchKeys) {
		if (criteria == null || criteria.getKey() == null || criteria.getValue() == null) {
			throw new IllegalArgumentException("Non valid search criteria");
		}
		PollSearchField searchField = getSearchField(criteria.getKey());
		PollSearchKeyValue searchKeyValue = getSearchKeyValue(searchField, searchKeys);
		searchKeyValue.getValues().add(criteria.getValue());
		return searchKeyValue;
	}

	private static PollSearchField getSearchField(SearchKey key) {
		switch (key) {
			case CONNECT_ID:
				return PollSearchField.CONNECT_ID;
			case POLL_ID:
				return PollSearchField.POLL_ID;
			case POLL_TYPE:
				return PollSearchField.POLL_TYPE;
			case TERMINAL_TYPE:
				return PollSearchField.TERMINAL_TYPE;
			case USER:
				return PollSearchField.USER;
			default:
				throw new IllegalArgumentException("No searchKey " + key.name());
		}
	}

    private static PollSearchKeyValue getSearchKeyValue(PollSearchField field, Map<PollSearchField, PollSearchKeyValue> searchKeys) {
        PollSearchKeyValue searchKeyValue = searchKeys.get(field);
        if (searchKeyValue == null) {
            searchKeyValue = new PollSearchKeyValue();
        }
        searchKeyValue.setSearchField(field);
        return searchKeyValue;
    }

	public static String createCountSearchSql(List<PollSearchKeyValue> searchKeys, boolean isDynamic, PollTypeEnum pollTypeEnum) {
        SearchTable searchTable = getPollTable(pollTypeEnum);
		return "SELECT COUNT (DISTINCT " + searchTable.getTableAlias() + ") FROM " + searchTable.getTableName() + " " + searchTable.getTableAlias() + " " + createSearchSql(searchKeys, isDynamic, searchTable);
	}

	public static String createSelectSearchSql(List<PollSearchKeyValue> searchKeys, boolean isDynamic, PollTypeEnum pollTypeEnum) {
	    SearchTable searchTable = getPollTable(pollTypeEnum);
		return "SELECT DISTINCT " + searchTable.getTableAlias() + " FROM " +  searchTable.getTableName() + " " + searchTable.getTableAlias() + " " + createSearchSql(searchKeys, isDynamic, searchTable);
	}

	private static SearchTable getPollTable(PollTypeEnum pollTypeEnum) {
		switch (pollTypeEnum) {
			case PROGRAM_POLL:
				return SearchTable.PROGRAM_POLL;
			case SAMPLING_POLL:
				return SearchTable.SAMPLING_POLL;
            case BASE_POLL:
			case MANUAL_POLL:
			case AUTOMATIC_POLL:
				return SearchTable.POLL_BASE;
			case CONFIGURATION_POLL:
				return SearchTable.CONFIGURATION_POLL;
			default:
				throw new RuntimeException("No valid Poll Type");
		}
	}

	private static String createSearchSql(List<PollSearchKeyValue> searchKeys, boolean isDynamic, SearchTable searchTable) {
		StringBuilder builder = new StringBuilder();
		String OPERATOR = " OR ";
		if (isDynamic) {
			OPERATOR = " AND ";
		}

		final List<String> searchFields = new ArrayList<>();

		for (PollSearchKeyValue keyValue : searchKeys) {
			PollSearchField pollSearchField = keyValue.getSearchField();
			String tableName = pollSearchField.getTable().getTableName();
			if (!searchFields.contains(tableName))
				searchFields.add(tableName);
		}

		String terminalTypeTableName = PollSearchField.TERMINAL_TYPE.getTable().getTableName();
		String connectIdTableName = PollSearchField.CONNECT_ID.getTable().getTableName();

		if(searchFields.contains(terminalTypeTableName)) {
			builder.append(" INNER JOIN ").append(searchTable.getTableAlias()).append(".mobileterminal mt ");

		}
		if(searchFields.contains(connectIdTableName)) {
			if (!searchFields.contains(terminalTypeTableName))
				builder.append(" INNER JOIN ").append(searchTable.getTableAlias()).append(".mobileterminal mt ");
			builder.append(" INNER JOIN mt.asset a ");
		}

		if (!searchKeys.isEmpty()) {
			builder.append(" WHERE ");
			boolean first = true;
			for (PollSearchKeyValue keyValue : searchKeys) {
				if (first) {
					first = false;
				} else {
					builder.append(OPERATOR);
				}
				if(keyValue.getSearchField().equals(PollSearchField.POLL_TYPE) &&
						(searchTable.equals(SearchTable.CONFIGURATION_POLL) ||
						searchTable.equals(SearchTable.SAMPLING_POLL) ||
						searchTable.equals(SearchTable.PROGRAM_POLL)) ||
						keyValue.getSearchField().equals(PollSearchField.POLL_ID)) {
					builder.append(searchTable.getTableAlias()).append(".")
							.append(keyValue.getSearchField().getSqlColumnName());
				} else {
					builder.append(keyValue.getSearchField().getTable().getTableAlias()).append(".")
							.append(keyValue.getSearchField().getSqlColumnName());
				}
				builder.append(" IN (:").append(keyValue.getSearchField().getSqlReplaceToken()).append(") ");
			}
		}
		return builder.toString();
	}

	public static String createPollableSearchSql(List<String> idList) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT DISTINCT c FROM Channel c");
		builder.append(" INNER JOIN FETCH c.mobileTerminal mt");
		builder.append(" INNER JOIN FETCH mt.plugin p");
		builder.append(" INNER JOIN FETCH p.capabilities cap");
		builder.append(" WHERE");
		builder.append(" c.pollChannel = '1'");
		builder.append(" AND mt.archived = '0' AND mt.active = '1' AND p.pluginInactive = '0'");
		builder.append(" AND (cap.name = 'POLLABLE' AND UPPER(cap.value) = 'TRUE' )");
		builder.append(" AND (mt.asset is not null) ");
		if (idList != null && !idList.isEmpty()) {
			builder.append(" AND mt.asset.id IN :idList");
		}
		builder.append(" ORDER BY c.id DESC");
		return builder.toString();
	}
}
