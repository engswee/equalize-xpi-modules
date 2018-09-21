package com.equalize.xpi.util.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConversionJSONInput {
	private final JSONObject jsonObj;	

	public ConversionJSONInput(String input)  {
		this.jsonObj = new JSONObject(input.trim());
	}

	public ConversionJSONInput(String input, String topArrayName)  {
		String content = input.trim();
		if(content.startsWith("[")) {
			JSONArray topArray = new JSONArray(content);
			this.jsonObj = new JSONObject();
			this.jsonObj.put(topArrayName, topArray);
		} else {
			this.jsonObj = new JSONObject(content);
		}
	}

	public ConversionJSONInput(byte[] inputBytes) throws UnsupportedEncodingException {
		this(Converter.toString(inputBytes));
	}

	public ConversionJSONInput(byte[] inputBytes, String encoding) throws UnsupportedEncodingException {
		this(Converter.toString(inputBytes, encoding));
	}

	public ConversionJSONInput(InputStream inStream) throws IOException {
		this(Converter.toString(inStream));
	}

	public ConversionJSONInput(InputStream inStream, String encoding) throws IOException {
		this(Converter.toString(inStream, encoding));
	}

	public ArrayList<Field> extractJSONContent() {
		return parseJSON(this.jsonObj);
	}

	private ArrayList<Field> parseJSON(JSONObject jo) {
		ArrayList<Field> arr = new ArrayList<Field>();
		Iterator<String> keyIter = jo.keys();
		while(keyIter.hasNext()) {
			String keyName = keyIter.next();
			Object parsedObj = parseJSON(jo.get(keyName));
			arr.add(new Field(keyName, parsedObj));
		}
		return arr;
	}

	private Object[] parseJSON(JSONArray ja) {
		Object[] objects = new Object[ja.length()];
		for(int i = 0; i < ja.length(); i++) {
			Object parsedObj = parseJSON(ja.get(i));
			objects[i] = parsedObj;
		}
		return objects;
	}

	private Object parseJSON(Object obj) {
		if (obj instanceof JSONObject) {
			return parseJSON((JSONObject) obj);
		} else if (obj instanceof JSONArray) {
			return parseJSON((JSONArray) obj);
		} else if (obj == JSONObject.NULL) {
			return null;
		} else {
			return obj.toString();
		}
	}
}
