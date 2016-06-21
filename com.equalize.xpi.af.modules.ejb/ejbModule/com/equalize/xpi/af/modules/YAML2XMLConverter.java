package com.equalize.xpi.af.modules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.ConversionDOMOutput;
import com.equalize.xpi.util.converter.ConversionYAMLInput;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;

public class YAML2XMLConverter extends AbstractModuleConverter {

	private ArrayList<Field> inputContents;
	
	public YAML2XMLConverter(Message msg, ParameterHelper param,
			AuditLogHelper audit, DynamicConfigurationHelper dyncfg,
			Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {
			ConversionDOMOutput domOut = new ConversionDOMOutput("MT_YAML2xml", "urn:equalize.com");
			domOut.setIndentFactor(2);
			domOut.setMangleInvalidNameChar(true);
			ByteArrayOutputStream baos = domOut.generateDOMOutput(this.inputContents);
			return baos.toByteArray();
		} catch (Exception e) {
			throw new ModuleException(e.getMessage(), e);
		}
		
	}

	@Override
	public void parseInput() throws ModuleException {
		
		try {
			String content = this.payload.getText();
			ConversionYAMLInput yamlIn = new ConversionYAMLInput(content);
			this.inputContents = yamlIn.extractYAMLContent();
		} catch (IOException e) {
			throw new ModuleException(e.getMessage(), e);
		}
		
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		// TODO Auto-generated method stub
		
	}

}
