package com.reyco.plugin.model;

import java.util.Arrays;

public class PluginConfig {
	private String id;
	private String name;
	private String[] methods;
	private String className;
	private String jarRemoteUrl;
	/**
	 * true:  启用
	 * false: 暂停
	 */
	private Boolean active;
	
	private String version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getMethods() {
		return methods;
	}

	public void setMethods(String[] methods) {
		this.methods = methods;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getJarRemoteUrl() {
		return jarRemoteUrl;
	}

	public void setJarRemoteUrl(String jarRemoteUrl) {
		this.jarRemoteUrl = jarRemoteUrl;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PluginConfig [id=" + id + ", name=" + name + ", methods=" + Arrays.toString(methods) + ", className="
				+ className + ", jarRemoteUrl=" + jarRemoteUrl + ", active=" + active + ", version=" + version + "]";
	}
}
