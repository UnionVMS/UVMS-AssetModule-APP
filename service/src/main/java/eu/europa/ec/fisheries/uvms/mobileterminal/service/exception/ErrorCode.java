package eu.europa.ec.fisheries.uvms.mobileterminal.service.exception;

public enum ErrorCode {

    ENUM_FIELD_TYPE_ERROR(101, "Couldn't map channel field type: "),
    RETRIEVING_MT_SOURCE_ENUM_ERROR(102, "Error when getting MobileTerminalSourceEnum from source"),
    MAP_CHANNEL_FIELD_TYPES_ERROR (103, "Error when mapping channel field types"),
    MAPPING_ATTR_TYPE_ERROR(104, "Couldn't map attribute type "),
    SENDING_MESSAGE_ERROR(105, "Error when sending data source message"),
    RETRIEVING_MESSAGE_ERROR(106, "Error when retrieving message: "),
    CREATE_POLL_FAILED(107, "Failed to create Poll"),
    RETRIEVING_BOOL_ERROR(108, "Error when getting Boolean value"),
    POLL_STATE_MODIFICATION_ERROR(109, "Can not change the status of the archived Program Poll with id: "),
    PP_SEND_STATUS_ERROR(110, "Error when setting PollProgram status"),
    MT_PARSING_ERROR(111, "Error when parsing MobileTerminal with ID: "),
    TERMINAL_ALREADY_LINKED_ERROR(112, "Terminal with ID (1) is already linked to an asset with guid (2) : "),
    TERMINAL_NOT_LINKED_ERROR(113, "Terminal with ID (1) is not linked to an asset with guid (2) : "),
    EXCHANGE_MAPPING_ERROR(114, "Failed to map to exchange get service list request");


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
