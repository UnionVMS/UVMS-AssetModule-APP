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
package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchFields;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchKeyValue;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFieldMapper {

    private SearchFieldMapper() {}


    public static List<MTSearchKeyValue> createSearchFields(MTQuery query) {
        List<MTSearchKeyValue> searchValues = new ArrayList<>();
        if (hasElements(query.getAntennas())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.ANTENNA, query.getAntennas()));
        }
        if (hasElements(query.getAssetIds())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.ASSET_ID, query.getAssetIds()));
        }
        if (query.getDate() != null) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.DATE, Arrays.asList(query.getDate().toString())));
        }
        if (hasElements(query.getDnids())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.DNID, query.getDnids().stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (hasElements(query.getHistoryIds())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.HIST_GUID, query.getHistoryIds()));
        }
        if (hasElements(query.getMemberNumbers())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.MEMBER_NUMBER, query.getMemberNumbers().stream().map(String::valueOf).collect(Collectors.toList())));
        }
        if (hasElements(query.getMobileterminalIds())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.MOBILETERMINAL_ID, query.getMobileterminalIds()));
        }
        if (hasElements(query.getMobileterminalTypes())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.TERMINAL_TYPE, query.getMobileterminalTypes()));
        }
        if (hasElements(query.getSateliteNumbers())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.SATELLITE_NUMBER, query.getSateliteNumbers()));
        }
        if (hasElements(query.getSerialNumbers())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.SERIAL_NUMBER, query.getSerialNumbers()));
        }
        if (hasElements(query.getSoftwareVersions())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.SOFTWARE_VERSION, query.getSoftwareVersions()));
        }
        if (hasElements(query.getTranceiverTypes())) {
            searchValues.add(new MTSearchKeyValue(MTSearchFields.TRANSCEIVER_TYPE, query.getTranceiverTypes()));
        }

        return searchValues;
    }

    private static <T> boolean hasElements(List<T> list) {
        return (list != null && !list.isEmpty());
    }
}