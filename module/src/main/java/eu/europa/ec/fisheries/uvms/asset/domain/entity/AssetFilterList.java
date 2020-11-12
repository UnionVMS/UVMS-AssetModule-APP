package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import java.io.Serializable;
import java.util.*;

public class AssetFilterList implements Serializable {

	private static final long serialVersionUID = 95262131085841494L;
	
	private Map<UUID, AssetFilter> savedFilters = new HashMap<>();

	public Map<UUID, AssetFilter> getSavedFilters() {
		return savedFilters;
	}

	public void setSavedFilters(Map<UUID, AssetFilter> savedFilters) {
		this.savedFilters = savedFilters;
	}
}
