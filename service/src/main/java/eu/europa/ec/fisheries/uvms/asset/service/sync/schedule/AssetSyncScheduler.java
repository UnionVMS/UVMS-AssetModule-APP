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
package eu.europa.ec.fisheries.uvms.asset.service.sync.schedule;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.service.sync.AssetSyncService;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Startup
@Slf4j
public class AssetSyncScheduler {

    private static final String FLEET_SYNC = "fleet-sync";
    public static final String FLEET_SYNC_CRON_SETTING_KEY = "asset.fleetsync.scheduler.cron.expression";

    @Inject
    private AssetSyncService assetSyncService;

    @Inject
    private ParameterService parameterService;

    @Resource
    private TimerService timerService;

    @Resource
    private ManagedExecutorService executorService;

    //////////////////////////////////
    //  public methods
    //////////////////////////////////

    @PostConstruct
    public void initTimer() {
        setTimer();
    }

    @Timeout
    public void timeout(Timer timer) {
        log.info("FLEET SYNC: Asset sync scheduler started");
        //assetSyncService.triggerSync();

        executorService.submit(() -> {
            assetSyncService.syncFleet(0);
        });
    }


    public void updateTimer(@Observes @ConfigSettingUpdatedEvent ConfigSettingEvent settingEvent) {
        if (FLEET_SYNC_CRON_SETTING_KEY.equals(settingEvent.getKey())) {
            setTimer();
        }
    }

    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    private void setTimer() {
        try {
            cancelExistingFleetSyncTimers();
            ScheduleExpression expr = createScheduleExpression(parameterService.getParamValueById(FLEET_SYNC_CRON_SETTING_KEY));
            timerService.createCalendarTimer(expr, new TimerConfig(FLEET_SYNC, false));
        } catch (ConfigServiceException e) {
            log.error("Could not initialize fleet sync scheduler with parameter {}", FLEET_SYNC_CRON_SETTING_KEY, e);
        }
    }

    private void cancelExistingFleetSyncTimers() {
        timerService.getAllTimers().forEach(t -> {
            if (FLEET_SYNC.equals(t.getInfo())) {
                t.cancel();
            }
        });
    }

    private ScheduleExpression createScheduleExpression(String cronSetting) {
        String[] cronArray = cronSetting.split(" ");
        ScheduleExpression expr = new ScheduleExpression();
        //quartz cron expre format: Seconds - Minutes - Hours - Day Of Month - Month - Day Of Week - Year
        expr.second(cronArray[0]);
        expr.minute(cronArray[1]);
        expr.hour(cronArray[2]);
        expr.dayOfMonth(cronArray[3]);
        expr.month(cronArray[4]);
        expr.dayOfWeek(cronArray[5]);
        expr.year(cronArray[6]);
        return expr;
    }
}
