package com.reyco.plugin.service;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reyco.plugin.model.PluginConfig;
import com.reyco.plugin.model.Plugins;
/**
 * 插件借口的抽象实现类
 * @author reyco
 *
 */
public abstract class AbstractPluginService implements PluginService,ApplicationContextAware {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final static String LINES = "-";
	/**
	 * 配置文件地址
	 */
	protected String configPath = "C:/Users/Terry/Desktop/workspace/资料/plugin/plugin.config"; 
	/**
	 * <pre>
	 * 缓存插件对象:
	 * key:  id +'-'+ version 
	 * value: PluginConfig
	 * </pre>
	 */
	protected final static Map<String, PluginConfig> pluginConfigCacheMap = new ConcurrentHashMap<>(256);
	/**
	 * <pre>
	 * 缓存通知对象:
	 * key:  id +'-'+ version 
	 * value: advice
	 * </pre>
	 */
	protected final static Map<String, Object> adviceCacheMap = new HashMap<>(256);
	/**
	 * <pre>
	 * 线程变量pluginKey
	 * key:  id +'-'+ version 
	 * value: id +'-'+ version 
	 * </pre>
	 */
	protected ThreadLocal<Map<String, String>> pluginKeyThreadLocal = new ThreadLocal<>();
	
	protected ApplicationContext applicationContext;
	/**
	 * 构建通知对象
	 * @param pluginConfig
	 * @return
	 * @throws Exception
	 */
	public Object loadAdvice(PluginConfig pluginConfig) throws Exception {
		String pluginKey = getPluginKey(pluginConfig);
		// 缓存中
		if (adviceCacheMap.containsKey(pluginKey)) {
			return adviceCacheMap.get(pluginKey);
		}
		URL targetURL = new URL(pluginConfig.getJarRemoteUrl());
		// 判断targetURL是否已经加载过了
		URLClassLoader loader = (URLClassLoader) this.getClass().getClassLoader();
		Boolean isLoader = false;
		for (URL url : loader.getURLs()) {
			if (url.equals(targetURL)) {
				isLoader = true;
				break;
			}
		}
		// 如果没有加载，则加载jar到当前环境中
		if (!isLoader) {
			 //Method addMethod = URLClassLoader.class.getDeclaredMethod("AddURL", new Class[]{URL.class});
			 //addMethod.setAccessible(true);
			 //addMethod.invoke(loader, targetURL);
			loader = new URLClassLoader(new URL[] { targetURL });
		}
		// 加载并反射创建对象
		Class<?> adviceClazz = loader.loadClass(pluginConfig.getClassName());
		Object adviceInstance = BeanUtils.instantiateClass(adviceClazz);
		
		adviceCacheMap.put(pluginKey,adviceInstance);
		
		return adviceCacheMap.get(pluginKey);
	}

	/**
	 * 刷新PluginConfig
	 */
	public Collection<PluginConfig> flushConfigs() {
		File configFile = new File(configPath);
		
		// 读取配置文件到String
		String pluginConfigStr = readConfigAsString(configFile);
		
		// 配置文件字符串转Plugins对象
		Plugins plugins = parseToPlugins(pluginConfigStr);
		
		// 解析plugins为List<PluginConfig>
		List<PluginConfig> pluginConfigs = pluginsToPluginConfigs(plugins);
		
		// 放入缓存
		for (PluginConfig pluginConfig : pluginConfigs) {
			String pluginKey = getPluginKey(pluginConfig);
			if (!pluginConfigCacheMap.containsKey(pluginKey)) {
				logger.debug("新添加的插件:【"+pluginConfig.toString()+"】");
				pluginConfigCacheMap.put(pluginKey, pluginConfig);
			}
		}
		
		return pluginConfigCacheMap.values();
	}

