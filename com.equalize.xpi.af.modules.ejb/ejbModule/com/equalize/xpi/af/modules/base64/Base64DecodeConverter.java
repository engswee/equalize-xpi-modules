package com.equalize.xpi.af.modules.base64;

import java.io.InputStream;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.ConversionBase64Decode;
import com.equalize.xpi.util.converter.ConversionDOMInput;
import com.equalize.xpi.util.converter.Converter;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class Base64DecodeConverter extends AbstractModuleConverter {

	private String inputType;
	private boolean zippedContent;
	private String xpath;
	private String base64String;
	private String contentType;

	public Base64DecodeConverter(Message msg, ParameterHelper param,
			AuditLogHelper audit, DynamicConfigurationHelper dyncfg, Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		this.inputType = this.param.getMandatoryParameter("inputType");
		this.param.checkParamValidValues("inputType", "plain,xml");
		this.zippedContent = this.param.getBoolParameter("zippedContent");
		if(this.inputType.equalsIgnoreCase("xml")) {
			this.xpath = this.param.getConditionallyMandatoryParameter("xpath", "inputType", "xml");
		}
		this.contentType = this.param.getParameter("contentType");
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			InputStream inStream = this.payload.getInputStream();
			if(this.inputType.equalsIgnoreCase("plain")) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input plain text");
				this.base64String = Converter.toString(inStream);

			} else if(this.inputType.equalsIgnoreCase("xml")) {
				ConversionDOMInput dom = new ConversionDOMInput(inStream);
				this.audit.addLog(AuditLogStatus.SUCCESS, "Evaluating XPath to retrieve Base64 string");
				this.base64String = dom.evaluateXPathToString(this.xpath);
			}
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {
			ConversionBase64Decode decoder = new ConversionBase64Decode(this.base64String, this.zippedContent);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Decoding Base64 string");
			if(this.zippedContent) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "Base64 content will be decompressed");
			}
			byte[] content = decoder.decode();
			this.audit.addLog(AuditLogStatus.SUCCESS, "Decoding complete");
			if(this.contentType != null) {
				this.payload.setContentType(this.contentType);
			}
			return content;
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} 
	}
}
