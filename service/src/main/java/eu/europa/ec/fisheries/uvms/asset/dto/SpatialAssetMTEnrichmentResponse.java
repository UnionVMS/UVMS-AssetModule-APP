package eu.europa.ec.fisheries.uvms.asset.dto;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;

import java.io.Serializable;
import java.util.Objects;

public class SpatialAssetMTEnrichmentResponse implements Serializable {

    private MobileTerminalType mobileTerminalType;
    private Asset asset;

    public SpatialAssetMTEnrichmentResponse(){}

    public MobileTerminalType getMobileTerminalType() {
        return mobileTerminalType;
    }

    public void setMobileTerminalType(MobileTerminalType mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpatialAssetMTEnrichmentResponse that = (SpatialAssetMTEnrichmentResponse) o;
        return Objects.equals(mobileTerminalType, that.mobileTerminalType) &&
                Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobileTerminalType, asset);
    }
}
