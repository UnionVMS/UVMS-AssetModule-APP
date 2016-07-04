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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
public class AssetGroupServiceBean implements AssetGroupService {

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetQueueConsumer receiver;

    final static Logger LOG = LoggerFactory.getLogger(AssetGroupServiceBean.class);

    @Override
    public List<AssetGroup> getAssetGroupList(String user) throws AssetException {
        LOG.info("Getting asset group list by user: {}.", user);
        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }
        String data = AssetDataSourceRequestMapper.mapAssetGroupListByUserRequest(user);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessage(messageId, TextMessage.class);
        return AssetDataSourceResponseMapper.mapToAssetGroupListFromResponse(response, messageId);
    }

    @Override
    public List<AssetGroup> getAssetGroupListByAssetGuid(String assetGuid) throws AssetException {
        LOG.info("Getting asset group list by asset guid: {}.", assetGuid);
        if (assetGuid == null || assetGuid.isEmpty()) {
            throw new InputArgumentException("Invalid asset");
        }
        String data = AssetDataSourceRequestMapper.mapAssetGroupListByAssetGuidRequest(assetGuid);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessage(messageId, TextMessage.class);
        return AssetDataSourceResponseMapper.mapToAssetGroupListFromResponse(response, messageId);
    }

    @Override
    public AssetGroup getAssetGroupById(String guid) throws AssetException {
        LOG.info("Getting asset group by id: {}.", guid);

        if (guid == null) {
            throw new InputArgumentException("No asset group to get");
        }
        String data = AssetDataSourceRequestMapper.mapGetAssetGroupById(guid);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessage(messageId, TextMessage.class);
        return AssetDataSourceResponseMapper.mapToAssetGroupFromResponse(response, messageId);
    }

    @Override
    public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws AssetException {
        LOG.info("Creating asset group.");

        if (assetGroup == null) {
            throw new InputArgumentException("No asset group to create");
        }
        String data = AssetDataSourceRequestMapper.mapCreateAssetGroup(assetGroup, username);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessage(messageId, TextMessage.class);

        AssetGroup createdAssetGroup = AssetDataSourceResponseMapper.mapToAssetGroupFromResponse(response, messageId);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetGroupCreated(createdAssetGroup.getGuid(), username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset Group with id {} was created", createdAssetGroup.getGuid());
        }

        return createdAssetGroup;
    }

    @Override
    public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws AssetException {
        LOG.info("Updating asset group.");

        if (assetGroup == null) {
            throw new InputArgumentException("No asset group to update");
        }
        if (assetGroup.getGuid() == null) {
            throw new InputArgumentException("No id on asset group to update");
        }

        String data = AssetDataSourceRequestMapper.mapUpdateAssetGroup(assetGroup, username);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessage(messageId, TextMessage.class);

        AssetGroup updatedAssetGroup = AssetDataSourceResponseMapper.mapToAssetGroupFromResponse(response, messageId);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetGroupUpdated(updatedAssetGroup.getGuid(), username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset Group with id {} was updated", updatedAssetGroup.getGuid());
        }

        return updatedAssetGroup;
    }

    @Override
    public AssetGroup deleteAssetGroupById(String guid, String username) throws AssetException {
        LOG.info("Deleting asset group by id: {}.", guid);

        if (guid == null) {
            throw new InputArgumentException("No asset group to remove");
        }
        String data = AssetDataSourceRequestMapper.mapRemoveAssetGroupById(guid, username);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessage(messageId, TextMessage.class);

        AssetGroup deletedAssetGroup = AssetDataSourceResponseMapper.mapToAssetGroupFromResponse(response, messageId);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetGroupDeleted(deletedAssetGroup.getGuid(), username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset Group with id {} was deleted", deletedAssetGroup.getGuid());
        }

        return deletedAssetGroup;
    }

}