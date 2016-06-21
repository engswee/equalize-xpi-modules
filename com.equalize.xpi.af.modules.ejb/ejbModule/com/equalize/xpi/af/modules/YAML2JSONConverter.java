package com.equalize.xpi.af.modules;

import java.io.IOException;
import java.util.ArrayList;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.equalize.xpi.util.converter.ConversionJSONOutput;
import com.equalize.xpi.util.converter.ConversionYAMLInput;
import com.equalize.xpi.util.converter.Field;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;

public class YAML2JSONConverter extends AbstractModuleConverter{

	private ArrayList<Field> inputContents;
	
	public YAML2JSONConverter(Message msg, ParameterHelper param,
			AuditLogHelper audit, DynamicConfigurationHelper dyncfg,
			Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public byte[] generateOutput() throws ModuleException {
		ConversionJSONOutput jsonOut = new ConversionJSONOutput();
		String out = jsonOut.generateJSONText(this.inputContents, 2);
		return out.getBytes();
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			ConversionYAMLInput yamlIn = new ConversionYAMLInput(this.payload.getText());
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
