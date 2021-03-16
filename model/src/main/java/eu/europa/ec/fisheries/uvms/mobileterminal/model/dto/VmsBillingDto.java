package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;


public class VmsBillingDto {
    private Integer dnid;
    private Integer memberNumber;
    private String name;
    private String serialNumber;
    private String satelliteNumber;
    private Long vesselId;
    private String startDate;
    private String endDate;
    
    public VmsBillingDto(Integer dnid, Integer memberNumber, String name, String serialNumber,
            String satelliteNumber, Long vesselId, String startDate, String endDate){
        this.dnid = dnid;
        this.memberNumber = memberNumber;
        this.name = name;
        this.serialNumber = serialNumber;
        this.satelliteNumber = satelliteNumber;
        this.vesselId = vesselId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public VmsBillingDto() {
    }
    
    public Integer getDnid() {
        return dnid;
    }
    public void setDnid(Integer dnid) {
        this.dnid = dnid;
    }
    public Integer getMemberNumber() {
        return memberNumber;
    }
    public void setMemberNumber(Integer memberNumber) {
        this.memberNumber = memberNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getSatelliteNumber() {
        return satelliteNumber;
    }
    public void setSatelliteNumber(String satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }
    public Long getVesselId() {
        return vesselId;
    }
    public void setVesselId(Long vesselId) {
        this.vesselId = vesselId;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
