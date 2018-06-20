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

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalFaultException;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.MTMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.MTMessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalTypeComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.HistoryMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalModelToEntityMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.SearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    /**
     * {@inheritDoc}
     *
     * @param mobileTerminal
     * @throws MobileTerminalException
     */
    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username) throws MobileTerminalException, MobileTerminalModelException {
        mobileTerminal.setSource(source);
        MobileTerminalType createdMobileTerminal = createMobileTerminal(mobileTerminal, username);
        boolean dnidUpdated = configModel.checkDNIDListChange(createdMobileTerminal.getPlugin().getServiceName());
        
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalCreated(createdMobileTerminal.getMobileTerminalId().getGuid(), username);
            MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was created", createdMobileTerminal.getMobileTerminalId()
                    .getGuid());
        }
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(createdMobileTerminal.getPlugin().getServiceName());
        }
        
        return createdMobileTerminal;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws MobileTerminalModelException
     */
    public MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query) throws MobileTerminalModelException {
        ListResponseDto listResponse = getTerminalListByQuery(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());
        return response;
    }

    /**
     * {@inheritDoc}
     *
     * @param guid
     * @return
     * @throws MobileTerminalModelException
     */
    public MobileTerminalType getMobileTerminalById(String guid) throws MobileTerminalModelException {
        if (guid == null) {
            throw new IllegalArgumentException("No id");
        }
        MobileTerminalId id = new MobileTerminalId();
        id.setGuid(guid);
        MobileTerminalType terminalGet = getMobileTerminalById(id);
        return terminalGet;
    }

    /**
     *
     * {@inheritDoc}
     *
     * @param data
     * @return
     * @throws MobileTerminalModelException
     */
    public MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username) throws MobileTerminalModelException {
        if (data == null) {
            throw new IllegalArgumentException("No Mobile terminal to update [ NULL ]");
        }
        data.setSource(source);
        MobileTerminalType terminalUpserted = upsertMobileTerminal(data, username);
        
        boolean dnidUpdated = configModel.checkDNIDListChange(terminalUpserted.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(data.getPlugin().getServiceName());
        }
        
        return terminalUpserted;
    }

    /**
     * {@inheritDoc}
     *
     * @param id
     * @throws MobileTerminalModelException
     */
    public MobileTerminalType getMobileTerminalById(MobileTerminalId id, DataSourceQueue queue) throws MobileTerminalException, MobileTerminalModelException, MobileTerminalFaultException {
        if (id == null) {
            throw new IllegalArgumentException("No id");
        }
        if (queue != null && queue.equals(DataSourceQueue.INTERNAL)) {
            return getMobileTerminalById(id.getGuid());
        }
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminal(id);
        String messageId = MTMessageProducer.sendDataSourceMessage(data, queue);
        TextMessage response = MTMessageConsumer.getMessage(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
    }

    /**
     * {@inheritDoc}
     *
     * @param mobileTerminal
     * @param source
     */
    public MobileTerminalType updateMobileTerminal(MobileTerminalType mobileTerminal, String comment, MobileTerminalSource source, String username)
            throws MobileTerminalException, MobileTerminalModelException {
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

    public MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException, MobileTerminalModelException {
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

    public MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException, MobileTerminalModelException {
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

    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username)
            throws MobileTerminalException {
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

    public MobileTerminalHistory getMobileTerminalHistoryList(String guid) throws MobileTerminalModelException {
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(guid);
        MobileTerminalHistory historyList = getMobileTerminalHistoryList(terminalId);
        return historyList;
    }

    public MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query) throws MobileTerminalModelException {
        ListResponseDto listResponse = pollModel.getMobileTerminalPollableList(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());
        return response;
    }

    /***************************************************************************************************************************/

    public MobileTerminal getMobileTerminalEntityById(MobileTerminalId id) {
        if(id == null || id.getGuid() == null || id.getGuid().isEmpty())
            throw new IllegalArgumentException("Non valid id");
        return terminalDao.getMobileTerminalByGuid(id.getGuid());
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(String serialNo) {
        if(serialNo == null || serialNo.isEmpty())
            throw new IllegalArgumentException("Non valid serial no");
        return terminalDao.getMobileTerminalBySerialNo(serialNo);
    }

    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, String username) throws MobileTerminalModelException {
        try {
            assertTerminalNotExists(mobileTerminal);
            String serialNumber = assertTerminalHasNeededData(mobileTerminal);

            MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getServiceName());

            MobileTerminal terminal = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminal, serialNumber, plugin, username);
            terminalDao.createMobileTerminal(terminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        } catch (MobileTerminalModelException e) {
            LOG.error("Error in model when creating mobile terminal: {}", e.getMessage());
            throw new MobileTerminalModelException("Error when persisting terminal " + mobileTerminal.getMobileTerminalId());
        }
    }

    private String assertTerminalHasNeededData(MobileTerminalType mobileTerminal) throws MobileTerminalModelException {
        String serialNumber = null;
        for (MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
            if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType()) &&
                    attribute.getValue() != null && !attribute.getValue().isEmpty()) {
                serialNumber = attribute.getValue();
                break;
            }
        }

        if (serialNumber == null) {
            throw new MobileTerminalModelException("Cannot create mobile terminal without serial number");
        }
        if(mobileTerminal.getPlugin() == null){
            throw new MobileTerminalModelException("Cannot create Mobile terminal when plugin is not null");
        }

        return serialNumber;
    }

    private void assertTerminalNotExists(MobileTerminalType mobileTerminal) throws MobileTerminalModelException {
        try {
            MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminal.getMobileTerminalId());
            throw new MobileTerminalModelException("Mobile terminal already exists in database for id: " + mobileTerminal.getMobileTerminalId());
        } catch (IllegalArgumentException e) {
            //Terminal does not exist, ok to create a new one
        }
        try {
            for (MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
                if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType())) {
                    MobileTerminal terminal = getMobileTerminalEntityBySerialNo(attribute.getValue());
                    if (!terminal.getArchived()) {
                        throw new MobileTerminalModelException("Mobile terminal already exists in database for serial number: " + attribute.getValue());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            //Terminal does not exist, ok to create a new one
        }
    }

    public MobileTerminalType getMobileTerminalById(MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new IllegalArgumentException("No id to fetch");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }

    public MobileTerminalType updateMobileTerminal(MobileTerminalType model, String comment, String username) throws MobileTerminalModelException {
        if (model == null) {
            throw new IllegalArgumentException("No terminal to update");
        }
        if (model.getMobileTerminalId() == null || model.getMobileTerminalId().getGuid() == null || model.getMobileTerminalId().getGuid().isEmpty()) {
            throw new IllegalArgumentException("Non valid id of terminal to update");
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

        String serialNumber = assertTerminalHasNeededData(model);

        //TODO check type
        if(terminal.getMobileTerminalType() != null) {
            MobileTerminal updatedTerminal = MobileTerminalModelToEntityMapper.mapMobileTerminalEntity(terminal, model, serialNumber, updatedPlugin, username, comment, EventCodeEnum.MODIFY);
            terminalDao.updateMobileTerminal(updatedTerminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(updatedTerminal);

        }
        throw new MobileTerminalModelException("Update - Not supported mobile terminal type");
    }

    public MobileTerminalType assignMobileTerminalToCarrier(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalModelException {
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
            event.setUpdatetime(DateUtils.getNowDateUTC());
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

        throw new MobileTerminalModelException("Terminal " + mobTermId + " is already linked to an asset with guid " + currentConnectId);
    }

    public MobileTerminalType unAssignMobileTerminalFromCarrier(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalModelException {
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
            event.setUpdatetime(DateUtils.getNowDateUTC());
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

        throw new MobileTerminalModelException("Terminal " + mobTermId + " is not linked to an asset with guid " + connectId);
    }

    public MobileTerminalType upsertMobileTerminal(MobileTerminalType mobileTerminal, String username) throws MobileTerminalModelException {

        if (mobileTerminal == null) {
            throw new IllegalArgumentException("RequestQuery is null");
        }
        if (mobileTerminal.getMobileTerminalId() == null) {
            throw new IllegalArgumentException("No Mobile terminalId in request");
        }

        try {
            return updateMobileTerminal(mobileTerminal, "Upserted by external module", username);
        } catch (NumberFormatException | MobileTerminalModelException e) {
            LOG.error("[ Error when upserting mobile terminal: Mobile terminal update failed trying to insert. ] {} {}", e.getMessage(), e.getStackTrace());
            if (e instanceof MobileTerminalModelException) {
                throw e;
            }
        }
        return createMobileTerminal(mobileTerminal, username);
    }


    public MobileTerminalHistory getMobileTerminalHistoryList(MobileTerminalId id) throws MobileTerminalModelMapperException {
        if (id == null) {
            throw new IllegalArgumentException("No Mobile Terminal");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);

        return HistoryMapper.getHistory(terminal);
    }

    public ListResponseDto getTerminalListByQuery(MobileTerminalListQuery query) throws MobileTerminalModelException {
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

        Collections.sort(mobileTerminalList, new MobileTerminalTypeComparator());
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
