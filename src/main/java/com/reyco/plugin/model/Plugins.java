package com.reyco.plugin.model;

public class Plugins {
	private String configs;
	private String name;
	public String getConfigs() {
		return configs;
	}
	public void setConfigs(String configs) {
		this.configs = configs;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Plugins [configs=" + configs + ", name=" + name + "]";
	}
	
}
