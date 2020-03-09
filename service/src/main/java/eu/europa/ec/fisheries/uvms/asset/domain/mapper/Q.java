package eu.europa.ec.fisheries.uvms.asset.domain.mapper;

import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;

public class Q implements AQ {

    boolean logicalAnd;

    List<AQ> fields = new ArrayList<>();

    @Override
    @JsonbTransient
    public boolean isLeaf() {
        return false;
    }

    public Q() {
    }

    public Q(boolean logicalAnd) {
        this.logicalAnd = logicalAnd;
    }

    public boolean isLogicalAnd() {
        return logicalAnd;
    }

    public void setLogicalAnd(boolean logicalAnd) {
        this.logicalAnd = logicalAnd;
    }

    public List<AQ> getFields() {
        return fields;
    }

    public void setFields(List<AQ> fields) {
        this.fields = fields;
    }
}
