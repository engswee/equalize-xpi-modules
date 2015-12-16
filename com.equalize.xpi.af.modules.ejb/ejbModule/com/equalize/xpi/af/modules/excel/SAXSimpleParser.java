package com.equalize.xpi.af.modules.excel;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXSimpleParser extends DefaultHandler {

	private int level;
	private String fieldName = null;
	private String fieldValue = "";
	private boolean addHeader;

	private ArrayList<ArrayList<String>> contents;
	private ArrayList<String> row;
	private ArrayList<String> header;

	public SAXSimpleParser (ArrayList<ArrayList<String>> contents, boolean addHeader) {
		this.contents = contents;
		this.addHeader = addHeader;
	}

	public void startDocument () throws SAXException {
		this.level = 0;
	}

	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts) throws SAXException {
		// At row level, create new arrays
		if(this.level == 1) {
			this.row = new ArrayList<String>();
			if(this.addHeader) {
				this.header  = new ArrayList<String>();
			}
		}
		// At field level, get the field name
		if(this.level == 2) {
			this.fieldName = rawName;
		}

		// Set level for next element
		this.level++;
	}

	public void endElement (String uri, String localName, String qName) throws SAXException {
		// At end of field level, add the field name and saved field value to current row
		if(this.fieldName != null) {
			if(this.addHeader) {
				this.header.add(this.fieldName);
			}
			this.row.add(this.fieldValue);
		}
		// Reset values
		this.fieldName = null;
		this.fieldValue = "";
		this.level--;

		// At end of row level, add contents of current row
		if(this.level == 1) {
			if(this.addHeader) {
				this.contents.add(this.header);
				this.addHeader = false;
			}
			this.contents.add(this.row);
		}
	}

	public void characters (char ch[], int start, int length) throws SAXException {
		// At field level, save value of field
		if(this.fieldName != null) {
			char[] ch2 = new char[length];
			System.arraycopy(ch, start, ch2, 0, length);
			// Concatenate into previous values, as for cases with special characters
			// the content might be split into multiple chunks
			this.fieldValue = this.fieldValue + new String(ch2);
		}
	}
}

