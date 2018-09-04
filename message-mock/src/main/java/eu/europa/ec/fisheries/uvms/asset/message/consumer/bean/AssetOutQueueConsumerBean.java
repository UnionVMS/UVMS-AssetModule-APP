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
package eu.europa.ec.fisheries.uvms.asset.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractConsumer;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

@Stateless
public class AssetOutQueueConsumerBean extends AbstractConsumer implements AssetQueueConsumer, ConfigMessageConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(AssetOutQueueConsumerBean.class);

    private final static long TIMEOUT = 30000;

    @Override
    public <T> T getMessageOv(String correlationId, Class type) throws AssetMessageException {
    	if (correlationId == null || correlationId.isEmpty()) {
    		throw new AssetMessageException("No CorrelationID provided!");
    	}
    	try {
            return getMessage(correlationId, type, TIMEOUT);
        } catch (Exception e) {
            LOG.error("[ Error when retrieving message. ] {}", e.getMessage());
            throw new AssetMessageException("Error when retrieving message: " + e.getMessage());
        }
    }

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessageOv(correlationId, type);
        }
        catch (AssetMessageException e) {
            LOG.error("[ Error when getting config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

    @Override
    public String getDestinationName() {
        return AssetConstants.QUEUE_ASSET;
    }
}

