package eu.europa.ec.fisheries.uvms.rest.asset.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryRow {

    private String className;
    private String updatedBy;
    private Instant updateTime;

    private List<ChangeHistoryRow> subclasses = new ArrayList<>();

    private List<ChangeHistoryItem> changes = new ArrayList<>();

    public ChangeHistoryRow(String className, String updatedBy, Instant updateTime) {
        this.className = className;
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
    }

    public ChangeHistoryRow() {
    }

    public void addNewItem(String field, Object oldValue, Object newValue){
        changes.add(new ChangeHistoryItem(field, oldValue, newValue));
    }



    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public List<ChangeHistoryItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeHistoryItem> changes) {
        this.changes = changes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ChangeHistoryRow> getSubclasses() {
        return subclasses;
    }

    public void setSubclasses(List<ChangeHistoryRow> subclasses) {
        this.subclasses = subclasses;
    }
}
