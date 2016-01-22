package com.equalize.xpi.af.modules.deepfcc.parameters;

import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.Separator;
import com.sap.aii.af.lib.mp.module.ModuleException;

public abstract class RecordTypeParametersXML2Plain {
	public final String fieldSeparator;
	public final String[] fixedLengths;
	public String endSeparator;
	// XML to Plain
	public String fixedLengthTooShortHandling;

	public RecordTypeParametersXML2Plain(String fieldSeparator, String[] fixedLengths) {
		this.fieldSeparator = fieldSeparator;
		this.fixedLengths = fixedLengths;
	}

	public void setAdditionalParameters(String recordTypeName, ParameterHelper param, String encoding) throws ModuleException {
		// End Separator
		String tempEndSeparator = param.getParameter(recordTypeName + ".endSeparator");
		if (tempEndSeparator == null) {
			tempEndSeparator = Separator.newLine;
		} else {
			Separator sep = new Separator(tempEndSeparator, encoding);
			tempEndSeparator = sep.toString();
		}
		this.endSeparator = tempEndSeparator;
	}
}
