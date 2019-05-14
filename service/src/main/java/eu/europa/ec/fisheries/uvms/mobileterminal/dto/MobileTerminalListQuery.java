package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MobileTerminalListQuery {

    private ListPagination pagination;
    private MobileTerminalSearchCriteria mobileTerminalSearchCriteria;
    private boolean includeArchived = false;

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

    public boolean isIncludeArchived() {
        return includeArchived;
    }

    public void setIncludeArchived(boolean includeArchived) {
        this.includeArchived = includeArchived;
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
