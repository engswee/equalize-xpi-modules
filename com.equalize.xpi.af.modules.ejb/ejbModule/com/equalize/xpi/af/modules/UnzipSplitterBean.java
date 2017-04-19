package com.equalize.xpi.af.modules;

import java.util.LinkedHashMap;

import javax.naming.NamingException;
import javax.resource.ResourceException;

import com.equalize.xpi.af.modules.util.AbstractModule;
import com.equalize.xpi.af.modules.util.MessageDispatcher;
import com.equalize.xpi.util.converter.ConversionZipInput;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.DeliverySemantics;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class UnzipSplitterBean extends AbstractModule {

	private String mode;
	private boolean reuse;
	private MessageDispatcher msgdisp;
	private String fileNameAttr;
	private String fileNameNS;
	private String mimeType;
	private final String reuseNS = "urn:equalize";

	@Override
	protected void processModule() throws ModuleException {
		// Module parameters
		this.reuse = this.param.getBoolParameter("reuse", "Y", true);
		if(!this.reuse) {
			this.mode = this.param.getMandatoryParameter("mode");
		}
		this.fileNameAttr = this.param.getParameter("fileNameAttr", "FileName", true);
		this.fileNameNS = this.param.getParameter("fileNameNS", "http://sap.com/xi/XI/System/File", true);
		this.mimeType = this.param.getParameter("mimeType", "application/xml", false);

		// Retrieve reuse Indicator to check if this is a child message routed back into the same channel
		String reuseIndicator = this.dyncfg.get(reuseNS, "reuseIndicator");

		try {
			if(this.reuse && reuseIndicator != null && reuseIndicator.equalsIgnoreCase("true")) {
				// This is a child message dispatched into the same channel, so no further processing required
				this.audit.addLog(AuditLogStatus.SUCCESS, "Processing of child message in same channel is skipped");
			} else {
				// Create message dispatcher
				if(this.reuse) {
					String channelID = this.param.getModuleContext().getChannelID();
					this.audit.addLog(AuditLogStatus.SUCCESS, "Retrieving current channel ID for processing of child messages");
					this.msgdisp = new MessageDispatcher(channelID, this.audit);
				} else if(this.mode.equals("binding")) {
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

				// Parse the zip file and extract the entries into a map
				this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing zip file");
				ConversionZipInput in = new ConversionZipInput(this.payload.getContent());
				LinkedHashMap<String, byte[]> map = in.getEntriesContent();
				this.audit.addLog(AuditLogStatus.SUCCESS, "Total number of zip entries: " + map.size());

				// Loop through the entries and dispatch them as new messages
				int count = 0;
				for(String keyName : map.keySet()) {
					count++;
					byte[] entryContent = map.get(keyName);
					this.audit.addLog(AuditLogStatus.SUCCESS, "Zip entry " + count + ": " + keyName);					

					if(this.reuse) {
						// First zip entry will replace main payload, subsequent entries are dispatched
						// into the same channel, with Dynamic Configuration set to skip it 
						// being processed by the channel again
						if(count == 1) {
							this.audit.addLog(AuditLogStatus.SUCCESS, "Replacing main payload");
							this.payload.setContent(entryContent);
							this.dyncfg.change(this.fileNameNS, this.fileNameAttr, keyName);
						} else {
							createAndDispatchMessages(keyName, entryContent, this.reuse);
						}
					} else {
						// Generate child message into a separate channel
						createAndDispatchMessages(keyName, entryContent, this.reuse);
					}
				}
			}
		} catch (Exception e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}		
	}

	private void createAndDispatchMessages(String name, byte[] content, boolean reuse) throws NamingException, MessagingException, ResourceException {
		this.msgdisp.createMessage(content, DeliverySemantics.ExactlyOnce);
		this.msgdisp.setRefToMessageId(this.msg.getMessageId());
		this.msgdisp.setPayloadContentType(this.mimeType + "; name=\"" + name +"\"");
		this.msgdisp.addDynamicConfiguration(this.fileNameNS, this.fileNameAttr, name);
		if(reuse)
			this.msgdisp.addDynamicConfiguration(reuseNS, "reuseIndicator", "true");
		// Dispatch child message
		this.msgdisp.dispatchMessage();
	}
}
