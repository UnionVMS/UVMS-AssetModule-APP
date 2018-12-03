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
package eu.europa.ec.fisheries.uvms.mock.asset.message.consumer.bean;

import javax.ejb.Stateless;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;

@Stateless
public class AssetQueueConsumerBean implements AssetQueueConsumer, ConfigMessageConsumer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMessageAss(String correlationId, Class type) throws AssetMessageException {
        try {
            return (T) type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AssetMessageException("");
        }
    }

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessageAss(correlationId, type);
        }
        catch (AssetMessageException e) {
            throw new ConfigMessageException(e.getMessage());
        }
    }
}
