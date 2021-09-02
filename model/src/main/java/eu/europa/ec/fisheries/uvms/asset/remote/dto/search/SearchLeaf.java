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
package eu.europa.ec.fisheries.uvms.asset.remote.dto.search;

import javax.json.bind.annotation.JsonbTransient;

public class SearchLeaf implements AssetSearchInterface {

	private SearchFields searchField;
	private String searchValue;
	private String operator;

    public SearchLeaf() {
        super();
    }

    public SearchLeaf(SearchFields searchField, String searchValue) {
        this.searchField = searchField;
        this.searchValue = searchValue;
        this.operator = "";
    }
    
    public SearchLeaf(SearchFields searchField, String searchValue, String operator) {
        this.searchField = searchField;
        this.searchValue = searchValue;
        if(operator != null) {
        	this.operator = operator;
        }else {
        	this.operator = "=";
        }
    }

	@Override
	@JsonbTransient
	public boolean isLeaf() {
		return true;
	}

    public SearchFields getSearchField() {
		return searchField;
	}
    
	public void setSearchField(SearchFields searchField) {
		this.searchField = searchField;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


}