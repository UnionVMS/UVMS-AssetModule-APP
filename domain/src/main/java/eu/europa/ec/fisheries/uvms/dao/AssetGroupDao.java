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
package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
@Local
public class AssetGroupDao extends Dao  {

    public AssetGroupEntity createAssetGroup(AssetGroupEntity group) {

        em.persist(group);
        return group;
    }

    public AssetGroupEntity getAssetGroupByGuid(UUID groupId) {
        try {
            TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID, AssetGroupEntity.class);
            query.setParameter("guid", groupId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetGroupEntity updateAssetGroup(AssetGroupEntity group) {
        em.merge(group);
        return group;
    }

    public AssetGroupEntity deleteAssetGroup(AssetGroupEntity group) {
        em.remove(group);
        return group;
    }

    public List<AssetGroupEntity> getAssetGroupAll() {
        TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_FIND_ALL, AssetGroupEntity.class);
        return query.getResultList();
    }

    public List<AssetGroupEntity> getAssetGroupByUser(String user) {
        TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroupEntity.class);
        query.setParameter("owner", user);
        return query.getResultList();
    }

    public List<AssetGroupEntity> getAssetGroupsByGroupGuidList(List<UUID> guidList) {
        TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID_LIST, AssetGroupEntity.class);
        query.setParameter("guidList", guidList);
        return query.getResultList();
    }

}