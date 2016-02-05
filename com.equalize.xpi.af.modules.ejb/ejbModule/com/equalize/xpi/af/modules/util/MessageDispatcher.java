package com.equalize.xpi.af.modules.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;

import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.lib.mp.processor.ModuleProcessor;
import com.sap.aii.af.lib.mp.processor.ModuleProcessorFactory;
import com.sap.aii.af.service.administration.api.cpa.CPAFactory;
import com.sap.aii.af.service.administration.api.cpa.CPAInboundRuntimeLookupManager;
import com.sap.aii.af.service.cpa.Binding;
import com.sap.aii.af.service.cpa.CPAException;
import com.sap.aii.af.service.cpa.CPAObjectType;
import com.sap.aii.af.service.cpa.Channel;
import com.sap.aii.af.service.cpa.LookupManager;
import com.sap.engine.interfaces.messaging.api.Action;
import com.sap.engine.interfaces.messaging.api.ConnectionFactory;
import com.sap.engine.interfaces.messaging.api.DeliverySemantics;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageFactory;
import com.sap.engine.interfaces.messaging.api.Party;
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.Service;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class MessageDispatcher {
	private final String channelID;
	private final AuditLogHelper audit;
	private String fromParty;
	private String fromService;
	private String toParty;
	private String toService;
	private String action;
	private String actionNS;
	private Message msg;
	private Payload payload;
	private DynamicConfigurationHelper dyncfg;

	public MessageDispatcher(String channelID, AuditLogHelper audit) throws CPAException {
		this.audit = audit;
		this.channelID = channelID;
		this.audit.addLog(AuditLogStatus.SUCCESS, "Searching for matching channel");
		Channel channel = (Channel) LookupManager.getInstance().getCPAObject(CPAObjectType.CHANNEL, this.channelID);
		this.audit.addLog(AuditLogStatus.SUCCESS, "Successfully found channel " + channel.getChannelName() + " for " + this.channelID);
		// This only works correctly if there is there is exactly one binding for the channel
		Binding binding = CPAFactory.getInstance().getLookupManager().getBindingByChannelId(channel.getObjectId());
		this.fromParty = binding.getFromParty();
		this.fromService = binding.getFromService();
		this.toParty = binding.getToParty();
		this.toService = binding.getToService();
		this.action = binding.getActionName();
		this.actionNS = binding.getActionNamespace();
		setMessageHeader();	
	}

	public MessageDispatcher(String adapterType, String adapterNamespace, String fromParty, String fromService, String toParty, String toService, String interfaceName, String interfaceNamespace, AuditLogHelper audit) throws CPAException {
		this.audit = audit;
		this.fromParty = fromParty;
		this.fromService = fromService;
		this.toParty = toParty;
		this.toService = toService;
		this.action = interfaceName;
		this.actionNS = interfaceNamespace;
		this.audit.addLog(AuditLogStatus.SUCCESS, "Searching for matching sender/receiver agreement binding");
		CPAFactory cpaFact = CPAFactory.getInstance();
		CPAInboundRuntimeLookupManager cpaLM = cpaFact.createInboundRuntimeLookupManager(adapterType, adapterNamespace, this.fromParty, this.toParty, this.fromService, this.toService, this.action, this.actionNS);
		this.channelID = cpaLM.getChannel().getObjectId();
		this.audit.addLog(AuditLogStatus.SUCCESS, "Successfully found channel ID " + this.channelID);
		setMessageHeader();	
	}

	public void createMessage(byte[] content, DeliverySemantics ds) throws NamingException, MessagingException {
		createMessage(content, ds, "application/xml; charset=UTF-8", "");
	}
	
	public void createMessage(byte[] content, DeliverySemantics ds, String sequenceId) throws NamingException, MessagingException {
		createMessage(content, ds, "application/xml; charset=UTF-8", sequenceId);
	}

	public void createMessage(byte[] content, DeliverySemantics ds, String contentType, String sequenceId) throws NamingException, MessagingException {					
		Party fp = new Party(this.fromParty);
		Party tp = new Party(this.toParty);
		Service fs = new Service(this.fromService);
		Service ts = new Service(this.toService);
		Action a = new Action(this.action, this.actionNS);

		InitialContext ctx = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("MessagingConnectionFactory");
		MessageFactory mf = connectionFactory.createMessageFactory("XI");

		// Create message with header details and QOS
		this.msg = mf.createMessage(fp, tp, fs, ts, a);
		this.msg.setDeliverySemantics(ds);
		if (ds.equals(DeliverySemantics.ExactlyOnceInOrder)){
			this.msg.setSequenceId(sequenceId);
		}
		// Create payload
		this.payload = this.msg.createPayload();
		// Set payload content and content type
		this.payload.setContent(content);
		setPayloadContentType(contentType);
		// Set the DC helper
		this.dyncfg = new DynamicConfigurationHelper(this.msg);
		// Set payload as main payload of message
		this.msg.setMainPayload(this.payload);			
	}

	public void setRefToMessageId(String refId) throws InvalidParamException {
		this.msg.setRefToMessageId(refId);
	}

	public void setPayloadName(String name) throws InvalidParamException {
		this.payload.setName(name);
	}

	public void setPayloadContentType(String type) throws InvalidParamException {
		this.payload.setContentType(type);
	}
	
	public void addDynamicConfiguration(String namespace, String attribute, String value) throws InvalidParamException {
		this.dyncfg.add(namespace, attribute, value);
	}

	public void dispatchMessage() throws ResourceException, ModuleException {	
		// Create module data with message
		ModuleData md = new ModuleData();
		md.setPrincipalData(this.msg);
		md.setSupplementalData("audit.key", this.msg.getMessageKey());
		ModuleProcessor mp = null;
		// Get the module processor
		try {
			mp = ModuleProcessorFactory.getModuleProcessor(true, 3, 30000);
		} catch (ModuleException e) {
			throw new ResourceException("Cannot get access to the XI AF module processor. Ejb might not have been started yet.", e);
		}
		// Process the module data via the module processor with the corresponding channel ID
		this.audit.addLog(AuditLogStatus.SUCCESS, "Dispatching new message ID " + this.msg.getMessageId());
		mp.process(this.channelID, md);
		this.audit.addLog(AuditLogStatus.SUCCESS, "Message " + this.msg.getMessageId() + " successfully dispatched");
	}

	private void setMessageHeader() {
		if ((this.fromParty == null) || (this.fromParty.equals("*"))) {
			this.fromParty = new String("");
		}
		if ((this.fromService == null) || (this.fromService.equals("*"))) {
			this.fromService = new String("");
		}
		if ((this.toParty == null) || (this.toParty.equals("*"))) {
			this.toParty = new String("");
		}
		if ((this.toService == null) || (this.toService.equals("*"))) {
			this.toService = new String("");
		}
		if ((this.action == null) || (this.action.equals("*"))) {
			this.action = new String("");
		}
		if ((this.actionNS == null) || (this.actionNS.equals("*"))) {
			this.actionNS = new String("");
		}
		this.audit.addLog(AuditLogStatus.SUCCESS, "New Message Header:- FP=" + this.fromParty + "|FS=" + this.fromService + "|TP=" + this.toParty + "|TS=" + this.fromParty + "|IF=" + this.action + "|NS=" + this.actionNS);		
	}
}