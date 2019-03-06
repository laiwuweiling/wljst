package org.wayne.feiq.cfg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigLoader {

	private static final Log log = LogFactory.getLog(ConfigLoader.class);

	private ConfigLoader() {
	}

	public static Config buildConfig() {
		Config config = null;
		try {
			if (XmlConfig.isFileExists()) {
				config = new XmlConfig();
				log.debug("load XmlConfig");
			} else if (PropertiesConfig.isFileExists()) {
				config = new PropertiesConfig();
				log.debug("load PropertiesConfig");
			}
		} catch (Exception e) {
			log.warn("load config error", e);
		}
		if (config == null) {
			config = XmlConfig.getDefault();
			log.debug("use default config");
		}
		return config;
	}
}
