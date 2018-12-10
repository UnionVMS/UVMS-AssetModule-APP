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
package eu.europa.ec.fisheries.uvms.mobileterminal.timer;

import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;

@Startup
@Singleton
public class MobileTerminalExecutorServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalExecutorServiceBean.class);

    @EJB
    private PollServiceBean pollService;

    @EJB
    private PluginTimerTask pluginTimerTask;

    private PollTimerTask pollTimerTask;

    @Resource
    private ManagedExecutorService executorService;
    
    @PostConstruct
    public void initPlugins() {
        try {
            executorService.submit(() -> pluginTimerTask.syncPlugins());
            LOG.info("PluginTimerTask initialized.");
        } catch (Exception e) {
            LOG.error("Error when initializing PluginTimerTask", e);
        }
    }
    
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void initPluginTimer() {
        try {
            pluginTimerTask.syncPlugins();
            LOG.info("PluginTimerTask initialized.");
        } catch (Exception e) {
            LOG.error("[ Error when initializing PluginTimerTask. ] {}", e);
        }
    }

    //TODO schedule: Ask andreas about how often we should do this check, change accordingly
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void initPollTimer() {
        try {
            if(pollTimerTask == null) {
                pollTimerTask = new PollTimerTask(pollService);
            }
            LOG.info("PollTimerTask initialized.");
            pollTimerTask.run();
        } catch (Exception e) {
            LOG.error("[ Error when initializing PollTimerTask. ] {}", e);
        }
    }
}
