package com.equalize.xpi.af.modules;

import com.equalize.xpi.af.modules.util.AbstractModule;
import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.ConverterFactory;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class FormatConversionBean extends AbstractModule {
	@Override
	protected void processModule() throws ModuleException {
		if (this.debug)
			this.audit.addLog(AuditLogStatus.SUCCESS, "DEBUG: System default encoding:- " + System.getProperty("file.encoding"));
		ConverterFactory factory = ConverterFactory.newInstance();
		AbstractModuleConverter converter = factory.newConverter(this.msg, this.param, this.audit, this.dyncfg, this.debug);
		converter.retrieveModuleParameters();
		converter.parseInput();
		byte[] outputBytes = converter.generateOutput();

		// Update payload content
		changePayloadContent(outputBytes);
	}
}
