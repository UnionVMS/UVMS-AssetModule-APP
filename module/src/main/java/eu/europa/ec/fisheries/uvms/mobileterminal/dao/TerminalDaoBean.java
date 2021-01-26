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
package eu.europa.ec.fisheries.uvms.mobileterminal.dao;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFieldType;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPluginCapability;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchFields;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchKeyValue;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.query.AuditAssociationQuery;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.AuditDisjunction;
import org.hibernate.envers.query.criteria.ExtendableCriterion;
import org.hibernate.envers.query.criteria.MatchMode;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.JoinType;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Stateless
public class TerminalDaoBean {

    @PersistenceContext
    private EntityManager em;

    public MobileTerminal getMobileTerminalById(UUID id) {
        return em.find(MobileTerminal.class, id);
    }

    public MobileTerminal getMobileTerminalBySerialNo(String serialNo) {
        try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminal.FIND_BY_SERIAL_NO, MobileTerminal.class);
            query.setParameter("serialNo", serialNo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void removeMobileTerminalAfterTests(String guid) {
        MobileTerminal mobileTerminal = getMobileTerminalById(UUID.fromString(guid));
        em.remove(em.contains(mobileTerminal) ? mobileTerminal : em.merge(mobileTerminal));
    }

    public MobileTerminal createMobileTerminal(MobileTerminal terminal) {
        em.persist(terminal);
        return terminal;
    }

    public MobileTerminal updateMobileTerminal(MobileTerminal terminal) {
        if (terminal == null || terminal.getId() == null)
            throw new IllegalArgumentException("Can't update a non-persisted MobileTerminal");
        return em.merge(terminal);
    }

    @SuppressWarnings("unchecked")
    public List<MobileTerminal> getMobileTerminalsByQuery(String sql) {
            Query query = em.createQuery(sql, MobileTerminal.class);
            return query.getResultList();
    }

    public List<MobileTerminal> getMobileTerminalListWithNoActiveAsset() {
        return em.createNamedQuery(MobileTerminal.FIND_BY_UNASSIGNED, MobileTerminal.class)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<MobileTerminal> getMobileTerminalRevisionByHistoryId(UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(MobileTerminal.class, true, true);
        return query.add(AuditEntity.property("historyId").eq(historyId)).getResultList();
    }

    public List<MobileTerminal> getMobileTerminalRevisionById(UUID mobileTerminalId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<MobileTerminal> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(MobileTerminal.class, mobileTerminalId);
        for (Number rev : revisionNumbers) {
            MobileTerminal audited = auditReader.find(MobileTerminal.class, mobileTerminalId, rev);
            resultList.add(audited);
        }
        return resultList;
    }

    public List<MobileTerminal> getMobileTerminalRevisionsRelevantToAsset(UUID mobileTerminalId, UUID assetId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<MobileTerminal> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(MobileTerminal.class, mobileTerminalId);
        MobileTerminal previous = null;
        MobileTerminal previousMatch = null;

        for (Number rev : revisionNumbers) {
            MobileTerminal audited = auditReader.find(MobileTerminal.class, mobileTerminalId, rev);
            if((audited.getAsset() != null && assetId.equals(audited.getAsset().getId()) )
                    || (previousMatch != null && previousMatch.getAsset() != null && assetId.equals(previousMatch.getAsset().getId()))) {

                if(previous != previousMatch){
                    resultList.add(previous);
                }
                resultList.add(audited);

                previousMatch = audited;
            }
            previous = audited;
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public List<UUID> getAllMobileTerminalIdsWithARelationToAsset(UUID assetId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(MobileTerminal.class, true, true)
                .add(AuditEntity.property("asset_id").eq(assetId))
                .addProjection(AuditEntity.id().distinct());

        return  query.getResultList();
    }

    public MobileTerminal getMobileTerminalByRequest(AssetMTEnrichmentRequest request) {
        try {
            Integer dnid = null;
            if (request.getDnidValue() != null) {
                dnid =Integer.parseInt(request.getDnidValue());
            }
            Integer memberNumber = null;
            if (request.getMemberNumberValue() != null) {
                memberNumber = Integer.parseInt(request.getMemberNumberValue());
            }
            return em.createNamedQuery(MobileTerminal.FIND_BY_DNID_AND_MEMBER_NR_OR_SERIALNUMBER_AND_TYPE, MobileTerminal.class)
                    .setParameter("dnid", dnid)
                    .setParameter("memberNumber", memberNumber)
                    .setParameter("serialNumber", request.getSerialNumberValue())
                    .setParameter("mobileTerminalType", MobileTerminalTypeEnum.valueOf(request.getTranspondertypeValue()))
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<MobileTerminal> getMTListSearch(List<MTSearchKeyValue> searchFields, boolean isDynamic, boolean includeArchived) {
        return getMTListSearchPaginated(null, null, searchFields, isDynamic, includeArchived);
    }

    public List<MobileTerminal> getMTListSearchPaginated(Integer pageNumber, Integer pageSize, List<MTSearchKeyValue> searchFields,
                                                   boolean isDynamic, boolean includeArchived) {
        try {
            AuditQuery query = createAuditQuery(searchFields, isDynamic, includeArchived);
            if (pageSize != null && pageNumber != null) {
                query.setFirstResult(pageSize * (pageNumber - 1));
                query.setMaxResults(pageSize);
            }

            //searching for a MT involves searching for values that reside in channel, thus we need to search the db for channels and extract the MT from there
            List<Channel> channelList = query.getResultList();
            Map<UUID, MobileTerminal> returnMap = new HashMap<>();
            for (Channel channel : channelList) {
                // loaderTest(channel.getMobileTerminal());
                forceLoad(channel.getMobileTerminal().getPlugin());
                for (MobileTerminalPluginCapability capability : channel.getMobileTerminal().getPlugin().getCapabilities()) {
                    forceLoad(capability);
                }
                returnMap.put(channel.getMobileTerminal().getId(), channel.getMobileTerminal());
            }
            return new ArrayList<>(returnMap.values());
        } catch (AuditException e) {
            return Collections.emptyList();
        }
    }

    private void forceLoad(Object plugin){
        String s = plugin.toString();
        s = s.concat(s);
    }

    private AuditQuery createAuditQuery(List<MTSearchKeyValue> searchFields, boolean isDynamic, boolean includeArchived) {
        AuditReader auditReader = AuditReaderFactory.get(em);

        //separate search fields for channel and for MT
        List<MTSearchKeyValue> channelSearchValues = new ArrayList<>();
        for (MTSearchKeyValue searchKeyValue : searchFields) {
            if(searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.CHILD)){
                channelSearchValues.add(searchKeyValue);
            }
        }
        searchFields.removeAll(channelSearchValues);


        AuditAssociationQuery aaQuery;
        MTSearchKeyValue dateSearchField = getDateSearchField(searchFields);
        if (dateSearchField != null) {
            Instant date = Instant.parse(dateSearchField.getSearchValues().get(0));
            Number revisionNumberForDate = auditReader.getRevisionNumberForDate(Date.from(date));
            aaQuery = auditReader.createQuery().forEntitiesAtRevision(Channel.class, revisionNumberForDate).traverseRelation("mobileTerminal", JoinType.INNER);
        } else {
            Number revisionNumberForDate = auditReader.getRevisionNumberForDate(new Date());
            aaQuery = auditReader.createQuery().forEntitiesAtRevision(Channel.class, revisionNumberForDate).traverseRelation("mobileTerminal", JoinType.INNER);

            if (!searchRevisions(searchFields)) {
                aaQuery.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
            }
            if(!includeArchived) {
                aaQuery.add(AuditEntity.property("archived").eq(false));
            }
        }

        ExtendableCriterion operatorMT;
        if (isDynamic) {
            operatorMT = AuditEntity.conjunction();
        } else {
            operatorMT = AuditEntity.disjunction();
        }

        //due to how traverseRelation works, we start out in mobile terminal
        boolean operatorUsed = false;
        for (MTSearchKeyValue searchKeyValue : searchFields) {
            if (useLike(searchKeyValue)) {
                AuditDisjunction op = AuditEntity.disjunction();
                for (String value : searchKeyValue.getSearchValues()) {
                    op.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ilike(value.replace("*", "%").toLowerCase(), MatchMode.ANYWHERE));
                }
                operatorUsed = true;
                operatorMT.add(op);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MIN_DECIMAL)) {
                operatorUsed = true;
                operatorMT.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ge(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.MAX_DECIMAL)) {
                operatorUsed = true;
                operatorMT.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).le(Double.valueOf(searchKeyValue.getSearchValues().get(0))));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                operatorUsed = true;
                AuditDisjunction disjunctionOperator = AuditEntity.disjunction();
                for (String v : searchKeyValue.getSearchValuesAsLowerCase()) {
                    if(searchKeyValue.getSearchField().equals(MTSearchFields.ASSET_ID)){
                        disjunctionOperator.add(AuditEntity.relatedId(searchKeyValue.getSearchField().getFieldName()).eq(UUID.fromString(v)));
                    }else {
                        disjunctionOperator.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).ilike(v, MatchMode.ANYWHERE));
                    }
                }
                operatorMT.add(disjunctionOperator);
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                List<Integer> intValues = searchKeyValue.getSearchValues().stream().map(Integer::parseInt).collect(Collectors.toList());
                operatorUsed = true;
                operatorMT.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(intValues));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.ID)) {
                List<UUID> ids = searchKeyValue.getSearchValues().stream().map(UUID::fromString).collect(Collectors.toList());
                operatorUsed = true;
                operatorMT.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).in(ids));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN) ||
                    searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.STRING)) {
                operatorUsed = true;
                operatorMT.add(AuditEntity.property(searchKeyValue.getSearchField().getFieldName()).eq(searchKeyValue.getSearchValues().get(0)));
            } else if (searchKeyValue.getSearchField().getFieldType().equals(SearchFieldType.CHILD)) {

            }
        }
        if (operatorUsed) {
            aaQuery.add((AuditCriterion) operatorMT);
        }


        AuditQuery query =  aaQuery.up();       //this moves the query to channel


        ExtendableCriterion operatorChannel;
        if (isDynamic) {
            operatorChannel = AuditEntity.conjunction();
        } else {
            operatorChannel = AuditEntity.disjunction();
        }

        boolean channelOperatorUsed = false;
        for (MTSearchKeyValue channelSearchValue : channelSearchValues) {
            if (useLike(channelSearchValue)) {
                AuditDisjunction op = AuditEntity.disjunction();
                for (String value : channelSearchValue.getSearchValues()) {
                    op.add(AuditEntity.property(channelSearchValue.getSearchField().getFieldName()).ilike(value.replace("*", "%").toLowerCase(), MatchMode.ANYWHERE));
                }
                channelOperatorUsed = true;
                operatorChannel.add(op);
            } else {
                channelOperatorUsed = true;
                AuditDisjunction disjunctionOperator = AuditEntity.disjunction();
                for (String v : channelSearchValue.getSearchValuesAsLowerCase()) {
                    if(channelSearchValue.getSearchField().equals(MTSearchFields.DNID) || channelSearchValue.getSearchField().equals(MTSearchFields.MEMBER_NUMBER)){
                        disjunctionOperator.add(AuditEntity.property(channelSearchValue.getSearchField().getFieldName()).eq(Integer.valueOf(v)));
                    }else {
                        disjunctionOperator.add(AuditEntity.property(channelSearchValue.getSearchField().getFieldName()).ilike(v, MatchMode.ANYWHERE));
                    }
                }
                operatorChannel.add(disjunctionOperator);
            }
        }

        if (channelOperatorUsed) {
            query.add((AuditCriterion) operatorChannel);
        }

        return query;
    }

    private boolean useLike(MTSearchKeyValue entry) {
        for (String searchValue : entry.getSearchValues()) {
            if (searchValue.contains("*")) {
                return true;
            }
        }
        return false;
    }

    private boolean searchRevisions(List<MTSearchKeyValue> searchFields) {
        for (MTSearchKeyValue searchKeyValue : searchFields) {
            if (searchKeyValue.getSearchField().equals(MTSearchFields.HIST_GUID)) {
                return true;
            }
        }
        return false;
    }

    private MTSearchKeyValue getDateSearchField(List<MTSearchKeyValue> searchFields) {
        for (MTSearchKeyValue searchKeyValue : searchFields) {
            if (searchKeyValue.getSearchField().equals(MTSearchFields.DATE)) {
                return searchKeyValue;
            }
        }
        return null;
    }

    public MobileTerminal getMtAtDate(UUID mtId, Instant instant) {
        Date date = Date.from(instant);
        AuditReader auditReader = AuditReaderFactory.get(em);
        try {
            return auditReader.find(MobileTerminal.class, mtId, date);
        } catch (RevisionDoesNotExistException ex) {
            return getFirstRevision(mtId);
        }
    }

    public MobileTerminal getFirstRevision(UUID mtId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<Number> revisions = auditReader.getRevisions(MobileTerminal.class, mtId);
        if (!revisions.isEmpty()) {
            return auditReader.find(MobileTerminal.class, mtId, revisions.get(0));
        }
        return null;
    }

    public MobileTerminal getMobileTerminalAtDateWithMemberNumberAndDnid(Integer memberNumber,Integer dnid, Instant date) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        Number revision;
        Channel channel;
        
        try {
            revision = auditReader.getRevisionNumberForDate(Date.from(date));
        } catch (RevisionDoesNotExistException ex) {
            return null;
        }
        
        try {
            channel = (Channel) auditReader.createQuery()
                    .forEntitiesAtRevision(Channel.class, revision)
                    .add(AuditEntity.property("memberNumber").eq(memberNumber) )
                    .add(AuditEntity.property("dnid").eq(dnid) )
                    .setMaxResults( 1 )
                    .getSingleResult();
        } catch (NoResultException ex){
            return null;
        }
        
        return channel.getMobileTerminal();
    }

    public void flushEm() {
        em.flush();
    }
    
    public List<MobileTerminal> getMTListBasedOnChannelRevisionsForInterval(Instant fromDate, Instant toDate ) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        try {
//            AuditDisjunction endOrStartDate  = AuditEntity.disjunction()
//                    .add(AuditEntity.property( "endDate" ).hasChanged())
//                    .add(AuditEntity.property( "startDate" ).hasChanged());
            AuditDisjunction endOrStartDate  = AuditEntity.disjunction()
                    .add( AuditEntity.conjunction()
                            .add(AuditEntity.property( "endDate" ).hasChanged())
                            .add(AuditEntity.property( "updateTime" ).gt(fromDate))
                            .add(AuditEntity.property( "updateTime" ).lt(toDate))
                    )
                    .add( AuditEntity.conjunction()
                            .add(AuditEntity.property( "startDate" ).hasChanged())
                            .add(AuditEntity.property( "updateTime" ).gt(fromDate))
                            .add(AuditEntity.property( "updateTime" ).lt(toDate))
                    );
            List<Channel> channelList = (List<Channel>) auditReader.createQuery().forRevisionsOfEntity(
                    Channel.class, true, true)
                    .add( endOrStartDate )
                    .getResultList();
            
            List<MobileTerminal> retListOfMT = new ArrayList<>();
            
            for (Channel channel : channelList) {
                retListOfMT.add(channel.getMobileTerminal());
            }
            return retListOfMT;
        } catch (AuditException e) {
            return Collections.emptyList();
        }
    }
}