	/**
	 * 解析plugins为List<PluginConfig>
	 * 
	 * @param plugins
	 * @return
	 */
	protected List<PluginConfig> pluginsToPluginConfigs(Plugins plugins) {
		List<PluginConfig> pluginConfigs = new ArrayList<>();
		
		// 获取插件集合字符串
		String configs = plugins.getConfigs();
		
		// 插件集合字符串转JSONArray数组
		JSONArray pluginConfigArray = JSONArray.parseArray(configs);
		
		// JSONArray转成List<PluginConfig>
		for (Object pluginConfigObj : pluginConfigArray) {
			JSONObject pluginConfigJSONObject = JSONObject.parseObject(pluginConfigObj.toString());
			// 单个插件JSONObject转PluginConfig对象
			PluginConfig pluginConfig = parseToPluginConfig(pluginConfigJSONObject);
			pluginConfigs.add(pluginConfig);
		}
		
		logger.debug("解析出的PluginConfigs对象:【"+pluginConfigs+"】");
		return pluginConfigs;
	}

	/**
	 * 配置文件字符串转Plugins对象
	 * @param configFileStr
	 * @return
	 */
	protected Plugins parseToPlugins(String pluginConfigStr) {
		// 将整个配置文件字符串解析成JSONObject对象
		JSONObject pluginConfigObject = JSONObject.parseObject(pluginConfigStr);
		String configs = pluginConfigObject.getString("configs");
		String name = pluginConfigObject.getString("name");
		
		// JSONObject转Plugins对象
		Plugins plugins = new Plugins();
		plugins.setConfigs(configs);
		plugins.setName(name);
		
		logger.debug("解析的Plugins对象:【"+plugins+"】");
		return plugins;
	}

	/**
	 * 读取配置文件到String
	 * 
	 * @param configFile
	 * @return
	 */
	protected String readConfigAsString(File configFile) {
		InputStream is = null;
		try {
			// 读取配置文件到is
			is = new FileInputStream(configFile);
			byte[] car = new byte[1024];
			int len; // 接收实际读取大小
			// 转成StringBuilder
			StringBuilder pluginConfigSb = new StringBuilder();
			while (-1 != (len = is.read(car))) {
				pluginConfigSb.append(new String(car, 0, len));
			}
			String pluginConfigStr = pluginConfigSb.toString();
			logger.debug("plugin.config配置文件内容:【"+pluginConfigStr+"】");
			
			return pluginConfigStr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(is);
		}
		return null;
	}

	/**
	 * configJSONObject转PluginConfig
	 * @param pluginConfigJSONObject
	 * @return
	 */
	protected PluginConfig parseToPluginConfig(JSONObject configJSONObject) {
		PluginConfig pluginConfig = new PluginConfig();
		
		pluginConfig.setId(configJSONObject.getString("id"));
		pluginConfig.setName(configJSONObject.getString("name"));
		pluginConfig.setClassName(configJSONObject.getString("className"));
		pluginConfig.setMethods(new String[] { configJSONObject.getString("methods") });
		pluginConfig.setJarRemoteUrl(configJSONObject.getString("jarRemoteUrl"));
		pluginConfig.setActive(configJSONObject.getString("active").equals("1") ? true : false);
		pluginConfig.setVersion(configJSONObject.getString("version"));
		
		return pluginConfig;
	}

	/**
	 * 获取pluginKey
	 * @param pluginConfig
	 * @return
	 */
	protected String getPluginKey(PluginConfig pluginConfig) {
		if (pluginConfig == null) {
			throw new RuntimeException("pluginConfig is not null");
		}
		
		Map<String, String> pluginKeyMap = pluginKeyThreadLocal.get();
		String pluginKey = pluginConfig.getId() + LINES + pluginConfig.getVersion();
		
		if (pluginKeyMap == null) {
			pluginKeyMap = new HashMap<>();
			pluginKeyThreadLocal.set(pluginKeyMap);
		}
		
		if (!pluginKeyMap.containsKey(pluginKey)) {
			pluginKeyMap.put(pluginKey, pluginKey);
		}
		
		return pluginKeyMap.get(pluginKey);
	}
	/**
	 * 流close
	 * @param closeable
	 */
	protected void close(Closeable... closeable) {
		for (Closeable close : closeable) {
			// 关闭流
			if (close != null) {
				try {
					close.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 缓存beanName
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	protected <T> String getBeanName(Class<T> clazz) {
		String name = clazz.getSimpleName();
		String beanName = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toLowerCase());
		return beanName;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
	public String getConfigPath() {
		return configPath;
	}
	
}
