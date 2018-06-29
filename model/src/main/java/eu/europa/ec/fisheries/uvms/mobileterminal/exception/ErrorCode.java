package eu.europa.ec.fisheries.uvms.mobileterminal.exception;

public enum ErrorCode {

    ENUM_FIELD_TYPE_ERROR(101, "Couldn't map channel field type: "),
    RETRIEVING_MT_SOURCE_ENUM_ERROR(102, "Error when getting MobileTerminalSourceEnum from source"),
    MAP_CHANNEL_FIELD_TYPES_ERROR (103, "Error when mapping channel field types"),
    MAPPING_ATTR_TYPE_ERROR(104, "Couldn't map attribute type ");

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
