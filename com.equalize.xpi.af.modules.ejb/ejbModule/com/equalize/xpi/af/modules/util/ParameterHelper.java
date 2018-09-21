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

	public String getParameter(String paramName, String defaultValue, boolean outputLog) {
		String paramValue = this.mc.getContextData(paramName);
		if (paramValue == null && defaultValue != null) {
			paramValue = defaultValue;
			if(outputLog) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "Parameter '" + paramName + "' is not set. Using default value = '" + paramValue + "'");
			}
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
		return getParameter(paramName, null, false);
	}

	public int getIntParameter(String paramName, int defaultValue, boolean outputLog) throws ModuleException {
		String paramValue = getParameter(paramName, Integer.toString(defaultValue), outputLog);
		try {
			int result = Integer.parseInt(paramValue);
			if (result < 0) {
				throw new ModuleException("Negative integers not allowed for "+ paramName);
			}
			return result;
		} catch (NumberFormatException e) {
			throw new ModuleException("Only integers allowed for "+ paramName);
		}
	}

	public int getIntParameter(String paramName) throws ModuleException {
		return getIntParameter(paramName, 0, false);
	}

	public int getIntMandatoryParameter(String paramName) throws ModuleException {
		getMandatoryParameter(paramName);
		return getIntParameter(paramName);
	}

	public boolean getBoolParameter(String paramName, String defaultValue, boolean outputLog) throws ModuleException {
		String paramValue = getParameter(paramName, defaultValue, outputLog);
		if(paramValue.equalsIgnoreCase("Y")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getBoolParameter(String paramName) throws ModuleException {
		return getBoolParameter(paramName, "N", false);
	}

	public boolean getBoolMandatoryParameter(String paramName) throws ModuleException {
		getMandatoryParameter(paramName);
		return getBoolParameter(paramName);
	}

	public void checkParamValidValues(String paramName, String validValues) throws ModuleException {
		String paramValue = this.mc.getContextData(paramName);
		if(paramValue != null) {
			String[] valid = validValues.split(",");
			boolean found = false;
			for (int i = 0 ; i < valid.length ; i++) {
				if (valid[i].trim().equalsIgnoreCase(paramValue)) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new ModuleException("Value '" + paramValue + "' not valid for parameter " + paramName);
			}
		}
	}
	
	public ModuleContext getModuleContext() {
		return this.mc;
	}
}
