package com.equalize.xpi.af.modules.util;

import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;

public class DynamicConfigurationHelper {

	private final Message msg;
	
	public DynamicConfigurationHelper (Message msg) {
		this.msg = msg;
	}

	public String get(String namespace, String attribute) {
		MessagePropertyKey mpk = new MessagePropertyKey(attribute, namespace);
		return this.msg.getMessageProperty(mpk);		
	}

	public void add(String namespace, String attribute, String value) throws InvalidParamException {
		MessagePropertyKey mpk = new MessagePropertyKey(attribute, namespace);
		this.msg.setMessageProperty(mpk, value );
	}

	public void change(String namespace, String attribute, String value) throws InvalidParamException {
		MessagePropertyKey mpk = new MessagePropertyKey(attribute, namespace);
		this.msg.removeMessageProperty(mpk);
		this.msg.setMessageProperty(mpk, value);
	}

	public void delete(String namespace, String attribute) {
		MessagePropertyKey mpk = new MessagePropertyKey(attribute, namespace);
		this.msg.removeMessageProperty(mpk);
	}
}
