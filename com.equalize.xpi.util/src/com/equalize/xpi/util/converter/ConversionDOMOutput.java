package com.equalize.xpi.util.converter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ConversionDOMOutput {
	private final Document doc;
	private final Node rootNode;
	private int indentFactor = 0;
	private boolean escapeInvalidNameStartChar = false;
	private boolean mangleInvalidNameChar = false;

	public ConversionDOMOutput(String rootName, String namespace) throws ParserConfigurationException {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.doc = docBuilder.newDocument();
		if (!namespace.isEmpty()) {
			this.rootNode = this.doc.createElementNS(namespace,"ns:"+ rootName);
		} else {
			this.rootNode = this.doc.createElement(rootName);
		}
		this.doc.appendChild(this.rootNode);
	}

	public ConversionDOMOutput(String rootName) throws ParserConfigurationException {
		this(rootName, "");
	}

	public ConversionDOMOutput(Document document) {
		this.doc = document;
		this.rootNode = this.doc.getDocumentElement();
	}

	public void setIndentFactor(int indentFactor) {
		this.indentFactor = indentFactor;
	}

	public boolean isEscapeInvalidNameStartChar() {
		return this.escapeInvalidNameStartChar;
	}

	public void setEscapeInvalidNameStartChar(boolean escapeInvalidNameStartChar) {
		this.escapeInvalidNameStartChar = escapeInvalidNameStartChar;
	}

	public boolean isMangleInvalidNameChar() {
		return this.mangleInvalidNameChar;
	}

	public void setMangleInvalidNameChar(boolean mangleInvalidNameChar) {
		this.mangleInvalidNameChar = mangleInvalidNameChar;
	}

	public ByteArrayOutputStream generateDOMOutput(ArrayList<Field> fieldList) throws TransformerException {
		constructDOMContent(this.rootNode, fieldList);
		return convertDOMtoBAOS();
	}

	public void generateDOMOutput(ArrayList<Field> fieldList, OutputStream outStream) throws TransformerException{
		constructDOMContent(this.rootNode, fieldList);
		convertDOMtoOutputStream(outStream);
	}

	private void convertDOMtoOutputStream(OutputStream outStream) throws TransformerException {
		// Transform the DOM to OutputStream
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		if (this.indentFactor > 0) { 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			//transformer.setOutputProperty(OutputKeys.ENCODING, xmlEncoding);
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(this.indentFactor));
		}
		transformer.transform(new DOMSource(this.doc), new StreamResult(outStream)); 
	}

	private ByteArrayOutputStream convertDOMtoBAOS() throws TransformerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		convertDOMtoOutputStream(baos);
		return baos;
	}

	private void constructDOMContent(Node parentNode, ArrayList<Field> fieldList) throws TransformerException {
		// Go through each entry of the ArrayList
		for (int i = 0; i < fieldList.size(); i++) {
			Field field = fieldList.get(i);
			constructDOMContent(parentNode, field.fieldName, field.fieldContent);
		}
	}

	private void constructDOMContent(Node parentNode, String keyName, Object[] contents) throws TransformerException {
		// Go through each item of the array
		for (Object entry: contents) {
			constructDOMContent(parentNode, keyName, entry);
		}
	}

	@SuppressWarnings("unchecked")
	private void constructDOMContent(Node parentNode, String keyName, Object fieldContent) throws TransformerException {
		if(fieldContent instanceof Object[]) {
			constructDOMContent(parentNode, keyName, (Object[]) fieldContent);
		} else if (fieldContent instanceof ArrayList<?>){
			Node node = addElementToNode(parentNode, keyName);
			constructDOMContent(node, (ArrayList<Field>) fieldContent);
		} else if (fieldContent == null) {
			// NOP
		} else {
			addElementToNode(parentNode, keyName, fieldContent.toString());
		}
	}

	private Node addElementToNode(Node parentNode, String elementName) throws TransformerException {
		try {
			Node element = null;
			if(this.escapeInvalidNameStartChar || this.mangleInvalidNameChar) {
				element = this.doc.createElement(generateElementName(elementName));
			} else {
				element = this.doc.createElement(elementName);
			}
			parentNode.appendChild(element);
			return element;
		} catch (DOMException e) {
			throw new TransformerException("Invalid character in XML element name: " + elementName, e);
		}
	}

	private Node addElementToNode(Node parentNode, String elementName, String elementTextValue) throws TransformerException {
		Node element = addElementToNode(parentNode, elementName);
		if (elementTextValue != null) {
			element.appendChild(this.doc.createTextNode(elementTextValue));
		}		
		return element;
	}

	private String generateElementName(String elementName) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < elementName.length(); i++) {
			char c = elementName.charAt(i);
			// First char of XML element
			if(i == 0 && this.escapeInvalidNameStartChar) {
				if(Character.isDigit(c)) {
					sb.append("__").append(c);
				} else if(!XMLChar.isNameStart(c)) {
					String hex = String.format("%04x", (int) c);
					sb.append("__u" + hex);
				} else {
					sb.append(c);
				}
			} else if (this.mangleInvalidNameChar) {
				if(!XMLChar.isName(c)) {
					String hex = String.format("%04x", (int) c);
					sb.append("__u" + hex);
				} else {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
