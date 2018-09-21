package com.equalize.xpi.af.modules.deepfcc.parameters;

import java.util.ArrayList;

import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class RecordTypeParametersPlain2XMLFixed extends RecordTypeParametersPlain2XML {

	public RecordTypeParametersPlain2XMLFixed(String fieldSeparator, String[] fixedLengths) {
		super(fieldSeparator, fixedLengths);
	}

	public void setAdditionalParameters(String recordTypeName, String[] recordsetList, ParameterHelper param) throws ModuleException {
		super.setAdditionalParameters(recordTypeName, recordsetList, param);
		if(this.fieldNames.length != this.fixedLengths.length) {
			throw new ModuleException("No. of fields in 'fieldNames' and 'fieldFixedLengths' does not match for record type = '" + recordTypeName + "'");
		}
		setKeyFieldParameters(recordTypeName, param, false);
	}
	
	public String parseKeyFieldValue(String lineInput) {
		String currentLineKeyFieldValue = null;
		String valueAtKeyFieldPosition = dynamicSubstring(lineInput, this.keyFieldStartPosition, this.keyFieldLength);
		if (valueAtKeyFieldPosition.trim().equals(this.keyFieldValue)) {
			currentLineKeyFieldValue = this.keyFieldValue;
		}
		return currentLineKeyFieldValue;
	}

	public Field[] extractLineContents(String lineInput, boolean trim, int lineIndex)throws ModuleException {
		ArrayList<Field> fields = new ArrayList<Field>();
		int start = 0;
		for(int i = 0; i < this.fieldNames.length; i++) {
			int length = Integer.parseInt(this.fixedLengths[i]);
			String content = dynamicSubstring(lineInput, start, length);

			if(lineInput.length() < start) {
				if(this.missingLastFields.equalsIgnoreCase("error")) {
					throw new ModuleException("Line " + (lineIndex+1) + " has less fields than configured");
				} else if(this.missingLastFields.equalsIgnoreCase("add")) {
					fields.add(createNewField(this.fieldNames[i], content, trim));
				}
			} else {
				fields.add(createNewField(this.fieldNames[i], content, trim));
			}
			// Set start location for next field
			start += length;

			// After the last configured field, check if there are any more content in the input
			if(i == this.fieldNames.length - 1 && lineInput.length() > start && 
					this.additionalLastFields.equalsIgnoreCase("error")) {
				throw new ModuleException("Line " + (lineIndex+1) + " has more fields than configured");
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}

	private String dynamicSubstring(String input, int start, int length) {
		int startPos = start;
		int endPos = start + length - 1;
		String output = "";
	
		if ( startPos < 0 ) {
			// (1) Start position is before start of input, return empty string
		} else if ( startPos >= 0 && startPos < input.length() ) {
			if ( endPos < input.length() ) {
				// (2) Start & end positions are before end of input, return the partial substring
				output = input.substring( startPos, endPos + 1 );
			} else if ( endPos >= input.length() ) {
				// (3) Start position is before start of input but end position is after end of input, return from start till end of input
				output = input.substring( startPos, input.length() );
			}
		} else if ( startPos >= input.length() ) {
			// (4) Start position is after end of input, return empty string
		}
		return output;
	}
}
