package com.equalize.xpi.af.modules.util;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.XMLPayload;

public abstract class AbstractModuleConverter {
	protected final Message msg;
	protected final XMLPayload payload;
	protected final AuditLogHelper audit;
	protected final ParameterHelper param;
	protected final DynamicConfigurationHelper dyncfg;
	protected final boolean debug;
	
	public AbstractModuleConverter (Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg, Boolean debug) {
		this.msg = msg;
		this.payload = this.msg.getDocument();
		this.param = param;
		this.dyncfg = dyncfg;
		this.audit = audit;	
		this.debug = debug;
	}
	
	public abstract void retrieveModuleParameters() throws ModuleException;

	public abstract void parseInput() throws ModuleException;

	public abstract byte[] generateOutput() throws ModuleException;
}
