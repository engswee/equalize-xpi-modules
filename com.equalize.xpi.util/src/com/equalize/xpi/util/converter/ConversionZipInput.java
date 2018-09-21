package com.equalize.xpi.util.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConversionZipInput {
	private final ZipInputStream zis;
	
	public ConversionZipInput(InputStream inStream) throws IOException {
		byte[] content = Converter.toBytes(inStream);
		this.zis = new ZipInputStream(new ByteArrayInputStream(content));
	}
	
	public ConversionZipInput(byte[] content) throws IOException {
		this.zis = new ZipInputStream(new ByteArrayInputStream(content));
	}
	
	public LinkedHashMap<String, byte[]> getEntriesContent() throws IOException {
		ZipEntry ze = null;
		LinkedHashMap<String, byte[]> map = new LinkedHashMap<String, byte[]>();
		
		// Loop through all entries in the zip file
		while((ze = this.zis.getNextEntry()) != null) {
			byte[] zipContent = Converter.toBytes(this.zis);
			map.put(ze.getName(), zipContent);
			this.zis.closeEntry();
		}
		this.zis.close();		
		return map;
	}
}
