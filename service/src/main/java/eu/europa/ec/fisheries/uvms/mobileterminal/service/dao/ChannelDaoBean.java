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

import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.poll.PollSearchMapper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class ChannelDaoBean  {

    @PersistenceContext
    private EntityManager em;

    public List<Channel> getPollableListSearch(List<String> idList) {
        if(idList == null){
            return new ArrayList<>();
        }
        String sql = PollSearchMapper.createPollableSearchSql(idList);
        List<UUID> uuidList = new ArrayList<>();
        for (String s: idList) {
            uuidList.add(UUID.fromString(s));
        }
        TypedQuery<Channel> query = em.createQuery(sql, Channel.class);
        if(!idList.isEmpty()) {
            query.setParameter("idList", uuidList);
        }
        return query.getResultList();
    }

    public List<String> getActiveDNID(String pluginName) {
        //return new ArrayList<String>();   //TODO: Fix so that this actually returns something sane  //might be fixed
        //throw new NotImplementedException();
        String sql = getSQLActiveDNID(pluginName);
        TypedQuery<String> query = em.createQuery(sql, String.class);
        return query.getResultList();

    }

    private String getSQLActiveDNID(String pluginName) {
        return "SELECT DISTINCT c.DNID FROM Channel c " +
                "INNER JOIN c.mobileTerminal mobTerm " + //Mobileterminal
                "INNER JOIN mobTerm.plugin p " +
                "WHERE c.active = '1' " +
                "AND mobTerm.archived = '0' AND p.pluginInactive = '0' " +
                "AND p.pluginServiceName = '" + pluginName + "'";
    }
}
