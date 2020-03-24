package eu.europa.ec.fisheries.uvms.asset.client.model.search;

public enum SearchLeafOperators {
	
	GREATER_THEN("greater_then"),
    LESS_THEN("less"),
    EQUALS("eq"),
    LIKE("ilike");

    private final String operator;

    private SearchLeafOperators(String operator) {
        this.operator = operator;
    }
}
