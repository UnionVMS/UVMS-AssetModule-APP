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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.timer;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;

@Startup
@Singleton
public class MobileTerminalExecutorServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalExecutorServiceBean.class);

    @EJB
    private ConfigService configService;

    @EJB
    private PollService pollService;

    private PluginTimerTask pluginTimerTask;
    private PollTimerTask pollTimerTask;

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void initPlugins() {
        try {
            if(pluginTimerTask == null) {
                pluginTimerTask = new PluginTimerTask(configService);
            }
            pluginTimerTask.run();
        } catch (Exception e) {
            LOG.error("Error when initializing PluginTimerTask", e);
        }
    }
    
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void initPluginTimer() {
        try {
            if(pluginTimerTask == null) {
                pluginTimerTask = new PluginTimerTask(configService);
            }
            LOG.info("PluginTimerTask initialized.");
            pluginTimerTask.run();
        } catch (Exception e) {
            LOG.error("[ Error when initializing PluginTimerTask. ] {}", e.getMessage());
        }
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false) //TODO schedule: Ask andreas about how often we should do this check, change accordingly
    public void initPollTimer() {
        try {
            if(pollTimerTask == null) {
                pollTimerTask = new PollTimerTask(pollService);
            }
            LOG.info("PollTimerTask initialized.");
            pollTimerTask.run();
        } catch (Exception e) {
            LOG.error("[ Error when initializing PollTimerTask. ] {}", e.getMessage());
        }
    }
}
