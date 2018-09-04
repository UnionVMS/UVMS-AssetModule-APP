/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.module.ActivityRulesAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
@LocalBean
@Slf4j
public class ActivityRulesServiceBean {

    @EJB
    private AssetMessageProducer messageProducer;

    @EJB
    private AssetDao assetDao;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void findAssetByCriteria(ActivityRulesAssetModuleRequest request, TextMessage jmsMessage) {
        try {
            List<AssetListCriteriaPair> criteria = request.getCriteria();
            List<AssetHistory> assetHistories = assetDao.getAssetHistoryByCriteria(criteria, 1);
            List<Asset> assets = EntityToModelMapper.toAssetFromAssetHistory(assetHistories);
            String findAssetByCfrResponse = AssetModuleRequestMapper.createActivityRulesAssetModuleResponse(assets);
            messageProducer.sendModuleResponseMessageOv(jmsMessage, findAssetByCfrResponse);
            log.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetModelException  | JMSException e) {
            log.error("Error when creating createFindAssetByCfrResponse", e);
        }
    }
}
