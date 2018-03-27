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
package eu.europa.ec.fisheries.uvms.asset.rest.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

public class SearchFieldMapper {
    
    private SearchFieldMapper() {}
    
    public static List<SearchKeyValue> createSearchFields(AssetQuery query) {
        List<SearchKeyValue> searchValues = new ArrayList<>();
        if (query.getId() != null) {
            List<String> assetIds = query.getId().stream().map(UUID::toString).collect(Collectors.toList());
            searchValues.add(new SearchKeyValue(SearchFields.GUID, assetIds));
        }
        if (query.getHistoryId() != null) {
            List<String> assetHistoryIds = query.getHistoryId().stream().map(UUID::toString).collect(Collectors.toList());
            searchValues.add(new SearchKeyValue(SearchFields.HIST_GUID, assetHistoryIds));
        }
        if (query.getCfr() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.CFR, query.getCfr()));
        }
        if (query.getIrcs() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.IRCS, query.getIrcs()));
        }
        if (query.getMmsi() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.MMSI, query.getMmsi()));
        }
        if (query.getImo() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.IMO, query.getImo()));
        }
        if (query.getIccat() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.ICCAT, query.getIccat()));
        }
        if (query.getUvi() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.UVI, query.getUvi()));
        }
        if (query.getGfcm() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.GFCM, query.getGfcm()));
        }
        if (query.getName() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.NAME, query.getName()));
        }
        if (query.getFlagState() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.FLAG_STATE, query.getFlagState()));
        }
        if (query.getExternalMarking() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.EXTERNAL_MARKING, query.getExternalMarking()));
        }
        if (query.getPortOfRegistration() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.HOMEPORT, query.getPortOfRegistration()));
        }
        if (query.getLicenseType() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.LICENSE, query.getLicenseType()));
        }
        if (query.getProducerName() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.PRODUCER_NAME, query.getProducerName()));
        }
        if (query.getGearType() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.GEAR_TYPE, Arrays.asList(query.getGearType().toString())));
        }
        if (query.getMinLength() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.MIN_LENGTH, Arrays.asList(query.getMinLength().toString())));
        }
        if (query.getMaxLength() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.MAX_LENGTH, Arrays.asList(query.getMaxLength().toString())));
        }
        if (query.getMinPower() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.MIN_POWER, Arrays.asList(query.getMinPower().toString())));
        }
        if (query.getMaxPower() != null) {
            searchValues.add(new SearchKeyValue(SearchFields.MAX_POWER, Arrays.asList(query.getMaxPower().toString())));
        }
        return searchValues;
    }
}