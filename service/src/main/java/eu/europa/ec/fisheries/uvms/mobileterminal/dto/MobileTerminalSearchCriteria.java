package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

import java.util.ArrayList;
import java.util.List;

public class MobileTerminalSearchCriteria {

    private List<ListCriteria> criterias;
    private Boolean isDynamic;

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
        return isDynamic;
    }

    public void setDynamic(Boolean dynamic) {
        isDynamic = dynamic;
    }
}
