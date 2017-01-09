package com.equalize.xpi.af.modules.yaml;

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

	private ArrayList<Field> content;
	
	public YAML2XMLConverter(Message msg, ParameterHelper param,
			AuditLogHelper audit, DynamicConfigurationHelper dyncfg,
			Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		try {
			ConversionDOMOutput domOut = new ConversionDOMOutput("MT_YAMLContent", "urn:equalize.com");
			domOut.setEscapeInvalidNameStartChar(true);
			domOut.setMangleInvalidNameChar(true);
			domOut.setIndentFactor(2);
			return domOut.generateDOMOutput(this.content).toByteArray();
		} catch (Exception e) {
			throw new ModuleException(e.getMessage(), e);
		}
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			ConversionYAMLInput yamlIn = new ConversionYAMLInput(this.payload.getText());
			this.content = yamlIn.extractYAMLContent();
		} catch (IOException e) {
			throw new ModuleException(e.getMessage(), e);
		}		
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		
	}

}
