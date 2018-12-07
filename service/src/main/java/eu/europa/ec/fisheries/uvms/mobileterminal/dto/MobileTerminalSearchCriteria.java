package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

import java.util.ArrayList;
import java.util.List;

public class MobileTerminalSearchCriteria {

    private List<ListCriteria> criterias;
    private Boolean dynamic;

    public List<ListCriteria> getCriterias() {
        if (criterias == null) {
            criterias = new ArrayList<>();
        }
        return this.criterias;
    }

    public void setCriterias(List<ListCriteria> criterias) {
        this.criterias = criterias;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }
}