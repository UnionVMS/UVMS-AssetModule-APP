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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MDR_Lite {


	static int counter = 0;

	private Map<String, Map<String, String>> MDR = new TreeMap<>();

	private String url = "jdbc:postgresql://localhost:25432/db71u";
	private Properties props = new Properties();
	private Connection conn = null;
	private PreparedStatement stmt_exists = null;
	private PreparedStatement stmt_insert = null;
	private PreparedStatement stmt_delete = null;
	private PreparedStatement stmt_select = null;
	private String sql_exists = "select count(*) from customcodes where constant=? and code=?";
	private String sql_insert = "insert into customcodes (constant,code,description,extradata) values(?,?,?,?)";
	private String sql_delete = "delete from customcodes where constant=? and code=?";
	private String sql_select = "select constant,code,description,extradata from asset.customcodes order by constant,code";

	private ObjectMapper MAPPER = new ObjectMapper();

	private static int lines = 0;

	public void exec() {
		try {

			props.setProperty("user", "asset");
			props.setProperty("password", "asset");
			// props.setProperty("ssl", "true");
			conn = DriverManager.getConnection(url, props);
			stmt_exists = conn.prepareStatement(sql_exists);
			stmt_insert = conn.prepareStatement(sql_insert);
			stmt_delete = conn.prepareStatement(sql_delete);
			stmt_select = conn.prepareStatement(sql_select);

			load();
			


			try {
				stmt_select = conn.prepareStatement(sql_select);
				ResultSet rs = stmt_select.executeQuery();
				StringBuilder  xml = new StringBuilder();
				xml.append("<root>");
				while (rs.next()) {
					lines++;
					xml.append("<insert tableName=\'customcodes\'>");
					xml.append("<column name=\'constant\' value=\'" + rs.getString(1) + "\'/>");
					
					String code = rs.getString(2);
					code = code.replace(">", "");
					xml.append("<column name=\'code\' value=\'" + code + "\'/>");
					String descr = rs.getString(3);
					descr = descr.replaceAll(">", "");
					xml.append("<column name=\'description\' value=\'" + descr + "\'/>");
					xml.append("<column name=\'extradata\' value=\'" + rs.getString(4) + "\'/>");
					xml.append("</insert>");
					xml.append("\n");
				}
				xml.append("</root>");

				System.out.println(xml.toString());
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (stmt_exists != null) {
				try {
					stmt_exists.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (stmt_insert != null) {
				try {
					stmt_insert.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (stmt_delete != null) {
				try {
					stmt_delete.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (stmt_select != null) {
				try {
					stmt_select.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private Collection<String> getResources() {

		final ArrayList<String> retval = new ArrayList<String>();
		final String classPath = System.getProperty("java.class.path", ".");
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
	 * @param args
	 *            args[0] is the pattern to match, or list all resources if there
	 *            are no args
	 * @throws JDOMException
	 * @throws DocumentException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws JAXBException
	 */

	public void load() throws JDOMException, IOException {

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

	private void interpret(Document document, String now) throws JsonProcessingException {

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
									

									try {
										stmt_exists.setString(1, tableName.toUpperCase());
										stmt_exists.setString(2, codeStr.toUpperCase());
										ResultSet rs = stmt_exists.executeQuery();
										rs.next();
										int n = rs.getInt(1);
										if (n > 0) {

											stmt_delete.setString(1, tableName.toUpperCase());
											stmt_delete.setString(2, codeStr.toUpperCase());
											stmt_delete.executeUpdate();
										}
										stmt_insert.setString(1, tableName.toUpperCase());
										stmt_insert.setString(2, codeStr.toUpperCase());
										stmt_insert.setString(3, descrStr);
										stmt_insert.setString(4, json);
										stmt_insert.executeUpdate();

										rs.close();

									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							} else {
								if (codeReceived && valueReceived) {
									codeReceived = false;
									valueReceived = false;

									try {
										stmt_exists.setString(1, tableName.toUpperCase());
										stmt_exists.setString(2, codeStr.toUpperCase());
										ResultSet rs = stmt_exists.executeQuery();
										rs.next();
										int n = rs.getInt(1);
										if (n > 0) {

											stmt_delete.setString(1, tableName.toUpperCase());
											stmt_delete.setString(2, codeStr.toUpperCase());
											stmt_delete.executeUpdate();
										}
										stmt_insert.setString(1, tableName.toUpperCase());
										stmt_insert.setString(2, codeStr.toUpperCase());
										stmt_insert.setString(3, descrStr);
										stmt_insert.setString(4, "");
										stmt_insert.executeUpdate();

										rs.close();

									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}
						}

					}
				}
			}
		}

	}

	public static void main(final String[] args) {

		MDR_Lite app = new MDR_Lite();
		app.exec();

		//System.out.println("Number of lines          = " + lines);
		//System.out.println("Number of xmls processed = " + counter);

	}

}
