package com.equalize.xpi.util.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConversionZipOutput {

	private final ByteArrayOutputStream baos;
	private final ZipOutputStream zos;
	
	public ConversionZipOutput() {
		this.baos = new ByteArrayOutputStream();
		this.zos = new ZipOutputStream(this.baos);
	}
	
	public void addEntry(String filename, byte[] content) throws IOException {
		this.zos.putNextEntry(new ZipEntry(filename));
		this.zos.write(content);
		this.zos.closeEntry();
	}
	
	public byte[] getBytes() throws IOException {
		this.zos.close();
		return this.baos.toByteArray();
	}
}
