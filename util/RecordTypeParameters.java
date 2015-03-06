package com.equalize.xpi.af.modules.util;

import com.equalize.xpi.util.converter.Separator;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class RecordTypeParameters {
	public final String fieldSeparator;
	public final String[] fixedLengths;
	public final String endSeparator;
	public final String fixedLengthTooShortHandling;

	public RecordTypeParameters(String recordTypeName, String encoding, ParameterHelper param) throws ModuleException {//String fieldSeparator, String fieldFixedLengths, String endSeparator, String fixedLengthTooShortHandling) {
		// Set parameter values for the record type
		// 1 - Field Separator
		String defaultFieldSeparator = param.getParameter("defaultFieldSeparator");
		String fieldSeparatorName = recordTypeName + ".fieldSeparator";
		String tempFieldSeparator; 
		if (defaultFieldSeparator != null) {
			tempFieldSeparator = param.getParameter(fieldSeparatorName, defaultFieldSeparator);
		} else {
			tempFieldSeparator = param.getParameter(fieldSeparatorName);
		}
		if (tempFieldSeparator != null) {
			Separator sep = new Separator(tempFieldSeparator, encoding);
			tempFieldSeparator = sep.toString();
		}
		this.fieldSeparator = tempFieldSeparator;

		// 2 - Fixed Lengths
		String fieldFixedLengthsName = recordTypeName + ".fieldFixedLengths";
		String tempFixedLengths = param.getParameter(fieldFixedLengthsName);
		if(tempFixedLengths == null) {
			this.fixedLengths = null;
		} else {
			String lengthsWithoutComma = tempFixedLengths.replaceAll(",", "");
			try {
				Integer.parseInt(lengthsWithoutComma);
			} catch (NumberFormatException e) {
				throw new ModuleException("Maintain only integers separated by commas for '"+ fieldFixedLengthsName + "'");
			}
			this.fixedLengths = tempFixedLengths.split(",");
		}
		
		// 3 - End Separator
		String endSeparatorName = recordTypeName + ".endSeparator";
		String tempEndSeparator = param.getParameter(endSeparatorName);
		if(tempEndSeparator == null) {
			tempEndSeparator = Separator.newLine;
		} else {
			Separator sep = new Separator(tempEndSeparator, encoding);
			tempEndSeparator = sep.toString();
		}
		this.endSeparator = tempEndSeparator;

		// 4 - Fixed Length too short handling
		String fixedLengthTooShortHandlingName = recordTypeName + ".fixedLengthTooShortHandling";
		String tempFixedLengthTooShortHandling = param.getParameter(fixedLengthTooShortHandlingName);
		if (tempFixedLengthTooShortHandling != null) {
			param.checkParamValidValues(fixedLengthTooShortHandlingName, "Error,Cut,Ignore");
			this.fixedLengthTooShortHandling = tempFixedLengthTooShortHandling;
		} else {
			this.fixedLengthTooShortHandling = "Error";
		}

		// Validate the parameter values
		if(this.fieldSeparator == null && this.fixedLengths == null) {
			throw new ModuleException("Either '" + fieldSeparatorName + "' or '" + fieldFixedLengthsName + "' must be populated");
		}
		if(this.fieldSeparator != null && this.fixedLengths != null) {
			throw new ModuleException("Use only parameter '" + fieldSeparatorName + "' or '" + fieldFixedLengthsName + "', not both");
		}
	}
}
