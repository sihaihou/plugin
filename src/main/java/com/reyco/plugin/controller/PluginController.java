package com.reyco.plugin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reyco.plugin.model.PluginConfig;
import com.reyco.plugin.service.PluginService;
import com.reyco.plugin.service.impl.PluginServiceImpl;

@RestController
@RequestMapping("plugin")
public class PluginController {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("pluginService")
	private PluginServiceImpl pluginServiceImpl;
	
	@Autowired
	private PluginService pluginService;
	
	@GetMapping("list")
	public Object list() throws Exception {
		logger.info("查询pluginConfig列表");
		return pluginService.list();
	}
	
	@GetMapping("add")
	public String add() throws Exception {
		logger.info("添加plugin对象");
		pluginService.add();
		return "ok";
	}
	
	@GetMapping("delete")
	public String delete(PluginConfig pluginConfig) throws Exception {
		logger.info("删除plugin对象");
		pluginConfig.setVersion("1.0.1");
		pluginService.delete(pluginConfig);
		return "ok";
	}
	
	
	@GetMapping("pause")
	public String pause(PluginConfig pluginConfig) throws Exception {
		logger.info("暂停plugin对象");
		pluginConfig.setVersion("1.0.1");
		pluginService.pause(pluginConfig);
		return "ok";
	}
	
	@GetMapping("resume")
	public String resume(PluginConfig pluginConfig) throws Exception {
		logger.info("启用plugin对象");
		pluginConfig.setVersion("1.0.1");
		pluginService.resume(pluginConfig);
		return "ok";
	}
}
