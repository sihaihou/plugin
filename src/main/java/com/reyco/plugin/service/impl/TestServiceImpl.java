package com.reyco.plugin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.reyco.plugin.service.TestService;

@Service("testService")
public class TestServiceImpl implements TestService{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public String test() {
		logger.info("##############################执行目标方法test");
		return "";
	}
	
	@Transactional
	public String test1() {
		logger.info("##############################执行目标方法test1");
		TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
		return "";
	}
	
}
