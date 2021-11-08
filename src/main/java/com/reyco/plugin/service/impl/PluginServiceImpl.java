package com.reyco.plugin.service.impl;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reyco.interfaceor.HelloTest;
import com.reyco.plugin.config.PluginConfigProperties;
import com.reyco.plugin.model.PluginConfig;
import com.reyco.plugin.service.AbstractPluginService;

@Service("pluginService")
public class PluginServiceImpl extends AbstractPluginService implements InitializingBean,DisposableBean {
	
	@Autowired
	public PluginConfigProperties pluginConfigProperties;
	
	public PluginServiceImpl() {
		logger.debug("1---------------构造器");
	}
	
	@Override
	public Collection<PluginConfig> list() {
		return pluginConfigCacheMap.values();
	}
	
	@Override
	public void add() throws Exception {
		// 刷新配置
		Collection<PluginConfig> flushConfigs = flushConfigs();
		
		// 循环处理配置
		for (Iterator<PluginConfig> iterator = flushConfigs.iterator(); iterator.hasNext();) {
			PluginConfig pluginConfig = iterator.next();
			loadAdvice(pluginConfig);
		}
		
		// 是否启用配置
		for (Iterator<PluginConfig> iterator = flushConfigs.iterator(); iterator.hasNext();) {
			PluginConfig pluginConfig = iterator.next();
			Boolean active = pluginConfig.getActive();
			if (active) {
				logger.debug("添加并启用的插件:" + pluginConfig);
				resume(pluginConfig);
			}
		}

		/*for (Iterator<PluginConfig> iterator = flushConfigs.iterator(); iterator.hasNext();) {
			PluginConfig pluginConfig = iterator.next();
			String pluginKey = getPluginKey(pluginConfig);
			Object instantiate = adviceCacheMap.get(pluginKey);
			if (instantiate instanceof HelloTest) {
				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(instantiate.getClass());
				AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
				DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
				beanFactory.registerBeanDefinition(getBeanName(instantiate.getClass()), beanDefinition);
			}
		}*/

		pluginKeyThreadLocal.remove();
	}

	@Override
	public void delete(PluginConfig pluginConfig) throws Exception {
		
		// 暂停插件
		pause(pluginConfig);

		String pluginKey = getPluginKey(pluginConfig);

		// 移除advice对象
		if (adviceCacheMap.containsKey(pluginKey)) {
			adviceCacheMap.remove(pluginKey);
		}
		
		// 移除pluginConfig对象
		if (pluginConfigCacheMap.containsKey(pluginKey)) {
			pluginConfigCacheMap.remove(pluginKey);
		}
		
		logger.debug("【" + pluginConfig + "】通知已移除");
		// 移除线程变量
		pluginKeyThreadLocal.remove();
	}

	@Override
	public void pause(PluginConfig pluginConfigParam) throws Exception {
		String pluginKey = getPluginKey(pluginConfigParam);
		
		if (!adviceCacheMap.containsKey(pluginKey)) {
			throw new RuntimeException("advice does not exist");
		}
		
		if (!pluginConfigCacheMap.containsKey(pluginKey)) {
			throw new RuntimeException("pluginConfig does not exist");
		}
		
		PluginConfig pluginConfig = pluginConfigCacheMap.get(pluginKey);
		logger.debug("需要暂停的插件:【" + pluginConfig + "】");
		// 如果插件未启用直接return
		Boolean active = pluginConfig.getActive();
		if (!active) {
			logger.debug("【" + pluginConfigParam + "】插件未启用,不需要暂停。");
			return;
		}
		
		// 移除通知
		for (String name : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(name);
			if (bean == this) {
				continue;
			}
			if (!(bean instanceof Advised)) {
				continue;
			}
			Object instantiate = adviceCacheMap.get(pluginKey);
			Advice advice = (Advice) instantiate;
			((Advised) bean).removeAdvice(advice);
		}
		
		// 修改通知未启用状态
		pluginConfig.setActive(false);
		logger.debug("【" + pluginConfig + "】插件已暂停.");
		
		// 移除线程变量
		pluginKeyThreadLocal.remove();
	}

	@Override
	public void resume(PluginConfig pluginConfigParam) throws Exception {
		String pluginKey = getPluginKey(pluginConfigParam);
		if (!pluginConfigCacheMap.containsKey(pluginKey)) {
			throw new RuntimeException("pluginConfig does not exist");
		}
		
		PluginConfig pluginConfig = pluginConfigCacheMap.get(pluginKey);
		logger.debug("需要启用的插件:【" + pluginConfig + "】");
		if (!adviceCacheMap.containsKey(pluginKey)) {
			throw new RuntimeException("advice does not exist");
		}
		
		Object instantiate = adviceCacheMap.get(pluginKey);
		if(instantiate instanceof HelloTest) {
			return;
		}
		
		// 添加通知
		for (String name : applicationContext.getBeanDefinitionNames()) {
			
			Object bean = applicationContext.getBean(name);
			
			if (bean == this) {
				continue;
			}
			
			if (!(bean instanceof Advised)) {
				continue;
			}

			Advisor[] advisors = ((Advised) bean).getAdvisors();
			Boolean exist = false; // 判定通知是否已存在
			for (Advisor advisor : advisors) {
				Integer hashCode = advisor.getAdvice().hashCode();
				Integer hashCode2 = instantiate.hashCode();
				if (hashCode.equals(hashCode2)) {
					exist = true;
				}
			}
			
			// 通知不存在则添加
			if (!exist) {
				Advice advice = (Advice) instantiate;
				((Advised) bean).addAdvice(advice);
			}
			
		}
		
		// 修改通知为启用状态
		pluginConfig.setActive(true);
		logger.debug("【" + pluginConfig + "】插件已启用.");
		
		// 移除线程变量
		pluginKeyThreadLocal.remove();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.debug("4---------------属性完成之后调用");
		setConfigPath(pluginConfigProperties.getConfigPath());
	}

	@Override
	public void destroy() throws Exception {
		logger.debug("执行销毁方法");
		adviceCacheMap.clear();
		pluginConfigCacheMap.clear();
	}
	
	@PostConstruct
	public void postConstruct() {
		logger.debug("3---------------postConstruct");
	}
	
	@Autowired
	public void autowired() {
		logger.debug("2---------------autowired");
	}

	public PluginConfigProperties getPluginConfigProperties() {
		return pluginConfigProperties;
	}

	public void setPluginConfigProperties(PluginConfigProperties pluginConfigProperties) {
		this.pluginConfigProperties = pluginConfigProperties;
	}
}
