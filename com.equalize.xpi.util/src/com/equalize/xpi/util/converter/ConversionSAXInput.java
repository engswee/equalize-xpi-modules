package com.equalize.xpi.util.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConversionSAXInput {

	private final InputStream inStream;
	private final DefaultHandler handler;
	
	public ConversionSAXInput(InputStream inStream, DefaultHandler handler) {
		this.inStream = inStream;
		this.handler = handler;
	}
	
	public void parse() throws ParserConfigurationException, SAXException, IOException {		
		SAXParserFactory factory = SAXParserFactory.newInstance();		
		SAXParser parser = factory.newSAXParser();
		parser.parse(this.inStream, this.handler);
	}
	
	// TODO - Initial class for SAX Parser. Need to implement logic to recursively parse
	//        the content and generate an XMLElementContainer tree
}
