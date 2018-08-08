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
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.ErrorCode.MT_PARSING_ERROR;

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

    @EJB
    private MobileTerminalPluginDaoBean pluginDao;

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


    public MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username) {
        if (data == null) {
            throw new NullPointerException("No Mobile terminal to update [ NULL ]");
        }
        data.setSource(source);
        MobileTerminalType terminalUpserted = upsertMobileTerminal(data, username);
        
        boolean dnidUpdated = configModel.checkDNIDListChange(terminalUpserted.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(data.getPlugin().getServiceName());
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

    public MobileTerminalType updateMobileTerminal(MobileTerminalType mobileTerminal, String comment, MobileTerminalSource source, String username) {
        mobileTerminal.setSource(source);
        MobileTerminalType terminalUpdate = updateMobileTerminal(mobileTerminal, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUpdated(terminalUpdate.getMobileTerminalId().getGuid(), comment, username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was updated", terminalUpdate.getMobileTerminalId()
                    .getGuid());
        }
        
        boolean dnidUpdated = configModel.checkDNIDListChange(terminalUpdate.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(terminalUpdate.getPlugin().getServiceName());
        }
        
        return terminalUpdate;
    }

    public MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) {
        MobileTerminalType terminalAssign = assignMobileTerminalToCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(terminalAssign.getMobileTerminalId().getGuid(), comment, username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", terminalAssign.getMobileTerminalId()
                    .getGuid());
        }

        return terminalAssign;
    }

    public MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) {
        MobileTerminalType terminalUnAssign = unAssignMobileTerminalFromCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(terminalUnAssign.getMobileTerminalId().getGuid(), comment, username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", terminalUnAssign.getMobileTerminalId()
                    .getGuid());
        }

        return terminalUnAssign;
    }

    // TODO: This method recurses infinitely!!!
    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username) {
        MobileTerminalType terminalStatus = setStatusMobileTerminal(terminalId, comment, status, username);
        try {
            String auditData = null;
            switch (status) {
            case ACTIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalActivated(terminalStatus.getMobileTerminalId().getGuid(),comment, username);
                break;
            case INACTIVE:
                auditData = AuditModuleRequestMapper
                .mapAuditLogMobileTerminalInactivated(terminalStatus.getMobileTerminalId().getGuid(), comment, username);
                break;
            case ARCHIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalArchived(terminalStatus.getMobileTerminalId().getGuid(), comment,username);
                break;
            default:
                break;
            }
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was set to status {}", terminalStatus
                    .getMobileTerminalId().getGuid(), status);
        }

        boolean dnidUpdated = configModel.checkDNIDListChange(terminalStatus.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(terminalStatus.getPlugin().getServiceName());
        }
        
        return terminalStatus;
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

    /***************************************************************************************************************************/

    public MobileTerminal getMobileTerminalEntityById(UUID id)
    {
        if(id == null)
            throw new IllegalArgumentException("Non valid id: " + id);
        return terminalDao.getMobileTerminalById(id);
    }

    public MobileTerminal getMobileTerminalEntityById(MobileTerminalId id) {
        if(id == null || id.getGuid() == null || id.getGuid().isEmpty())
            throw new IllegalArgumentException("Non valid id: " + id);
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
        MobileTerminal terminal = null;

        if(mobileTerminalGUID == null || mobileTerminalGUID.toString().isEmpty()){
            //do nothing
        }else{
            terminal = getMobileTerminalEntityById(mobileTerminalGUID);
        }

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

    public MobileTerminalType updateMobileTerminal(MobileTerminalType model, String comment, String username) {
        if (model == null) {
            throw new NullPointerException("No terminal to update");
        }
        if (model.getMobileTerminalId() == null || model.getMobileTerminalId().getGuid() == null || model.getMobileTerminalId().getGuid().isEmpty()) {
            throw new NullPointerException("Non valid id of terminal to update");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(model.getMobileTerminalId());
        MobileTerminalPlugin updatedPlugin = null;

        if(model.getPlugin() != null && model.getPlugin().getLabelName() != null && terminal.getPlugin() != null) {
            if(!model.getPlugin().getLabelName().equalsIgnoreCase(terminal.getPlugin().getName())) {
                updatedPlugin = pluginDao.getPluginByServiceName(model.getPlugin().getServiceName());
                terminal.setPlugin(updatedPlugin);
            }
        }

        if (updatedPlugin == null) {
            updatedPlugin = terminal.getPlugin();
        }

        String serialNumber = assertTerminalHasSerialNumber(model);

        //TODO check type
        if(terminal.getMobileTerminalType() != null) {
            MobileTerminal updatedTerminal = MobileTerminalModelToEntityMapper.mapMobileTerminalEntity(terminal, model, serialNumber, updatedPlugin, username, comment, EventCodeEnum.MODIFY);
            terminalDao.updateMobileTerminal(updatedTerminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(updatedTerminal);

        }
        throw new UnsupportedOperationException("Update - Not supported mobile terminal type");
    }

    public MobileTerminalType assignMobileTerminalToCarrier(MobileTerminalAssignQuery query, String comment, String username) {
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

        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        String currentConnectId = terminal.getCurrentEvent().getConnectId();
        if (currentConnectId == null || currentConnectId.isEmpty()) {
            MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            MobileTerminalEvent event = new MobileTerminalEvent();
            event.setActive(true);
            event.setPollChannel(current.getPollChannel());
            event.setDefaultChannel(current.getDefaultChannel());
            event.setUpdatetime(LocalDateTime.now(ZoneOffset.UTC));
            event.setConfigChannel(current.getConfigChannel());
            event.setAttributes(current.getAttributes());
            event.setComment(comment);
            event.setConnectId(connectId);
            event.setMobileterminal(terminal);
            event.setUpdateuser(username);
            event.setEventCodeType(EventCodeEnum.LINK);
            terminal.getMobileTerminalEvents().add(event);
            terminalDao.updateMobileTerminal(terminal);

            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        }

        throw new IllegalArgumentException("Terminal " + mobTermId + " is already linked to an asset with guid " + currentConnectId);
    }

    public MobileTerminalType unAssignMobileTerminalFromCarrier(MobileTerminalAssignQuery query, String comment, String username) {
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
        String currentConnectId = terminal.getCurrentEvent().getConnectId();
        if (currentConnectId != null && currentConnectId.equals(connectId)) {
            MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            MobileTerminalEvent event = new MobileTerminalEvent();
            event.setActive(true);
            event.setPollChannel(current.getPollChannel());
            event.setDefaultChannel(current.getDefaultChannel());
            event.setUpdatetime(LocalDateTime.now(ZoneOffset.UTC));
            event.setConfigChannel(current.getConfigChannel());
            event.setAttributes(current.getAttributes());
            event.setComment(comment);
            event.setConnectId(null);
            event.setMobileterminal(terminal);
            event.setUpdateuser(username);
            event.setEventCodeType(EventCodeEnum.UNLINK);
            terminal.getMobileTerminalEvents().add(event);
            terminalDao.updateMobileTerminal(terminal);

            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        }

        throw new IllegalArgumentException("Terminal " + mobTermId + " is not linked to an asset with guid " + connectId);
    }

    public MobileTerminalType upsertMobileTerminal(MobileTerminalType mobileTerminal, String username) {

        if (mobileTerminal == null) {
            throw new NullPointerException("RequestQuery is null");
        }
        if (mobileTerminal.getMobileTerminalId() == null) {
            throw new NullPointerException("No Mobile terminalId in request");
        }

        try {
            return updateMobileTerminal(mobileTerminal, "Upserted by external module", username);
        } catch (RuntimeException e) {
            LOG.error("[ Error when upserting mobile terminal: Mobile terminal update failed trying to insert. ] {} {}", e.getMessage(), e.getStackTrace());
        }

        MobileTerminal mobileTerminal1Entity = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminal, assertTerminalHasSerialNumber(mobileTerminal), pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getServiceName()), username);
        mobileTerminal1Entity = createMobileTerminal(mobileTerminal1Entity, username);

        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal1Entity);
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
        if (query.getMobileTerminalSearchCriteria().getCriterias() == null) {
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
}
