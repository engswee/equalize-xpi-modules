package com.equalize.xpi.af.modules.deepfcc.parameters;

import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class RecordTypeParametersXML2PlainFixed extends RecordTypeParametersXML2Plain {

	public RecordTypeParametersXML2PlainFixed(String fieldSeparator, String[] fixedLengths) {
		super(fieldSeparator, fixedLengths);
	}

	public void setAdditionalParameters(String recordTypeName, ParameterHelper param, String encoding) throws ModuleException {
		super.setAdditionalParameters(recordTypeName, param, encoding);
		// Fixed Length too short handling
		this.fixedLengthTooShortHandling = param.getParameter(recordTypeName + ".fixedLengthTooShortHandling", "Error", false);
		param.checkParamValidValues(recordTypeName + ".fixedLengthTooShortHandling", "Error,Cut,Ignore");
	}
}
