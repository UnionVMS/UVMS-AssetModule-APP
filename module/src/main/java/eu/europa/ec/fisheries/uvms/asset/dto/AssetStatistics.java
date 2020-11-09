package eu.europa.ec.fisheries.uvms.asset.dto;

public class AssetStatistics {

    Long amountOfVMSAsset;
    Long amountOfVMSAssetsWithLicense;
    Long amountOfVMSAssetsWithInactiveLicense;

    public Long getAmountOfVMSAsset() {
        return amountOfVMSAsset;
    }

    public void setAmountOfVMSAsset(Long amountOfVMSAsset) {
        this.amountOfVMSAsset = amountOfVMSAsset;
    }

    public Long getAmountOfVMSAssetsWithLicense() {
        return amountOfVMSAssetsWithLicense;
    }

    public void setAmountOfVMSAssetsWithLicense(Long amountOfVMSAssetsWithLicense) {
        this.amountOfVMSAssetsWithLicense = amountOfVMSAssetsWithLicense;
    }

    public Long getAmountOfVMSAssetsWithInactiveLicense() {
        return amountOfVMSAssetsWithInactiveLicense;
    }

    public void setAmountOfVMSAssetsWithInactiveLicense(Long amountOfVMSAssetsWithInactiveLicense) {
        this.amountOfVMSAssetsWithInactiveLicense = amountOfVMSAssetsWithInactiveLicense;
    }
}
