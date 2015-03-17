package com.equalize.xpi.af.modules.json;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.ConversionDOMInput;
import com.equalize.xpi.util.converter.ConversionJSONOutput;
import com.equalize.xpi.util.converter.XMLElementContainer;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class XML2JSONConverter extends AbstractModuleConverter {
	private ConversionDOMInput domIn;
	private ConversionJSONOutput jsonOut;
	private XMLElementContainer rootXML;
	private int indentFactor;
	private boolean skipRootNode;

	public XML2JSONConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg) {
		super(msg, param, audit, dyncfg);
	}
	@Override
	public void retrieveModuleParameters() throws ModuleException {
		this.indentFactor = this.param.getIntParameter("indentFactor");
		this.skipRootNode = this.param.getBoolParameter("skipRootNode");
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			// Parse input XML contents
			this.domIn = new ConversionDOMInput(this.payload.getInputStream());
			this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input XML");
			this.rootXML = this.domIn.extractDOMContent();
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {
			// Create output converter and generate output JSON
			this.jsonOut = new ConversionJSONOutput();
			this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output JSON");
			String output = this.jsonOut.generateJSONText(this.rootXML, this.skipRootNode, this.indentFactor);

			this.audit.addLog(AuditLogStatus.SUCCESS, "Conversion complete");
			this.payload.setContentType("application/json; charset=utf-8");
			return output.getBytes();
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}
