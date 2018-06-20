/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.mobileterminal.service.dao;


import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.AttributeMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.poll.PollSearchMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Stateless
public class ChannelDaoBean  {

    @PersistenceContext
    private EntityManager em;


    public List<Channel> getPollableListSearch(List<String> idList) {
        String sql = PollSearchMapper.createPollableSearchSql(idList);
        TypedQuery<Channel> query = em.createQuery(sql, Channel.class);
        if(idList != null && !idList.isEmpty()) {
            query.setParameter("idList", idList);
        }
        return query.getResultList();
    }

    public List<String> getActiveDNID(String pluginName) {
        throw new NotImplementedException();
//        String sql = getSQLActiveDNID(pluginName);
//        TypedQuery<String> query = em.createQuery(sql, String.class);
//        List<Map<String, String>> attributes = AttributeMapper.mapAttributeStrings(query.getResultList());

//        List<String> dnidList = new ArrayList<>();
//        for (Map<String, String> attribute : attributes) {
//            for (String key : attribute.keySet()) {
//                if (key.equalsIgnoreCase("DNID")) {
//                    dnidList.add(attribute.get(key));
//                }
//            }
//        }
//        return dnidList;
    }

//    private String getSQLActiveDNID(String pluginName) {
//        return "SELECT DISTINCT ch_hist.attributes FROM ChannelHistory ch_hist " +
//                "INNER JOIN ch_hist.channel ch " + // channel
//                "INNER JOIN ch.mobileTerminal mobTerm " + //Mobileterminal
//                "INNER JOIN mobTerm.plugin p " +
//                "WHERE ch_hist.active = '1' " +
//                "AND mobTerm.archived = '0' AND p.pluginInactive = '0' " +
//                "AND p.pluginServiceName = '" + pluginName + "'";
//    }
}
