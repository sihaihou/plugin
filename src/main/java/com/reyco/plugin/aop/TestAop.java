package com.reyco.plugin.aop;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.reyco.interfaceor.HelloTest;
import com.reyco.plugin.model.PluginConfig;
import com.reyco.plugin.service.impl.PluginServiceImpl;

@Aspect
@Component
public class TestAop extends PluginServiceImpl{
	
	@Pointcut("execution(public * com.reyco.plugin.service.impl.*.*(..))")
    public void testPointCat(){}
	
	@Before("testPointCat()")
	public void after(JoinPoint joinPoint) throws Throwable {
		Collection<Object> values = adviceCacheMap.values();
		for (Object object : values) {
			if(object instanceof HelloTest) {
				Map<String, ? extends Object> beansMap = this.applicationContext.getBeansOfType(object.getClass());
				for(Iterator<? extends Object> iterator = beansMap.values().iterator();iterator.hasNext();) {
					Object bean = iterator.next();
					HelloTest helloTest = (HelloTest)bean;
					helloTest.hello();
					return;
				}
				
			}
		}
	}

	@Override
	public Collection<PluginConfig> list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add() throws Exception {
		
	}

	@Override
	public void delete(PluginConfig pluginConfig) throws Exception {
		
	}

	@Override
	public void pause(PluginConfig pluginConfig) throws Exception {
		
	}

	@Override
	public void resume(PluginConfig pluginConfig) throws Exception {
		
	}
}
