package com.equalize.xpi.af.modules.deepfcc;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;

public class DeepFCCConverterSimpleFactory {
	// private constructor
	private DeepFCCConverterSimpleFactory() {		
	}

	public static DeepFCCConverterSimpleFactory newInstance() {
		return new DeepFCCConverterSimpleFactory();		
	}

	public AbstractModuleConverter newConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg) throws ModuleException {
		AbstractModuleConverter converter = null;
		String conversionType = param.getMandatoryParameter("conversionType");
		param.checkParamValidValues("conversionType", "DeepXML2Plain,Plain2DeepXML");

		if(conversionType.equalsIgnoreCase("DeepXML2Plain")) {
			converter = new DeepXML2PlainConverter(msg, param, audit, dyncfg);
		} /*else if(conversionType.equalsIgnoreCase("Plain2DeepXML")) {
			converter = new Plain2DeepXMLConverter(msg, param, audit, dyncfg);
		}*/

		return converter;
	}
}
