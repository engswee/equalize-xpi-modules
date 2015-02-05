package com.equalize.xpi.af.modules;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.equalize.xpi.af.modules.excel.ExcelTransformer;
import com.equalize.xpi.af.modules.excel.ExcelTransformerSimpleFactory;
import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class ExcelTransformBean implements Module {

	private AuditAccess audit;
	private Message msg;
	private MessageKey key;
	private InputStream inStream;
	private XMLPayload payload;
	private ModuleContext moduleParam;

	@Override
	public ModuleData process (ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException { 

		try {
			init(inputModuleData, moduleContext);

			ExcelTransformer transformer = ExcelTransformerSimpleFactory.newInstance().newTransformer(this.moduleParam, this.key, this.audit);
			transformer.retrieveModuleParameters();
			transformer.parseInput(this.inStream);
			ByteArrayOutputStream baos = transformer.generateOutput();

			updateModuleData(inputModuleData, baos.toByteArray());
			return inputModuleData;

		} catch (Exception e) {
			addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}		
	}

	private void init (ModuleData imd, ModuleContext mc) {
		// Get message, payload and input stream
		this.msg = (Message) imd.getPrincipalData();
		this.payload = this.msg.getDocument();
		this.inStream = this.payload.getInputStream();	
		this.moduleParam = mc;

		// Get audit log
		this.key = new MessageKey(this.msg.getMessageId(), this.msg.getMessageDirection());
		try {
			this.audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();						
		} catch (MessagingException e) {
			System.out.println("WARNING: Audit log not available in standalone testing");
		}
		addLog(AuditLogStatus.SUCCESS, this.getClass().getName() + ": Module Initialized");
	}

	private void updateModuleData (ModuleData imd, byte[] byteArray) throws InvalidParamException {
		// Set changed content and update the message
		this.payload.setContent(byteArray);
		imd.setPrincipalData(this.msg);
	}

	private void addLog (AuditLogStatus status, String message) {
		if (this.audit != null) {
			this.audit.addAuditLogEntry(this.key, status, message);	
		} else {
			System.out.println( "Audit Log: " + message);
		}
	}
}
