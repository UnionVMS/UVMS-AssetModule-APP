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

package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.constant.SearchTables;
import eu.europa.ec.fisheries.uvms.dao.DynamicQueryGenerator;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.fisheries.uvms.constant.SearchFields.*;
import static eu.europa.ec.fisheries.uvms.mapper.SearchFieldType.LIST;

@ApplicationScoped
public class DynamicQueryGeneratorBean  implements DynamicQueryGenerator {
    public String findAssetGroupByAssetAndHistory() {
        String listElements = Stream.of(SearchFields.values())
                .filter(t -> LIST.equals(t.getFieldType()))
                .map(f -> "(NOT EXISTS (SELECT ff FROM a.fields ff  WHERE ff.field = '"+f.toString()+"' AND ff.value = :"+f.getFieldName()+"))")
                .collect(Collectors.joining(" AND "));

        String otherElements = Stream.of(SearchFields.values())
                .filter(t -> !LIST.equals(t.getFieldType()))
                .map(f -> {
                    if (f == GEAR_TYPE) {
                        return "(f.field = '" + f.toString() + "' AND f.value <> :" + f.getFieldName() + ")";
                    } else {
                        switch (f.getFieldType()) {
                            case NUMBER:
                                return "(f.field = '" + f.toString() + "' AND CAST(f.value as integer) <> :" + f.getFieldName() + ")";
                            case MAX_DECIMAL:
                                return "(f.field = '" + f.toString() + "' AND CAST(f.value as double) < :" + f.getFieldName() + " )";
                            case MIN_DECIMAL:
                                return "(f.field = '" + f.toString() + "' AND CAST(f.value as double) > :" + f.getFieldName() + " )";
                            default:
                                throw new IllegalArgumentException("Unmapped FieldType " + f.getFieldType());
                        }
                    }
                })
                .collect(Collectors.joining(" OR "));

        return "SELECT distinct a.guid FROM AssetGroup a WHERE NOT EXISTS (SELECT f.assetgroup FROM a.fields f WHERE " + listElements + " OR "+ otherElements + ")";
    }

    public Map<SearchFields,String> searchFieldValueMapper(AssetEntity asset, AssetHistory assetHistory) {
        Map<SearchFields,String> searchTables = new HashMap<>();
        Stream.of(SearchFields.values()).forEach(searchField -> {
            SearchTables searchTable = searchField.getSearchTable();

            if (SearchTables.ASSET.equals(searchTable)) {
                switch (searchField) {
                    case IRCS:
                        searchTables.put(IRCS, asset.getIRCS());
                        break;
                    case CFR:
                        searchTables.put(CFR, asset.getCFR());
                        break;
                    case MMSI:
                        searchTables.put(MMSI, asset.getMMSI() == null ? "0" :asset.getMMSI());
                        break;
                    case IMO:
                        searchTables.put(IMO, asset.getIMO() == null ? "0" :asset.getIMO());
                        break;
                    case GUID:
                        searchTables.put(GUID, asset.getGuid());
                        break;
                    case ICCAT:
                        searchTables.put(ICCAT, asset.getIccat());
                        break;
                    case UVI:
                        searchTables.put(UVI, asset.getUvi());
                        break;
                    case GFCM:
                        searchTables.put(GFCM, asset.getGfcm());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid search field value for Asset: " + searchField.getFieldName());
                }
            } else
            if (SearchTables.ASSET_HIST.equals(searchTable)) {
                switch (searchField) {
                    case FLAG_STATE:
                        searchTables.put(FLAG_STATE, assetHistory.getCountryOfRegistration());
                        break;
                    case EXTERNAL_MARKING:
                        searchTables.put(EXTERNAL_MARKING, assetHistory.getExternalMarking());
                        break;
                    case NAME:
                        searchTables.put(NAME, assetHistory.getName());
                        break;
                    case HOMEPORT:
                        searchTables.put(HOMEPORT, assetHistory.getPortOfRegistration());
                        break;
                    case LICENSE:
                        searchTables.put(LICENSE, assetHistory.getLicenceType());
                        break;
                    case HIST_GUID:
                        searchTables.put(HIST_GUID, assetHistory.getGuid());
                        break;
                    case GEAR_TYPE:
                        searchTables.put(GEAR_TYPE, String.valueOf(assetHistory.getType()));
                        break;
                    case MAX_LENGTH:
                        searchTables.put(MAX_LENGTH, String.valueOf(assetHistory.getLengthOverAll() == null? BigDecimal.ZERO : assetHistory.getLengthOverAll()));
                        break;
                    case MIN_LENGTH:
                        searchTables.put(MIN_LENGTH, String.valueOf(assetHistory.getLengthOverAll() == null? BigDecimal.ZERO: assetHistory.getLengthOverAll()));
                        break;
                    case MAX_POWER:
                        searchTables.put(MAX_POWER, String.valueOf(assetHistory.getPowerOfMainEngine() == null? BigDecimal.ZERO: assetHistory.getPowerOfMainEngine()));
                        break;
                    case MIN_POWER:
                        searchTables.put(MIN_POWER,String.valueOf(assetHistory.getPowerOfMainEngine() == null? BigDecimal.ZERO: assetHistory.getPowerOfMainEngine()));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid search field value for AssetHistory: " + searchField.getFieldName());
                }
            }
            else {
                throw new IllegalArgumentException("Cannot map values for invalid table: " + searchTable.getTableName());
            }
        });

        return searchTables;
    }
}
