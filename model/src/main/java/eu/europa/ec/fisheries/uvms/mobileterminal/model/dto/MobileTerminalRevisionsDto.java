package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChangeHistoryRow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MobileTerminalRevisionsDto {

    List<MobileTerminalDto> mobileTerminalVersions;

    Map<UUID, ChangeHistoryRow> changes;

    public List<MobileTerminalDto> getMobileTerminalVersions() {
        return mobileTerminalVersions;
    }

    public void setMobileTerminalVersions(List<MobileTerminalDto> mobileTerminalVersions) {
        this.mobileTerminalVersions = mobileTerminalVersions;
    }

    public Map<UUID, ChangeHistoryRow> getChanges() {
        return changes;
    }

    public void setChanges(Map<UUID, ChangeHistoryRow> changes) {
        this.changes = changes;
    }
}
