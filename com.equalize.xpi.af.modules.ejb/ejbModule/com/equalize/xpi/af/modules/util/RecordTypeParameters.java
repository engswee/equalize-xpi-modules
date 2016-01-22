package com.equalize.xpi.af.modules.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.equalize.xpi.util.converter.Field;
import com.equalize.xpi.util.converter.MyStringTokenizer;
import com.equalize.xpi.util.converter.Separator;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class RecordTypeParameters {
	public final String fieldSeparator;
	public final String[] fixedLengths;
	public String endSeparator;
	private final boolean csvMode;
	// XML to Plain
	public String fixedLengthTooShortHandling;
	// Plain to XML
	private String keyFieldName;
	private String keyFieldValue;
	private String[] fieldNames;
	private int keyFieldIndex;
	private int keyFieldStartPosition = 0;
	private int keyFieldLength = 0;
	private boolean enclosureConversion;
	private String enclBegin;
	private String enclEnd;
	private String enclBeginEsc;
	private String enclEndEsc;
	protected String missingLastFields;
	protected String additionalLastFields;	
	public String parentRecordType;

	public RecordTypeParameters(String recordTypeName, String[] recordsetList, String encoding, ParameterHelper param, String convType) throws ModuleException {
		// Set parameter values for the record type
		// 1 - Field Separator
		String defaultFieldSeparator = param.getParameter("defaultFieldSeparator");
		String fieldSeparatorName = recordTypeName + ".fieldSeparator";
		String tempFieldSeparator; 
		if (defaultFieldSeparator != null) {
			tempFieldSeparator = param.getParameter(fieldSeparatorName, defaultFieldSeparator, true);
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
		if (tempFixedLengths == null) {
			this.fixedLengths = null;
		} else {
			String lengthsWithoutComma = tempFixedLengths.replaceAll(",", "");
			if(!checkNumeric(lengthsWithoutComma)) {
				throw new ModuleException("Maintain only integers separated by commas for '"+ fieldFixedLengthsName + "'");
			}
			this.fixedLengths = tempFixedLengths.split(",");
		}

		// Validate the parameter values
		if (this.fieldSeparator == null && this.fixedLengths == null) {
			throw new ModuleException("Either '" + fieldSeparatorName + "' or '" + fieldFixedLengthsName + "' must be populated");
		} else if (this.fieldSeparator != null && this.fixedLengths != null) {
			throw new ModuleException("Use only parameter '" + fieldSeparatorName + "' or '" + fieldFixedLengthsName + "', not both");
		}

		if (this.fieldSeparator != null) {
			this.csvMode = true;
		} else {
			this.csvMode = false;
		}

		if(convType.equals("xml2plain")) {
			storeXML2PlainParameters(recordTypeName, param, encoding);
		} else if(convType.equals("plain2xml")) {
			storePlain2XMLParameters(recordTypeName, recordsetList, param);
		}
	}

	private void storeXML2PlainParameters(String recordTypeName, ParameterHelper param, String encoding) throws ModuleException {
		// End Separator
		String tempEndSeparator = param.getParameter(recordTypeName + ".endSeparator");
		if (tempEndSeparator == null) {
			tempEndSeparator = Separator.newLine;
		} else {
			Separator sep = new Separator(tempEndSeparator, encoding);
			tempEndSeparator = sep.toString();
		}
		this.endSeparator = tempEndSeparator;
		// Fixed Length too short handling
		if(!this.csvMode) {
			this.fixedLengthTooShortHandling = param.getParameter(recordTypeName + ".fixedLengthTooShortHandling", "Error", false);
			param.checkParamValidValues(recordTypeName + ".fixedLengthTooShortHandling", "Error,Cut,Ignore");
		}
	}

	private void storePlain2XMLParameters(String recordTypeName, String[] recordsetList, ParameterHelper param) throws ModuleException {

		String genericRecordType = param.getParameter("genericRecordType");
		if(genericRecordType == null || !genericRecordType.equals(recordTypeName)) {
			// Key field name and value
			this.keyFieldName = param.getMandatoryParameter("keyFieldName");
			this.keyFieldValue = param.getMandatoryParameter(recordTypeName + ".keyFieldValue");
		}
		// Parent record type
		this.parentRecordType = param.getMandatoryParameter(recordTypeName + ".parent");
		if(this.parentRecordType.equals(recordTypeName)) {
			throw new ModuleException("Value in '" + recordTypeName + ".parent" + "' cannot be the same as substructure name");
		} else if (!this.parentRecordType.equals("Root")) {
			boolean found = false;
			for(int i = 0; i < recordsetList.length; i++) {
				if(this.parentRecordType.equals(recordsetList[i])) {
					found = true;
					break;					
				}
			}
			if(!found) {
				throw new ModuleException("Value '" + this.parentRecordType + "' in '" + recordTypeName + ".parent" + "' not found in parameter 'recordsetStructure'");
			}
		}

		// Field names 
		String fieldNamesColumn = recordTypeName + ".fieldNames";
		String tempFieldNames = param.getMandatoryParameter(fieldNamesColumn);
		this.fieldNames = tempFieldNames.split(",");
		// Validate the field names
		validateFieldNames(this.fieldNames);
		if(!this.csvMode && this.fieldNames.length != this.fixedLengths.length) {
			throw new ModuleException("No. of fields in 'fieldNames' and 'fieldFixedLengths' does not match for record type = '" + recordTypeName + "'");
		}
		if(genericRecordType == null || !genericRecordType.equals(recordTypeName)) {
			// Index and position of key field in record type
			boolean found = false;
			for (int i = 0; i < this.fieldNames.length; i++) {
				if(this.fieldNames[i].equals(this.keyFieldName)) {
					this.keyFieldIndex = i;
					found = true;
					if(!this.csvMode) {
						this.keyFieldLength = Integer.parseInt(this.fixedLengths[i]);
					}
					break;
				}
				if(!this.csvMode) {
					this.keyFieldStartPosition += Integer.parseInt(this.fixedLengths[i]);
				}
			}
			if (!found) {
				throw new ModuleException("Key field '" + this.keyFieldName + "' not found in '" + fieldNamesColumn + "'");
			}
		}
		// Enclosure signs
		if(this.csvMode) {
			this.enclBegin = param.getParameter(recordTypeName + ".enclosureSignBegin", "", false);
			this.enclEnd = param.getParameter(recordTypeName + ".enclosureSignEnd", this.enclBegin, false);
			this.enclBeginEsc = param.getParameter(recordTypeName + ".enclosureSignBeginEscape", "", false);
			this.enclEndEsc = param.getParameter(recordTypeName + ".enclosureSignEndEscape", this.enclBeginEsc, false);
			this.enclosureConversion = param.getBoolParameter(recordTypeName + ".enclosureConversion", "Y", false);
		}
		// Structure deviations
		this.missingLastFields = param.getParameter(recordTypeName + ".missingLastFields", "ignore", false);
		this.additionalLastFields = param.getParameter(recordTypeName + ".additionalLastFields", "ignore", false);
	}

	public String parseKeyFieldValue (String lineInput) {
		String currentLineKeyFieldValue = null;
		if (this.csvMode) {
			String[] inputFieldContents = splitLineBySeparator(lineInput);
			if (this.keyFieldIndex <= inputFieldContents.length) {
				if (inputFieldContents[this.keyFieldIndex].equals(this.keyFieldValue)) {
					currentLineKeyFieldValue = this.keyFieldValue;
				}
			}
		} else {
			String valueAtKeyFieldPosition = dynamicSubstring(lineInput, this.keyFieldStartPosition, this.keyFieldLength);
			if (valueAtKeyFieldPosition.trim().equals(this.keyFieldValue)) {
				currentLineKeyFieldValue = this.keyFieldValue;
			}
		}	
		return currentLineKeyFieldValue;
	}

	public Field[] extractLineContents(String lineInput, boolean trim, int lineIndex) throws ModuleException {
		ArrayList<Field> fields = new ArrayList<Field>();
		// (A) Field separator
		if (this.csvMode) {
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
		// (B) Fixed length	
		} else {
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
		}	
		return fields.toArray(new Field[fields.size()]);
	}

	private Field createNewField(String fieldName, String fieldValue, boolean trim) {
		if(trim) {
			fieldValue = fieldValue.trim();
		}
		return new Field(fieldName, fieldValue);
	}

	private String dynamicSubstring(String input, int start, int length ) {
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
	
	private void validateFieldNames(String[] fieldNames) throws ModuleException {
		// No duplicates in field names
		HashMap<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < fieldNames.length; i++) {
			if(map.containsKey(fieldNames[i])) {
				throw new ModuleException("Duplicate field found in 'fieldNames': " + fieldNames[i]);
			} else {
				map.put(fieldNames[i], null);
			}
		}
	}
}
