package com.equalize.xpi.af.modules.util;

import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;

public abstract class AbstractModule implements Module {

	protected Message msg;
	protected XMLPayload payload;
	protected AuditLogHelper audit;
	protected ParameterHelper param;
	protected DynamicConfigurationHelper dyncfg;
	protected String messageLog;
	protected boolean debug;
	
	@Override
	public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
		
		// Initialize attributes and helpers
		this.msg = (Message) inputModuleData.getPrincipalData();
		this.payload = this.msg.getDocument();
		this.audit = new AuditLogHelper(this.msg);
		this.param = new ParameterHelper(moduleContext, this.audit);
		this.dyncfg = new DynamicConfigurationHelper(this.msg);
		// Entered module successfully
		this.audit.addLog(AuditLogStatus.SUCCESS, this.getClass().getName() + ": Module Initialized");
		
		// Get logging requirements
		this.messageLog = this.param.getParameter("messageLog");
		if (this.messageLog != null) {
			this.param.checkParamValidValues("messageLog", "pre,post");			
		}
		
		logMessageVersion("pre");
		
		// Debug
		this.debug = this.param.getBoolParameter("debug");
		if(this.debug) {
			this.audit.addLog(AuditLogStatus.WARNING, "WARNING: Debug activated! Use only in non-productive systems!");
		}
		
		// Implemented at subclass
		processModule();
		
		logMessageVersion("post");
		
		// Exit module successfully
		inputModuleData.setPrincipalData(this.msg);	
		this.audit.addLog(AuditLogStatus.SUCCESS, this.getClass().getName() + ": Module Completed Successfully");
		return inputModuleData;
	}
	
	abstract protected void processModule() throws ModuleException;

	private void logMessageVersion(String messageLogIndicator) {
		if(this.messageLog != null && this.messageLog.equalsIgnoreCase(messageLogIndicator)) {
			String logLocation = this.param.getParameter("logLocation", messageLogIndicator, true);
			MessageLoggerHelper log = new MessageLoggerHelper(this.audit, logLocation);
			log.logMessage(this.msg);
		}
	}
	
	protected void changePayloadContent(byte[] contentBytes) throws ModuleException {
		try {
			this.payload.setContent(contentBytes);
		} catch (InvalidParamException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
	protected void changePayloadContent(byte[] contentBytes, String encoding) throws ModuleException {
		try {
			this.payload.setContent(contentBytes, encoding);
		} catch (InvalidParamException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}
