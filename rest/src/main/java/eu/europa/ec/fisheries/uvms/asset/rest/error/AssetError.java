package eu.europa.ec.fisheries.uvms.asset.rest.error;

public enum AssetError {
    SQL_ERROR(100, "SQL_ERROR", "Error occurred in query execution"),
    ASSET_ERROR(1700,"ASSET_ERROR", "Error occurred when getting asset"),
    UNKNOWN_ERROR(1000, "UNKNOWN_ERROR", "Unknown error ocurred");

    public int getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getDescription() {
        return description;
    }

    AssetError(int code, String codeName, String description) {
        this.code = code;
        this.codeName = codeName;
        this.description = description;
    }

    private int code;
    private String codeName;
    private String description;


}
