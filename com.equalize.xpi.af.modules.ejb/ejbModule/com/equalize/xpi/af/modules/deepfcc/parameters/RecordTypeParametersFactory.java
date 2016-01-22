package com.equalize.xpi.af.modules.deepfcc.parameters;

import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.Separator;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class RecordTypeParametersFactory {

	// Private constructor
	private RecordTypeParametersFactory() {		
	}
	
	public static RecordTypeParametersFactory newInstance() {
		return new RecordTypeParametersFactory();
	}
	
	public Object newParameter(String recordTypeName, String[] recordsetList, String encoding, ParameterHelper param, String convType) throws ModuleException {
		// Set parameter values for the record type
		// 1 - Field Separator
		String defaultFieldSeparator = param.getParameter("defaultFieldSeparator");
		String fieldSeparatorName = recordTypeName + ".fieldSeparator";
		String fieldSeparator; 
		if (defaultFieldSeparator != null) {
			fieldSeparator = param.getParameter(fieldSeparatorName, defaultFieldSeparator, true);
		} else {
			fieldSeparator = param.getParameter(fieldSeparatorName);
		}
		if (fieldSeparator != null) {
			Separator sep = new Separator(fieldSeparator, encoding);
			fieldSeparator = sep.toString();
		}
		//this.fieldSeparator = fieldSeparator;

		// 2 - Fixed Lengths
		String fieldFixedLengthsName = recordTypeName + ".fieldFixedLengths";
		String tempFixedLengths = param.getParameter(fieldFixedLengthsName);
		String[] fixedLengths;
		if (tempFixedLengths == null) {
			fixedLengths = null;
		} else {
			String lengthsWithoutComma = tempFixedLengths.replaceAll(",", "");
			if(!checkNumeric(lengthsWithoutComma)) {
				throw new ModuleException("Maintain only integers separated by commas for '"+ fieldFixedLengthsName + "'");
			}
			fixedLengths = tempFixedLengths.split(",");
		}

		// Validate the parameter values
		if (fieldSeparator == null && fixedLengths == null) {
			throw new ModuleException("Either '" + fieldSeparatorName + "' or '" + fieldFixedLengthsName + "' must be populated");
		} else if (fieldSeparator != null && fixedLengths != null) {
			throw new ModuleException("Use only parameter '" + fieldSeparatorName + "' or '" + fieldFixedLengthsName + "', not both");
		}

		if(convType.equals("xml2plain")) {
			if (fieldSeparator != null) {
				return new RecordTypeParametersXML2PlainCSV(fieldSeparator, fixedLengths);
			} else {
				return new RecordTypeParametersXML2PlainFixed(fieldSeparator, fixedLengths);
			}
		} else if(convType.equals("plain2xml")) {
			if (fieldSeparator != null) {
				return new RecordTypeParametersPlain2XMLCSV(fieldSeparator, fixedLengths);
			} else {
				return new RecordTypeParametersPlain2XMLFixed(fieldSeparator, fixedLengths);
			}
		} else {
			throw new ModuleException("Conversion type " + convType + " not supported.");
		}
	}
	
	private boolean checkNumeric(String input) {
		boolean result = true;
		for (int i = 0; i < input.length(); i++) {
			if (!Character.isDigit(input.charAt(i))) {
				result = false;
				break;
			}
		}
		return result;
	}
}
