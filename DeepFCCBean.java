package com.equalize.xpi.af.modules;

import com.equalize.xpi.af.modules.deepfcc.DeepFCCConverterSimpleFactory;
import com.equalize.xpi.af.modules.util.AbstractModule;
import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class DeepFCCBean extends AbstractModule {

	@Override
	protected void processModule() throws ModuleException {
		AbstractModuleConverter converter = DeepFCCConverterSimpleFactory.newInstance().newConverter(this.msg, this.param, this.audit, this.dyncfg);
		converter.retrieveModuleParameters();
		converter.parseInput();
		byte[] outputBytes = converter.generateOutput();

		// Update payload content
		changePayloadContent(outputBytes);
	}
}
