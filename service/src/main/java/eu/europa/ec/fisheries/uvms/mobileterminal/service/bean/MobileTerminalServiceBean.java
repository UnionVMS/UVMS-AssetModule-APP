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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalTypeComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalAttributes;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.SearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.TextMessage;
import javax.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class MobileTerminalServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalServiceBean.class);

    @EJB
    private MTMessageProducer MTMessageProducer;

    @EJB
    private MTMessageConsumer MTMessageConsumer;

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
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was created", createdMobileTerminal.getId().toString());
        }
        if(dnidUpdated) {
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
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(data.getPlugin().getPluginServiceName());
        }
        return terminalUpserted;
    }

    public MobileTerminalType getMobileTerminalByIdFromInternalOrExternalSource(MobileTerminalId id, DataSourceQueue queue) throws AssetException {
        if (id == null) {
            throw new NullPointerException("No id");
        }
        if (queue != null && queue.equals(DataSourceQueue.INTERNAL)) {
            //return getMobileTerminalByIdFromInternalOrExternalSource(id.getGuid());
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminalDao.getMobileTerminalById(UUID.fromString(id.getGuid())));
        }
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminal(id);
        String messageId = MTMessageProducer.sendDataSourceMessage(data, queue);
        TextMessage response = MTMessageConsumer.getMessage(messageId, TextMessage.class);
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

        if(mobileTerminal.getPlugin() == null || mobileTerminal.getPlugin().getName() == null) {
            /*if(!mobileTerminal.getPlugin().getName().equalsIgnoreCase(oldTerminal.getPlugin().getName())) {
                updatedPlugin = pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getPluginServiceName());
                oldTerminal.setPlugin(updatedPlugin);
            }*/
            updatedPlugin = oldTerminal.getPlugin();
        }

        if (updatedPlugin == null) {
            updatedPlugin = oldTerminal.getPlugin();
        }

        mobileTerminal.setPlugin(updatedPlugin);

        //TODO check type
        MobileTerminal updatedTerminal;
        if(oldTerminal.getMobileTerminalType() != null) {
            updatedTerminal = terminalDao.updateMobileTerminal(mobileTerminal);

        }else {
            throw new UnsupportedOperationException("Update - Not supported mobile terminal type");
        }

        //send to audit
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUpdated(updatedTerminal.getId().toString(), comment, username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was updated", updatedTerminal.getId().toString());
        }
        
        boolean dnidUpdated = configModel.checkDNIDListChange(updatedTerminal.getPlugin().getName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(updatedTerminal.getPlugin().getName());
        }
        
        return updatedTerminal;
    }

    public MobileTerminal assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) {
        MobileTerminal terminalAssign = assignMobileTerminalToCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(terminalAssign.getId().toString(), comment, username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", terminalAssign.getId()
                    .toString());
        }
        return terminalAssign;
    }

    public MobileTerminal unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) {
        MobileTerminal terminalUnAssign = unAssignMobileTerminalFromCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(terminalUnAssign.getId().toString(), comment, username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", terminalUnAssign.getId().toString());
        }
        return terminalUnAssign;
    }

    public MobileTerminal setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username) {
        //MobileTerminal terminalStatus = setStatusMobileTerminal(terminalId, comment, status, username);
        MobileTerminal terminalStatus = getMobileTerminalEntityById(terminalId);

        //create event and update MT for this happening
        terminalStatus = createMTEventForStatusChange(terminalStatus, comment, status, username);

        //audit stuff
        try {
            String auditData = null;
            switch (status) {
            case ACTIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalActivated(terminalStatus.getId().toString(),comment, username);
                break;
            case INACTIVE:
                auditData = AuditModuleRequestMapper
                .mapAuditLogMobileTerminalInactivated(terminalStatus.getId().toString(), comment, username);
                break;
            case ARCHIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalArchived(terminalStatus.getId().toString(), comment,username);
                break;
            default:
                break;
            }
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was set to status {}", terminalStatus
                    .getId().toString(), status);
        }

        boolean dnidUpdated = configModel.checkDNIDListChange(terminalStatus.getPlugin().getName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(terminalStatus.getPlugin().getName());
        }
        
        return terminalStatus;
    }

    private MobileTerminal createMTEventForStatusChange(MobileTerminal mobileTerminal, String comment, MobileTerminalStatus status, String username) {
        if (mobileTerminal == null) {
            throw new IllegalArgumentException("No Mobile Terminal");
        }
        if (status == null) {
            throw new IllegalArgumentException("No terminal status to set");
        }

        MobileTerminalEvent current = mobileTerminal.getCurrentEvent();
        current.setActive(false);

        MobileTerminalEvent event = new MobileTerminalEvent();
        event.setActive(true);
        event.setPollChannel(current.getPollChannel());
        event.setDefaultChannel(current.getDefaultChannel());
        event.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
        event.setConfigChannel(current.getConfigChannel());
        event.setAttributes(current.getAttributes());
        event.setComment(comment);
        event.setAsset(current.getAsset());
        event.setMobileterminal(mobileTerminal);
        event.setUpdateuser(username);
        switch (status) {
            case ACTIVE:
                event.setEventCodeType(EventCodeEnum.ACTIVATE);
                mobileTerminal.setInactivated(false);
                break;
            case INACTIVE:
                event.setEventCodeType(EventCodeEnum.INACTIVATE);
                mobileTerminal.setInactivated(true);
                break;
            case ARCHIVE:
                event.setEventCodeType(EventCodeEnum.ARCHIVE);
                mobileTerminal.setArchived(true);
                mobileTerminal.setInactivated(true);
                break;
            default:
                LOG.error("[ Non valid status to set ] {}", status);
                throw new IllegalArgumentException("Non valid status to set");
        }

        event.setMobileTerminalAttributes(current.getMobileTerminalAttributes());
        for(MobileTerminalAttributes mta : current.getMobileTerminalAttributes()){
            mta.setMobileTerminalEvent(event);
        }
        mobileTerminal.getMobileTerminalEvents().add(event);
        terminalDao.updateMobileTerminal(mobileTerminal);

        return mobileTerminal;
    }

    public MobileTerminalHistory getMobileTerminalHistoryList(String guid) {
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(guid);
        MobileTerminalHistory historyList = getMobileTerminalHistoryList(terminalId);
        return historyList;
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
        for(MobileTerminalType terminalType : response.getMobileTerminal()) {
            PollChannelDto terminal = PollMapper.mapPollChannel(terminalType);
            pollChannelList.add(terminal);
        }
        channelListDto.setPollableChannels(pollChannelList);
        return channelListDto;
    }

    /*****************************************************************************************************************/

    public MobileTerminal getMobileTerminalEntityById(UUID id) {
        if(id == null)
            throw new IllegalArgumentException("Non valid id: NULL");
        return terminalDao.getMobileTerminalById(id);
    }

    public MobileTerminal getMobileTerminalEntityById(MobileTerminalId id) {
        if(id == null || id.getGuid() == null || id.getGuid().isEmpty())
            throw new IllegalArgumentException("Non valid id: NULL or Empty");
        return terminalDao.getMobileTerminalById(UUID.fromString(id.getGuid()));
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(String serialNo) {
        if(serialNo == null || serialNo.isEmpty())
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
        if(mobileTerminal.getPlugin() == null){
            throw new NullPointerException("Cannot create Mobile terminal when plugin is null");
        }
        return serialNumber;
    }

    public void assertTerminalNotExists(UUID mobileTerminalGUID, String serialNr) {

        MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminalGUID);

        if(terminal != null){
            throw new IllegalArgumentException("Mobile terminal already exists in database for id: " + mobileTerminalGUID.toString());
        }

        MobileTerminal terminalBySerialNo = getMobileTerminalEntityBySerialNo(serialNr);
        if(terminalBySerialNo == null){  //aka the serial number does not exist in the db
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
        if(asset == null){
            throw new NotFoundException("No Asset with ID " + connectId + " found. Can not link Mobile Terminal.");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        String currentConnectId = null;
        if (terminal.getCurrentEvent().getAsset() != null){
            currentConnectId = terminal.getCurrentEvent().getAsset().getId().toString();
        }
        if (currentConnectId == null || currentConnectId.isEmpty()) {
            MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            MobileTerminalEvent event = new MobileTerminalEvent();
            event.setActive(true);
            event.setPollChannel(current.getPollChannel());
            event.setDefaultChannel(current.getDefaultChannel());
            event.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
            event.setConfigChannel(current.getConfigChannel());
            event.setAttributes(current.getAttributes());
            event.setComment(comment);
            event.setAsset(asset);

            event.setMobileterminal(terminal);
            event.setUpdateuser(username);
            event.setEventCodeType(EventCodeEnum.LINK);

            event.setMobileTerminalAttributes(current.getMobileTerminalAttributes());
            for(MobileTerminalAttributes mta : current.getMobileTerminalAttributes()){
                mta.setMobileTerminalEvent(event);
            }

            terminal.getMobileTerminalEvents().add(event);
            asset.getMobileTerminalEvent().add(event);

            terminalDao.updateMobileTerminal(terminal);

            return terminal;
        }
        throw new IllegalArgumentException("Terminal " + mobTermId + " is already linked to an asset with guid " + currentConnectId);
    }

    public MobileTerminal unAssignMobileTerminalFromCarrier(MobileTerminalAssignQuery query, String comment, String username) {
        if (query == null) {
            throw new IllegalArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new IllegalArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new IllegalArgumentException("No connect id in requesst");
        }

        MobileTerminalId mobTermId = query.getMobileTerminalId();
        String connectId = query.getConnectId();

        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        String currentConnectId = terminal.getCurrentEvent().getAsset().getId().toString();
        if (currentConnectId != null && currentConnectId.equals(connectId)) {
            MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            MobileTerminalEvent event = new MobileTerminalEvent();
            event.setActive(true);
            event.setPollChannel(current.getPollChannel());
            event.setDefaultChannel(current.getDefaultChannel());
            event.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
            event.setConfigChannel(current.getConfigChannel());
            event.setAttributes(current.getAttributes());
            event.setComment(comment);
            event.setAsset(null);
            event.setMobileterminal(terminal);
            event.setUpdateuser(username);
            event.setEventCodeType(EventCodeEnum.UNLINK);

            event.setMobileTerminalAttributes(current.getMobileTerminalAttributes());
            for(MobileTerminalAttributes mta : current.getMobileTerminalAttributes()){
                mta.setMobileTerminalEvent(event);
            }


            terminal.getMobileTerminalEvents().add(event);
            terminalDao.updateMobileTerminal(terminal);

            return terminal;
        }
        throw new IllegalArgumentException("Terminal " + mobTermId + " is not linked to an asset with guid " + connectId);
    }

    public MobileTerminal upsertMobileTerminal(MobileTerminal mobileTerminal, String username) {

        if (mobileTerminal == null) {
            throw new NullPointerException("RequestQuery is null");
        }
        /*if (mobileTerminalType.getMobileTerminalId() == null) {
            throw new NullPointerException("No Mobile terminalId in request");
        }*/

        try {

            MobileTerminal updatedMobileTerminal = updateMobileTerminal(mobileTerminal, "Upserted by external module", username);

            return updatedMobileTerminal;

        } catch (RuntimeException e) {
            LOG.error("[ Error when upserting mobile terminal: Mobile terminal update failed trying to insert. ] {} {}", e, e.getStackTrace());
            //TODO: Should this swallow an error and just continue on?
        }

        //MobileTerminal mobileTerminal1Entity = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminalType, assertTerminalHasSerialNumber(mobileTerminalType), plugin, username);
        MobileTerminal createdmobileTerminal = createMobileTerminal(mobileTerminal, username);

        return createdmobileTerminal;
    }

    public MobileTerminalHistory getMobileTerminalHistoryList(MobileTerminalId id) {
        if (id == null) {
            throw new NullPointerException("No Mobile Terminal");
        }
        MobileTerminal terminal = getMobileTerminalEntityById(id);
        return HistoryMapper.getHistory(terminal);
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
        int startIndex = (page-1)*listSize;
        int stopIndex = startIndex+listSize;
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

        int numberOfPages =  totalMatches / listSize;
        if (totalMatches % listSize != 0) {
            numberOfPages += 1;
        }
        response.setMobileTerminalList(mobileTerminalList);

        if ((totalMatches - 1) > 0) {
            if(stopIndex >= totalMatches) {
                stopIndex = totalMatches;
            }
            LOG.debug("stopIndex: " + stopIndex);
            response.setMobileTerminalList(new ArrayList<>(mobileTerminalList.subList(startIndex, stopIndex)));
        }
        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(page);

        return response;
    }

    public MobileTerminalType findMobileTerminalByAsset(UUID assetid) {
        MobileTerminal terminal = terminalDao.findMobileTerminalByAsset(assetid);
        if(terminal == null) return null;
        MobileTerminalType terminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        return terminalType;
    }

    public MobileTerminalType getMobileTerminalByRawMovement(RawMovementType rawMovement) {
        MobileTerminalListQuery query = new MobileTerminalListQuery();

        // If no mobile terminal information exists, don't look for one
        if (rawMovement.getMobileTerminal() == null || rawMovement.getMobileTerminal().getMobileTerminalIdList() == null) {
            return null;
        }

        List<IdList> ids = rawMovement.getMobileTerminal().getMobileTerminalIdList();
        MobileTerminalSearchCriteria criteria = new MobileTerminalSearchCriteria();
        for (IdList id : ids) {
            eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListCriteria crit = new eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListCriteria();
            switch (id.getType()) {
                case DNID:
                    if (id.getValue() != null) {
                        crit.setKey(eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey.DNID);
                        crit.setValue(id.getValue());
                        criteria.getCriterias().add(crit);
                    }
                    break;
                case MEMBER_NUMBER:
                    if (id.getValue() != null) {
                        crit.setKey(eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey.MEMBER_NUMBER);
                        crit.setValue(id.getValue());
                        criteria.getCriterias().add(crit);
                    }
                    break;
                case SERIAL_NUMBER:
                    if (id.getValue() != null) {
                        crit.setKey(eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey.SERIAL_NUMBER);
                        crit.setValue(id.getValue());
                        criteria.getCriterias().add(crit);
                    }
                    break;
                case LES:
                default:
                    LOG.error("[ERROR] Unhandled Mobile Terminal id: {} ]", id.getType());
                    break;
            }
        }

        // If no valid criterias, don't look for a mobile terminal
        if (criteria.getCriterias().isEmpty()) {
            return null;
        }

        // If we know the transponder type from the source, use it in the search criteria

        if(rawMovement.getSource() != null && rawMovement.getSource().name() != null){
            eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListCriteria transponderTypeCrit = new eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListCriteria();
            transponderTypeCrit.setKey(eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey.TRANSPONDER_TYPE);
            transponderTypeCrit.setValue(rawMovement.getSource().name());
            criteria.getCriterias().add(transponderTypeCrit);
        }

        query.setMobileTerminalSearchCriteria(criteria);
        eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination pagination = new eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination();
        // To leave room to find erroneous results - it must be only one in the list
        pagination.setListSize(2);
        pagination.setPage(1);
        query.setPagination(pagination);

        MobileTerminalListResponse mobileTerminalListResponse = getMobileTerminalList(query);
        List<MobileTerminalType> resultList = mobileTerminalListResponse.getMobileTerminal();
        return resultList.size() != 1 ? null : resultList.get(0);
    }

}
