package eu.europa.ec.fisheries.uvms.rest;

public enum AppInfoCodes {

    AssetValidationError(1, "Asset validation error"),
    AssetIsNull(2, "Asset is null"),
    AssetGroupListByUser(3, "Error when getting asset group list by user. %s"),



    MobileTerminaiIsNull(1, "Moileterminal ids null");


    private Integer code;
    private String description;

    AppInfoCodes(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    public Integer getCode(){ return code;}
    public String getDescription(){ return description;}

}
