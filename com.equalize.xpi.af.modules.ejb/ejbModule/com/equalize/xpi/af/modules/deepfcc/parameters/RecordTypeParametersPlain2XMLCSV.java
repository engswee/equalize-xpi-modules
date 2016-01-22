package com.equalize.xpi.af.modules.deepfcc.parameters;

import java.util.ArrayList;

import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.Field;
import com.equalize.xpi.util.converter.MyStringTokenizer;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class RecordTypeParametersPlain2XMLCSV extends RecordTypeParametersPlain2XML {
	private boolean enclosureConversion;
	private String enclBegin;
	private String enclEnd;
	private String enclBeginEsc;
	private String enclEndEsc;

	public RecordTypeParametersPlain2XMLCSV(String fieldSeparator, String[] fixedLengths) {
		super(fieldSeparator, fixedLengths);
	}

	public void setAdditionalParameters(String recordTypeName, String[] recordsetList, ParameterHelper param) throws ModuleException {
		super.setAdditionalParameters(recordTypeName, recordsetList, param);
		setKeyFieldParameters(recordTypeName, param, true);
		// Enclosure signs
		this.enclBegin = param.getParameter(recordTypeName + ".enclosureSignBegin", "", false);
		this.enclEnd = param.getParameter(recordTypeName + ".enclosureSignEnd", this.enclBegin, false);
		this.enclBeginEsc = param.getParameter(recordTypeName + ".enclosureSignBeginEscape", "", false);
		this.enclEndEsc = param.getParameter(recordTypeName + ".enclosureSignEndEscape", this.enclBeginEsc, false);
		this.enclosureConversion = param.getBoolParameter(recordTypeName + ".enclosureConversion", "Y", false);
	}

	public String parseKeyFieldValue(String lineInput) {
		String currentLineKeyFieldValue = null;
		String[] inputFieldContents = splitLineBySeparator(lineInput);
		if (this.keyFieldIndex <= inputFieldContents.length) {
			if (inputFieldContents[this.keyFieldIndex].equals(this.keyFieldValue)) {
				currentLineKeyFieldValue = this.keyFieldValue;
			}
		}
		return currentLineKeyFieldValue;
	}

	public Field[] extractLineContents(String lineInput, boolean trim, int lineIndex) throws ModuleException {
		ArrayList<Field> fields = new ArrayList<Field>();

		String[] inputFieldContents = splitLineBySeparator(lineInput);
		int outputSize = inputFieldContents.length; // Use length of input line for default 'ignore' or anything else
		// Content has less fields than specified in configuration
		if(inputFieldContents.length < this.fieldNames.length) {				
			if(this.missingLastFields.equalsIgnoreCase("add")) {
				outputSize = this.fieldNames.length;
			} else if(this.missingLastFields.equalsIgnoreCase("error")) {
				throw new ModuleException("Line " + (lineIndex+1) + " has less fields than configured");
			}
			// Content has more fields than specified in configuration	
		} else if (inputFieldContents.length > this.fieldNames.length) {
			outputSize = this.fieldNames.length; // Default to length of configuration fields
			if(this.additionalLastFields.equalsIgnoreCase("error")) {
				throw new ModuleException("Line " + (lineIndex+1) + " has more fields than configured");
			}
		}
		for (int i = 0; i < outputSize; i++ ) {
			String content = "";
			if(i < inputFieldContents.length) {
				content = (inputFieldContents[i] == null) ? "" : inputFieldContents[i];
			}
			fields.add(createNewField(this.fieldNames[i], content, trim));
		}
		return fields.toArray(new Field[fields.size()]);
	}

	private String[] splitLineBySeparator(String input) {
		// Split input with enclosure signs and escapes
		ArrayList<String> contents = new ArrayList<String>();
		MyStringTokenizer tokenizer = new MyStringTokenizer(input, this.fieldSeparator, this.enclBegin, this.enclEnd, this.enclBeginEsc, this.enclEndEsc, true);
		for(int i = 0; i < tokenizer.countTokens(); i++) {	
			String fieldContent = (String)tokenizer.nextElement();
			// If the token field content is not a separator, then store it in the output array
			if(!fieldContent.equalsIgnoreCase(this.fieldSeparator)) {
				if(this.enclosureConversion) {
					contents.add(tokenizer.convertEncls(fieldContent));
				} else {
					contents.add(fieldContent);
				}
			}
		}
		return contents.toArray(new String[contents.size()]);
	}
}
