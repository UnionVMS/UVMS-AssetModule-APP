package eu.europa.ec.fisheries.uvms.asset.bean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

@Stateless
public class AssetFilterServiceBean{

    @Inject
    private AssetFilterDao assetFilterDao;
    
	public List<AssetFilter> getAssetFilterList(String user) {
		if (user == null || user.trim().isEmpty()) {
            throw new NullPointerException("Invalid user");
        }
        return assetFilterDao.getAssetFilterByUser(user);
    }
	
	public List<AssetFilter> getAssetFilterListByAssetId(UUID assetId) {
        if (assetId == null) {
            throw new NullPointerException("Invalid asset");
        }
        List<AssetFilter> searchResultList = new ArrayList<>();
        List<AssetFilter> filterList = assetFilterDao.getAssetFilterAll();
        for (AssetFilter assetFilter : filterList) {
        	List<AssetFilterQuery> filterQueryList = assetFilterDao.retrieveFilterQuerysForAssetFilter(assetFilter);
        	for (AssetFilterQuery assetFilterQuery  : filterQueryList) {
        		if ("GUID".equals(assetFilterQuery.getType())) {
        			List<AssetFilterValue> values = assetFilterDao.retrieveValuesForFilterQuery(assetFilterQuery);
        			for (AssetFilterValue value : values) {
        				if (assetId.toString().equals(value.getValueString())) {
        					searchResultList.add(assetFilter);
			            }
			        }
	            }
		    }
        }
        return searchResultList;
    }
	
	public AssetFilter getAssetFilterById(UUID guid) {
		if (guid == null) {
            throw new NullPointerException("Cannot get asset filter because ID is null.");
        }
        return assetFilterDao.getAssetFilterByGuid(guid);
	}

