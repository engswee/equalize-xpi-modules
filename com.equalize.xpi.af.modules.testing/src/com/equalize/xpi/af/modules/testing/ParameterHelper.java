package com.equalize.xpi.af.modules.testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Hashtable;

public class ParameterHelper {
	private final String fileName;
	
	public static ParameterHelper newInstance(String fileName) {
		return new ParameterHelper(fileName);
	}
	
	private ParameterHelper(String fileName) {
		this.fileName = fileName;
	}
	
	public Hashtable<String, String> getParams() {
		Hashtable<String, String> param = new Hashtable<String, String>();
		if(this.fileName == null) {
			return param;
		}
		try {
			InputStream inpStr = new FileInputStream(this.fileName);			

			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inpStr));		
			String lineContent;
			// Get the tab delimited values
			while ((lineContent = lnr.readLine()) != null) {
				if(!lineContent.startsWith("//")) {
					String[] fields = lineContent.split("\t");
					param.put(fields[0], fields[1]);
				}
			}
			lnr.close();
			
			return param;	
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Can't get Parameter file " + this.fileName);
	}
}
