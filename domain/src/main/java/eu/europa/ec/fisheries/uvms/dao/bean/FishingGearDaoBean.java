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
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;


@Stateless
public class FishingGearDaoBean extends Dao implements FishingGearDao {

    private Logger LOG = LoggerFactory.getLogger(FishingGearDaoBean.class);

    @Override
    public List<FishingGear> getAllFishingGear(){
        TypedQuery<FishingGear> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_ALL, FishingGear.class);
        List<FishingGear> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public FishingGear getFishingGearById(Long id) throws Exception{
        try {
            TypedQuery<FishingGear> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_BY_ID, FishingGear.class);
            query.setParameter("id", id);
            FishingGear singleResult = query.getSingleResult();
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
    public FishingGear getFishingGearByExternalId(Long externalId) throws Exception{
        try {
            TypedQuery<FishingGear> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_BY_EXT_ID, FishingGear.class);
            query.setParameter("externalId", externalId);
            FishingGear singleResult = query.getSingleResult();
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
    public void create(FishingGear fishingGear){
        em.persist(fishingGear);
    }

    @Override
    public FishingGear update(FishingGear fishingGear){
        FishingGear result = em.merge(fishingGear);
        return result;
    }


    @Override
    public FishingGear getFishingGearByCode(String code)  {
        TypedQuery<FishingGear> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_FIND_BY_CODE, FishingGear.class);
        query.setParameter("code", code);
        List<FishingGear> resultsList = query.getResultList();
        if (resultsList == null || resultsList.isEmpty()) {
            LOG.warn("getFishingGearByCode found no results for code " + code);
            return null;
        }
        if (resultsList.size() > 1) {
            LOG.warn("getFishingGearByCode found more than one results for code " + code);
            return resultsList.get(0);
        }
        return resultsList.get(0);
    }
}
