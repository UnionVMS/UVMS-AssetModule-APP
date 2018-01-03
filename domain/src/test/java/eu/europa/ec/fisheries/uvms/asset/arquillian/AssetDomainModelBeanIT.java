package eu.europa.ec.fisheries.uvms.asset.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

@RunWith(Arquillian.class)
public class AssetDomainModelBeanIT extends TransactionalTests {

	@Inject
	AssetDomainModelBean assetDomainModelBean;
	
	@Test
	public void createAsset() throws Exception {
		Asset asset = new Asset();
		asset.setName("Test");
		asset.setCfr("SWE000123456");
        asset.setSource(CarrierSource.INTERNAL);
        asset.setActive(true);
		
		Asset createdAsset = assetDomainModelBean.createAsset(asset, "user");
		assertThat(createdAsset, is(notNullValue()));
	}
	
	@Test(expected = AssetModelException.class)
	public void createAssetDuplicateCRFShouldThrowException() throws Exception {
		Asset asset = new Asset();
		asset.setName("Test");
		asset.setCfr("SWE000123456");
        asset.setSource(CarrierSource.INTERNAL);
        asset.setActive(true);
		
		assetDomainModelBean.createAsset(asset, "user");
		assetDomainModelBean.createAsset(asset, "user");
	}
	
	@Test
	public void getAssetListByCFR() throws Exception {
		Asset asset = new Asset();
		asset.setName("Test");
		String cfr = "SWE000123456";
		asset.setCfr(cfr);
        asset.setSource(CarrierSource.INTERNAL);
        asset.setActive(true);
		
		Asset createdAsset = assetDomainModelBean.createAsset(asset, "user");
		
		AssetListQuery assetQuery = getAssetListQuery(ConfigSearchField.CFR, cfr);
		
		GetAssetListResponseDto assetList = assetDomainModelBean.getAssetList(assetQuery);
		List<Asset> assets = assetList.getAssetList();
		assertThat(assets, hasItem(createdAsset));
	}
	
	@Test
	public void getAssetListByHistoryGuid() throws Exception {
		Asset asset = new Asset();
		asset.setName("Test");
		asset.setCfr("SWE000123456");
        asset.setSource(CarrierSource.INTERNAL);
        asset.setActive(true);
		
		Asset createdAsset = assetDomainModelBean.createAsset(asset, "user");
		
		AssetListQuery assetQuery = getAssetListQuery(ConfigSearchField.HIST_GUID, createdAsset.getEventHistory().getEventId());
		
		GetAssetListResponseDto assetList = assetDomainModelBean.getAssetList(assetQuery);
		List<Asset> assets = assetList.getAssetList();
		assertThat(assets, hasItem(createdAsset));
	}
	
	private AssetListQuery getAssetListQuery(ConfigSearchField searchField, String value) {
		AssetListQuery assetQuery = new AssetListQuery();
		AssetListPagination pagination = new AssetListPagination();
		pagination.setListSize(100);
		pagination.setPage(1);
		assetQuery.setPagination(pagination);
		AssetListCriteria criteria = new AssetListCriteria();
		AssetListCriteriaPair assetCriteriaPair = new AssetListCriteriaPair();
		assetCriteriaPair.setKey(searchField);
		assetCriteriaPair.setValue(value);
		criteria.getCriterias().add(assetCriteriaPair);
		criteria.setIsDynamic(true);
		assetQuery.setAssetSearchCriteria(criteria);
		return assetQuery;
	}
}
