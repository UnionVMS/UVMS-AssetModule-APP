package eu.europa.ec.fisheries.uvms.asset.client.model.search;

import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;

public class SearchBranch implements AssetSearchInterface {

    boolean logicalAnd;

    List<AssetSearchInterface> fields = new ArrayList<>();

    @Override
    @JsonbTransient
    public boolean isLeaf() {
        return false;
    }

    public SearchBranch() {
    }

    public void addNewSearchLeaf(SearchFields searchField, String value){
        SearchLeaf leaf = new SearchLeaf(searchField, value);
        fields.add(leaf);
    }


    public SearchBranch(boolean logicalAnd) {
        this.logicalAnd = logicalAnd;
    }

    public boolean isLogicalAnd() {
        return logicalAnd;
    }

    public void setLogicalAnd(boolean logicalAnd) {
        this.logicalAnd = logicalAnd;
    }

    public List<AssetSearchInterface> getFields() {
        return fields;
    }

    public void setFields(List<AssetSearchInterface> fields) {
        this.fields = fields;
    }
}
