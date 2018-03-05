package eu.europa.ec.fisheries.uvms.dao.bean;
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

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.FishingGearDao;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class FishingGearDaoBean extends Dao implements FishingGearDao {

    private Logger LOG = LoggerFactory.getLogger(FishingGearDaoBean.class);

    @Override
    public List<FishingGearEntity> getAllFishingGear(){
        TypedQuery<FishingGearEntity> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_ALL, FishingGearEntity.class);
        List<FishingGearEntity> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public FishingGearEntity getFishingGearById(Long id) throws Exception{
        try {
            TypedQuery<FishingGearEntity> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_BY_ID, FishingGearEntity.class);
            query.setParameter("id", id);
            FishingGearEntity singleResult = query.getSingleResult();
            return singleResult;
        }catch (NoResultException e){
            LOG.debug("getFishingGearById: " + id + " has no entity found");
        }catch (NonUniqueResultException ex){
            LOG.debug("NonUniqueResultException getFishingGearById: " + id  );
            throw ex;
        }catch (Exception e){
            LOG.debug("Unexpected exception getFishingGearById: " + id  );
            throw e;
        }
        return null;
    }

    @Override
    public FishingGearEntity getFishingGearByExternalId(Long externalId) throws Exception{
        try {
            TypedQuery<FishingGearEntity> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_BY_EXT_ID, FishingGearEntity.class);
            query.setParameter("externalId", externalId);
            FishingGearEntity singleResult = query.getSingleResult();
            return singleResult;
        }catch (NoResultException e){
            LOG.debug("GetFishingGearByExternalId: " + externalId + " has no entity found");
        }catch (NonUniqueResultException ex){
            LOG.debug("NonUniqueResultException GetFishingGearByExternalId: " + externalId );
            throw ex;
        }catch (Exception e){
            LOG.debug("Unexpected exception GetFishingGearByExternalId: " + externalId  );
            throw e;
        }
        return null;
    }

    @Override
    public void create(FishingGearEntity fishingGear){
        em.persist(fishingGear);
    }

    @Override
    public FishingGearEntity update(FishingGearEntity fishingGear){
        FishingGearEntity result = em.merge(fishingGear);
        return result;
    }
}
