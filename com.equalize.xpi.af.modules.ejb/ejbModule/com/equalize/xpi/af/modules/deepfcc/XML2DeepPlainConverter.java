package com.equalize.xpi.af.modules.deepfcc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.af.modules.util.RecordTypeParameters;
import com.equalize.xpi.util.converter.ConversionDOMInput;
import com.equalize.xpi.util.converter.ConversionPlainOutput;
import com.equalize.xpi.util.converter.Converter;
import com.equalize.xpi.util.converter.Field;
import com.equalize.xpi.util.converter.XMLElementContainer;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class XML2DeepPlainConverter extends AbstractModuleConverter {
	private ConversionDOMInput domIn;
	private ConversionPlainOutput plainOut;
	private XMLElementContainer rootXML;
	private String encoding;
	private String recordsetStructure;
	private final HashMap<String, RecordTypeParameters> recordTypes;

	public XML2DeepPlainConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg, Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
		this.recordTypes = new HashMap<String, RecordTypeParameters>();
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		this.encoding = this.param.getParameter("encoding", "UTF-8", true);
		this.recordsetStructure = this.param.getMandatoryParameter("recordsetStructure");

		String[] recordsetList = this.recordsetStructure.split(",");
		for(String recordTypeName: recordsetList) {	
			if(!this.recordTypes.containsKey(recordTypeName)) {
				this.recordTypes.put(recordTypeName, new RecordTypeParameters(recordTypeName, recordsetList, this.encoding, this.param, "xml2plain"));
			}
		}
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			// Parse input XML contents
			this.domIn = new ConversionDOMInput(this.payload.getInputStream());
			this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input XML");
			this.rootXML = this.domIn.extractDOMContent();
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {		
			// Create output converter and generate output flat content
			this.plainOut = new ConversionPlainOutput();
			this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output plain text");

			String output = constructTextfromXML(this.rootXML, true);
			ByteArrayOutputStream baos = Converter.toBAOS(output, this.encoding);

			this.audit.addLog(AuditLogStatus.SUCCESS, "Conversion complete");
			this.payload.setContentType("text/plain;charset=" + this.encoding);
			return baos.toByteArray();
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	private String constructTextfromXML(XMLElementContainer element, boolean isRoot) throws IOException, ModuleException {
		StringBuilder sb = new StringBuilder();
		// First, construct output for current element's child fields
		if(!isRoot) {
			sb.append(generateRowTextForElement(element));
		}
		// Then recursively process child elements that are segments
		for (Field childField : element.getChildFields()) {
			Object fieldContent = childField.fieldContent;			
			if(fieldContent instanceof XMLElementContainer) {
				sb.append(constructTextfromXML((XMLElementContainer) fieldContent, false));
			}
		}
		return sb.toString();
	}

	private String generateRowTextForElement(XMLElementContainer element) throws ModuleException, IOException {

		ArrayList<Field> childFields = element.getChildFields();
		String segmentName = element.getElementName();		
		if(!this.recordTypes.containsKey(segmentName)) {
			throw new ModuleException("Record Type " + segmentName + " not listed in parameter 'recordsetStructure'");
		}

		RecordTypeParameters rtp = this.recordTypes.get(segmentName);
		if (rtp.fixedLengths != null) {
			checkFieldCountConsistency(segmentName, childFields, rtp.fixedLengths.length);
		}

		try {			
			return this.plainOut.generateLineText(childFields, rtp.fieldSeparator, rtp.fixedLengths, rtp.endSeparator, rtp.fixedLengthTooShortHandling);
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	private void checkFieldCountConsistency(String segmentName, ArrayList<Field> childFields, int noOfColumns) throws ModuleException {
		int leafFieldCount = 0;
		// Count the number of child leaf nodes
		for (Field childField : childFields) {
			Object fieldContent = childField.fieldContent;			
			if (fieldContent instanceof String) {
				leafFieldCount++;
			}			
		}
		if (leafFieldCount > noOfColumns ) {
			throw new ModuleException("More fields found in XML structure than specified in parameter '" + segmentName + ".fieldFixedLengths'");
		}
	}
}
