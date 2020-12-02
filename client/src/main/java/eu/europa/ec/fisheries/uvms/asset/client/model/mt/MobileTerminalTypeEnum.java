package eu.europa.ec.fisheries.uvms.asset.client.model.mt;

public enum MobileTerminalTypeEnum {
        INMARSAT_C, IRIDIUM;
        
        public static MobileTerminalTypeEnum getType(String name) {
            for(MobileTerminalTypeEnum type : MobileTerminalTypeEnum.values()) {
                if(type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
}
