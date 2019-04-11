package br.unifesp.ict.seg.geniesearchapi.infrastructure.util;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;

public class GenieSearchAPIConfig {

	private static Properties properties;
	private static final String configFileName = "geniesearchapi.properties";

	private GenieSearchAPIConfig() {
	}

	static {
		try {
			properties = new Properties();
			URL url = ClassLoader.getSystemResource(configFileName);
			properties.load(url.openStream());
			
			InputStream is = url.openStream();
			ArgumentManager.PROPERTIES_STREAM.setValue(is);
			JavaRepositoryFactory.INPUT_REPO.permit();
			DatabaseConnectionFactory.DATABASE_URL.permit();
			DatabaseConnectionFactory.DATABASE_USER.permit();
			DatabaseConnectionFactory.DATABASE_PASSWORD.permit();
			ArgumentManager.initializeProperties();		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logConfigStatus();
			createDefaultFolders();
		}

	}
	
	private static void logConfigStatus() {
		
		//crawled-projects folder name
		if(!"crawled-projects".equals(getCrawledProjectsPath().getFileName()+"")) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("A pasta dos c�digos-fonte do reposit�rio precisa chamar-se 'crawled-projects' no arquivo de propriedades: " + ClassLoader.getSystemResource("") + configFileName);
			LogUtils.getLogger().error("O valor atualmente usado � inv�lido: input-repo = " + getCrawledProjectsPath() );
			throw new RuntimeException();
		}
		
		//Config File existence
		URL url = ClassLoader.getSystemResource(configFileName);
		if(url == null) {

			String example = "\n\nExemplo para o conte�do do arquivo:";
			example += "\n\n-- Repository paths";
			example += "\ninput-repo = D:/Sourcerer_portable/repositories/test_repo/crawled-projects/";
			example += "\ndatabase-url=jdbc:mysql://localhost:3306/test_repo";
			example += "\ndatabase-user=root";
			example += "\ndatabase-password=123";
			example += "\nwebserver-url = http://localhost:8080\n";
			
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Arquivo n�o encontrado: " + ClassLoader.getSystemResource("") + configFileName);
			LogUtils.getLogger().error(example);
			throw new RuntimeException();
		}else {
			LogUtils.getLogger().info("Genie Search API");
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info(ClassLoader.getSystemResource(configFileName).getPath());
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info("input-repo = " + getRepoPath());
			LogUtils.getLogger().info("database-url = " + getDatabaseURL());
			LogUtils.getLogger().info("database-user = " + getDatabaseUser());
			LogUtils.getLogger().info("database-password = ***");
			LogUtils.getLogger().info("webserver-url = " + getWebServerURL());
			LogUtils.getLogger().info("");
			LogUtils.getLogger().info("Genie Search API");
			LogUtils.getLogger().info("");
		}
		
		//Solr connection
		String solrURL = GenieSearchAPIConfig.getSolrURL();
		try {
			url = new URL(solrURL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
		}catch (Exception e) {
			LogUtils.getLogger().error("");
			LogUtils.getLogger().error("Solr OFFLINE: " + solrURL);
			LogUtils.getLogger().error("");
		}
	
	}
	
	private static void createDefaultFolders() {
		File dir = getSolrConfigPath().toFile();
		if(!dir.isDirectory())
			dir.mkdirs();

		dir = getSolrIndexPath().toFile();
		if(!dir.isDirectory())
			dir.mkdirs();

		dir = getSlicedPath().toFile();
		if(!dir.isDirectory())
			dir.mkdirs();

		dir = getExtractTempPath().toFile();
		if(!dir.isDirectory())
			dir.mkdirs();

		dir = getJarPath().toFile();
		if(!dir.isDirectory())
			dir.mkdirs();
	}

	public static Path getRepoPath() {
		return Paths.get(properties.getProperty("input-repo")).getParent();
}
	
	public static String getDatabaseURL() {
		return properties.getProperty("database-url");
	}

	public static String getDatabaseUser() {
		return properties.getProperty("database-user");
	}

	public static String getDatabasePassword() {
		return properties.getProperty("database-password");
	}

	public static Path getCrawledProjectsPath() {
		return Paths.get(properties.getProperty("input-repo"));
	}

	public static String getWebServerURL() {
		return properties.getProperty("webserver-url");
	}
	
	public static String getSolrURL() {
		return getWebServerURL() + "/solr";
	}
	
	public static Path getSolrConfigPath() {
		return Paths.get(getRepoPath().toString(), "solr-repo", "conf");
	}

	public static Path getSolrIndexPath() {
		return Paths.get(getRepoPath().toString(), "solr-repo", "data", "index");
	}

	public static Path getSlicedPath() {
		return Paths.get(getRepoPath().toString(), "methods", "slice");
	}

	public static Path getExtractTempPath() {
		return Paths.get(getRepoPath().toString(), "methods", "extract-temp");
	}

	public static Path getJarPath() {
		return Paths.get(getRepoPath().toString(), "methods", "jar");
	}

}