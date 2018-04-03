package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.UpdatedAssetService;
import eu.europa.ec.fisheries.uvms.asset.service.constants.ParameterKey;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.wsdl.asset.module.FLUXVesselSendInformation;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Singleton
@Slf4j
public class UpdatedAssetServiceBean implements UpdatedAssetService {

    protected static final int SYNC_TO_FLUX_AFTER_MINUTES = 30;

    @EJB
    private MessageProducer messageProducer;

    @EJB
    private AssetService assetService;

    @EJB
    private ParameterService parameters;

    protected Map<String, DateTime> updatedAssets = new HashMap<>();


    @Override
    public void assetWasUpdated(String cfr) {
        try {
            if (isBlank(cfr) || !parameters.getBooleanValue(ParameterKey.SYNC_WITH_FLEET.getKey())) {
                // If the cfr is blank, or the admin has disabled the sync with Fleet, we won't send it to Fleet
                return;
            }
            putCfrAndDate(cfr, DateTime.now());
        } catch (ConfigServiceException e) {
            log.error("Could not retrieve parameter " + ParameterKey.SYNC_WITH_FLEET.getKey(), e);
        }
    }

    @Schedule(hour = "*", minute = "*/10", persistent = false)
    protected void processUpdatedAssets() {
        List<String> updatedAssetsToBeRemoved = new ArrayList<>();

        for (Map.Entry<String, DateTime> entry : updatedAssets.entrySet()) {
            if (entry.getValue().plusMinutes(SYNC_TO_FLUX_AFTER_MINUTES).isBefore(DateTime.now())) {
                String cfr = entry.getKey();
                Asset assetByCFR = findAssetByCFR(cfr);

                if (assetByCFR != null) {
                    sendUpdateToFleet(assetByCFR);
                    updatedAssetsToBeRemoved.add(cfr);
                }
            }
        }

        for (String key : updatedAssetsToBeRemoved) {
            updatedAssets.remove(key);
        }
    }

    private void sendUpdateToFleet(Asset updatedAsset) {
        log.info("Sending update to fleet for asset with CFR " + updatedAsset.getCfr());
        try {
            FLUXVesselSendInformation request = new FLUXVesselSendInformation();
            request.setAsset(updatedAsset);

            String sendAssetInformation = ExchangeModuleRequestMapper.createSendAssetInformation(JAXBMarshaller.marshallJaxBObjectToString(request), "asset");
            messageProducer.sendModuleMessage(sendAssetInformation, ModuleQueue.EXCHANGE);
        } catch (AssetModelMarshallException | ExchangeModelMarshallException e) {
            log.error("Couldn't marshall asset", e);
        } catch (AssetMessageException e) {
            log.error("Couldn't send message from asset to exchange", e);
        }
    }

    private Asset findAssetByCFR(String cfr) {
        try {
            AssetId assetId = new AssetId();
            assetId.setType(AssetIdType.CFR);
            assetId.setValue(cfr);

            return assetService.getAssetById(assetId, AssetDataSourceQueue.INTERNAL);
        } catch (AssetException e) {
            log.error("Couldn't find an asset with CFR " + cfr, e);
        }

        return null;
    }

    protected void putCfrAndDate(String cfr, DateTime dateTime) {
        if (!updatedAssets.containsKey(cfr)) {
            updatedAssets.put(cfr, dateTime);
        } //todo: can be replaced with putIfAbsent in Java 8
    }

}
