package com.equalize.xpi.af.modules.testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.equalize.xpi.util.converter.Converter;

public class ResultHelper {
	private final String fileName;
	
	private ResultHelper(String fileName) {		
		this.fileName = fileName;
	}
	
	public static ResultHelper newInstance(String fileName) {
		return new ResultHelper(fileName);
	}
	
	public String getResultContent(){
		try {
			InputStream inpStr = new FileInputStream(this.fileName);
			return Converter.toString(inpStr);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		throw new RuntimeException("Can't get Result file " + this.fileName);
	}
}
