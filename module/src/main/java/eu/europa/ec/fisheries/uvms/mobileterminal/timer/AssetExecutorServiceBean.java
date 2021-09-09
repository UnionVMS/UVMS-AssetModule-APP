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

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingRequest;
import eu.europa.ec.fisheries.uvms.asset.message.AssetConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.ExchangeProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;

@Startup
@Singleton
public class AssetExecutorServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetExecutorServiceBean.class);
    
    private static final long RECEIVE_TIMEOUT = 600000L;

    @EJB
    private PollServiceBean pollService;

    @EJB
    private PluginTimerTask pluginTimerTask;

    private PollTimerTask pollTimerTask;

    @Inject
    private ExchangeProducer exchangeProducer;

    @EJB
    private AssetConsumer assetConsumer;

    @Inject
    private AssetRemapTask assetRemapTask;
    
    @Resource
    private ManagedExecutorService executorService;
    
    @PostConstruct
    public void initPlugins() {
        try {
            executorService.submit(() -> {
                pingExchange(2);
                pluginTimerTask.syncPlugins();
                pingExchange(10);
                pluginTimerTask.syncPlugins();
                LOG.info("Done synchronizing plugins at startup");
            });
        } catch (Exception e) {
            LOG.error("Error when synchronizing plugins at startup", e);
        }
    }
    
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void initPluginTimer() {
        try {
            pluginTimerTask.syncPlugins();
            LOG.info("PluginTimerTask initialized.");
        } catch (Exception e) {
            LOG.error("[ Error when initializing PluginTimerTask. ]", e);
        }
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void initPollTimer() {
        try {
            if(pollTimerTask == null) {
                pollTimerTask = new PollTimerTask(pollService);
            }
            LOG.info("PollTimerTask initialized.");
            pollTimerTask.run();
        } catch (Exception e) {
            LOG.error("[ Error when initializing PollTimerTask. ]", e);
        }
    }

    @Schedule(minute = "30", hour = "*", persistent = false)
    public void assetRemapTimer() {
        assetRemapTask.remap();
    }
    
    private void pingExchange(long delay) {
        try {
            PingRequest exchangeRequest = new PingRequest();
            exchangeRequest.setMethod(ExchangeModuleMethod.PING);
            String request = JAXBMarshaller.marshallJaxBObjectToString(exchangeRequest);
            String messageId = exchangeProducer.sendModuleMessage(request, ExchangeModuleMethod.PING.value());
            assetConsumer.getMessage(messageId, TextMessage.class, RECEIVE_TIMEOUT);
            TimeUnit.SECONDS.sleep(delay);
        } catch (Exception e) {
            LOG.warn("Could not ping exchange", e);
        }
    }
}
