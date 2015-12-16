package com.equalize.xpi.af.modules.base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.ConversionDOMOutput;
import com.equalize.xpi.util.converter.Converter;
import com.equalize.xpi.util.converter.ConversionBase64Encode;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class Base64EncodeConverter extends AbstractModuleConverter {

	private String outputType;
	private String documentName;
	private String documentNamespace;
	private String base64FieldName;
	private boolean compress;
	private byte[] content;

	public Base64EncodeConverter(Message msg, ParameterHelper param,
			AuditLogHelper audit, DynamicConfigurationHelper dyncfg, Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		this.outputType = this.param.getMandatoryParameter("outputType");
		this.param.checkParamValidValues("outputType", "plain,xml");
		this.compress = this.param.getBoolParameter("compress");
		if(this.outputType.equalsIgnoreCase("xml")) {
			this.documentName = this.param.getConditionallyMandatoryParameter("documentName", "outputType", "xml");
			this.documentNamespace = this.param.getConditionallyMandatoryParameter("documentNamespace", "outputType", "xml");
			this.base64FieldName = this.param.getParameter("base64FieldName", "base64Content", false);
		}
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			InputStream inStream = this.payload.getInputStream();
			this.content = Converter.toBytes(inStream);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input content");			
		} catch (IOException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {	
			// Encode content into Base64 string
			ConversionBase64Encode encoder = new ConversionBase64Encode(this.content);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Encoding content into Base64 string");
			if(this.compress) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "Content will be compressed before encoding");
			}
			String base64String = encoder.encode(this.compress, "Base64.txt");
			this.audit.addLog(AuditLogStatus.SUCCESS, "Encoding complete");

			// Generate XML or plain output
			if(this.outputType.equalsIgnoreCase("xml")){
				ConversionDOMOutput domOut = new ConversionDOMOutput(this.documentName, this.documentNamespace);
				ArrayList<Field> xmlContent = new ArrayList<Field>();
				xmlContent.add(new Field(this.base64FieldName, base64String));				
				
				this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output XML");
				domOut.setIndentFactor(2);
				ByteArrayOutputStream baos = domOut.generateDOMOutput(xmlContent);
				this.payload.setContentType("application/xml; charset=UTF-8");
				return baos.toByteArray();
			} else {
				this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output plain text");
				this.payload.setContentType("text/plain; charset=UTF-8");
				return base64String.getBytes("UTF-8");	
			}
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}