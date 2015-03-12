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
		param.checkParamValidValues("conversionType", "XML2DeepPlain,DeepPlain2XML");

		if(conversionType.equalsIgnoreCase("XML2DeepPlain")) {
			converter = new XML2DeepPlainConverter(msg, param, audit, dyncfg);
		} else if(conversionType.equalsIgnoreCase("DeepPlain2XML")) {
			converter = new DeepPlain2XMLConverter(msg, param, audit, dyncfg);
		}

		return converter;
	}
}
