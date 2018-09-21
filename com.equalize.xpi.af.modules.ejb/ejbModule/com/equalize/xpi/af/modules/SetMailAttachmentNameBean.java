package com.equalize.xpi.af.modules;

import com.equalize.xpi.af.modules.util.AbstractModule;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;

public class SetMailAttachmentNameBean extends AbstractModule {

	@Override
	protected void processModule() throws ModuleException {

		try {	
			// Retrieve module parameters
			String namespace = this.param.getParameter("namespace", "http://sap.com/xi/XI/System/File", true);
			String attribute = this.param.getParameter("attribute", "FileName", true);	
			
			boolean setContentType = this.param.getBoolParameter("setContentType", "Y", false);
			boolean setContentDisposition = this.param.getBoolParameter("setContentDisposition", "Y", false);
			boolean setContentDescription = this.param.getBoolParameter("setContentDescription", "Y", false);

			String mimeType = this.param.getParameter("mimeType", "application/xml", false);

			// Get the filename from Dynamic Configuration
			String filename = this.dyncfg.get(namespace, attribute);
			if (filename == null || filename.trim().equals("")) {
				throw new ModuleException("Dynamic Configuration for " + namespace + ":" + attribute + " is not set or contains only whitespaces");
			}
			this.audit.addLog(AuditLogStatus.SUCCESS, "Filename retrieved from Dynamic Configuration");

			// Set the MIME headers
			if (setContentType)
				setContentType(this.payload, filename, mimeType);
			if (setContentDisposition)
				setContentDisposition(this.payload, filename);
			if (setContentDescription)
				setContentDescription(this.payload, filename);

		} catch (InvalidParamException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}
	}

	private void setContentType(XMLPayload payload, String filename, String mimeType) throws InvalidParamException {
		// Based on RFC1341
		String cType = mimeType + "; name=\"" + filename + "\"";
		this.audit.addLog(AuditLogStatus.SUCCESS, "Payload " + payload.getName() + ": Setting content type to - " + cType);
		payload.setContentType(cType);
	}

	private void setContentDisposition(XMLPayload payload, String filename) {
		// Based on RFC1521 and RFC1806
		String cDispo = "attachment; filename=\"" + filename + "\"";
		this.audit.addLog(AuditLogStatus.SUCCESS, "Payload " + payload.getName() + ": Setting content disposition to - " + cDispo);
		payload.setAttribute("content-disposition", cDispo);
	}

	private void setContentDescription(XMLPayload payload, String filename) {
		// Based on RFC1341
		this.audit.addLog(AuditLogStatus.SUCCESS, "Payload " + payload.getName() + ": Setting content description to - " + filename);
		payload.setAttribute("content-description", filename);
	}
}
