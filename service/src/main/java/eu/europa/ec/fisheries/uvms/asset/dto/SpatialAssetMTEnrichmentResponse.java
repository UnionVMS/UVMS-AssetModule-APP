package eu.europa.ec.fisheries.uvms.asset.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SpatialAssetMTEnrichmentResponse implements Serializable {

    // from mobileTerminal
    private  UUID connectIdMT;
    private  String typeMT;


    // from Asset
    private UUID guidAsset;
    private String mameAsset;

    public SpatialAssetMTEnrichmentResponse() {
    }

    public UUID getConnectIdMT() {
        return connectIdMT;
    }

    public void setConnectIdMT(UUID connectIdMT) {
        this.connectIdMT = connectIdMT;
    }

    public String getTypeMT() {
        return typeMT;
    }

    public void setTypeMT(String typeMT) {
        this.typeMT = typeMT;
    }

    public UUID getGuidAsset() {
        return guidAsset;
    }

    public void setGuidAsset(UUID guidAsset) {
        this.guidAsset = guidAsset;
    }

    public String getMameAsset() {
        return mameAsset;
    }

    public void setMameAsset(String mameAsset) {
        this.mameAsset = mameAsset;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpatialAssetMTEnrichmentResponse that = (SpatialAssetMTEnrichmentResponse) o;
        return Objects.equals(connectIdMT, that.connectIdMT) &&
                Objects.equals(typeMT, that.typeMT) &&
                Objects.equals(guidAsset, that.guidAsset) &&
                Objects.equals(mameAsset, that.mameAsset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectIdMT, typeMT, guidAsset, mameAsset);
    }
}
