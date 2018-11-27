package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MobileTerminalListQuery {

    private ListPagination pagination;
    private MobileTerminalSearchCriteria mobileTerminalSearchCriteria;

    public ListPagination getPagination() {
        return pagination;
    }

    public void setPagination(ListPagination pagination) {
        this.pagination = pagination;
    }

    public MobileTerminalSearchCriteria getMobileTerminalSearchCriteria() {
        return mobileTerminalSearchCriteria;
    }

    public void setMobileTerminalSearchCriteria(MobileTerminalSearchCriteria mobileTerminalSearchCriteria) {
        this.mobileTerminalSearchCriteria = mobileTerminalSearchCriteria;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
