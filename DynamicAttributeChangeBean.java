package com.equalize.xpi.af.modules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;

import com.equalize.xpi.af.modules.util.AbstractModule;

public class DynamicAttributeChangeBean extends AbstractModule {

	@Override
	protected void processModule() throws ModuleException {		

		// Module parameters
		String mode = this.param.getMandatoryParameter("mode");
		this.param.checkParamValidValues("mode", "add,change,delete,regex,none");
		String namespace = this.param.getParameter("namespace", "http://sap.com/xi/XI/System/File");
		String attribute = this.param.getParameter("attribute", "FileName");		
		String outNamespace = this.param.getParameter("outNamespace");
		if (outNamespace == null) {
			outNamespace = namespace;
		}
		String outAttribute = this.param.getParameter("outAttribute");
		if (outAttribute == null) {
			outAttribute = attribute;
		}			
		// Get attribute value from Dynamic Configuration
		String inAttrVal = this.dyncfg.get(namespace, attribute);
		if (inAttrVal == null) {
			throw new ModuleException("Dynamic Configuration for " + namespace + ":" + attribute + " is not set");
		}	
		this.audit.addLog(AuditLogStatus.SUCCESS, "Input: " + namespace + "/" + attribute + " = " + inAttrVal );
		String outAttrVal = inAttrVal;

		// -----------------------------------------------
		// (1) ADD
		// -----------------------------------------------
		if (mode.equalsIgnoreCase("add")) {
			String prefix = this.param.getParameter("prefix");
			String suffix = this.param.getParameter("suffix");
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
			String oldValue = this.param.getConditionallyMandatoryParameter("oldValue", "mode", mode);
			String newValue = this.param.getConditionallyMandatoryParameter("newValue", "mode", mode);
			boolean replaceAll = this.param.getBoolParameter("replaceAll");
			if (replaceAll) {
				outAttrVal = outAttrVal.replaceAll(oldValue, newValue);
			} else {
				outAttrVal = outAttrVal.replaceFirst(oldValue, newValue);
			}
			// -----------------------------------------------				
			// (3) DELETE
			// -----------------------------------------------	
		} else if (mode.equalsIgnoreCase("delete")) {			
			String prefix = this.param.getParameter("prefix");
			String suffix = this.param.getParameter("suffix");
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
			String regex = this.param.getConditionallyMandatoryParameter("regex", "mode", mode);
			String replacement = this.param.getConditionallyMandatoryParameter("replacement", "mode", mode);
			outAttrVal = outAttrVal.replaceAll(regex, replacement);
		} else if (mode.equalsIgnoreCase("none")) {
			// Skip, possible to just add timestamp
		}

		// Add timestamp before file extension
		boolean addTimestamp = this.param.getBoolParameter("addTimestamp");				
		if (addTimestamp) {
			this.audit.addLog(AuditLogStatus.SUCCESS, "Adding timestamp");
			// Get extension (after the last period .)
			String extension = outAttrVal.substring(outAttrVal.lastIndexOf("."));				
			String timestampFormat = this.param.getParameter("timestampFormat","yyyyMMdd-HHmmss-SSS");
			try {					
				Date date = new Date();
				DateFormat dateFormat = new SimpleDateFormat(timestampFormat);//"ABC");
				String valBeforeExt = outAttrVal.substring(0, outAttrVal.lastIndexOf(extension));
				outAttrVal = valBeforeExt + dateFormat.format(date) + extension;
			} catch (IllegalArgumentException e) {
				this.audit.addLog(AuditLogStatus.ERROR, "Invalid pattern for timestamp: " + timestampFormat );
				this.audit.addLog(AuditLogStatus.ERROR, "Refer to java.text.SimpleDateFormat for valid formats");
				throw new ModuleException(e.getMessage(), e);
			}
		}
		// Modify the output atribute in Dynamic Configuration
		try {
			this.dyncfg.change(outNamespace, outAttribute, outAttrVal);	
			this.audit.addLog(AuditLogStatus.SUCCESS, "Output: " + outNamespace + "/" + outAttribute + " = " + outAttrVal );
		} catch (InvalidParamException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}
