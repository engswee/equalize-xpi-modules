package com.equalize.xpi.af.modules;

import java.util.Iterator;
import java.util.Set;

import com.equalize.xpi.af.modules.util.AbstractModule;
import com.equalize.xpi.af.modules.util.MessageDispatcher;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.DeliverySemantics;
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class AttachmentSplitterBean extends AbstractModule {

	private String mode;
	private MessageDispatcher msgdisp;	
	private String contentType;
	private String qualityOfService;
	private String sequenceId;
	private boolean storeFileName;
	private boolean filenameFromAttachmentName;
	private String fileNameAttr;
	private String fileNameNS;
	private boolean replaceMainWithAttachment;

	@SuppressWarnings("unchecked")
	@Override
	protected void processModule() throws ModuleException {
		try {			
			// Module parameters
			this.mode = this.param.getMandatoryParameter("mode");
			this.param.checkParamValidValues("mode", "binding,channel");
			this.qualityOfService = this.param.getMandatoryParameter("qualityOfService");
			if (this.qualityOfService.equals("EOIO")){
				this.sequenceId = this.param.getConditionallyMandatoryParameter("sequenceId", "qualityOfService", "EOIO");
			}
			this.param.checkParamValidValues("qualityOfService", "EO,EOIO,BE");
			this.contentType = this.param.getParameter("contentType");
			this.storeFileName = this.param.getBoolParameter("storeFileName", "N", false);
			this.filenameFromAttachmentName = this.param.getBoolParameter("filenameFromAttachmentName", "N", false);
			if(this.storeFileName) {
				this.fileNameAttr = this.param.getParameter("fileNameAttr", "FileName", true);
				this.fileNameNS = this.param.getParameter("fileNameNS", "http://sap.com/xi/XI/System/File", true);
			}
			this.replaceMainWithAttachment = this.param.getBoolParameter("replaceMainWithAttachment", "N", false);

			// Get attachments of the message
			Iterator<Payload> iter = this.msg.getAttachmentIterator();
			if(iter.hasNext()) {
				// Create message dispatcher
				if(this.mode.equals("binding")) {
					String adapterType = this.param.getConditionallyMandatoryParameter("adapterType", "mode", "binding");
					String adapterNS = this.param.getConditionallyMandatoryParameter("adapterNS", "mode", "binding");
					String fromParty = this.param.getParameter("fromParty", "", false);
					String fromService = this.param.getConditionallyMandatoryParameter("fromService", "mode", "binding");
					String toParty = this.param.getParameter("toParty", "", false);
					String toService = this.param.getParameter("toService", "", false);
					String interfaceName = this.param.getConditionallyMandatoryParameter("interfaceName", "mode", "binding");
					String interfaceNamespace = this.param.getConditionallyMandatoryParameter("interfaceNamespace", "mode", "binding");
					this.msgdisp = new MessageDispatcher(adapterType, adapterNS, fromParty, fromService, toParty, toService, interfaceName, interfaceNamespace, this.audit);
				} else if(this.mode.equals("channel")) {
					String channelID = this.param.getConditionallyMandatoryParameter("channelID", "mode", "channel");
					this.msgdisp = new MessageDispatcher(channelID, this.audit);					
				}
				
				// Iterate through the attachments and dispatch each as a child message
				int count = 0;
				while(iter.hasNext()) {
					count++;
					Payload childPayload = iter.next();
					if(this.replaceMainWithAttachment && count == 1) {						
						// Replace the main document with content from first attachment
						this.payload.setContent(childPayload.getContent());
						if(this.storeFileName) {
							this.dyncfg.change(this.fileNameNS, this.fileNameAttr, retrieveFileName(childPayload, count));
						}
						this.audit.addLog(AuditLogStatus.SUCCESS, "Replacing main payload with first attachment's content");
					} else {
						// Create child message and set reference message ID
						if (this.qualityOfService.equals("EOIO")){
							this.msgdisp.createMessage(childPayload.getContent(), getDeliverySemantics(this.qualityOfService), this.sequenceId);
						}
						else {
							this.msgdisp.createMessage(childPayload.getContent(), getDeliverySemantics(this.qualityOfService));
						}
						this.msgdisp.setRefToMessageId(this.msg.getMessageId());
						// Set child message content type
						if(this.contentType == null) {
							this.msgdisp.setPayloadContentType(childPayload.getContentType());
						} else {
							this.msgdisp.setPayloadContentType(this.contentType);
						}
						if(this.storeFileName) {
							this.msgdisp.addDynamicConfiguration(this.fileNameNS, this.fileNameAttr, retrieveFileName(childPayload, count));						
						}
						// Dispatch child message
						this.msgdisp.dispatchMessage();
					}
				}
			} else {
				// No attachments in message
				this.audit.addLog(AuditLogStatus.WARNING, "Message has no attachments to split");
			}
		} catch (Exception e) {
			throw new ModuleException(e.getMessage(), e);
		}
	}

	private DeliverySemantics getDeliverySemantics(String qos) {
		if(qos.equals("EO")) {
			return DeliverySemantics.ExactlyOnce;
		} else if (qos.equals("EOIO")) {
			return DeliverySemantics.ExactlyOnceInOrder;
		} else if (qos.equals("BE")) {
			return DeliverySemantics.BestEffort;
		}
		throw new IllegalArgumentException("Invalid QoS: " + qos);
	}
	
	private String retrieveFileName(Payload payload, int count) {
		// Filename could either be from:-
		// i) Attachment's payload name
		// ii) Attachment's content type
		String filename = null;
		if(this.filenameFromAttachmentName) {
			this.audit.addLog(AuditLogStatus.SUCCESS, "Getting filename from Payload name");
			filename = payload.getName();
		} else {
			// Filename normally is included in the name parameter
			// It can be enclosed in double quotes
			// It can also be followed by other parameters or white spaces
			// Some sample below:-
			// text/plain;charset="UTF-8";name="file.txt" ;otherParam=ParamValue
			// text/plain; charset=us-ascii; name=sample.txt
			
			// Get the value of the filename from parameter name
			this.audit.addLog(AuditLogStatus.SUCCESS, "Trying to get filename from content-type header.");
			String cType = payload.getContentType();
			int nameIndex = cType.indexOf("name=");
			if(nameIndex == -1) {
				
				//Name wasn't found in Content-Type. Write to log.
				this.audit.addLog(AuditLogStatus.WARNING, "Unable to retrieve file name from content-type: " + cType);
				
				//Filename isn't in ContentType, so ContentDisposition is checked	
				this.audit.addLog(AuditLogStatus.SUCCESS, "Trying to get filename from content-disposition header.");
				
				Set<String> attributeList = payload.getAttributeNames();
				String contentDispoName = "";
				
				//Handle different header attribute styles
				if (attributeList.contains("Content-Disposition")){
					contentDispoName = "Content-Disposition";
				}
				if (attributeList.contains("content-disposition")){
					contentDispoName = "content-disposition";
				}
				cType = payload.getAttribute(contentDispoName);
				
				nameIndex = cType.indexOf("filename=");
				if(nameIndex == -1) {
					// Set to default file name
					this.audit.addLog(AuditLogStatus.WARNING, "Unable to retrieve file name from content-disposition: " + cType);
					String defaultFileName = "Attachment" + count + ".txt";
					this.audit.addLog(AuditLogStatus.WARNING, "Setting filename to: " + defaultFileName );
					return defaultFileName;
				}
				filename = cType.substring(nameIndex+9);				
			}
			else{
				filename = cType.substring(nameIndex+5);
			}
			
			// Check if there are other parameters after name and strip them
			int additionalInfo = filename.indexOf(";");
			if(additionalInfo != -1) {
				filename = filename.substring(0, additionalInfo);
			}
			// Remove double quotes and white spaces
			filename = filename.replaceAll("\"", "").trim();
		}
		this.audit.addLog(AuditLogStatus.SUCCESS, "Setting filename to: " + filename );
		return filename;
	}
}
