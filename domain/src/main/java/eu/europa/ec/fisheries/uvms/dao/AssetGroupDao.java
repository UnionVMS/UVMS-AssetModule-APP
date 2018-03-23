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

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import static eu.europa.ec.fisheries.uvms.entity.AssetGroup.*;
import java.util.List;
import java.util.UUID;

@Stateless
@Local
public class AssetGroupDao  {

    @PersistenceContext
    private EntityManager em;


    public AssetGroup createAssetGroup(AssetGroup group) {
        em.persist(group);
        return group;
    }

    public AssetGroup getAssetGroupByGuid(UUID groupId) {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(GROUP_ASSET_BY_GUID, AssetGroup.class);
            query.setParameter("guid", groupId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetGroup updateAssetGroup(AssetGroup group) {
        em.merge(group);
        return group;
    }

    public AssetGroup deleteAssetGroup(AssetGroup group) {
        em.remove(group);
        return group;
    }

    public List<AssetGroup> getAssetGroupAll() {
        TypedQuery<AssetGroup> query = em.createNamedQuery(GROUP_ASSET_FIND_ALL, AssetGroup.class);
        return query.getResultList();
    }

    public List<AssetGroup> getAssetGroupByUser(String user) {
        TypedQuery<AssetGroup> query = em.createNamedQuery(GROUP_ASSET_BY_USER, AssetGroup.class);
        query.setParameter("owner", user);
        return query.getResultList();
    }

    public List<AssetGroup> getAssetGroupsByGroupGuidList(List<UUID> guidList) {
        TypedQuery<AssetGroup> query = em.createNamedQuery(GROUP_ASSET_BY_GUID_LIST, AssetGroup.class);
        query.setParameter("guidList", guidList);
        return query.getResultList();
    }

}