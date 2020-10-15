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
package eu.europa.ec.fisheries.uvms.asset.message.event;

import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetBO;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetMessageJSONBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMessageJSONBean.class);

    @Inject
    private AssetServiceBean assetService;

    private Jsonb jsonb;

    @PostConstruct
    public void init(){
        jsonb =  new JsonBConfigurator().getContext(null);
    }
    
    public void upsertAsset(TextMessage message) throws IOException, JMSException {
        AssetBO assetBo = jsonb.fromJson(message.getText(), AssetBO.class);
        assetService.upsertAssetBO(assetBo, assetBo.getAsset().getUpdatedBy() == null ? "UVMS (JMS)" : assetBo.getAsset().getUpdatedBy());
    }

    public void assetInformation(TextMessage message) throws IOException, JMSException {
        List<Asset> assetBos = jsonb.fromJson(message.getText(), new ArrayList<Asset>(){}.getClass().getGenericSuperclass());
        for(Asset oneAsset : assetBos){
            assetService.assetInformation(oneAsset, oneAsset.getUpdatedBy() == null ? "UVMS (JMS)" : oneAsset.getUpdatedBy());
        }
        LOG.info("Processed update asset list of size: " + assetBos.size());
    }
}
