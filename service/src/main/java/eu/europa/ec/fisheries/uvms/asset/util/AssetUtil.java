package eu.europa.ec.fisheries.uvms.asset.util;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;

import java.time.OffsetDateTime;

public class AssetUtil {

    public static Asset createNewAssetFromRequest(AssetMTEnrichmentRequest request){
        Asset asset = new Asset();

        asset.setName("Unknown ship: " + (Math.random() * 10000d) );
        asset.setUpdateTime(OffsetDateTime.now());
        asset.setUpdatedBy("UVMS");
        asset.setFlagStateCode("UNK");
        asset.setActive(true);

        asset.setCfr(request.getCfrValue());
        asset.setImo(request.getImoValue());
        asset.setIrcs(request.getIrcsValue());
        asset.setGfcm(request.getGfcmValue());
        asset.setIccat(request.getIccatValue());
        asset.setMmsi(request.getMmsiValue());
        asset.setUvi(request.getUviValue());


        return asset;
    }
}
