package eu.europa.ec.fisheries.uvms.asset.model.exception;

public enum ErrorCode {

    FISHING_GEAR_RESPONSE_UNMARSHALLER_ERROR(101, "Could not marshall the response to FishingGearResponse"),
    FISHING_GEAR_LIST_RESPONSE_UNMARSHALLER_ERROR(102, "Could not marshall the response to FishingGearListResponse"),
    ASSET_MAPPING_ERROR(103, "Error when returning asset from response in ResponseMapper"),
    ASSET_LIST_MAPPING_ERROR(104, "Error when returning assetList from response in ResponseMapper"),
    ASSET_GROUP_MAPPING_ERROR(105, "Error when returning single asset group from response in ResponseMapper"),
    ASSET_GROUP_LIST_MAPPING_ERROR(106, "Error when returning asset group list from response in ResponseMapper"),
    CONFIG_LIST_MAPPING_ERROR(107, "Error when returning config list from response in ResponseMapper"),
    MARSHALLING_ERROR(108, "Error when marshalling to String. Class name: "),
    UNMARSHALLING_ERROR(109, "Error when unmarshalling response in ResponseMapper");

    private String message;
    private int code;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public int getCode() {
        return this.code;
    }
}
