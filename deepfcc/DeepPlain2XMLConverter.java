package com.equalize.xpi.af.modules.deepfcc;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.af.modules.util.RecordTypeParameters;
import com.equalize.xpi.util.converter.ConversionDOMOutput;
import com.equalize.xpi.util.converter.ConversionPlainInput;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class DeepPlain2XMLConverter extends AbstractModuleConverter {
	private ConversionPlainInput plainIn;
	private ConversionDOMOutput domOut;
	private String documentName;
	private String documentNamespace;
	private boolean indentXML;
	private String recordsetStructure;
	private final HashMap<String, RecordTypeParameters> recordTypes;
	private String encoding;
	private ArrayList<Field> nestedContents;
	private boolean debug;
	private int rowOffset;
	private boolean trimContents;
	
	public DeepPlain2XMLConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg) {
		super(msg, param, audit, dyncfg);
		this.recordTypes = new HashMap<String, RecordTypeParameters>();
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		this.encoding = this.param.getParameter("encoding", "UTF-8", true);
		this.documentName = this.param.getMandatoryParameter("documentName");
		this.documentNamespace = this.param.getMandatoryParameter("documentNamespace");
		this.indentXML = this.param.getBoolParameter("indentXML");
		this.recordsetStructure = this.param.getMandatoryParameter("recordsetStructure");
		this.debug = this.param.getBoolParameter("debug");
		this.rowOffset = this.param.getIntParameter("rowOffset");
		this.trimContents = this.param.getBoolParameter("trimContents", "Y", false);

		// Get the parameters for each substructure type
		String[] recordsetList = this.recordsetStructure.split(",");
		for(String recordTypeName: recordsetList) {	
			if(recordTypeName.equals("Root")) {
				throw new ModuleException("'Root' is a reserved name and not allowed in parameter 'recordsetStructure'");
			}
			if(!this.recordTypes.containsKey(recordTypeName)) {
				this.recordTypes.put(recordTypeName, new RecordTypeParameters(recordTypeName, recordsetList, this.encoding, this.param, "plain2xml"));
			}
		}
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			// Parse input plain text contents
			InputStream inStream = this.payload.getInputStream();
			this.plainIn = new ConversionPlainInput(inStream);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input plain text");
			this.nestedContents = generateNestedContents();
		} catch (IOException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {
			// Create output converter and generate output DOM
			this.domOut = new ConversionDOMOutput(this.documentName, this.documentNamespace);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output XML");	

			// Generate OutputStream from DOM
			if(this.indentXML) {
				this.domOut.setIndentOutput();
				this.audit.addLog(AuditLogStatus.SUCCESS, "Output XML will be indented");
			}
			ByteArrayOutputStream baos = this.domOut.generateDOMOutput(this.nestedContents);

			this.audit.addLog(AuditLogStatus.SUCCESS, "Conversion complete");
			return baos.toByteArray();
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	private ArrayList<Field> generateNestedContents() throws ModuleException {
		ArrayList<Field> nestedContents = new ArrayList<Field>();		
		// Stack is used to track the depth of the traversal of the hierarchy
		ArrayList<Field> depthStack = new ArrayList<Field>(this.recordTypes.size());
		depthStack.add(new Field("Root:0", nestedContents));
		
		if(this.rowOffset > 0) {
			this.audit.addLog(AuditLogStatus.SUCCESS, "Processing starting from offset: " + this.rowOffset);
		}
		
		// Get the raw line contents and process them line by line
		ArrayList<String> rawLineContents = this.plainIn.getLineContents();			
		for (int i = this.rowOffset; i < rawLineContents.size(); i++) {			
			String currentLine = rawLineContents.get(i);
			// Determine record type for line
			String lineRecordType = determineRecordType(currentLine, i);
			// Extract the content of line into node containing field-value pairs
			ArrayList<Field> lineNode = extractLineToFieldList(lineRecordType, currentLine);
			// Get the parent node for current line from stack
			ArrayList<Field> parentNode = getParentNode(depthStack, lineRecordType, i+1, lineNode);
			// Add the line node contents to the parent node
			parentNode.add(new Field(lineRecordType, lineNode));
		}
		return nestedContents;
	}

	private String determineRecordType(String inputLine, int lineIndex) throws ModuleException {
		// Loop through all record sets and parse to figure out key value
		for (String keyName: this.recordTypes.keySet()){
			RecordTypeParameters recordType = this.recordTypes.get(keyName);
			String keyValue = recordType.parseKeyFieldValue(inputLine);
			if (keyValue != null) {
				return keyName;
			}
		}
		throw new ModuleException("Unable to determine record type for line " + (lineIndex+1) );
	}

	private ArrayList<Field> extractLineToFieldList(String lineRecordType, String lineInput) {
		ArrayList<Field> fieldList = new ArrayList<Field>();		
		// Extract the fields of the current line based on the line's record type
		Field[] currentLineFields = this.recordTypes.get(lineRecordType).extractLineContents(lineInput, this.trimContents);				
		for (Field field : currentLineFields) {
			fieldList.add(new Field(field.fieldName, field.fieldContent));
		}
		return fieldList;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Field> getParentNode(ArrayList<Field> stack, String lineRecordType, int lineNo, ArrayList<Field> lineNode) throws ModuleException {
		boolean found = false;
		ArrayList<Field> parentNode = null;
		// Go through the stack in reverse order (from bottom) to determine which is the parent node
		// Entries in the stack are repeatedly removed from the bottom if it does not match the
		// current line's parent. This way we do a reverse traversal of the stack back to root
		// to find the parent node
		while(!found && stack.size()!= 0) {
			Field currentStackLevel = stack.get(stack.size()-1); // Always get the last item
			String parentRecordType = this.recordTypes.get(lineRecordType).parentRecordType;
			String[] stackKey = currentStackLevel.fieldName.split(":");
			// If the stack key matches the line's parent type, then get the parent node from the stack
			if (parentRecordType.equals(stackKey[0])) {
				parentNode =  (ArrayList<Field>) currentStackLevel.fieldContent;
				// Add the current line to the bottom of the stack
				stack.add(new Field(lineRecordType+":" + lineNo, lineNode));
				found = true;
				if(debug) {
					this.audit.addLog(AuditLogStatus.SUCCESS, "Line " + lineNo + ": Record Type = " + lineRecordType + ", Parent = " + parentRecordType + " (Line " + stackKey[1] + ")");
				}
			} else {
				stack.remove(stack.size()-1);
			}
		}
		if (parentNode == null) {
			throw new ModuleException("Cannot find parent for line " + lineNo + ": Record Type = " + lineRecordType);
		}
		return parentNode;
	}
}
