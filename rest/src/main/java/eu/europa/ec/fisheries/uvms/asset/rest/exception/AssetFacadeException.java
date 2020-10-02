package eu.europa.ec.fisheries.uvms.asset.rest.exception;

import eu.europa.ec.fisheries.uvms.asset.rest.error.AssetError;

public class AssetFacadeException extends Exception {

    private AssetError error;
    private String message;

    public AssetFacadeException(AssetError error, String message) {
        this.error = error;
        this.message = message;
    }

    public AssetError getError() {
        return this.error;
    }

    public void setError(AssetError error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
