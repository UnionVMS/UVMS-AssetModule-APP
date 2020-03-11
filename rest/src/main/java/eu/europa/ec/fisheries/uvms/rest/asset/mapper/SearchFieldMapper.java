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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchFields;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchKeyValue;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTQuery;

public class SearchFieldMapper {
    
    private SearchFieldMapper() {}
    

    public static List<MTSearchKeyValue> createSearchFields(MTQuery query) {
        List<MTSearchKeyValue> searchValues = new ArrayList<>();
        if(query.getAntennas() != null && !query.getAntennas().isEmpty()){
                searchValues.add(new MTSearchKeyValue(MTSearchFields.ANTENNA, query.getAntennas()));
        }
        if(query.getAssetIds() != null && !query.getAssetIds().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.ASSET_ID, query.getAssetIds()));
        }
        if(query.getDate() != null){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.DATE, Arrays.asList(query.getDate().toString())));
        }
        if(query.getDnids() != null && !query.getDnids().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.DNID, query.getDnids()));
        }
        if(query.getHistoryIds() != null && !query.getHistoryIds().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.HIST_GUID, query.getHistoryIds()));
        }
        if(query.getMemberNumbers() != null && !query.getMemberNumbers().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.MEMBER_NUMBER, query.getMemberNumbers()));
        }
        if(query.getMobileterminalIds() != null && !query.getMobileterminalIds().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.MOBILETERMINAL_ID, query.getMobileterminalIds()));
        }
        if(query.getMobileterminalTypes() != null && !query.getMobileterminalTypes().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.TERMINAL_TYPE, query.getMobileterminalTypes()));
        }
        if(query.getSateliteNumbers() != null && !query.getSateliteNumbers().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.SATELLITE_NUMBER, query.getSateliteNumbers()));
        }
        if(query.getSerialNumbers() != null && !query.getSerialNumbers().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.SERIAL_NUMBER, query.getSerialNumbers()));
        }
        if(query.getSoftwareVersions() != null && !query.getSoftwareVersions().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.SOFTWARE_VERSION, query.getSoftwareVersions()));
        }
        if(query.getTranceiverTypes() != null && !query.getTranceiverTypes().isEmpty()){
            searchValues.add(new MTSearchKeyValue(MTSearchFields.TRANSCEIVER_TYPE, query.getTranceiverTypes()));
        }


        return searchValues;
    }
}