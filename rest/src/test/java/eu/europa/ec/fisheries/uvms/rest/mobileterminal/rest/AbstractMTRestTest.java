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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;

//@ArquillianSuiteDeployment
public abstract class AbstractMTRestTest {

	protected WebTarget getWebTarget() {
		Client client = ClientBuilder.newClient();
		return client.target("http://localhost:28080/test/rest");
	}

	@Deployment(name = "normal", order = 1)
	public static Archive<?> createDeployment() {

		WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

		File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
				.withTransitivity().asFile();
		testWar.addAsLibraries(files);

		testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.rest");

		testWar.delete("/WEB-INF/web.xml");
		testWar.addAsWebInfResource("mock-web.xml", "web.xml");

		return testWar;
	}
}
