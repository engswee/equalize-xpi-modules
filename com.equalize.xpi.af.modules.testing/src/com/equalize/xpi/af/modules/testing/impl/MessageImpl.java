package com.equalize.xpi.af.modules.testing.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.interfaces.messaging.api.AckType;
import com.sap.engine.interfaces.messaging.api.Action;
import com.sap.engine.interfaces.messaging.api.DeliverySemantics;
import com.sap.engine.interfaces.messaging.api.ErrorInfo;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageClass;
import com.sap.engine.interfaces.messaging.api.MessageDirection;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.Party;
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.Service;
import com.sap.engine.interfaces.messaging.api.TextPayload;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.engine.interfaces.messaging.api.exception.PayloadFormatException;

public class MessageImpl implements Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 107523302968913019L;
	private Payload payload;
	private String correlationID;
	private DeliverySemantics deliverySemantics;
	private String description;
	private ErrorInfo errorInfo;
	private String refMsgID;
	private String seqID;
	private Set<MessagePropertyKey> msgPropertyKeys;
	private int attachmentCount = 0;

	private HashMap<MessagePropertyKey, String> hm;

	private final Action action;
	private final Party fromParty;
	private final Party toParty;
	private final Service fromService;
	private final Service toService;
	private final MessageClass msgClass;
	private final MessageDirection msgDir;
	private final String msgID;
	private final MessageKey msgKey;
	private final String protocol;
	private final String serialContext;
	private long timeRcvd;
	private long timeSent;

	public MessageImpl(String fromParty, String fromService, String action,
			String toParty, String toService, MessageClass msgClass,
			MessageDirection msgDir, String msgID, String protocol,
			String serialContext) {
		this.fromParty = new Party(fromParty);
		this.fromService = new Service(fromService);
		this.action = new Action(action);
		this.toParty = new Party(toParty);
		this.toService = new Service(toService);
		this.msgClass = msgClass;
		this.msgDir = msgDir;
		this.msgID = msgID;
		this.msgKey = new MessageKey(msgID, msgDir);
		this.protocol = protocol;
		this.serialContext = serialContext;
		// this.timeRcvd = 1;
		// this.timeSent = 2;
		this.hm = new HashMap<MessagePropertyKey, String>();
		this.msgPropertyKeys = new HashSet<MessagePropertyKey>();
	}

	public void addAttachment(Payload arg0) throws PayloadFormatException {
		// TODO Auto-generated method stub
		attachmentCount++;
	}

	public int countAttachments() {
		return attachmentCount;
	}

	public ErrorInfo createErrorInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public Payload createPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	public TextPayload createTextPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	public XMLPayload createXMLPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	public Action getAction() {
		return this.action;
	}

	public Payload getAttachment(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Payload> getAttachmentIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCorrelationId() {
		return this.correlationID;
	}

	public DeliverySemantics getDeliverySemantics() {
		return this.deliverySemantics;
	}

	public String getDescription() {
		return this.description;
	}

	public XMLPayload getDocument() {
		return (XMLPayload) this.payload; // Cast to XMLPayload - will error if set
		// by setMainPayload
	}

	public ErrorInfo getErrorInfo() {
		return this.errorInfo;
	}

	public Party getFromParty() {
		return this.fromParty;
	}

	public Service getFromService() {
		return this.fromService;
	}

	public Payload getMainPayload() {
		return this.payload;
	}

	public MessageClass getMessageClass() {
		return this.msgClass;
	}

	public MessageDirection getMessageDirection() {
		return this.msgDir;
	}

	public String getMessageId() {
		return this.msgID;
	}

	public MessageKey getMessageKey() {
		return this.msgKey;
	}

	public String getMessageProperty(MessagePropertyKey msgPropertyKey) {
		return this.hm.get(msgPropertyKey);
	}

	public String getMessageProperty(String arg0, String arg1) {
		// Deprecated
		return null;
	}

	public Set<MessagePropertyKey> getMessagePropertyKeys() {
		return this.msgPropertyKeys;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public String getRefToMessageId() {
		return this.refMsgID;
	}

	public String getSequenceId() {
		return this.seqID;
	}

	public String getSerializationContext() {
		return this.serialContext;
	}

	public long getTimeReceived() {
		return this.timeRcvd;
	}

	public long getTimeSent() {
		return this.timeSent;
	}

	public Party getToParty() {
		return this.toParty;
	}

	public Service getToService() {
		return this.toService;
	}

	public boolean isAck() {
		return false;
	}

	public boolean isAck(AckType arg0) {
		return false;
	}

	public boolean isAckRequested() {
		return false;
	}

	public boolean isAckRequested(AckType arg0) {
		return false;
	}

	public void removeAttachment(String arg0) {
		// TODO Auto-generated method stub
		attachmentCount--;
	}

	public void removeMessageProperty(MessagePropertyKey msgPropertyKey) {
		if (this.msgPropertyKeys.contains(msgPropertyKey)) {
			this.msgPropertyKeys.remove(msgPropertyKey);
			this.hm.remove(msgPropertyKey);
		}
	}

	public void setCorrelationId(String correlationID)
	throws InvalidParamException {
		this.correlationID = correlationID;
	}

	public void setDeliverySemantics(DeliverySemantics deliverySemantics)
	throws InvalidParamException {
		this.deliverySemantics = deliverySemantics;
	}

	public void setDescription(String description) throws InvalidParamException {
		this.description = description;
	}

	public void setDocument(XMLPayload payload) throws PayloadFormatException {
		this.payload = payload;
	}

	public void setErrorInfo(ErrorInfo errorInfo) throws InvalidParamException {
		this.errorInfo = errorInfo;
	}

	public void setMainPayload(Payload payload) throws PayloadFormatException {
		this.payload = payload;
	}

	public void setMessageProperty(MessagePropertyKey msgPropertyKey, String value)
	throws InvalidParamException {
		// Check if Property already exists
		if (this.msgPropertyKeys.contains(msgPropertyKey)) {
			if (value == null) {
				this.msgPropertyKeys.remove(msgPropertyKey);
				this.hm.remove(msgPropertyKey);
			} else {
				// Update the property value
				this.hm.put(msgPropertyKey, value);
			}
		} else {
			this.msgPropertyKeys.add(msgPropertyKey);
			this.hm.put(msgPropertyKey, value);
		}
	}

	public void setMessageProperty(String arg0, String arg1, String arg2)
	throws InvalidParamException {
		// Deprecated
	}

	public void setRefToMessageId(String refMsgID) throws InvalidParamException {
		this.refMsgID = refMsgID;
	}

	public void setSequenceId(String seqID) throws InvalidParamException {
		this.seqID = seqID;
	}


}