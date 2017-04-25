package com.equalize.xpi.af.modules.json;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.ConversionDOMOutput;
import com.equalize.xpi.util.converter.ConversionJSONInput;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class JSON2XMLConverter extends AbstractModuleConverter {
	private ConversionJSONInput jsonIn;
	private ConversionDOMOutput domOut;
	private String documentName;
	private String documentNamespace;
	private int indentFactor;
	private boolean escapeInvalidNameStartChar;
	private boolean mangleInvalidNameChar;
	private boolean allowArrayAtTop;
	private String topArrayName;
	private ArrayList<Field> inputContents;

	public JSON2XMLConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg, Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		this.documentName = this.param.getMandatoryParameter("documentName");  //eg MT_myMessageType
		this.documentNamespace = this.param.getMandatoryParameter("documentNamespace");
		this.indentFactor = this.param.getIntParameter("indentFactor");		
		this.escapeInvalidNameStartChar = this.param.getBoolParameter("escapeInvalidNameStartChar", "N", false);
		this.mangleInvalidNameChar = this.param.getBoolParameter("mangleInvalidNameChar", "N", false);
		this.allowArrayAtTop = this.param.getBoolParameter("allowArrayAtTop", "N", false);
		if(this.allowArrayAtTop) {
			this.topArrayName = this.param.getConditionallyMandatoryParameter("topArrayName", "allowArrayAtTop", "Y");
		}
	}

	@Override
	public void parseInput() throws ModuleException {
		// Parse input JSON contents
		String content = this.payload.getText();
		if(this.allowArrayAtTop) {
			this.jsonIn = new ConversionJSONInput(content, this.topArrayName);
		} else {
			this.jsonIn = new ConversionJSONInput(content);
		}
		this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input JSON");
		this.inputContents = this.jsonIn.extractJSONContent();
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {
			// Create output converter and generate output DOM
			this.domOut = new ConversionDOMOutput(this.documentName, this.documentNamespace);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output XML");	

			// Generate OutputStream from DOM
			if(this.indentFactor > 0) {
				this.domOut.setIndentFactor(this.indentFactor);
				this.audit.addLog(AuditLogStatus.SUCCESS, "Output XML will be indented");
			}
			
			this.domOut.setEscapeInvalidNameStartChar(this.escapeInvalidNameStartChar);
			this.domOut.setMangleInvalidNameChar(this.mangleInvalidNameChar);

			ByteArrayOutputStream baos = this.domOut.generateDOMOutput(this.inputContents);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Conversion complete");
			return baos.toByteArray();
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}
