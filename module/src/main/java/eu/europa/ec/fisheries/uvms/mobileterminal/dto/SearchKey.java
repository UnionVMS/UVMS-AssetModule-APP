package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

public enum SearchKey {
    CONNECT_ID,
    SERIAL_NUMBER,
    MEMBER_NUMBER,
    DNID,
    SATELLITE_NUMBER,
    SOFTWARE_VERSION,
    TRANSCEIVER_TYPE,
    TRANSPONDER_TYPE,
    ANTENNA,
    MOBILETERMINAL_ID;

    public String value() {
        return name();
    }

    public static SearchKey fromValue(String v) {
        return valueOf(v);
    }
}
