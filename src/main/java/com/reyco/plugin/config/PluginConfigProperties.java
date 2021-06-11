package com.reyco.plugin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
*@author reyco
*@date  2021年3月17日---下午12:46:23
*<pre>
*
*<pre> 
*/
@Configuration
@ConfigurationProperties(prefix=PluginConfigProperties.PLUGIN)
public class PluginConfigProperties {
	
	public final static String PLUGIN = "plugin";
	
	private String configPath;

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
}
