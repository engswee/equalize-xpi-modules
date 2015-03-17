package com.equalize.xpi.af.modules.json;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;

public class JSONConverterSimpleFactory {
	// private constructor
	private JSONConverterSimpleFactory() {		
	}

	public static JSONConverterSimpleFactory newInstance() {
		return new JSONConverterSimpleFactory();		
	}

	public AbstractModuleConverter newConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg) throws ModuleException {
		AbstractModuleConverter converter = null;
		String conversionType = param.getMandatoryParameter("conversionType");
		param.checkParamValidValues("conversionType", "JSON2XML,XML2JSON");

		if(conversionType.equalsIgnoreCase("JSON2XML")) {
			converter = new JSON2XMLConverter(msg, param, audit, dyncfg);
		} else if(conversionType.equalsIgnoreCase("XML2JSON")) {
			converter = new XML2JSONConverter(msg, param, audit, dyncfg);
		}

		return converter;
	}
}
