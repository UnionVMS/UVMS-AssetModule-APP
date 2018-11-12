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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.search;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SearchMapper {

    private final static Logger LOG = LoggerFactory.getLogger(SearchMapper.class);

    /*public static String createSelectSearchSql(List<ListCriteria> criteriaList, boolean isDynamic) {
        StringBuilder builder = new StringBuilder();

        builder.append("SELECT DISTINCT mt")
                .append(" FROM MobileTerminal mt")
                .append(" INNER JOIN FETCH mt.mobileTerminalEvents me")
                .append(" LEFT JOIN FETCH mt.channels c")
                //.append(" LEFT JOIN FETCH c.histories ch")    //TODO: Add proper look into the audited part of teh db when that is finished
                .append(" WHERE ( ")
                .append("me.active = true ")
                .append("AND ")
                .append("mt.archived = false ")
                //.append("AND ")
                //.append("ch.active = true ")
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
                    builder.append(" ( me.connectId = ")
                          .append("'").append(criteria.getValue()).append("' ) ");
                } else if("SERIAL_NUMBER".equals(key)){    //this where previsouly searchable through MTEvents attributes field   //possible qND solution: select SUBSTRING (attributes from position('serialNumber=' in attributes)+13 for 10) FROM mobterm.mobileterminalevent
                    builder.append(" ( mt.serialNo = ")
                            .append("'").append(criteria.getValue().replace('*', '%')).append("' ) ");
                /*} else if("SATELLITE_NUMBER".equals(key)){*/ //this does not have the information anywhere else

       /*         } else {
                    if (MobileTerminalSearchAttributes.isAttribute(key)) {
                        builder.append(" ( me.attributes LIKE ")
                                .append("'%").append(key).append("=")
                                .append(criteria.getValue().replace('*', '%')).append(";%' ) ");
                    } else if (ChannelSearchAttributes.isAttribute(key)) {
                        /*builder.append(" ( ch.attributes LIKE ")        //this does not work as channel history does not exist
                                .append("'%").append(key).append("=")
                                .append(criteria.getValue().replace('*', '%')).append(";%' ) ");*/
         /*           } else {
                        /*builder.append(" ( ch.attributes LIKE ")        //this does not work as channel history does not exist
                                .append("'%").append(key).append("=")
                                .append(criteria.getValue().replace('*', '%')).append(";%' ");
                        builder.append(" OR ");*/
          /*              builder.append(" me.attributes LIKE ")
                                .append("'%").append(key).append("=")
                                .append(criteria.getValue().replace('*', '%')).append(";%' ) ");
                    }
                }
            }
            builder.append(")");
        }
        LOG.debug("SELECT SQL {}", builder.toString());
        return builder.toString();
    }*/


          /*possible way to solve the Attribute problem using sql instead of creating a new table:
          select
                CASE
                        WHEN length(attributes) - length(regexp_replace(attributes,';','','g')) / length(';') = 1 THEN REPLACE(SUBSTRING(attributes from position('serialNumber=' in attributes)+13 for 60),';','')
                        WHEN length(attributes) - length(regexp_replace(attributes,';','','g')) / length(';') > 1 THEN REPLACE(SUBSTRING(split_part(attributes, ';',length(LEFT(attributes,position('serialNumber=' in attributes))) - (length(regexp_replace(LEFT(attributes,position('serialNumber=' in attributes)),';','','g'))-1) / length(';')) from position('serialNumber=' in split_part(attributes, ';',length(LEFT(attributes,position('serialNumber=' in attributes))) - (length(regexp_replace(LEFT(attributes,position('serialNumber=' in attributes)),';','','g'))-1) / length(';')))+13 for 60),';','')


                END

          FROM mobterm.mobileterminalevent
           */
    public static String createSelectSearchSql(List<ListCriteria> criteriaList, boolean isDynamic) {
        StringBuilder builder = new StringBuilder();

        builder.append("SELECT DISTINCT mt")
                .append(" FROM MobileTerminal mt")
//                .append(" INNER JOIN FETCH mt.mobileTerminalEvents me")
                .append(" LEFT JOIN FETCH mt.channels c")
                .append(" LEFT JOIN FETCH mt.mobileTerminalAttributes mta")
                .append(" WHERE ( ")
//                .append("me.active = true ")
//                .append("AND ")
                .append("mt.archived = false ")
                .append("AND ")
                .append("c.archived = false ")
                .append(" ) ");

        String operator = isDynamic ? "OR" : "AND";

        if (criteriaList != null && !criteriaList.isEmpty()) {
            builder.append(" AND ");
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
                } else {
                    if (MobileTerminalSearchAttributes.isAttribute(key)) {
                        builder.append(" ( mta.attribute LIKE ")
                                .append("'").append(key).append("'")
                                .append(" AND ")
                                .append(" mta.value LIKE ")
                                .append("'%")
                                .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                    } else if (ChannelSearchAttributes.isAttribute(key)) {
                        if(ChannelSearchAttributes.DNID.name().equals(key)) {
                            builder.append(" ( c.DNID LIKE ")
                                    .append("'%")
                                    .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                        }else if(ChannelSearchAttributes.MEMBER_NUMBER.name().equals(key)){
                            builder.append(" ( c.memberNumber LIKE ")
                                    .append("'%")
                                    .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                        }
                    } else {
                        /*builder.append(" ( ch.attributes LIKE ")
                                .append("'%").append(key).append("=")
                                .append(criteria.getValue().replace('*', '%')).append(";%' ");
                        builder.append(" OR ");*/
                        builder.append(" ( mta.attribute LIKE ")
                                .append("'%").append(key).append("%'")
                                .append(" AND ")
                                .append(" mta.value LIKE ")
                                .append("'%")
                                .append(criteria.getValue().replace('*', '%')).append("%' ) ");
                    }
                }
            }
//            builder.append(")");
        }
        LOG.debug("SELECT SQL {}", builder.toString());
        System.out.println("SQL: " + builder.toString());
        return builder.toString();
    }
}
