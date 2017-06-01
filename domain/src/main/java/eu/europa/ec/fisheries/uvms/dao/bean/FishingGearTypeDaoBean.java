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
import eu.europa.ec.fisheries.uvms.dao.FishingGearTypeDao;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.*;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;

@Stateless
public class FishingGearTypeDaoBean extends Dao implements FishingGearTypeDao {

    private Logger LOG = LoggerFactory.getLogger(FishingGearTypeDaoBean.class);


    @Override
    public FishingGearType getFishingGearByCode(Long code)throws Exception{
        try{
            TypedQuery<FishingGearType> query = em.createNamedQuery(UvmsConstants.FISHING_GEAR_TYPE_FIND_BY_CODE, FishingGearType.class);
            query.setParameter("code", code);
            FishingGearType result = query.getSingleResult();
            return result;
        }catch (NoResultException e) {
            LOG.debug("getFishingGearByCode: " + code + " has no entity found");
        }catch (NonUniqueResultException ex){
            LOG.error("NonUniqueResultException exception, code: " + code);
            throw ex;
        }catch (Exception e){
            LOG.error("Unexpected exception code: " + code);
            throw e;

        }
        return null;
    }

    @Override
    public void create(FishingGearType fishingGearType){
        em.persist(fishingGearType);
    }

    @Override
    public FishingGearType update(FishingGearType fishingGearType){
        FishingGearType result = em.merge(fishingGearType);
        return result;
    }

    private void getGearTypeById(long id){
        FishingGearType fishingGearType = em.find(FishingGearType.class, id);
        LOG.debug(fishingGearType.getId().toString());

    }
}
