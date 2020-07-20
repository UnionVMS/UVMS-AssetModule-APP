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
package eu.europa.ec.fisheries.uvms.mapper;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.constant.SearchFields;

public class SearchFieldMapperTest {

    @Test
    public void testQuerySql() {

        List<SearchKeyValue> searchFields = new ArrayList<>();

        SearchKeyValue val1 = new SearchKeyValue();
        val1.setSearchField(SearchFields.CFR);
        val1.setSearchValues(Arrays.asList("abc"));

        SearchKeyValue val2 = new SearchKeyValue();
        val2.setSearchField(SearchFields.CFR);
        val2.setSearchValues(Arrays.asList("cde"));

        searchFields.add(val1);
        searchFields.add(val2);

        // System.out.println(SearchFieldMapper.createSelectSearchSql(searchFields, true));

    }

    @Test
    public void testHistoryGUIDSQL() {
        List<SearchKeyValue> searchFields = new ArrayList<>();

        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.HIST_GUID);
        searchValue.setSearchValues(Arrays.asList("history_guid"));

        searchFields.add(searchValue);

        String createSelectSearchSql = SearchFieldMapper.createSelectSearchSql(searchFields);
        String expected = "active IN (true,false)";
        assertThat(createSelectSearchSql, containsString(expected));
    }

    @Test
    public void testNoHistoryGUIDSQL() {
        List<SearchKeyValue> searchFields = new ArrayList<>();

        SearchKeyValue searchValue = new SearchKeyValue();
        searchValue.setSearchField(SearchFields.GUID);
        searchValue.setSearchValues(Arrays.asList("guid"));

        searchFields.add(searchValue);

        String createSelectSearchSql = SearchFieldMapper.createSelectSearchSql(searchFields);
        String expected = "active IN (true)";
        assertThat(createSelectSearchSql, containsString(expected));
    }
}
