package com.equalize.xpi.util.converter;

import java.io.IOException;
import java.util.ArrayList;

public class ConversionPlainOutput {

	public String generateLineText(ArrayList<Field> childFields, String fieldSeparator, String[] fixedLengths, String endSeparator, String fixedLengthTooShortHandling) throws Exception{
		if(fixedLengths == null) {
			return generateDelimitedLine(childFields, fieldSeparator, endSeparator);
		} else {
			return generateFixedLengthLine(childFields, fixedLengths, endSeparator, fixedLengthTooShortHandling);
		}
	}

	private String generateDelimitedLine(ArrayList<Field> childFields, String fieldSeparator, String endSeparator) throws IOException {		
		StringBuilder sb = new StringBuilder();
		int leafFieldCount = 0;
		// Process all child elements that are fields
		for (Field childField : childFields) {
			Object fieldContent = childField.fieldContent;			
			if (fieldContent instanceof String) {
				if(leafFieldCount == 0) {
					sb.append(fieldContent);
				} else {
					sb.append(fieldSeparator).append(fieldContent);
				}
				leafFieldCount++;
			}			
		}
		if(leafFieldCount>0) {
			sb.append(endSeparator);
		}
		return sb.toString();
	}

	private String generateFixedLengthLine(ArrayList<Field> childFields, String[] fixedLengths, String endSeparator, String fixedLengthTooShortHandling) throws ConverterException {		
		StringBuilder sb = new StringBuilder();
		int leafFieldCount = 0;
		// Process all child elements that are fields
		for (Field childField : childFields) {
			Object fieldContent = childField.fieldContent;			
			if (fieldContent instanceof String) {
				int fieldLength = Integer.parseInt(fixedLengths[leafFieldCount]);
				leafFieldCount++;
				String fieldValue = (String) fieldContent;
				// Handle case if field value is longer than configured length
				if(fieldValue.length() > fieldLength) {
					if(fixedLengthTooShortHandling.equalsIgnoreCase("Cut")) {
						fieldValue = fieldValue.substring(0, fieldLength);
					} else if(fixedLengthTooShortHandling.equalsIgnoreCase("Error")) {
						throw new ConverterException("Field value '" + fieldValue + "' longer than allowed length " + fieldLength + " for field '" + childField.fieldName + "'");
					}					
				}	
				sb.append(padRight(fieldValue, fieldLength));
			}			
		}
		if(leafFieldCount > 0) {
			sb.append(endSeparator);
		}
		return sb.toString();
	}

	private String padRight(String input, int width) {
		return String.format("%1$-" + width + "s", input);
	}
}
