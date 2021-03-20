package com.reyco.plugin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	
	@PostMapping("upload")
	public String upload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		return "test";
	}
	
}
