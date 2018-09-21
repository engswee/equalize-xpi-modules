package com.equalize.xpi.util.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.json.JSONObject;
import org.json.JSONArray;

public class ConversionJSONOutput {

	private boolean forceArray = false;
	private HashSet<String> arrayFields;
	
	public String generateJSONText(XMLElementContainer xmlElement, boolean skipRoot, int indentFactor) {
		JSONObject jo = new JSONObject();
		constructJSONContentfromXML(jo, xmlElement);
		if (skipRoot) {
			return getJSONText(jo, indentFactor);
		} else {
			JSONObject rootjo = new JSONObject();
			rootjo.put(xmlElement.getElementName(), jo);
			return getJSONText(rootjo, indentFactor);
		}
	}

	public String generateJSONText(XMLElementContainer element, boolean skipRoot) {		
		return generateJSONText(element, skipRoot, 0);
	}

	public ByteArrayOutputStream generateJSONOutputStream(XMLElementContainer element, boolean skipRoot, int indentFactor) throws IOException {
		String output = generateJSONText(element, skipRoot, indentFactor);
		return Converter.toBAOS(output);
	}

	public ByteArrayOutputStream generateJSONOutputStream(XMLElementContainer element, boolean skipRoot) throws IOException {
		return generateJSONOutputStream(element, skipRoot, 0);
	}
	
	public void setForceArray(boolean forceArray) {
		this.forceArray = forceArray;
	}

	public void setArrayFields(HashSet<String> arrayFields) {
		this.arrayFields = arrayFields;
	}

	private void constructJSONContentfromXML(JSONObject parent, XMLElementContainer element) {
		LinkedHashMap<String, JSONArray> map = new LinkedHashMap<String, JSONArray>();
		// Process all the child fields of the XML element
		for (Field childField : element.getChildFields()) {
			// Check if it is an array first
			int count = element.getChildFieldList().get(childField.fieldName);

			Object fieldContent = childField.fieldContent;			
			if(fieldContent instanceof XMLElementContainer) {
				// If it is a segment, create a JSON object for it first before adding into parent
				JSONObject childjo = new JSONObject();
				constructJSONContentfromXML(childjo, (XMLElementContainer) fieldContent);
				putObjectIntoJSONObject(count, map, parent, childField.fieldName, childjo);
			} else if (fieldContent instanceof String) {
				// If it is a string, directly add it to parent
				putObjectIntoJSONObject(count, map, parent, childField.fieldName, fieldContent);
			}
			// If a JSONArray is created for this field and hasn't been added to the parent,
			// then add the JSONArray to the parent
			if(map.containsKey(childField.fieldName) && !parent.has(childField.fieldName)) {
				parent.put(childField.fieldName, map.get(childField.fieldName));
			}
		}
	}
	
	private void putObjectIntoJSONObject(int fieldCount, LinkedHashMap<String, JSONArray> jsonArrMap, JSONObject parent, String fieldName, Object child) {
		// If it is an array, put it into the corresponding JSON array in the map		
		if (fieldCount > 1 || 
		    this.forceArray || 
			(this.arrayFields != null && this.arrayFields.contains(fieldName)) ) {
			JSONArray ja = getJSONArray(jsonArrMap, fieldName); 
			ja.put(child);
		} else {
			// Otherwise directly put it into the parent
			parent.put(fieldName, child);
		}
	}

	private String getJSONText(JSONObject jo, int indentFactor) {
		return jo.toString(indentFactor);
	}

	private JSONArray getJSONArray(LinkedHashMap<String, JSONArray> map, String arrayName) {
		// Get the current JSONArray for this key or create a new JSONArray
		if(map.containsKey(arrayName)) {
			return map.get(arrayName);
		} else {
			JSONArray ja = new JSONArray();
			map.put(arrayName, ja);
			return ja;
		}
	}
}
