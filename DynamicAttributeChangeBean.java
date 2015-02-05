package com.equalize.xpi.af.modules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;

public class DynamicAttributeChangeBean implements Module {

	@Override
	public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
		// Get message & helpers
		Message msg = (Message) inputModuleData.getPrincipalData();
		AuditLogHelper audit = new AuditLogHelper(msg);
		ParameterHelper param = new ParameterHelper(moduleContext, audit);
		DynamicConfigurationHelper dyncfg = new DynamicConfigurationHelper(msg);

		audit.addLog(AuditLogStatus.SUCCESS, this.getClass().getName() + ": Module Initialized");

		try {
			// Module parameters
			String mode = param.getMandatoryParameter("mode");
			String namespace = param.getParameter("namespace", "http://sap.com/xi/XI/System/File");
			String attribute = param.getParameter("attribute", "FileName");		
			String outNamespace = param.getParameter("outNamespace");
			if (outNamespace == null) {
				outNamespace = namespace;
			}
			String outAttribute = param.getParameter("outAttribute");
			if (outAttribute == null) {
				outAttribute = attribute;
			}			
			// Get attribute value from Dynamic Configuration
			String inAttrVal = dyncfg.get(namespace, attribute);
			if (inAttrVal == null) {
				throw new ModuleException("Dynamic Configuration for " + namespace + ":" + attribute + " is not set");
			}	
			audit.addLog(AuditLogStatus.SUCCESS, "Input: " + namespace + "/" + attribute + " = " + inAttrVal );
			String outAttrVal = inAttrVal;

			// -----------------------------------------------
			// (1) ADD
			// -----------------------------------------------
			if (mode.equalsIgnoreCase("add")) {
				String prefix = param.getParameter("prefix");
				String suffix = param.getParameter("suffix");
				if (prefix != null) {
					outAttrVal = prefix + outAttrVal;
				}
				if (suffix != null) {
					outAttrVal = outAttrVal + suffix;
				}	
			// -----------------------------------------------				
			// (2) CHANGE
			// -----------------------------------------------				
			} else if (mode.equalsIgnoreCase("change")) {
				String oldValue = param.getConditionallyMandatoryParameter("oldValue", "mode", mode);
				String newValue = param.getConditionallyMandatoryParameter("newValue", "mode", mode);
				String replaceAll = param.getParameter("replaceAll");
				if (replaceAll != null && replaceAll.equalsIgnoreCase("Y")) {
					outAttrVal = outAttrVal.replaceAll(oldValue, newValue);
				} else {
					outAttrVal = outAttrVal.replaceFirst(oldValue, newValue);
				}
			// -----------------------------------------------				
			// (3) DELETE
			// -----------------------------------------------	
			} else if (mode.equalsIgnoreCase("delete")) {			
				String prefix = param.getParameter("prefix");
				String suffix = param.getParameter("suffix");
				if (prefix != null) {
					outAttrVal = outAttrVal.replaceFirst(prefix, "");
				}
				if (suffix != null) {
					outAttrVal = outAttrVal.substring(0, outAttrVal.lastIndexOf(suffix));
				}
			// -----------------------------------------------				
			// (4) REGEX
			// -----------------------------------------------	
			} else if (mode.equalsIgnoreCase("regex")) {
				String regex = param.getConditionallyMandatoryParameter("regex", "mode", mode);
				String replacement = param.getConditionallyMandatoryParameter("replacement", "mode", mode);
				outAttrVal = outAttrVal.replaceAll(regex, replacement);
			} else if (mode.equalsIgnoreCase("none")) {
				// Skip, possible to just add timestamp
			}
			
			// Add timestamp before file extension
			String addTimestamp = param.getParameter("addTimestamp");				
			if (addTimestamp != null && addTimestamp.equalsIgnoreCase("Y")) {
				audit.addLog(AuditLogStatus.SUCCESS, "Adding timestamp");
				// Get extension (after the last period .)
				String extension = outAttrVal.substring(outAttrVal.lastIndexOf("."));				
				String timestampFormat = param.getParameter("timestampFormat","yyyyMMdd-HHmmss-SSS");
				try {					
					Date date = new Date();
					DateFormat dateFormat = new SimpleDateFormat(timestampFormat);//"ABC");
					String valBeforeExt = outAttrVal.substring(0, outAttrVal.lastIndexOf(extension));
					outAttrVal = valBeforeExt + dateFormat.format(date) + extension;
				} catch (IllegalArgumentException e) {
					audit.addLog(AuditLogStatus.ERROR, "Invalid pattern for timestamp: " + timestampFormat );
					audit.addLog(AuditLogStatus.ERROR, "Refer to java.text.SimpleDateFormat for valid formats");
					throw new ModuleException(e.getMessage(), e);
				}
			}

			// Modify the output atribute in Dynamic Configuration
			dyncfg.change(outNamespace, outAttribute, outAttrVal);	
			audit.addLog(AuditLogStatus.SUCCESS, "Output: " + outNamespace + "/" + outAttribute + " = " + outAttrVal );
			
			// No changes to the payload content, just the dynamic configuration
			inputModuleData.setPrincipalData(msg);
			return inputModuleData;

		} catch (InvalidParamException e) {
			audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}
