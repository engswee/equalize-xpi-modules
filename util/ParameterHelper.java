package com.equalize.xpi.af.modules.util;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class ParameterHelper {

	private final ModuleContext mc;
	private final AuditLogHelper audit;
	
	public ParameterHelper (ModuleContext mc, AuditLogHelper audit) {
		this.mc = mc;
		this.audit = audit;
	}
	
	public String getMandatoryParameter(String paramName) throws ModuleException {
		String paramValue = this.mc.getContextData(paramName);
		if (paramValue == null) {
			throw new ModuleException("Mandatory parameter '" + paramName + "' is missing");
		}	
		return paramValue;
	}
	
	public String getParameter(String paramName, String defaultValue) {
		String paramValue = this.mc.getContextData(paramName);
		if (paramValue == null && !defaultValue.equals("")) {
			paramValue = defaultValue;
			this.audit.addLog(AuditLogStatus.SUCCESS, "Defaulting '" + paramName + "' = " + paramValue);				
		}	
		return paramValue;
	}

	public String getConditionallyMandatoryParameter(String paramName, String dependentParamName, String dependentParamValue) throws ModuleException {
		String paramValue = this.mc.getContextData(paramName);
		if (paramValue == null) {
			throw new ModuleException("Parameter '" + paramName + "' required when '" + dependentParamName + "' = " + dependentParamValue);
		}	
		return paramValue;
	}
	
	public String getParameter(String paramName) {
		return getParameter(paramName, "");
	}
	
}
