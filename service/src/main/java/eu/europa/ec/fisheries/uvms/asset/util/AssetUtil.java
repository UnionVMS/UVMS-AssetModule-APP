package eu.europa.ec.fisheries.uvms.asset.util;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;

import java.time.OffsetDateTime;

public class AssetUtil {

    public static Asset createNewAssetFromRequest(AssetMTEnrichmentRequest request,int shipNumber){
        Asset asset = new Asset();

        asset.setName((request.getAssetName() == null) ? ("Unknown ship: " + shipNumber) : request.getAssetName());
        asset.setUpdateTime(OffsetDateTime.now());
        asset.setSource(TerminalSourceEnum.INTERNAL.toString());
        asset.setUpdatedBy("UVMS");
        asset.setFlagStateCode((request.getFlagState() == null) ? ("UNK") : request.getFlagState());
        asset.setActive(true);

        asset.setCfr(request.getCfrValue());
        asset.setImo(request.getImoValue());
        asset.setIrcs( ((request.getIrcsValue() == null || request.getIrcsValue().length() > 8) ? null : request.getIrcsValue()) );
        asset.setGfcm(request.getGfcmValue());
        asset.setIccat(request.getIccatValue());
        asset.setMmsi(request.getMmsiValue());
        asset.setUvi(request.getUviValue());
        asset.setExternalMarking(request.getExternalMarking());


        return asset;
    }
}
