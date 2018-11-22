package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.OceanRegionDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.OceanRegion;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class OceanRegionDaoBeanIntTest extends TransactionalTests {

    @EJB
    private OceanRegionDaoBean oceanRegionDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetOceanRegionList() {
        // Since we have at least 4 regions inserted by LIQUIBASE this should always work
        List<OceanRegion> oceanRegions = oceanRegionDao.getOceanRegionList();
        assertNotNull(oceanRegions);
        assertTrue(oceanRegions.size() > 3);
    }
}
