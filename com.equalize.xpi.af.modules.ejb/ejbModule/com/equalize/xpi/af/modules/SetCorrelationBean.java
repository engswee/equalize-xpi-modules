package com.equalize.xpi.af.modules;

import com.equalize.xpi.af.modules.util.AbstractModule;
import com.equalize.xpi.util.converter.ConversionDOMInput;
import com.equalize.xpi.util.converter.Converter;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class SetCorrelationBean extends AbstractModule {
	private String xpath;
	
	@Override
	protected void processModule() throws ModuleException {
		try {
			this.xpath = this.param.getMandatoryParameter("xpath");
			
			// Parse input XML and retrieve message ID from XPath
			ConversionDOMInput domIn = new ConversionDOMInput(this.payload.getInputStream());
			String msgID = domIn.evaluateXPathToString(this.xpath);			
			this.audit.addLog(AuditLogStatus.SUCCESS, "Message ID: " +  msgID);

			// Convert message ID to UUID format
			String msgUUID = Converter.convertMessageIDToUUID(msgID);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Convert to UUID: " +  msgUUID);
			
			// Set UUID as message correlation ID
			this.msg.setCorrelationId(msgUUID);

		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}
}