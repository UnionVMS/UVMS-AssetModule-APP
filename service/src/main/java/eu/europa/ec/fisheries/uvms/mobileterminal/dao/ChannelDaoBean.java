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

package eu.europa.ec.fisheries.uvms.mobileterminal.dao;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public Integer getLowestFreeMemberNumberForDnid(Integer dnid){
        Query q = em.createNamedQuery(Channel.LOWEST_UNUSED_MEMBER_NUMBER_FOR_DNID_NATIV_SQL);
        q.setParameter("dnid", dnid);
        return (Integer) q.getSingleResult();
    }

}
