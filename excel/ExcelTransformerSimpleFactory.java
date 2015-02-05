package com.equalize.xpi.af.modules.excel;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;

public class ExcelTransformerSimpleFactory {

	private String conversionType;
	// private constructor
	private ExcelTransformerSimpleFactory() {		
	}

	public static ExcelTransformerSimpleFactory newInstance() {
		return new ExcelTransformerSimpleFactory();		
	}

	public ExcelTransformer newTransformer(ModuleContext mc, MessageKey key, AuditAccess audit) throws Exception {
		ExcelTransformer transformer;
		this.conversionType = mc.getContextData("conversionType");
		if(this.conversionType == null) {
			throw new Exception("Mandatory parameter conversionType is missing");
		} else if(this.conversionType.equalsIgnoreCase("SimpleExcel2XML")) {
			transformer = new Excel2XMLTransformer(mc, key, audit);
		} else if(this.conversionType.equalsIgnoreCase("SimpleXML2Excel")) {
			transformer = new XML2ExcelTransformer(mc, key, audit);
		} else {
			throw new Exception("Value " + this.conversionType + " not valid for parameter conversionType");
		}
		return transformer;
	}
}
