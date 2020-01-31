package eu.europa.ec.fisheries.uvms.asset.bean;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterValueDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

public class AssetFilterServiceBean{

	    @Inject
	    private AssetFilterDao assetFilterDao;

	    @Inject
	    private AssetFilterValueDao assetFilterValueDao;

	    
		public List<AssetFilter> getAssetFilterList(String user) {
			if (user == null || user.trim().isEmpty()) {
	            throw new NullPointerException("Invalid user");
	        }
	        return assetFilterDao.getAssetFilterByUser(user);
		}

		public List<AssetFilter> getAssetFilterListByAssetId(UUID assetId) {
			   // TODO maybe this could be done more efficient if search is from the other side and joining . . . .
	        if (assetId == null) {
	            throw new NullPointerException("Invalid asset");
	        }
	        List<AssetFilter> searchResultList = new ArrayList<>();
	        List<AssetFilter> filterList = assetFilterDao.getAssetFilterAll();
	        for (AssetFilter filter : filterList) {
	            List<AssetFilterValue> values = assetFilterValueDao.retrieveFieldsForGroup(filter);
	            for (AssetFilterValue value : values) {
	                if ("GUID".equals(value.getKey()) && assetId.toString().equals(value.getValue())) {
	                    searchResultList.add(filter);
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
	        assetFilter.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
	        return assetFilterDao.createAssetGroup(assetFilter);
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
	        assetFilter.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
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
		        filterEntity.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
		        return filterEntity;
		}
		
		public AssetFilterValue createAssetFilterValue(UUID parentAssetFilterId, AssetFilterValue assetFilterValue,
				String username) {
			 if (parentAssetFilterId == null) {
		            throw new NullPointerException("Cannot create assetFilterValue because the assetFilter ID is Null");
		        }
		        if (assetFilterValue == null) {
		            throw new NullPointerException("Cannot create assetFilterValue because the assetFilterValue is Null");
		        }
		        if (username == null || username.trim().isEmpty()) {
		            throw new NullPointerException("Username must be provided for selected operation");
		        }
		        AssetFilter parentAssetFilter = assetFilterDao.getAssetFilterByGuid(parentAssetFilterId);
		        if (parentAssetFilter == null) {
		            throw new NullPointerException("AssetGroup with ID: " + parentAssetFilterId + " does not exist");
		        }

		        assetFilterValue.setAssetFilter(parentAssetFilter);
		        assetFilterValue.setUpdatedBy(username);
		        assetFilterValue.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
		        return assetFilterValueDao.create(assetFilterValue);
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
		        assetFilterValue.setUpdatedBy(username);
		        assetFilterValue.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
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

		
		public List<AssetFilterValue> retrieveValuesForFilter(UUID assetFilterId) {
			 if (assetFilterId == null) {
		            throw new NullPointerException("AssetFilterId is null.");
		        }
			 AssetFilter assetFilter = assetFilterDao.getAssetFilterByGuid(assetFilterId);
		        if (assetFilter == null) {
		            throw new NullPointerException("Cannot retrieve list for group because assetGroup does not exist.");
		        }
		        return assetFilterValueDao.retrieveFieldsForGroup(assetFilter);
		}

		public void removeAssetFilterValue(UUID assetFilterId) {
			if (assetFilterId == null) {
	            throw new NullPointerException("AssetFilterId is null.");
	        }
			AssetFilter assetFilter = assetFilterDao.getAssetFilterByGuid(assetFilterId);
	        if (assetFilter == null) {
	            throw new NullPointerException("AssetFilter does not exist.");
	        }
	        assetFilterValueDao.removeValuesFromFilter(assetFilter);	
		}
}
