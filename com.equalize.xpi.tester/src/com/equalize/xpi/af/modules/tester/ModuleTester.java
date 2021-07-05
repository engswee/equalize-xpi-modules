package com.equalize.xpi.af.modules.tester;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Set;

import com.equalize.xpi.af.modules.tester.impl.MessageImpl;
import com.equalize.xpi.af.modules.tester.impl.ModuleContextImpl;
import com.equalize.xpi.af.modules.tester.impl.XMLPayloadImpl;
import com.equalize.xpi.tester.util.Converter;
import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageClass;
import com.sap.engine.interfaces.messaging.api.MessageDirection;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class ModuleTester {
	private final String encoding;
	private final String fileName;
	private final Hashtable<String, String> parameters;
	private final Module module;
	private ModuleData data;
	private ModuleContextImpl context;
	private Message msg;

	public static ModuleTester newInstance(String className, String fileName, Hashtable<String, String> parameters) {
		return new ModuleTester(className, fileName, parameters, "UTF-8");
	}

	public static ModuleTester newInstance(String className, String fileName, Hashtable<String, String> parameters, String encoding) {
		return new ModuleTester(className, fileName, parameters, encoding);
	}

	private ModuleTester(String className, String fileName, Hashtable<String, String> parameters, String encoding) {
		try {
			this.fileName = fileName;
			this.encoding = encoding;
			this.parameters = parameters;
			Class<?> moduleClass = Class.forName(className);
			this.module = (Module) moduleClass.getConstructor().newInstance();
			initialize();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

	private void initialize() throws IOException, MessagingException {
		// Set PI message details
		this.msg = new MessageImpl("PartyA", "ServiceA", "Interface1", //Sender details
								   "PartyB", "ServiceB", 			   //Receiver details
								   MessageClass.APPLICATION_MESSAGE,
								   MessageDirection.INBOUND,
								   "005056B777881ED491EE4E73A55ACD5D", //Message ID
								   "FTP",							   //Transport Protocol
								   "");								   //Serialization Context

		// Get payload from file
		InputStream inpStr = new FileInputStream(new File(this.fileName));
		ByteArrayOutputStream baos = Converter.toBAOS(inpStr);

		// Set XML payload as the main document of the message
		XMLPayload xml = new XMLPayloadImpl();
		xml.setContent(baos.toByteArray(), this.encoding);
		this.msg.setDocument(xml);

		// Set message as the module data
		this.data = new ModuleData();
		this.data.setPrincipalData(this.msg);

		// Set module parameters
		this.context = new ModuleContextImpl("abcdef1234567890", this.parameters);
	}
	
	public String execute() throws ModuleException {
		// Execute Module processing
		this.data = this.module.process(this.context, this.data);

		// Return the content
		Message output = (Message) this.data.getPrincipalData();		
		return output.getDocument().getText();
	}
	
	public void execute(String outputFileName) throws IOException, ModuleException {
		// Execute Module processing
		this.data = this.module.process(this.context, this.data);
		
		Message output = (Message) this.data.getPrincipalData();
		ByteArrayInputStream bais = (ByteArrayInputStream) output.getDocument().getInputStream();
		ByteArrayOutputStream baos = Converter.toBAOS(bais);
		FileOutputStream fileOutStr = new FileOutputStream(new File(outputFileName));
		baos.writeTo(fileOutStr);
		fileOutStr.close();
	}
	
	public void addDynCfg(String namespace, String attribute, String value) throws InvalidParamException {
		MessagePropertyKey key = new MessagePropertyKey(attribute, namespace);
		this.msg.setMessageProperty(key, value);
	}
	
	public void getDynCfg(String step) {
		Message message = (Message) this.data.getPrincipalData();
		Set<MessagePropertyKey> mpkSet = message.getMessagePropertyKeys();
		if (mpkSet.size() != 0) {
			System.out.println("==============================================");
			System.out.println("Dynamic Configuration " + step + " module processing");
			System.out.println("==============================================");
			for (MessagePropertyKey mpk : mpkSet) {
				System.out.println(mpk.getPropertyNamespace() + ";"
						+ mpk.getPropertyName() + ";"
						+ message.getMessageProperty(mpk));
			}
		}
	}
	
	public String getDynCfg() {
		Message message = (Message) this.data.getPrincipalData();
		Set<MessagePropertyKey> mpkSet = message.getMessagePropertyKeys();
		if (mpkSet.size() != 0) {
			StringBuilder sb = new StringBuilder();
			for (MessagePropertyKey mpk : mpkSet) {
				sb.append(mpk.getPropertyNamespace() + ";" + mpk.getPropertyName() + ";" + message.getMessageProperty(mpk));
				sb.append("\r\n");
			}
			return sb.toString();
		} else {
			return "";
		}
	}
}
