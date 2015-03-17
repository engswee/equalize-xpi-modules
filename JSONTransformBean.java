package com.equalize.xpi.af.modules;

import com.equalize.xpi.af.modules.json.JSONConverterSimpleFactory;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AbstractModule;

public class JSONTransformBean extends AbstractModule {

	@Override
	protected void processModule() throws ModuleException {

		AbstractModuleConverter converter = JSONConverterSimpleFactory.newInstance().newConverter(this.msg, this.param, this.audit, this.dyncfg);
		converter.retrieveModuleParameters();
		converter.parseInput();
		byte[] outputBytes = converter.generateOutput();

		// Update payload content
		changePayloadContent(outputBytes);
	}
}
