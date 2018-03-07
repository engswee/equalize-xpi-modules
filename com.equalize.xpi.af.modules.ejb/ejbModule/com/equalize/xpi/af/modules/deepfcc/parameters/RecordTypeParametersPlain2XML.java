package com.equalize.xpi.af.modules.deepfcc.parameters;

import java.util.HashSet;

import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;

public abstract class RecordTypeParametersPlain2XML {
	public final String fieldSeparator;
	public final String[] fixedLengths;
	public String endSeparator;
	// Plain to XML
	private String keyFieldName;
	protected String keyFieldValue;
	protected String[] fieldNames;
	protected int keyFieldIndex;
	protected int keyFieldStartPosition = 0;
	protected int keyFieldLength = 0;

	protected String missingLastFields;
	protected String additionalLastFields;	
	public String parentRecordType;

	public RecordTypeParametersPlain2XML(String fieldSeparator, String[] fixedLengths) {
		this.fieldSeparator = fieldSeparator;
		this.fixedLengths = fixedLengths;
	}

	public void setAdditionalParameters(String recordTypeName, String[] recordsetList, ParameterHelper param) throws ModuleException {
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
		validateFieldNames(recordTypeName, this.fieldNames);
		// Structure deviations
		this.missingLastFields = param.getParameter(recordTypeName + ".missingLastFields", "ignore", false);
		this.additionalLastFields = param.getParameter(recordTypeName + ".additionalLastFields", "ignore", false);
	}

	abstract public String parseKeyFieldValue(String lineInput);

	abstract public Field[] extractLineContents(String lineInput, boolean trim, int lineIndex) throws ModuleException;

	protected Field createNewField(String fieldName, String fieldValue, boolean trim) {
		if(trim) {
			fieldValue = fieldValue.trim();
		}
		return new Field(fieldName, fieldValue);
	}

	protected void setKeyFieldParameters(String recordTypeName, ParameterHelper param, boolean csvMode) throws ModuleException {
		String genericRecordType = param.getParameter("genericRecordType");
		if(genericRecordType == null || !genericRecordType.equals(recordTypeName)) {
			// Key field name and value
			this.keyFieldName = param.getMandatoryParameter("keyFieldName");
			this.keyFieldValue = param.getMandatoryParameter(recordTypeName + ".keyFieldValue");
			
			// Index and position of key field in record type
			boolean found = false;
			for (int i = 0; i < this.fieldNames.length; i++) {
				if(this.fieldNames[i].equals(this.keyFieldName)) {
					this.keyFieldIndex = i;
					found = true;
					if(!csvMode) {
						this.keyFieldLength = Integer.parseInt(this.fixedLengths[i]);
					}
					break;
				}
				if(!csvMode) {
					this.keyFieldStartPosition += Integer.parseInt(this.fixedLengths[i]);
				}
			}
			if (!found) {
				throw new ModuleException("Key field '" + this.keyFieldName + "' not found in '" + recordTypeName + ".fieldNames'");
			}
		}
	}

	private void validateFieldNames(String recordTypeName, String[] fieldNames) throws ModuleException {
		// No duplicates in field names
		HashSet<String> set = new HashSet<String>();
		for(int i = 0; i < fieldNames.length; i++) {
			if(set.contains(fieldNames[i])) {
				throw new ModuleException("Duplicate field found in '" + recordTypeName + ".fieldNames': " + fieldNames[i]);
			} else {
				set.add(fieldNames[i]);
			}
		}
	}
}
