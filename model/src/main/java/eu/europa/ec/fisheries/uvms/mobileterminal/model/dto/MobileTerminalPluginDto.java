package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class MobileTerminalPluginDto {
    private UUID id;

    private String description;

    private String name;

    private String pluginServiceName;

    private String pluginSatelliteType;

    private Boolean pluginInactive;

    private Instant updateTime;

    private String updatedBy;

    private Set<MobileTerminalPluginCapabilityDto> capabilities;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginServiceName() {
        return pluginServiceName;
    }

    public void setPluginServiceName(String pluginServiceName) {
        this.pluginServiceName = pluginServiceName;
    }

    public String getPluginSatelliteType() {
        return pluginSatelliteType;
    }

    public void setPluginSatelliteType(String pluginSatelliteType) {
        this.pluginSatelliteType = pluginSatelliteType;
    }

    public Boolean getPluginInactive() {
        return pluginInactive;
    }

    public void setPluginInactive(Boolean pluginInactive) {
        this.pluginInactive = pluginInactive;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Set<MobileTerminalPluginCapabilityDto> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<MobileTerminalPluginCapabilityDto> capabilities) {
        this.capabilities = capabilities;
    }
}
