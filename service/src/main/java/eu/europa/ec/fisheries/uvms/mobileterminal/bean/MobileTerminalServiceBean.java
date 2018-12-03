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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalTypeComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalAttributes;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.TextMessage;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Stateless
@LocalBean
public class MobileTerminalServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalServiceBean.class);

    @EJB
    private AssetMessageProducer assetMessageProducer;

    @EJB
    private AssetQueueConsumer AssetMessageConsumer;

    @EJB
    private PluginServiceBean pluginService;

    @EJB
    private ConfigServiceBeanMT configModel;

    @EJB
    private PollServiceBean pollModel;

    @EJB
    private TerminalDaoBean terminalDao;

    @Inject
    private AssetDao assetDao;

    public MobileTerminal createMobileTerminal(MobileTerminal mobileTerminal, String username) {
        MobileTerminal createdMobileTerminal = terminalDao.createMobileTerminal(mobileTerminal);

        boolean dnidUpdated = configModel.checkDNIDListChange(createdMobileTerminal.getPlugin().getPluginServiceName());

        //send stuff to audit
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalCreated(createdMobileTerminal.getId().toString(), username);
            assetMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was created", createdMobileTerminal.getId().toString());
        }
        if (dnidUpdated) {
            pluginService.processUpdatedDNIDList(createdMobileTerminal.getPlugin().getPluginServiceName());
        }
        return createdMobileTerminal;
    }

    public MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query) {
        ListResponseDto listResponse = getTerminalListByQuery(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());
        return response;
    }

    public MobileTerminal upsertMobileTerminal(MobileTerminal data, MobileTerminalSource source, String username) {
        if (data == null) {
            throw new NullPointerException("No Mobile terminal to update [ NULL ]");
        }
        data.setSource(source);
        MobileTerminal terminalUpserted = upsertMobileTerminal(data, username);

        boolean dnidUpdated = configModel.checkDNIDListChange(terminalUpserted.getPlugin().getPluginServiceName());
        if (dnidUpdated) {
            pluginService.processUpdatedDNIDList(data.getPlugin().getPluginServiceName());
        }
        return terminalUpserted;
    }

    public MobileTerminalType getMobileTerminalByIdFromInternalOrExternalSource(MobileTerminalId id, AssetDataSourceQueue queue) throws AssetException, AssetMessageException {
        if (id == null) {
            throw new NullPointerException("No id");
        }
        if (queue != null && queue.equals(AssetDataSourceQueue.INTERNAL)) {
            //return getMobileTerminalByIdFromInternalOrExternalSource(id.getGuid());
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminalDao.getMobileTerminalById(UUID.fromString(id.getGuid())));
        }
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminal(id);
        String messageId = assetMessageProducer.sendDataSourceMessage(data, queue);
        TextMessage response = AssetMessageConsumer.getMessageAss(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
    }

    public MobileTerminal updateMobileTerminal(MobileTerminal mobileTerminal, String comment, String username) {

        if (mobileTerminal == null) {
            throw new NullPointerException("No terminal to update");
        }
        if (mobileTerminal.getId() == null) {
            throw new NullPointerException("Non valid id of terminal to update");
        }

        MobileTerminal oldTerminal = getMobileTerminalEntityById(mobileTerminal.getId());
        MobileTerminalPlugin updatedPlugin = null;

        if (mobileTerminal.getPlugin() == null || mobileTerminal.getPlugin().getName() == null) {
            updatedPlugin = oldTerminal.getPlugin();
        }

        if (updatedPlugin == null) {
            updatedPlugin = oldTerminal.getPlugin();
        }

        mobileTerminal.setPlugin(updatedPlugin);

        //TODO check type
        MobileTerminal updatedTerminal;
        if (oldTerminal.getMobileTerminalType() != null) {
            updatedTerminal = terminalDao.updateMobileTerminal(mobileTerminal);

        } else {
            throw new UnsupportedOperationException("Update - Not supported mobile terminal type");
        }

        //send to audit
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUpdated(updatedTerminal.getId().toString(), comment, username);
            assetMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was updated", updatedTerminal.getId().toString());
        }

        boolean dnidUpdated = configModel.checkDNIDListChange(updatedTerminal.getPlugin().getName());
        if (dnidUpdated) {
            pluginService.processUpdatedDNIDList(updatedTerminal.getPlugin().getName());
        }

        return updatedTerminal;
    }

    public MobileTerminal assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) {
        MobileTerminal terminalAssign = assignMobileTerminalToCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(terminalAssign.getId().toString(), comment, username);
            assetMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", terminalAssign.getId()
                    .toString());
        }
        return terminalAssign;
    }

    public MobileTerminal unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) {
        MobileTerminal terminalUnAssign = unAssignMobileTerminalFromCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(terminalUnAssign.getId().toString(), comment, username);
            assetMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", terminalUnAssign.getId().toString());
        }
        return terminalUnAssign;
    }

    public MobileTerminal setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username) {
        MobileTerminal terminalStatus = getMobileTerminalEntityById(terminalId);

        terminalStatus = changeUpdateMobileTerminalStatus(terminalStatus, comment, status, username);

        //audit stuff
        try {
            String auditData = null;
            switch (status) {
                case ACTIVE:
                    auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalActivated(terminalStatus.getId().toString(), comment, username);
                    break;
                case INACTIVE:
                    auditData = AuditModuleRequestMapper
                            .mapAuditLogMobileTerminalInactivated(terminalStatus.getId().toString(), comment, username);
                    break;
                case ARCHIVE:
                    auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalArchived(terminalStatus.getId().toString(), comment, username);
                    break;
                default:
                    break;
            }
            assetMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException | AssetMessageException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was set to status {}", terminalStatus
                    .getId().toString(), status);
        }

        boolean dnidUpdated = configModel.checkDNIDListChange(terminalStatus.getPlugin().getName());
        if (dnidUpdated) {
            pluginService.processUpdatedDNIDList(terminalStatus.getPlugin().getName());
        }

        return terminalStatus;
    }

    private MobileTerminal changeUpdateMobileTerminalStatus(MobileTerminal mobileTerminal, String comment, MobileTerminalStatus status, String username) {
        if (mobileTerminal == null) {
            throw new IllegalArgumentException("No Mobile Terminal");
        }
        if (status == null) {
            throw new IllegalArgumentException("No terminal status to set");
        }

        switch (status) {
            case ACTIVE:
                mobileTerminal.setInactivated(false);
                break;
            case INACTIVE:
                mobileTerminal.setInactivated(true);
                break;
            case ARCHIVE:
                mobileTerminal.setArchived(true);
                mobileTerminal.setInactivated(true);
                break;
            default:
                LOG.error("[ Non valid status to set ] {}", status);
                throw new IllegalArgumentException("Non valid status to set");
        }
        mobileTerminal.setUpdateuser(username);
        return terminalDao.updateMobileTerminal(mobileTerminal);
    }

    public List<MobileTerminal> getMobileTerminalRevisions(UUID historyId) {
        return terminalDao.getMobileTerminalRevisionForHistoryId(historyId);
    }

    public PollChannelListDto getPollableMobileTerminal(PollableQuery query) {

        PollChannelListDto channelListDto = new PollChannelListDto();

        ListResponseDto listResponse = pollModel.getMobileTerminalPollableList(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());

        channelListDto.setCurrentPage(response.getCurrentPage());
        channelListDto.setTotalNumberOfPages(response.getTotalNumberOfPages());

        ArrayList<PollChannelDto> pollChannelList = new ArrayList<>();
        for (MobileTerminalType terminalType : response.getMobileTerminal()) {
            PollChannelDto terminal = PollMapper.mapPollChannel(terminalType);
            pollChannelList.add(terminal);
        }
        channelListDto.setPollableChannels(pollChannelList);
        return channelListDto;
    }

    /*****************************************************************************************************************/

    public MobileTerminal getMobileTerminalEntityById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Non valid id: NULL");
        return terminalDao.getMobileTerminalById(id);
    }

    public MobileTerminal getMobileTerminalEntityById(MobileTerminalId id) {
        if (id == null || id.getGuid() == null || id.getGuid().isEmpty())
            throw new IllegalArgumentException("Non valid id: NULL or Empty");
        return terminalDao.getMobileTerminalById(UUID.fromString(id.getGuid()));
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(String serialNo) {
        if (serialNo == null || serialNo.isEmpty())
            throw new NullPointerException("Non valid serial no");
        return terminalDao.getMobileTerminalBySerialNo(serialNo);
    }

    public String assertTerminalHasSerialNumber(MobileTerminalType mobileTerminal) {
        String serialNumber = null;
        for (MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
            if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType()) &&
                    attribute.getValue() != null && !attribute.getValue().isEmpty()) {
                serialNumber = attribute.getValue();
                break;
            }
        }
        if (serialNumber == null) {
            throw new NullPointerException("Cannot create mobile terminal without serial number");
        }
        if (mobileTerminal.getPlugin() == null) {
            throw new NullPointerException("Cannot create Mobile terminal when plugin is null");
        }
        return serialNumber;
    }

    public void assertTerminalNotExists(UUID mobileTerminalGUID, String serialNr) {

        MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminalGUID);

        if (terminal != null) {
            throw new IllegalArgumentException("Mobile terminal already exists in database for id: " + mobileTerminalGUID.toString());
        }

        MobileTerminal terminalBySerialNo = getMobileTerminalEntityBySerialNo(serialNr);
        if (terminalBySerialNo == null) {  //aka the serial number does not exist in the db
            return;
        }
        if (!terminalBySerialNo.getArchived()) {
            throw new IllegalArgumentException("Mobile terminal already exists in database for serial number: " + serialNr);
        }
    }

    public MobileTerminal assignMobileTerminalToCarrier(MobileTerminalAssignQuery query, String comment, String username) {
        if (query == null) {
            throw new NullPointerException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new NullPointerException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new NullPointerException("No connect id in request");
        }

        MobileTerminalId mobTermId = query.getMobileTerminalId();
        String connectId = query.getConnectId();

        Asset asset = assetDao.getAssetById(UUID.fromString(connectId));
        if (asset == null) {
            throw new NotFoundException("No Asset with ID " + connectId + " found. Can not link Mobile Terminal.");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);

        if(terminal.getAsset() != null) {
            throw new IllegalArgumentException("Terminal " + mobTermId + " is already linked to an asset with guid " + query.getConnectId());
        }
        terminal.setAsset(asset);
        terminal.setUpdateuser(username);
        terminalDao.updateMobileTerminal(terminal);

        asset.getMobileTerminals().add(terminal);
        assetDao.updateAsset(asset);
        return terminal;
    }

    public MobileTerminal unAssignMobileTerminalFromCarrier(MobileTerminalAssignQuery query, String comment, String username) {
        if (query == null) {
            throw new IllegalArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new IllegalArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new IllegalArgumentException("No connect id in request");
        }

        MobileTerminalId mobTermId = query.getMobileTerminalId();

        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        terminal.setUpdateuser(username);

        Asset asset = terminal.getAsset();
        asset.setUpdatedBy(username);
        terminal.setAsset(null);
        terminalDao.updateMobileTerminal(terminal);

        boolean remove = asset.getMobileTerminals().remove(terminal);
        if(!remove) {
            throw new IllegalArgumentException("Terminal " + mobTermId + " is not linked to an asset with ID " + asset.getId());
        }
        assetDao.updateAsset(asset);
        return terminal;
    }

    public MobileTerminal upsertMobileTerminal(MobileTerminal mobileTerminal, String username) {

        if (mobileTerminal == null) {
            throw new NullPointerException("RequestQuery is null");
        }

        try {
            MobileTerminal updatedMobileTerminal = updateMobileTerminal(mobileTerminal, "Upserted by external module", username);
            return updatedMobileTerminal;
        } catch (RuntimeException e) {
            LOG.error("[ Error when upserting mobile terminal: Mobile terminal update failed trying to insert. ] {} {}", e, e.getStackTrace());
            //TODO: Should this swallow an error and just continue on?
        }
        MobileTerminal createdMobileTerminal = null;
        createdMobileTerminal = createMobileTerminal(mobileTerminal, username);

        return createdMobileTerminal;
    }

    public ListResponseDto getTerminalListByQuery(MobileTerminalListQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("No list query");
        }
        if (query.getPagination() == null) {
            throw new IllegalArgumentException("No list pagination");
        }
        if (query.getMobileTerminalSearchCriteria() == null) {
            throw new IllegalArgumentException("No list criteria");
        }

        ListResponseDto response = new ListResponseDto();
        List<MobileTerminalType> mobileTerminalList = new ArrayList<>();

        Integer page = query.getPagination().getPage();
        Integer listSize = query.getPagination().getListSize();
        int startIndex = (page - 1) * listSize;
        int stopIndex = startIndex + listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);

        boolean isDynamic = query.getMobileTerminalSearchCriteria().isIsDynamic() == null ? true : query.getMobileTerminalSearchCriteria().isIsDynamic();

        List<ListCriteria> criterias = query.getMobileTerminalSearchCriteria().getCriterias();

        String searchSql = SearchMapper.createSelectSearchSql(criterias, isDynamic);

        List<MobileTerminal> terminals = terminalDao.getMobileTerminalsByQuery(searchSql);

        for (MobileTerminal terminal : terminals) {
            MobileTerminalType terminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
            mobileTerminalList.add(terminalType);
        }

        mobileTerminalList.sort(new MobileTerminalTypeComparator());
        int totalMatches = mobileTerminalList.size();
        LOG.debug("totalMatches: " + totalMatches);

        int numberOfPages = totalMatches / listSize;
        if (totalMatches % listSize != 0) {
            numberOfPages += 1;
        }
        response.setMobileTerminalList(mobileTerminalList);

        if ((totalMatches - 1) > 0) {
            if (stopIndex >= totalMatches) {
                stopIndex = totalMatches;
            }
            LOG.debug("stopIndex: " + stopIndex);
            response.setMobileTerminalList(new ArrayList<>(mobileTerminalList.subList(startIndex, stopIndex)));
        }
        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(page);

        return response;
    }

    public MobileTerminalType getMobileTerminalBySourceAndSearchCriteria(MobileTerminalSearchCriteria criteria) {
        MobileTerminalListQuery query = new MobileTerminalListQuery();

        // If no valid criterias, don't look for a mobile terminal
        if (criteria.getCriterias().isEmpty()) {
            return null;
        }

        query.setMobileTerminalSearchCriteria(criteria);
        eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination pagination =
                new eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination();
        // To leave room to find erroneous results - it must be only one in the list
        pagination.setListSize(2);
        pagination.setPage(1);
        query.setPagination(pagination);

        MobileTerminalListResponse mobileTerminalListResponse = getMobileTerminalList(query);
        List<MobileTerminalType> resultList = mobileTerminalListResponse.getMobileTerminal();
        return resultList.size() != 1 ? null : resultList.get(0);
    }


    public MobileTerminalType findMobileTerminalTypeByAsset(UUID assetId) {
        MobileTerminal terminal = terminalDao.findMobileTerminalByAsset(assetId);
        return terminal == null ? null : MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }

    public MobileTerminal findMobileTerminalByAsset(UUID assetid) {
        MobileTerminal terminal = terminalDao.findMobileTerminalByAsset(assetid);
        return terminal;
    }

    //@formatter:off
    public MobileTerminalType getMobileTerminalByAssetMTEnrichmentRequest(AssetMTEnrichmentRequest request) {

        String request_dnid = null;
        String request_memberNumber = null;
        String request_serialNumber = null;
        String request_transponderType = null;

        String  sql = "";
        sql += "SELECT DISTINCT mt";
        sql += " FROM MobileTerminal mt";
        sql += " LEFT JOIN FETCH mt.channels c";
        sql += " INNER JOIN FETCH mt.mobileTerminalAttributes mta";
        sql += " WHERE (";
        sql += "mt.archived = false ";
        sql += "AND ";
        sql += "c.archived = false) ";

        String operator = " AND (";
        if (request.getDnidValue() != null && request.getDnidValue().length() > 0) {

            request_dnid = request.getDnidValue();
            String value = request_dnid.replace("*","%");
            sql += operator;
            sql += "  c.DNID LIKE ";
            sql += "'";
            sql += value;
            sql += "') ";

            operator = " OR (";
        }
        if (request.getMemberNumberValue() != null && request.getMemberNumberValue().length() > 0) {
            request_memberNumber = request.getMemberNumberValue();
            String value = request_memberNumber.replace("*","%");
            sql += operator;
            sql += "  c.memberNumber LIKE ";
            sql += "'";
            sql += value;
            sql += "')";

            operator = " AND (";
        }

        // in table MobileTerminalAttribute
        if (request.getSerialNumberValue() != null && request.getSerialNumberValue().length() > 0) {

            request_serialNumber = request.getSerialNumberValue();
            sql += operator;
            sql += "  mta.attribute = 'SERIAL_NUMBER' AND mta.value LIKE ";
            sql += "'%";
            sql += request_serialNumber;
            sql += "%')";

            operator = " OR (";
        }

        if (request.getTranspondertypeValue() != null && request.getTranspondertypeValue().length() > 0) {

            request_transponderType = request.getTranspondertypeValue();
            sql += operator;
            sql += "  mta.attribute = 'TRANCEIVER_TYPE' AND mta.value LIKE ";
            sql += "'%";
            sql += request_transponderType;
            sql += "%')";
        }

        List<MobileTerminal> terminals = terminalDao.getMobileTerminalsByQuery(sql);
        switch (terminals.size()){
            case 0 : return null;
            case 1 : return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminals.get(0));
            default :{
                MobileTerminal  triedMobileTerminal = tryToFindBasedOnRequestData(request_dnid,request_memberNumber,request_serialNumber,request_transponderType, terminals);
                if(triedMobileTerminal != null){
                    return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(triedMobileTerminal);
                }else{
                    return null;
                }
            }
        }
    }
    //@ formatter:on

    private MobileTerminal tryToFindBasedOnRequestData(String dnid,String memberNumber,String serialNumber,String transponderType, List<MobileTerminal> terminals) {
        // iterate the list and try to find something we recognize from the request
        for(MobileTerminal terminal : terminals){
            Set<Channel> channels =  terminal.getChannels();
            for(Channel channel : channels){
                if(dnid != null) {
                    String channelDNID = channel.getDNID();
                    if(channelDNID != null){
                        if(channelDNID.equals(dnid)){
                            return terminal;
                        }
                    }
                }
                if(memberNumber != null) {
                    String channelMemberNumber = channel.getMemberNumber();
                    if(channelMemberNumber != null){
                        if(channelMemberNumber.equals(memberNumber)){
                            return terminal;
                        }
                    }
                }
            }

            Set<MobileTerminalAttributes> attributes = terminal.getMobileTerminalAttributes();
            for(MobileTerminalAttributes attr : attributes){

                String key = attr.getAttribute();
                String val = attr.getValue();

                if(serialNumber != null){
                    if(key.equals("SERIAL_NUMBER")){
                        if(val != null){
                            if(serialNumber.equals(val)){
                                return terminal;
                            }
                        }
                    }
                }
                if(transponderType != null){
                    if(key.equals("TRANSCEIVER_TYPE")){
                        if(val != null){
                            if(transponderType.equals(val)){
                                return terminal;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
