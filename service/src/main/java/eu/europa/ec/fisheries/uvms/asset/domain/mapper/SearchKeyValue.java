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
package eu.europa.ec.fisheries.uvms.asset.domain.mapper;

import java.util.ArrayList;
import java.util.List;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;

public class SearchKeyValue {
	
	private SearchFields searchField;
	private List<String> searchValues = new ArrayList<>();

    public SearchKeyValue() {
        super();
    }

    public SearchKeyValue(SearchFields searchField, List<String> searchValues) {
        this.searchField = searchField;
        this.searchValues = searchValues;
    }
    
    public SearchFields getSearchField() {
		return searchField;
	}
    
	public void setSearchField(SearchFields searchField) {
		this.searchField = searchField;
	}
	
	public List<String> getSearchValues() {
		if(searchValues == null) searchValues = new ArrayList<>();
		return searchValues;
	}
	
	public void setSearchValues(List<String> searchValues) {
		this.searchValues = searchValues;
	}
}