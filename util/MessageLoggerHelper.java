package com.equalize.xpi.af.modules.util;

import com.sap.engine.interfaces.messaging.api.APIAccessFactory;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;
import com.sap.engine.interfaces.messaging.api.logger.MessageLogger;

public class MessageLoggerHelper {
	private MessageLogger logger;
	private final AuditLogHelper audit;
	private final String logLocation;

	public MessageLoggerHelper(AuditLogHelper audit) {
		this(audit, "default");
	}

	public MessageLoggerHelper(AuditLogHelper audit, String logLocation) {
		this.audit = audit;
		this.logLocation = logLocation;	
		try {
			this.logger = APIAccessFactory.getAPIAccess().getMessageLogger(this.logLocation);
		} catch (MessagingException e) {
			this.logger = null;
			System.out.println("WARNING: Message logger not available in standalone testing");
		}
	}

	public void logMessage(Message msg) {
		if (this.logger != null) {
			this.audit.addLog(AuditLogStatus.SUCCESS, "Logging message in location " + this.logLocation);
			this.logger.log(msg);
		}
	}
}
