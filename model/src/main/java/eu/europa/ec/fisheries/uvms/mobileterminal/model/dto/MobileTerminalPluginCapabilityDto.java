package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;

import java.time.Instant;
import java.util.UUID;

public class MobileTerminalPluginCapabilityDto {
    private UUID id;

    private String value;

    private String name;

    private Instant updateTime;

    private String updatedBy;

    private UUID plugin;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public UUID getPlugin() {
        return plugin;
    }

    public void setPlugin(UUID plugin) {
        this.plugin = plugin;
    }
}
