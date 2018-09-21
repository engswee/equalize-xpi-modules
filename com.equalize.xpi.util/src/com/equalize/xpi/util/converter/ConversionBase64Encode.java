package com.equalize.xpi.util.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.DatatypeConverter;

public class ConversionBase64Encode {
	private final byte[] content;

	public ConversionBase64Encode(byte[] content) {
		this.content = content;
	}

	public String encode(boolean compress, String filename) throws IOException {
		byte[] bytes;
		if(compress) {
			// Zip the content
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream	zos = new ZipOutputStream(baos);
			zos.putNextEntry(new ZipEntry(filename));
			zos.write(this.content);
			zos.closeEntry();
			zos.close();
			bytes = baos.toByteArray();
		} else {
			bytes = this.content;
		}
		return DatatypeConverter.printBase64Binary(bytes);
	}
	
	public String encode() throws IOException {
		return encode(false, "");
	}
}
