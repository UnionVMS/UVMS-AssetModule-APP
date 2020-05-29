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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.bean.AssetGroupDomainModelBean;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.ZeroBasedIndexListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AssetGroupServiceBean implements AssetGroupService {

    private static  final String GROUP_QUALIFIER_PREFIX = "Group: ";

    @EJB
    private AssetMessageProducer messageProducer;

    @EJB
    private AssetGroupDomainModelBean assetGroupDomainModel;

    final static Logger LOG = LoggerFactory.getLogger(AssetGroupServiceBean.class);

    @Override
    public List<AssetGroup> getAssetGroupList(String user) throws AssetException {
        LOG.info("Getting asset group list by user: {}.", user);
        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }

        return assetGroupDomainModel.getAssetGroupListByUser(user);
    }

    @Override
    public ZeroBasedIndexListAssetGroupResponse getAssetGroupList(String user, AssetListQuery assetQuery) throws AssetException {
        LOG.info("Getting asset group list by user: {}.", user);
        if (user == null || user.isEmpty()) {
            throw new InputArgumentException("Invalid user");
        }

        return assetGroupDomainModel.getZeroBasedAssetGroupListByUser(user,assetQuery);
    }

    @Override
    public List<AssetGroup> getAssetGroupListByAssetGuid(String assetGuid) throws AssetException {
        LOG.info("Getting asset group list by asset guid: {}.", assetGuid);
        if (assetGuid == null || assetGuid.isEmpty()) {
            throw new InputArgumentException("Invalid asset");
        }

        List<AssetGroup> assetGroups = assetGroupDomainModel.getAssetGroupsByAssetGuid(assetGuid);
        return assetGroups;
    }

    @Override
    public AssetGroup getAssetGroupById(String guid) throws AssetException {
        LOG.info("Getting asset group by id: {}.", guid);
        if (guid == null) {
            throw new InputArgumentException("No asset group to get");
        }

        AssetGroup assetGroup = assetGroupDomainModel.getAssetGroup(guid);
        return assetGroup;
    }

    @Override
    public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws AssetException {
        if (assetGroup == null) {
            throw new InputArgumentException("No asset group to create");
        }
        AssetGroup createdAssetGroup = assetGroupDomainModel.createAssetGroup(assetGroup, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetGroupCreated(createdAssetGroup.getGuid(), username, GROUP_QUALIFIER_PREFIX + createdAssetGroup.getName());
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset Group with id {} was created", createdAssetGroup.getGuid());
        }
        return createdAssetGroup;
    }

    @Override
    public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws AssetException {
        if (assetGroup == null) {
            throw new InputArgumentException("No asset group to update");
        }
        if (assetGroup.getGuid() == null) {
            throw new InputArgumentException("No id on asset group to update");
        }
        AssetGroup updatedAssetGroup = assetGroupDomainModel.updateAssetGroup(assetGroup, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetGroupUpdated(updatedAssetGroup.getGuid(), username, GROUP_QUALIFIER_PREFIX + updatedAssetGroup.getName());
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

        AssetGroup deletedAssetGroup = assetGroupDomainModel.deleteAssetGroup(guid, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetGroupDeleted(deletedAssetGroup.getGuid(),  username, GROUP_QUALIFIER_PREFIX  + deletedAssetGroup.getName() );
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset Group with id {} was deleted", deletedAssetGroup.getGuid());
        }
        return deletedAssetGroup;
    }

}