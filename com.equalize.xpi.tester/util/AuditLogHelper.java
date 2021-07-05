package com.equalize.xpi.af.modules.util;

import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class AuditLogHelper {

	private final MessageKey msgkey;
	private AuditAccess audit;
	
	public AuditLogHelper (Message msg){
		// Get audit log
		this.msgkey = new MessageKey(msg.getMessageId(), msg.getMessageDirection());
		try {
			this.audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();						
		} catch (MessagingException e) {
			this.audit = null;
			System.out.println("WARNING: Audit log not available in standalone testing");
		}
	}

	public void addLog (AuditLogStatus status, String message) {
		if (this.audit != null) {
			this.audit.addAuditLogEntry(this.msgkey, status, message);	
		} else {
			System.out.println( "Audit Log: " + message);
		}
	}

}
