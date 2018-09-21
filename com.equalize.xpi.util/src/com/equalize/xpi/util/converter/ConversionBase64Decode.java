package com.equalize.xpi.util.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

public class ConversionBase64Decode {
	private final String base64String;
	private final boolean zippedContent;

	public ConversionBase64Decode(String base64String) {
		this(base64String, false);
	}

	public ConversionBase64Decode(String base64String, boolean zippedContent) {
		this.base64String = base64String;
		this.zippedContent = zippedContent;
	}

	public byte[] decode() throws IOException {
		byte[] decoded = DatatypeConverter.parseBase64Binary(this.base64String);

		if(!this.zippedContent) {
			return decoded;
		} else {
			// Unzip the contents, assumption is only 1 zip entry in the zip content
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(decoded));
			ByteArrayOutputStream baos = null;
			ZipEntry ze = zis.getNextEntry();
			// Check if there is a zip entry
			if (ze == null) {
				throw new NullPointerException("Unable to decompress as content is not zipped");
			}				
			baos = Converter.toBAOS(zis);
			zis.closeEntry();
			zis.close();
			return baos.toByteArray();
		}
	}
}
