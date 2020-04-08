package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssetFilterList implements Serializable {

	private static final long serialVersionUID = 95262131085841494L;
	
	private List<AssetFilter> assetFilterList = new ArrayList<AssetFilter>();

	public List<AssetFilter> getAssetFilterList() {
		return assetFilterList;
	}

	public void setAssetFilterList(List<AssetFilter> assetFilterList) {
		this.assetFilterList = assetFilterList;
	}

}
