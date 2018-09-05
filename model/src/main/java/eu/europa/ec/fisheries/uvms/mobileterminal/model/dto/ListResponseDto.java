/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;

import java.io.Serializable;
import java.util.List;

public class ListResponseDto implements Serializable {

    private static final long serialVersionUID = -6741750801554281012L;

    private List<MobileTerminalType> mobileTerminalList;
    private Integer totalNumberOfPages;
    private Integer currentPage;

    public List<MobileTerminalType> getMobileTerminalList() {
        return mobileTerminalList;
    }

    public void setMobileTerminalList(List<MobileTerminalType> mobileTerminalList) {
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
