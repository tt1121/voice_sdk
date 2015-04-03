package com.yzx.http.net;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetParameters {

	private String method;
	private List<String> fields = new ArrayList<String>();
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, FileItem> attachments = new HashMap<String, FileItem>();

	public void addFields(String... value) {
		if (value != null) {
			for (String v : value) {
				fields.add(v);
			}
		}
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	public String getParam(String key) {
		return params.get(key);
	}

	public void removeParam(String key) {
		params.remove(key);
	}

	public void addAttachment(String key, FileItem file) {
		if (file == null) {
			return;
		}
		attachments.put(key, file);
	}

	public FileItem getAttachment(String key) {
		return attachments.get(key);
	}

	public void removeAttachment(String key) {
		attachments.remove(key);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Map<String, FileItem> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, FileItem> attachment) {
		this.attachments = attachment;
	}

}
