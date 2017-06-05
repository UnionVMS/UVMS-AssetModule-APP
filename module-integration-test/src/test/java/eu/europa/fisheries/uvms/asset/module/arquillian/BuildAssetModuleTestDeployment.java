package eu.europa.fisheries.uvms.asset.module.arquillian;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ArquillianSuiteDeployment
public abstract class BuildAssetModuleTestDeployment {

	final static Logger LOG = LoggerFactory.getLogger(BuildAssetModuleTestDeployment.class);

	@Deployment(name = "assetmodule", order = 1)
	public static Archive<?> createAssetModuleDeployment() throws IOException {
		final EnterpriseArchive ear = ShrinkWrap.createFromZipFile(EnterpriseArchive.class,
				new File("target/asset-module.ear"));

		modifyWarWithTestPackages(ear);
		return ear;
	}

	private static void modifyWarWithTestPackages(final EnterpriseArchive ear) {
		WebArchive war = ear.getAsType(WebArchive.class, getWarContext(ear).get());
		war.addPackages(true, "eu.europa.fisheries.uvms.asset.module.arquillian");
	}

	private static ArchivePath getWarContext(final EnterpriseArchive ear) {
		Map<ArchivePath, Node> content = ear.getContent();

		Set<ArchivePath> paths = content.keySet();
		for (ArchivePath archivePath : paths) {
			if (archivePath.get().contains(".war")) {
				return archivePath;
			}

		}
		return null;
	}
}
