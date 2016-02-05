package com.equalize.xpi.af.modules.testing.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;

public class XMLPayloadImpl implements XMLPayload {
	public byte[] content;
	public String encoding;
	public String contentType;
	public String description;
	public String name;
	public String schema;
	public String version;

	public String getEncoding() {
		return this.encoding;
	}

	public Reader getReader() {
		return null;
	}

	public String getText() {
		try {
			return new String(this.content, this.encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public void setContent(byte[] content, String encoding)
	throws InvalidParamException {
		this.content = content;
		this.encoding = encoding;
	}

	public void setText(String text) throws IOException {
		this.content = text.getBytes();
		this.encoding = new OutputStreamWriter(new ByteArrayOutputStream())
		.getEncoding();
	}

	public void setText(String text, String encoding) throws IOException {
		this.content = text.getBytes(encoding);
		this.encoding = encoding;
	}

	public void clearAttributes() {
		// TODO Auto-generated method stub

	}

	public String getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getContent() {
		return this.content;
	}

	public String getContentType() {
		return this.contentType;
	}

	public String getDescription() {
		return this.description;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.content);
	}

	public String getName() {
		return this.name;
	}

	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setAttribute(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void setContent(byte[] content) throws InvalidParamException {
		this.content = content;
	}

	public void setContentType(String contentType) throws InvalidParamException {
		this.contentType = contentType;
	}

	public void setDescription(String description) throws InvalidParamException {
		this.description = description;
	}

	public void setName(String name) throws InvalidParamException {
		this.name = name;
	}

	public String getSchema() {
		return this.schema;
	}

	public String getVersion() {
		return this.version;
	}

	public void setSchema(String schema) throws InvalidParamException {
		this.schema = schema;
	}

	public void setVersion(String version) throws InvalidParamException {
		this.version = version;
	}


}