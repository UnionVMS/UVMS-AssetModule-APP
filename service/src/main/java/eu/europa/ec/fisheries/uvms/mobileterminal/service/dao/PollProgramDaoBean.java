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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.dao;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class PollProgramDaoBean {

    @PersistenceContext
    private EntityManager em;

    private final static Logger LOG = LoggerFactory.getLogger(PollProgramDaoBean.class);

    public void createPollProgram(PollProgram pollProgram) {
        em.persist(pollProgram);
    }

    public PollProgram updatePollProgram(PollProgram pollProgram) {
        pollProgram = em.merge(pollProgram);
        em.flush();
        return pollProgram;
    }

    public List<PollProgram> getProgramPollsAlive()  {
        TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, PollProgram.class);
        query.setParameter("currentDate", DateUtils.getUTCNow());
        return query.getResultList();
    }

    public List<PollProgram> getPollProgramRunningAndStarted()  {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED, PollProgram.class);
            query.setParameter("currentDate", DateUtils.getUTCNow());
            List<PollProgram> pollPrograms = query.getResultList();
            List<PollProgram> validPollPrograms = new ArrayList<>();

            for (PollProgram pollProgram : pollPrograms) {
                Date lastRun = pollProgram.getLatestRun();
                Integer frequency = pollProgram.getFrequency();
                Date now = DateUtils.getUTCNow();

                boolean createPoll = lastRun == null || now.getTime() >= lastRun.getTime() + frequency * 1000;

                if (createPoll) {
                    pollProgram.setLatestRun(now);
                    validPollPrograms.add(pollProgram);
                }
            }
            return validPollPrograms;
    }

    public PollProgram getPollProgramByGuid(String guid) throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, PollProgram.class);
            query.setParameter("guid", guid);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting poll program by id. ] {}", e.getMessage());
            throw new PollDaoException("No entity found getting by id");
        }
    }
}
