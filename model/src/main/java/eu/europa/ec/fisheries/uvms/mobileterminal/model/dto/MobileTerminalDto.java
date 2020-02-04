package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;

import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class MobileTerminalDto {
    private UUID id;

    private UUID historyId;

    private MobileTerminalPluginDto plugin;

    private Boolean archived = false;

    private Boolean active = true;

    private TerminalSourceEnum source;

    private MobileTerminalTypeEnum mobileTerminalType;

    private Instant updatetime;

    private Instant createTime;

    private String updateuser;

    private String serialNo;

    private String satelliteNumber;

    private String antenna;

    private String transceiverType;

    private String softwareVersion;

    private Set<ChannelDto> channels;

    private String assetId;

    private String comment;

    private Boolean eastAtlanticOceanRegion = false;

    private Boolean westAtlanticOceanRegion = false;

    private Boolean pacificOceanRegion = false;

    private Boolean indianOceanRegion = false;

    private Instant installDate;

    private Instant uninstallDate;

    private String installedBy;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public MobileTerminalPluginDto getPlugin() {
        return plugin;
    }

    public void setPlugin(MobileTerminalPluginDto plugin) {
        this.plugin = plugin;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public TerminalSourceEnum getSource() {
        return source;
    }

    public void setSource(TerminalSourceEnum source) {
        this.source = source;
    }

    public MobileTerminalTypeEnum getMobileTerminalType() {
        return mobileTerminalType;
    }

    public void setMobileTerminalType(MobileTerminalTypeEnum mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }

    public Instant getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Instant updatetime) {
        this.updatetime = updatetime;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(String satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public String getAntenna() {
        return antenna;
    }

    public void setAntenna(String antenna) {
        this.antenna = antenna;
    }

    public String getTransceiverType() {
        return transceiverType;
    }

    public void setTransceiverType(String transceiverType) {
        this.transceiverType = transceiverType;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public Set<ChannelDto> getChannels() {
        return channels;
    }

    public void setChannels(Set<ChannelDto> channels) {
        this.channels = channels;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getEastAtlanticOceanRegion() {
        return eastAtlanticOceanRegion;
    }

    public void setEastAtlanticOceanRegion(Boolean eastAtlanticOceanRegion) {
        this.eastAtlanticOceanRegion = eastAtlanticOceanRegion;
    }

    public Boolean getWestAtlanticOceanRegion() {
        return westAtlanticOceanRegion;
    }

    public void setWestAtlanticOceanRegion(Boolean westAtlanticOceanRegion) {
        this.westAtlanticOceanRegion = westAtlanticOceanRegion;
    }

    public Boolean getPacificOceanRegion() {
        return pacificOceanRegion;
    }

    public void setPacificOceanRegion(Boolean pacificOceanRegion) {
        this.pacificOceanRegion = pacificOceanRegion;
    }

    public Boolean getIndianOceanRegion() {
        return indianOceanRegion;
    }

    public void setIndianOceanRegion(Boolean indianOceanRegion) {
        this.indianOceanRegion = indianOceanRegion;
    }

    public Instant getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Instant installDate) {
        this.installDate = installDate;
    }

    public Instant getUninstallDate() {
        return uninstallDate;
    }

    public void setUninstallDate(Instant uninstallDate) {
        this.uninstallDate = uninstallDate;
    }

    public String getInstalledBy() {
        return installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }
}
