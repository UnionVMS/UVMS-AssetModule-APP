package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

public enum SearchKey {
    CONNECT_ID,
    SERIAL_NUMBER,
    MEMBER_NUMBER,
    DNID,
    SATELLITE_NUMBER,
    TRANSPONDER_TYPE;

    public String value() {
        return name();
    }

    public static SearchKey fromValue(String v) {
        return valueOf(v);
    }
}