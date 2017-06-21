package eu.europa.fisheries.uvms.asset.service.arquillian;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.asset.service.ConfigService;
import eu.europa.ec.fisheries.wsdl.asset.config.Config;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

	@EJB
	ConfigService configServiceBean;

	@Test
	@OperateOnDeployment("normal")
	public void getConfigurationTest() throws Exception {
		List<Config> configuration = configServiceBean.getConfiguration();
		assertNotNull(configuration);
		assertFalse(configuration.isEmpty());
	}

	@Test
	@OperateOnDeployment("normal")
	public void getParametersTest() throws Exception {
		Map<String, String> parameters = configServiceBean.getParameters();
		assertNotNull(parameters);
		assertFalse(parameters.isEmpty());
	}

}
