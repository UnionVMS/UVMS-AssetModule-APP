package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.dto;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;

import java.util.ArrayList;
import java.util.List;

public class TestMTListResponse {
    private List<MobileTerminal> mobileTerminalList = new ArrayList<>();
    private Integer totalNumberOfPages = 0;
    private Integer currentPage = 0;

    public List<MobileTerminal> getMobileTerminalList() {
        return mobileTerminalList;
    }

    public void setMobileTerminalList(List<MobileTerminal> mobileTerminalList) {
        this.mobileTerminalList = mobileTerminalList;
    }

    public Integer getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(Integer totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
