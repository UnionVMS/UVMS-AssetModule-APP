package eu.europa.ec.fisheries.uvms.asset.model.exception;


public class AssetFaultException extends Exception {


    public AssetFaultException() {
        super();
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




}