	public AssetFilter createAssetFilter(AssetFilter assetFilter, String username) {
		if (assetFilter == null) {
            throw new NullPointerException("Cannot create asset filter because the assetFilter is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }
        assetFilter.setOwner(username);
        assetFilter.setUpdatedBy(username);
        assetFilter.setUpdateTime(Instant.now());
        return assetFilterDao.createAssetFilter(assetFilter);
	}

	public AssetFilter updateAssetFilter(AssetFilter assetFilter, String username) {
	   if (assetFilter == null || assetFilter.getId() == null) {
            throw new NullPointerException("Cannot update asset group because group or ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }

        AssetFilter fetchedassetFilter = assetFilterDao.getAssetFilterByGuid(assetFilter.getId());
        if (fetchedassetFilter == null) {
            throw new NullPointerException("No assetGroup found.");
        }
        assetFilter.setUpdatedBy(username);
        assetFilter.setUpdateTime(Instant.now());
        return assetFilterDao.updateAssetFilter(assetFilter);
	}

	public AssetFilter deleteAssetFilterById(UUID guid, String username) {
		if (guid == null) {
            throw new NullPointerException("Cannot delete asset filter because the group ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }
        AssetFilter filterEntity = assetFilterDao.getAssetFilterByGuid(guid);
        if (filterEntity == null) {
            throw new NullPointerException("No assetfilter found.");
        }
        assetFilterDao.deleteAssetFilter(filterEntity);
        return filterEntity;
	}
	
	public AssetFilterValue createAssetFilterValue(UUID parentAssetFilterQueryId, AssetFilterValue assetFilterValue) {
	 	if (parentAssetFilterQueryId == null) {
            throw new NullPointerException("Cannot create assetFilterValue because the assetFilterQuery ID is Null");
        }
        if (assetFilterValue == null) {
            throw new NullPointerException("Cannot create assetFilterValue because the assetFilterValue is Null");
        }
        AssetFilterQuery parentAssetFilterQuery = assetFilterDao.getAssetFilterQuery(parentAssetFilterQueryId);
        if (parentAssetFilterQuery == null) {
            throw new NullPointerException("AssetFilter with ID: " + parentAssetFilterQueryId + " does not exist");
        }

        assetFilterValue.setAssetFilterQuery(parentAssetFilterQuery);
        return assetFilterDao.create(assetFilterValue);
	}
	
	public AssetFilterQuery createAssetFilterQuery(UUID parentAssetFilterId, AssetFilterQuery assetFilterQuery) {
	 	if (parentAssetFilterId == null) {
            throw new NullPointerException("Cannot create assetFilterQuery because the assetFilterID is Null");
        }
        if (assetFilterQuery == null) {
            throw new NullPointerException("Cannot create assetFilterQuery because the assetFilterQuery is Null");
        }
        AssetFilter parentAssetFilter= assetFilterDao.getAssetFilterByGuid(parentAssetFilterId);
        if (parentAssetFilter == null) {
            throw new NullPointerException("AssetFilter with ID: " + parentAssetFilterId + " does not exist");
        }

        assetFilterQuery.setAssetFilter(parentAssetFilter);
        return assetFilterDao.create(assetFilterQuery);
	}
	public AssetFilterQuery deleteAssetFilterQuery(UUID id) {
		if (id == null) {
            throw new NullPointerException("AssetFilterValueId fail because ID is null.");
        }
		AssetFilterQuery assetFilterQuery = assetFilterDao.getAssetFilterQuery(id);
        if (assetFilterQuery == null) {
            return null;
        }
        return assetFilterDao.delete(assetFilterQuery);
	}

	public AssetFilterValue updateAssetFilterValue(AssetFilterValue assetFilterValue, String username) {
	     if (assetFilterValue == null) {
	    	 throw new NullPointerException("Cannot update assetFilterValue because assetFilterValue is invalid.");
	     }
	     if (username == null || username.trim().isEmpty()) {
	    	 throw new NullPointerException("Username must be provided for selected operation");
	     }
	     AssetFilterValue fetchedValue = assetFilterDao.get(assetFilterValue.getId());
	     if (fetchedValue == null) {
	    	 throw new NullPointerException("AssetGroupField does not exist " + assetFilterValue.getId().toString());
	     }
	     return assetFilterDao.update(assetFilterValue);
	}
	
	public AssetFilterValue getAssetFilterValue(UUID id) {
		if (id == null) {
			throw new NullPointerException("Cannot get AssetFilterValue because ID is null.");
        }
        return assetFilterDao.get(id);
	}

	public AssetFilterValue deleteAssetFilterValue(UUID id, String username) {
		if (id == null) {
            throw new NullPointerException("AssetFilterValueId fail because ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }

        AssetFilterValue assetFilterValue = assetFilterDao.get(id);
        if (assetFilterValue == null) {
            return null;
        }
        return assetFilterDao.delete(assetFilterValue);
	}
	
	
	
	public AssetFilter updateAllAssetFilter(AssetFilter assetFilter, String username) {
	     if (assetFilter == null) {
	    	 throw new NullPointerException("Cannot update assetFilterList because assetFilters is invalid.");
	     }
	     if (username == null || username.trim().isEmpty()) {
	    	 throw new NullPointerException("Username must be provided for update all filter queries and values");
	     }
    	 
	     UUID assetfileterId =  assetFilter.getId();
	     AssetFilter oldAssetFilter = assetFilterDao.getAssetFilterByGuid(assetfileterId);
	     // delete all assetfilter children for assetfileterId
	     for(AssetFilterQuery assetFilterQuery: oldAssetFilter.getQueries()) {
	    	 for(AssetFilterValue assetFilterValue : assetFilterQuery.getValues()) {
	    		 assetFilterDao.delete(assetFilterValue);
	    	 }
	    	 assetFilterDao.delete(assetFilterQuery);
	     }
	     // create new assetfilter children
    	 for(AssetFilterQuery assetFilterQuery : assetFilter.getQueries()) {
    		 assetFilterQuery.setAssetFilter(assetFilter);
    		 assetFilterDao.create(assetFilterQuery);
    		 for(AssetFilterValue assetFilterValue : assetFilterQuery.getValues()) {
    			 assetFilterValue.setAssetFilterQuery(assetFilterQuery);
    			 assetFilterDao.create(assetFilterValue);
    		 }
    	 }
    	 assetFilter.setUpdatedBy(username);
    	 assetFilter.setUpdateTime(Instant.now());
	     return assetFilterDao.updateAssetFilter(assetFilter);
	}

	
}
