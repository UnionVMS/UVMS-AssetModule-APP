package eu.europa.ec.fisheries.uvms.asset.domain.mapper;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchKeyValueTest {

    private SearchKeyValue searchKeyValue = setUp();

    private SearchKeyValue setUp() {
        SearchFields searchField = SearchFields.NAME;
        List<String> searchValues = new ArrayList<>();
        searchValues.add("TEST");
        return new SearchKeyValue(searchField, searchValues);
    }

    @Test
    public void createSearchKeyValueObjectWithParameters() {
        assertThat(searchKeyValue.getSearchField(), is(SearchFields.NAME));
        assertThat(searchKeyValue.getSearchValues(), hasItem("TEST"));
    }

    @Test
    public void getSearchFieldTest() {
        assertThat(searchKeyValue.getSearchField(), is(SearchFields.NAME));
    }

    @Test
    public void setSearchFieldTest() {
        searchKeyValue.setSearchField(SearchFields.CFR);
        assertThat(searchKeyValue.getSearchField(), is(SearchFields.CFR));
        setUp();
    }

    @Test
    public void getSearchValuesAsLowerCaseTest() {
        assertThat(searchKeyValue.getSearchValuesAsLowerCase().get(0), is("test"));
    }
}


