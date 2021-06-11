package com.reyco.plugin.service;

import java.util.Collection;

import com.reyco.plugin.model.PluginConfig;

/**
 * 插件借口
 * @author reyco
 *
 */
public interface PluginService {
	/**
	 * 查询所有的插件
	 * @return
	 */
	Collection<PluginConfig> list();
	
	/**
	 * 添加插件
	 * @param pluginConfig
	 */
	void add() throws Exception;
	
	/**
	 * 删除插件
	 * @param pluginConfig
	 */
	void delete(PluginConfig pluginConfig) throws Exception;
	
	/**
	 * 暂停插件
	 * @param pluginConfig
	 */
	void pause(PluginConfig pluginConfig) throws Exception;
	
	/**
	 * 启用插件
	 * @param pluginConfig
	 */
	void resume(PluginConfig pluginConfig) throws Exception;
	
}
