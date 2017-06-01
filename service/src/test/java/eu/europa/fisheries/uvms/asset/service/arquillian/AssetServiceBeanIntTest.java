package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

/**
 * Created by thofan on 2017-06-01.
 */
@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest  extends TransactionalTests {

    @EJB
    AssetService assetService;


    @Test
    @OperateOnDeployment("normal")
    @Ignore
    public void t1(){

        assetService.getNoteActivityCodes();



    }


}
