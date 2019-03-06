package org.wayne.feiq.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wayne.feiq.FeiQException;
import org.wayne.feiq.util.Util;

public class PropertiesConfig extends Config {

	private static final Log log = LogFactory.getLog(PropertiesConfig.class);
	private static final File FILE = new File("cfg/javafeiq.properties");

	private Properties config = new Properties();

	public PropertiesConfig() {
		InputStream in = null;
		try {
			in = new FileInputStream(FILE);
			init(in);
		} catch (Exception e) {
			throw new FeiQException(e);
		} finally {
			Util.closeQuietly(in, log);
		}
	}

	private void init(InputStream in) throws IOException {
		config.load(in);
		setDefaultSavePath(config.getProperty("defaultSavePath", ""));
		in.close();
	}

	public static boolean isFileExists() {
		return FILE.exists();
	}

	public void save() {
		try {
			config.setProperty("defaultSavePath", getDefaultSavePath());
			save(FILE);
		} catch (IOException e) {
			log.error("save config file error", e);
		}
	}

	public void save(File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		save(out);
		out.close();
	}

	public void save(OutputStream out) throws IOException {
		config.store(out, "JavaFeiQConfigration");
	}
}
