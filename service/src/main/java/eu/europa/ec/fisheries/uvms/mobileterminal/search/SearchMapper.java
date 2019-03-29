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
package eu.europa.ec.fisheries.uvms.mobileterminal.search;

import eu.europa.ec.fisheries.uvms.mobileterminal.dto.ListCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SearchMapper {

    private final static Logger LOG = LoggerFactory.getLogger(SearchMapper.class);

    public static String createSelectSearchSql(List<ListCriteria> criteriaList, boolean isDynamic, boolean includeArchived) {
        StringBuilder builder = new StringBuilder();

        builder
                .append("SELECT DISTINCT mt")
                .append(" FROM MobileTerminal mt")
                .append(" LEFT JOIN FETCH mt.channels c")
                .append(" WHERE ( ");

        if(!includeArchived) {
            builder.append("mt.archived = false ");
        } else {
            builder.append("mt.archived = true ");
        }

        builder
                .append("AND ")
                .append("c.archived = false ")
                .append(" ) ");

        String operator = isDynamic ? "OR" : "AND";

        if (criteriaList != null && !criteriaList.isEmpty()) {
            builder.append(" AND (");
            boolean first = true;
            for (ListCriteria criteria : criteriaList) {
                String key = criteria.getKey().value();
                if (first) {
                    first = false;
                } else {
                    builder.append(operator);
                }
                if ("CONNECT_ID".equals(key)) {
                    builder.append(" ( mt.asset.id = ")
                            .append("'").append(criteria.getValue()).append("' ) ");
                } else if("SERIAL_NUMBER".equals(key)) {
                    builder.append(" ( mt.serialNo LIKE ")
                            .append("'")
                            .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                } else if("SATELLITE_NUMBER".equals(key)) {
                    builder.append(" ( mt.satelliteNumber LIKE ")
                            .append("'")
                            .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                } else if("ANTENNA".equals(key)) {
                    builder.append(" ( mt.antenna LIKE ")
                            .append("'")
                            .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                } else if("TRANSCEIVER_TYPE".equals(key) || "TRANSPONDER_TYPE".equals(key)) {
                    builder.append(" ( mt.transceiverType LIKE ")
                            .append("'")
                            .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                } else if("SOFTWARE_VERSION".equals(key)) {
                    builder.append(" ( mt.softwareVersion LIKE ")
                            .append("'")
                            .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                } else {
                    if (ChannelSearchAttributes.isAttribute(key)) {
                        if(ChannelSearchAttributes.DNID.name().equals(key)) {
                            builder.append(" ( c.DNID LIKE ")
                                    .append("'%")
                                    .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                        }else if(ChannelSearchAttributes.MEMBER_NUMBER.name().equals(key)){
                            builder.append(" ( c.memberNumber LIKE ")
                                    .append("'%")
                                    .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                        }
                    }
                }
            }
            builder.append(")");
        }
        LOG.debug("SELECT SQL {}", builder.toString());
        return builder.toString();
    }
}
