package com.reyco.plugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reyco.plugin.service.TestService;


@RestController
public class TestController {
	
	@Autowired
	private TestService testService;
	
	@GetMapping("test")
	public String test() {
		testService.test();
		return "test";
	}
	
	@GetMapping("test1")
	public String test1() {
		testService.test1();
		return "test1";
	}
	
}
