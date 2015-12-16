package com.equalize.xpi.util.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Converter {

	private static final int DEF_BUFFER_SIZE = 8192;	
	
	// Input = InputStream
	public static ByteArrayOutputStream toBAOS (InputStream inStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[DEF_BUFFER_SIZE];
		int read = 0;
		while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
			baos.write(buffer, 0, read);
		}
		baos.flush();		
		return baos;
	}
	
/*	public static String toString (InputStream inStream, boolean addNewLine) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));		
		String lineContent;
		StringBuilder sb = new StringBuilder();
		while ((lineContent = br.readLine()) != null) {
			sb.append(lineContent);
			if(addNewLine) {
				sb.append("\n");
			}
		}
		br.close();
		return sb.toString();
	}*/
	
	public static String toString (InputStream inStream, String encoding) throws IOException {
		return toString(toBytes(inStream), encoding);
	}
	
	public static String toString (InputStream inStream) throws IOException {
		return toString(toBytes(inStream));
	}
	
	public static byte[] toBytes (InputStream inStream) throws IOException {	
		return toBAOS(inStream).toByteArray();
	}
	
	// Input = Byte array	
	public static ByteArrayOutputStream toBAOS (byte[] bytes) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(bytes);
		baos.flush();
		return baos;
	}
	
	public static String toString (byte[] bytes, String encoding) throws UnsupportedEncodingException {
		return new String(bytes, encoding);
	}
	
	public static String toString (byte[] bytes) throws UnsupportedEncodingException {
		return toString(bytes, "UTF-8");
	}
	
	public static InputStream toInputStream (byte[] bytes) {
		return new ByteArrayInputStream(bytes);
	}
	
	// Input = String
	public static ByteArrayOutputStream toBAOS (String string, String encoding) throws IOException {
		return toBAOS(toBytes(string, encoding));
	}
	
	public static ByteArrayOutputStream toBAOS (String string) throws IOException {
		return toBAOS(string, "UTF-8");
	}

	public static InputStream toInputStream (String string, String encoding) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(toBytes(string, encoding));
	}
	
	public static InputStream toInputStream (String string) throws UnsupportedEncodingException {
		return toInputStream(string, "UTF-8");
	}
	
	public static byte[] toBytes (String string, String encoding) throws UnsupportedEncodingException {
		return string.getBytes(encoding);
	}
	
	public static byte[] toBytes (String string) throws UnsupportedEncodingException {
		return toBytes(string, "UTF-8");
	}

	public static String convertMessageIDToUUID(String messageID) {
		if(messageID.length()!= 32) {
			throw new IllegalArgumentException("Invalid message ID - length not 32");
		}
		String timeLow = messageID.substring(0, 8);  
		String timeMid = messageID.substring(8, 12);  
		String timeHighAndVersion = messageID.substring(12, 16);  
		String clockSeqAndReserved = messageID.substring(16, 18);  
		String clockSeqLow = messageID.substring(18, 20);  
		String node = messageID.substring(20, 32);  
		String msgUUID = timeLow + "-" + timeMid + "-" + timeHighAndVersion + "-" + clockSeqAndReserved + clockSeqLow + "-" + node;
		return msgUUID;
	}
	
/*	public static String toString (OutputStream outStream) {
		return null;
	}*/
	
/*	public static byte[] toBytes (OutputStream outStream) {
		// TODO
		return null;//outStream;
	}*/
}
