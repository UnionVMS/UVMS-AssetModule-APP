package populatecustomcodesfromexistsingxmls;

import java.io.File;

/*
 * 
CREATE TABLE asset.customcodes
(
    id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default",
    constant character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    jsonstr character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT customcodes_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE asset.customcodes
    OWNER to asset;
    
    
    // every line must generate this structure
    	<insert tableName="customcodes">
			<column name="code" value="?" />
			<column name="constant" value="?" />
			<column name="description" value="?" />
			<column name="jsonstr" value="?" />
		</insert>
    
    
    
 */

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import eu.europa.ec.fisheries.uvms.asset.client.AbstractClientTest;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.client.model.CustomCodesPK;
import org.jboss.arquillian.junit.Arquillian;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(Arquillian.class)
public class MDR_Lite extends AbstractClientTest {


	@Inject
	AssetClient assetClient;

	static int counter = 0;

	private Map<String, Map<String, String>> MDR = new TreeMap<>();

	private String url = "jdbc:postgresql://localhost:25432/db71u";

	private ObjectMapper MAPPER = new ObjectMapper();

	private static int lines = 0;


	private Collection<String> getResources() {

		final ArrayList<String> retval = new ArrayList<String>();
		final String userDir = System.getProperty("user.dir" );

		final String classPath = "/opt/jboss/wildfly/standalone/tmp/asset.war";
		final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
		for (final String element : classPathElements) {
			final File file = new File(element);
			if (file.isDirectory()) {
				retval.addAll(getResourcesFromDirectory(file));
			}
		}
		return retval;
	}

	private Collection<String> getResourcesFromDirectory(final File directory) {

		final ArrayList<String> retval = new ArrayList<String>();
		final File[] fileList = directory.listFiles();
		for (final File file : fileList) {
			if (file.isDirectory()) {
				retval.addAll(getResourcesFromDirectory(file));
			} else {
				try {
					final String fileName = file.getCanonicalPath();
					if (fileName.contains("v02") || fileName.contains("v03") || fileName.contains("v04")
							|| fileName.contains("v05")) {
						retval.add(fileName);
					}
				} catch (final IOException e) {
					throw new Error(e);
				}
			}
		}
		return retval;
	}

	/**
	 * list the resources that match args[0]
	 * 
	 *            args[0] is the pattern to match, or list all resources if there
	 *            are no args
	 * @throws JDOMException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws JAXBException
	 */

	@Test
	public void exec() throws JDOMException, IOException {

		SAXBuilder reader = new SAXBuilder();
		String now = LocalDateTime.now(Clock.systemUTC()).toString();

		final Collection<String> list = getResources();
		for (final String url : list) {

			if (url.contains("Carrieractive")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Carriersource")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Eventcode")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Fishinggearmobility")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Fishinggeartype")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Fishinggear")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Fishingtype")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Hullmaterial")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Parameter")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Publicaid")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Typeofexport")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Vesselsegment")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Assetsegment")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Flagstate")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("NoteActivityCodes")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("License")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else if (url.contains("Settings")) {
				Document document = reader.build(url);
				interpret(document, now);
			} else {
				System.out.println("---------->>>> NOT handled " + url);
			}
		}
	}

	private void interpret(Document document, String now) throws IOException {

		Element classElement = document.getRootElement();

		List<Element> children = classElement.getChildren("changeSet");

		for (Element child : children) {
			List<Element> inserts = child.getChildren();
			String oldTableName = "";
			for (Element insert : inserts) {
				String tableName = insert.getAttributeValue("tableName").toUpperCase();
				if (!oldTableName.equals(tableName)) {
					oldTableName = tableName;
					counter++;
				}
				List<Element> columns = insert.getChildren();
				boolean codeReceived = false;
				boolean valueReceived = false;
				boolean gtypReceived = false;
				boolean mobtypeReceived = false;
				boolean ftypeReceived = false;
				String codeStr = "";
				String descrStr = "";
				Map<String,String> internalreferencesList = new HashMap<>();
				for (Element column : columns) {

					List<Attribute> attributes = column.getAttributes();
					for (Attribute attr : attributes) {
						String nameOfAttribute = attr.getName();
						String valueOfAttribute = column.getAttribute(nameOfAttribute).getValue();

						if (nameOfAttribute.equals("name")) {

							if (valueOfAttribute.equals("code")) {
								codeStr = column.getAttributeValue("value");
								codeStr = codeStr.trim();
								codeReceived = true;

							}
							if (valueOfAttribute.equals("description")) {
								descrStr = column.getAttributeValue("value");
								valueReceived = true;
							}
							
							// oscar special
							
							if (valueOfAttribute.equals("notesactcode_id")) {
								codeStr = column.getAttributeValue("value");
								descrStr = column.getAttributeValue("value");
								codeStr = codeStr.trim();
								codeReceived = true;
								valueReceived = true;

							}
							
							//----------------------------

							if (tableName.equals("FISHINGGEAR")) {
								
								if (valueOfAttribute.equals("fishg_fishgtyp_id")) {

									/*
									String value = column.getAttribute("valueNumeric") == null ? ""
											: column.getAttribute("valueNumeric").getValue();
									internalreferences ir = new internalreferences("fishinggeartype", value);
									internalreferencesList.add(ir);
									
									*/
									gtypReceived = true;
								}
								if (valueOfAttribute.equals("fishg_fishgm_id")) {
									/*
									String value = column.getAttribute("valueNumeric") == null ? ""
											: column.getAttribute("valueNumeric").getValue();
									internalreferences ir = new internalreferences("fishinggearmobility", value);
									internalreferencesList.add(ir);
									*/
									mobtypeReceived = true;
								}
								if (valueOfAttribute.equals("fishg_fishtyp_id")) {
									/*
									String value = column.getAttribute("valueNumeric") == null ? ""
											: column.getAttribute("valueNumeric").getValue();
									internalreferences ir = new internalreferences("fishingtype", value);
									internalreferencesList.add(ir);
									*/
									ftypeReceived = true;
								}
								
								if (codeReceived && valueReceived && gtypReceived && mobtypeReceived && ftypeReceived) {
									codeReceived = false;
									valueReceived = false;
									gtypReceived = false;
									mobtypeReceived = false;
									ftypeReceived = false;
									
									
									String json = MAPPER.writeValueAsString(internalreferencesList);
									internalreferencesList.clear();
									// OBS här kan även props stoppas i med lite fiffel
									insert(tableName.toUpperCase(), codeStr.toUpperCase(), descrStr);

								}
								
							} else {
								if (codeReceived && valueReceived) {
									codeReceived = false;
									valueReceived = false;
									insert(tableName.toUpperCase(), codeStr.toUpperCase(), descrStr);
								}
							}
						}

					}
				}
			}
		}
	}



	LocalDateTime validStartDate = LocalDateTime.of(1827, 01,01,1,1);
	LocalDateTime validEndDate = LocalDateTime.of(2999, 01,01,1,1);

	private void insert(String constant, String code , String description) throws IOException {

		CustomCode customCode = new CustomCode();
		CustomCodesPK pk = new CustomCodesPK();
		pk.setConstant(constant);
		pk.setCode(code);
		pk.setValidFromDate(validStartDate);
		pk.setValidToDate(validEndDate);
		customCode.setPrimaryKey(pk);
		customCode.setDescription(description);
		assetClient.createCustomCode(customCode);
	}
}
