package com.equalize.xpi.util.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class ConversionGZIPInput {
	private final GZIPInputStream gzis;
	
	public ConversionGZIPInput(InputStream inStream) throws IOException {
		byte[] content = Converter.toBytes(inStream);
		this.gzis = new GZIPInputStream(new ByteArrayInputStream(content));
	}
	
	public ConversionGZIPInput(byte[] content) throws IOException {
		this.gzis = new GZIPInputStream(new ByteArrayInputStream(content));
	}
	
	public byte[] getContent() throws IOException {
		return Converter.toBytes(this.gzis);
	}
}
