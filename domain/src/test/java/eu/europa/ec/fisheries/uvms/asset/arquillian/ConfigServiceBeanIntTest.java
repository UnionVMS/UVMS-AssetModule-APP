package eu.europa.ec.fisheries.uvms.asset.arquillian;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import eu.europa.ec.fisheries.uvms.bean.ConfigServiceBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.wsdl.asset.config.Config;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

	@EJB
	ConfigServiceBean configServiceBean;

	@Test
	@Ignore
	@OperateOnDeployment("normal")
	public void getConfigurationTest() throws Exception {
		List<Config> configuration = configServiceBean.getConfiguration();
		Assert.assertNotNull(configuration);
		Assert.assertFalse(configuration.isEmpty());
	}

	@Test
	@OperateOnDeployment("normal")
	public void getParametersTest() throws Exception {
		Map<String, String> parameters = configServiceBean.getParameters();
		Assert.assertNotNull(parameters);
		Assert.assertFalse(parameters.isEmpty());
	}

}
