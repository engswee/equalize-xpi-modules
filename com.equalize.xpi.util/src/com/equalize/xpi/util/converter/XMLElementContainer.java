package com.equalize.xpi.util.converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class XMLElementContainer {

	private final String elementName;
	private final LinkedHashMap<String, Integer> childFieldList;
	private final ArrayList<Field> childFields;
	
	public XMLElementContainer(String elementName) {
		this.childFieldList = new LinkedHashMap<String, Integer>();
		this.childFields = new ArrayList<Field>();
		this.elementName = elementName;
	}

	public String getElementName() {
		return this.elementName;
	}	
	
	public LinkedHashMap<String, Integer> getChildFieldList() {
		return this.childFieldList;
	}

	public ArrayList<Field> getChildFields() {
		return this.childFields;
	}
	
	public void addChildField(String fieldName, Object fieldContent) {
		// Add field	
		this.childFields.add(new Field(fieldName, fieldContent));
		// Update count of field in list
		int count;
		if (this.childFieldList.containsKey(fieldName)) {
			count = this.childFieldList.get(fieldName);
			count++;
		} else {
			count = 1;
		}
		this.childFieldList.put(fieldName, count);
	}
}
