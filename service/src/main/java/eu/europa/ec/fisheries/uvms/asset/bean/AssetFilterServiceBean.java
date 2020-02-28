package eu.europa.ec.fisheries.uvms.asset.bean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterQueryDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterValueDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

@Stateless
public class AssetFilterServiceBean{

    @Inject
    private AssetFilterDao assetFilterDao;

    @Inject
    private AssetFilterValueDao assetFilterValueDao;
    
    @Inject
    private AssetFilterQueryDao assetFilterQueryDao;

    
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
        	List<AssetFilterQuery> filterQueryList = assetFilterQueryDao.retrieveFilterQuerysForAssetFilter(assetFilter);
        	for (AssetFilterQuery assetFilterQuery  : filterQueryList) {
        		if ("GUID".equals(assetFilterQuery.getType())) {
        			List<AssetFilterValue> values = assetFilterValueDao.retrieveValuesForFilterQuery(assetFilterQuery);
        			for (AssetFilterValue value : values) {
        				if (assetId.toString().equals(value.getValue())) {
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
            throw new NullPointerException("Cannot create asset group because the group is null.");
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
            throw new NullPointerException("No assetgroup found.");
        }
       // TODO remove Dont do soft delete
      //  filterEntity.setArchived(true);
        filterEntity.setUpdatedBy(username);
        filterEntity.setUpdateTime(Instant.now());
        
        return filterEntity;
	}
	
	public AssetFilterValue createAssetFilterValue(UUID parentAssetFilterQueryId, AssetFilterValue assetFilterValue) {
	 	if (parentAssetFilterQueryId == null) {
            throw new NullPointerException("Cannot create assetFilterValue because the assetFilterQuery ID is Null");
        }
        if (assetFilterValue == null) {
            throw new NullPointerException("Cannot create assetFilterValue because the assetFilterValue is Null");
        }
        AssetFilterQuery parentAssetFilterQuery = assetFilterQueryDao.getAssetFilterQueryByGuid(parentAssetFilterQueryId);
        if (parentAssetFilterQuery == null) {
            throw new NullPointerException("AssetGroup with ID: " + parentAssetFilterQueryId + " does not exist");
        }

        assetFilterValue.setAssetFilterQuery(parentAssetFilterQuery);
        return assetFilterValueDao.create(assetFilterValue);
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
        assetFilterQuery.setInverse(false);
        assetFilterQuery.setIsNumber(true);
        return assetFilterQueryDao.create(assetFilterQuery);
	}
	public AssetFilterQuery deleteAssetFilterQuery(UUID id) {
		if (id == null) {
            throw new NullPointerException("AssetFilterValueId fail because ID is null.");
        }
		AssetFilterQuery assetFilterQuery = assetFilterQueryDao.get(id);
        if (assetFilterQuery == null) {
            return null;
        }
        return assetFilterQueryDao.delete(assetFilterQuery);
	}

	public AssetFilterValue updateAssetFilterValue(AssetFilterValue assetFilterValue, String username) {
	     if (assetFilterValue == null) {
	    	 throw new NullPointerException("Cannot update assetFilterValue because assetFilterValue is invalid.");
	     }
	     if (username == null || username.trim().isEmpty()) {
	    	 throw new NullPointerException("Username must be provided for selected operation");
	     }
	     AssetFilterValue fetchedValue = assetFilterValueDao.get(assetFilterValue.getId());
	     if (fetchedValue == null) {
	    	 throw new NullPointerException("AssetGroupField does not exist " + assetFilterValue.getId().toString());
	     }
	     return assetFilterValueDao.update(assetFilterValue);
	}
	
	public AssetFilterValue getAssetFilterValue(UUID id) {
		if (id == null) {
			throw new NullPointerException("Cannot get AssetFilterValue because ID is null.");
        }
        return assetFilterValueDao.get(id);
	}

	public AssetFilterValue deleteAssetFilterValue(UUID id, String username) {
		if (id == null) {
            throw new NullPointerException("AssetFilterValueId fail because ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }

        AssetFilterValue assetFilterValue = assetFilterValueDao.get(id);
        if (assetFilterValue == null) {
            return null;
        }
        return assetFilterValueDao.delete(assetFilterValue);
	}
	
	public List<AssetFilterQuery> retrieveQuerysForFilter(UUID assetFilterId) {
		if (assetFilterId == null) {
            throw new NullPointerException("AssetFilterId is null.");
		}
		AssetFilter assetFilter = assetFilterDao.getAssetFilterByGuid(assetFilterId);
		if (assetFilter == null) {
            throw new NullPointerException("Cannot retrieve list for group because assetGroup does not exist.");
		}
		return assetFilterQueryDao.retrieveFilterQuerysForAssetFilter(assetFilter);
	}
	
	public List<AssetFilterValue> retrieveValuesForFilterQuery(UUID assetFilterQueryId) {
		if (assetFilterQueryId == null) {
	        throw new NullPointerException("AssetFilterId is null.");
	    }
		AssetFilterQuery assetFilterQuery = assetFilterQueryDao.getAssetFilterQueryByGuid(assetFilterQueryId);
        if (assetFilterQuery == null) {
            throw new NullPointerException("Cannot retrieve list for group because assetGroup does not exist.");
        }
        return assetFilterValueDao.retrieveValuesForFilterQuery(assetFilterQuery);
	}

	public void removeQuerysFromFilter(UUID assetFilterId) {
		if (assetFilterId == null) {
            throw new NullPointerException("AssetFilterId is null.");
        }
		AssetFilter assetFilter = assetFilterDao.getAssetFilterByGuid(assetFilterId);
        if (assetFilter == null) {
            throw new NullPointerException("AssetFilter does not exist.");
        }
        assetFilterQueryDao.removeQuerysFromFilter(assetFilter);
	}

	public AssetFilterQuery getAssetFilterQuery(UUID id) {
		if (id == null) {
            throw new NullPointerException("Id of AssetFilterQuery is null.");
        }
		AssetFilterQuery assetFilterQuery = assetFilterQueryDao.getAssetFilterQuery(id);
		if (assetFilterQuery == null) {
            throw new NullPointerException("assetFilterQuery does not exist.");
        }
		return assetFilterQuery;
	}
	
	public AssetFilterQuery updateAssetFilterQuery(AssetFilterQuery query, String username) {
	   if (query == null || query.getId() == null) {
            throw new NullPointerException("Cannot update asset filter query because query or ID is null.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new NullPointerException("Username must be provided for selected operation");
        }

        AssetFilterQuery fetchedassetFilter = assetFilterQueryDao.getAssetFilterQueryByGuid(query.getId());
        if (fetchedassetFilter == null) {
            throw new NullPointerException("No assetGroup found.");
        }
        query.setUpdatedBy(username);
        query.setUpdateTime(Instant.now());
        return assetFilterQueryDao.update(query);
	}
}
