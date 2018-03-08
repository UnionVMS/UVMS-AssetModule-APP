package eu.europa.ec.fisheries.uvms.asset.model.exception;

import eu.europa.ec.fisheries.uvms.asset.types.AssetFault;

public class AssetFaultException extends Exception {

    AssetFault assetFault;

    public AssetFaultException() {
        super();
    }

    public AssetFaultException(AssetFault assetFault) {
        super();
        this.assetFault =  assetFault;
    }

    public AssetFaultException(String message) {
        super(message);
    }

    public AssetFaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssetFaultException(Throwable cause) {
        super(cause);
    }

    protected AssetFaultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }



    public void  setFaultInfo(AssetFault assetFault){
        this.assetFault =  assetFault;
    }

    public AssetFault getFaultInfo(){
        return assetFault;
    }

}
