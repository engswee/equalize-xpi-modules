package com.equalize.xpi.util.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.yamlbeans.YamlReader;

public class ConversionYAMLInput {
	private final HashMap<String, Object> rootMap;

	@SuppressWarnings("unchecked")
	public ConversionYAMLInput(String input) throws IOException {
		YamlReader reader = new YamlReader(input);
		this.rootMap = reader.read(HashMap.class);
	}

	public ArrayList<Field> extractYAMLContent() {
		return parseYAML(this.rootMap);
	}

	private ArrayList<Field> parseYAML(HashMap<String, Object> hm) {
		ArrayList<Field> arr = new ArrayList<Field>();
		for(String keyName: hm.keySet()) {
			Object parsedObj = parseYAML(hm.get(keyName));
			arr.add(new Field(keyName, parsedObj));
		}
		return arr;
	}

	private Object[] parseYAML(ArrayList<Object> list) {
		Object[] objects = new Object[list.size()];
		for(int i = 0; i < list.size(); i++) {
			Object parsedObj = parseYAML(list.get(i));
			objects[i] = parsedObj;
		}
		return objects;
	}

	@SuppressWarnings("unchecked")
	private Object parseYAML(Object obj) {
		if (obj instanceof HashMap<?, ?>) {
			return parseYAML((HashMap<String, Object>) obj);
		} else if (obj instanceof ArrayList<?>) {
			return parseYAML((ArrayList<Object>) obj);
		} else {
			return obj.toString();
		}
	}
}
