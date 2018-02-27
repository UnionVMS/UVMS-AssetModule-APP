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
package eu.europa.ec.fisheries.uvms.asset.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetSEDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;

@RunWith(Arquillian.class)
public class AssetSEDaoTest extends TransactionalTests {

    @Inject
    AssetSEDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void createAssetTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        assertThat(asset.getId(), is(notNullValue()));
        
        AssetSE fetchedAsset = assetDao.getAssetById(asset.getId());
        
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test(expected = AssetDaoException.class)
    @OperateOnDeployment("normal")
    public void createAssetNullInputShouldThrowExceptionTest() throws AssetDaoException {
        assetDao.createAsset(null);
    }
    
    // TODO should test history GUID
    @Ignore
    @Test
    @OperateOnDeployment("normal")
    public void createAssetCheckHistoryGuid() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
       
//        assertThat(asset.getHistoryGuid()), is(notNullValue()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByCfrTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByCfr(asset.getCfr());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getCfr(), is(asset.getCfr()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIrcsTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByIrcs(asset.getIrcs());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIrcs(), is(asset.getIrcs()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByImoTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByImo(asset.getImo());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getImo(), is(asset.getImo()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByMmsiTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByMmsi(asset.getMmsi());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getMmsi(), is(asset.getMmsi()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetByIccatTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        AssetSE fetchedAsset = assetDao.getAssetByMmsi(asset.getIccat());

        assertThat(fetchedAsset.getId(), is(asset.getId()));
        assertThat(fetchedAsset.getName(), is(asset.getName()));
        assertThat(fetchedAsset.getIccat(), is(asset.getIccat()));
        assertThat(fetchedAsset.getActive(), is(asset.getActive()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void updateAssetTest() throws AssetDaoException {
        AssetSE asset = AssetTestsHelper.createBasicAsset();
        asset = assetDao.createAsset(asset);
        
        String newName = "UpdatedName";
        asset.setName(newName);
        asset = assetDao.updateAsset(asset);
        assertThat(asset.getName(), is(newName));
        
        AssetSE updatedAsset = assetDao.getAssetById(asset.getId());
        assertThat(updatedAsset.getId(), is(asset.getId()));
        assertThat(updatedAsset.getName(), is(newName));
    }
}
