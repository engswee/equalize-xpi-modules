package com.equalize.xpi.af.modules.excel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public abstract class ExcelTransformer {
	protected AuditAccess audit;
	protected ModuleContext moduleParam;
	protected MessageKey key;

	public ExcelTransformer (ModuleContext mc, MessageKey key, AuditAccess audit) {
		this.moduleParam = mc;
		this.key = key;
		this.audit = audit;						
	}

	public abstract void retrieveModuleParameters() throws Exception;

	public abstract void parseInput(InputStream inStream) throws Exception;

	public abstract ByteArrayOutputStream generateOutput() throws Exception;

	protected void addLog (AuditLogStatus status, String message) {
		if (this.audit != null) {
			this.audit.addAuditLogEntry(this.key, status, message);	
		} else {
			System.out.println( "Audit Log: " + message);
		}
	}

	protected String getParaWithDefault(String key, String deflt) {
		String value = this.moduleParam.getContextData(key);
		if (value == null) {
			value = deflt;
		}
		return value;
	}

	protected String getParaWithErrorDescription(String key) throws Exception {
		String value = this.moduleParam.getContextData(key);
		if (value == null) {
			throw new Exception("Mandatory parameter " + key + " is missing");
		}
		return value;
	}
	
	protected int checkIntegerInput(String input, String fieldName) throws Exception {
		try {
			int result = Integer.parseInt(input);
			if (result < 0 ) {
				throw new Exception("Negative integers not allowed for "+ fieldName);
			}
			return result;
		} catch (NumberFormatException e) {
			throw new Exception("Only integers allowed for "+ fieldName);
		}
	}
}
