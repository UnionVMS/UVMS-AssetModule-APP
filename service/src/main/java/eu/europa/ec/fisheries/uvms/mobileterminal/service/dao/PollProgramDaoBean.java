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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class PollProgramDaoBean {

    @PersistenceContext
    private EntityManager em;

    public void removePollProgrameAfterTests(String id){
        PollProgram pollProgram = getPollProgramById(UUID.fromString(id));
        em.remove(em.contains(pollProgram) ? pollProgram : em.merge(pollProgram));
    }

    public void createPollProgram(PollProgram pollProgram) {
        em.persist(pollProgram);
    }

    public PollProgram updatePollProgram(PollProgram pollProgram) {
        pollProgram = em.merge(pollProgram);
        return pollProgram;
    }

    public List<PollProgram> getProgramPollsAlive()  {
        TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, PollProgram.class);
        query.setParameter("currentDate", OffsetDateTime.now(ZoneOffset.UTC));
        return query.getResultList();
    }

    public List<PollProgram> getPollProgramRunningAndStarted()  {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED, PollProgram.class);
            query.setParameter("currentDate", OffsetDateTime.now(ZoneOffset.UTC)/*.toString()*/);
            List<PollProgram> pollPrograms = query.getResultList();
            List<PollProgram> validPollPrograms = new ArrayList<>();

            for (PollProgram pollProgram : pollPrograms) {
                OffsetDateTime lastRun = pollProgram.getLatestRun();
                Integer frequency = pollProgram.getFrequency();
                OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

                long lastRunEpoch = lastRun == null ? 0 : lastRun.toEpochSecond();
                long nowEpoch = now.toEpochSecond();

                boolean createPoll = lastRun == null || nowEpoch >= lastRunEpoch + frequency * 1000;

                if (createPoll) {
                    pollProgram.setLatestRun(now);
                    validPollPrograms.add(pollProgram);
                }
            }
            return validPollPrograms;
    }

    public PollProgram getPollProgramByGuid(String guid) {
        return getPollProgramById(UUID.fromString(guid));
    }

    public PollProgram getPollProgramById(UUID id) {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, PollProgram.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
