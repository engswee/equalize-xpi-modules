package com.equalize.xpi.af.modules.testing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import com.equalize.xpi.af.modules.testing.CustomModuleBean;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.service.cpa.CPAException;
import com.sap.aii.af.service.cpa.CPAObjectType;
import com.sap.aii.af.service.cpa.Channel;
//import com.sap.aii.af.service.cpa.LookupManager;
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

public class TestingModuleMain {

	public static void main(String[] args) {
		String encoding = "UTF-8";
		Hashtable<String, String> contextData = new Hashtable<String, String>();

		CustomModuleBean cmb = new CustomModuleBean();
		try {
			// Set PI message details
			Message msg = new MessageImpl("PartyA", "ServiceA", "Interface1", //Sender details
										  "PartyB", "ServiceB", 			  //Receiver details
										  MessageClass.APPLICATION_MESSAGE,
										  MessageDirection.INBOUND,
										  "005056B777881ED491EE4E73A55ACD5D", //Message ID
										  "FTP",							  //Transport Protocol
										  "");								  //Serialization Context

			// Get payload from file
			InputStream inpStr = new FileInputStream(new File("C:\\Users\\engswee\\Desktop\\input.xml"));
			ByteArrayOutputStream baos = InputStreamToBAOS(inpStr);

			// Set XML payload as the main document of the message
			XMLPayload xml = new XMLPayloadImpl();
			xml.setContent(baos.toByteArray(), encoding);
			msg.setDocument(xml);

			// Set message as the module data
			ModuleData data = new ModuleData();
			data.setPrincipalData(msg);

			// Set Dynamic Configuration
			addDynCfg(msg, "http://sap.com/xi/XI/System/File", "FileName", "FileA.txt");

			// Set optional module parameters
			contextData.put("PARAM1", "value1");
			ModuleContextImpl context = new ModuleContextImpl("abcdef1234567890", contextData);

			// Display pre dynamic configuration in console
			Set<MessagePropertyKey> mpkSet = msg.getMessagePropertyKeys();
			if (mpkSet.size() != 0) {
				System.out.println("==============================================");
				System.out.println("Dynamic Configuration before module processing");
				System.out.println("==============================================");
				for (MessagePropertyKey mpk : mpkSet) {
					System.out.println(mpk.getPropertyNamespace() + ";"
									 + mpk.getPropertyName() + ";" 
									 + msg.getMessageProperty(mpk));
				}
			}

			// --------------------------------------------
			// Execute Module processing
			// --------------------------------------------
			data = cmb.process(context, data);

			// Output to file
			Message output = (Message) data.getPrincipalData();
			ByteArrayInputStream bais = (ByteArrayInputStream) output.getDocument().getInputStream();
			baos = InputStreamToBAOS(bais);
			FileOutputStream fileOutStr = new FileOutputStream(new File("C:\\Users\\engswee\\Desktop\\output.txt"));
			baos.writeTo(fileOutStr);
			fileOutStr.close();

			// Display post dynamic configuration in console
			mpkSet = output.getMessagePropertyKeys();
			if (mpkSet.size() != 0) {
				System.out.println("==============================================");
				System.out.println("Dynamic Configuration after module processing");
				System.out.println("==============================================");
				for (MessagePropertyKey mpk : mpkSet) {
					System.out.println(mpk.getPropertyNamespace() + ";"
									 + mpk.getPropertyName() + ";"
									 + output.getMessageProperty(mpk));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ByteArrayOutputStream InputStreamToBAOS(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read = 0;
		while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
			baos.write(buffer, 0, read);
		}
		baos.flush();
		return baos;
	}

	private static void addDynCfg(Message msg, String namespace,
			String attribute, String value) throws InvalidParamException {
		MessagePropertyKey key = new MessagePropertyKey(attribute, namespace);
		msg.setMessageProperty(key, value);
	}
}

class XMLPayloadImpl implements XMLPayload {

	private byte[] content;
	private String encoding;
	private String contentType;
	private String description;
	private String name;
	private String schema;
	private String version;

	public String getEncoding() {
		return this.encoding;
	}

	public Reader getReader() {
		return null;
	}

	public String getText() {
		try {
			return new String(this.content, this.encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public void setContent(byte[] content, String encoding)
			throws InvalidParamException {
		this.content = content;
		this.encoding = encoding;
	}

	public void setText(String text) throws IOException {
		this.content = text.getBytes();
		this.encoding = new OutputStreamWriter(new ByteArrayOutputStream())
				.getEncoding();
	}

	public void setText(String text, String encoding) throws IOException {
		this.content = text.getBytes(encoding);
		this.encoding = encoding;
	}

	public void clearAttributes() {
		// TODO Auto-generated method stub

	}

	public String getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getContent() {
		return this.content;
	}

	public String getContentType() {
		return this.contentType;
	}

	public String getDescription() {
		return this.description;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.content);
	}

	public String getName() {
		return this.name;
	}

	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setAttribute(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void setContent(byte[] content) throws InvalidParamException {
		this.content = content;
	}

	public void setContentType(String contentType) throws InvalidParamException {
		this.contentType = contentType;
	}

	public void setDescription(String description) throws InvalidParamException {
		this.description = description;
	}

	public void setName(String name) throws InvalidParamException {
		this.name = name;
	}

	public String getSchema() {
		return this.schema;
	}

	public String getVersion() {
		return this.version;
	}

	public void setSchema(String schema) throws InvalidParamException {
		this.schema = schema;
	}

	public void setVersion(String version) throws InvalidParamException {
		this.version = version;
	}

}

class ModuleContextImpl implements ModuleContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5411762348958215886L;
	protected static String SEPARATOR = ".";
	protected String channelID;
	protected Hashtable contextData;
	protected Channel channel;

	public ModuleContextImpl(String channelID, Hashtable contextData) {
		this.channelID = channelID;
		this.contextData = contextData;
	}

	public String getChannelID() {
		return this.channelID;
	}

	public String getContextData(String name) {
		return (String) this.contextData.get(name);
	}

	public String getContextData(String name, boolean fallback) {
		String val = (String) this.contextData.get(name);
		if (val != null)
			return val;

		if (fallback) {
/*			if (this.channel == null) {
				try {
					LookupManager lookupManager = LookupManager.getInstance();
					this.channel = ((Channel) lookupManager.getCPAObject(
							CPAObjectType.CHANNEL, this.channelID));
				} catch (CPAException ce) {
					this.channel = null;
				}
			}*/
			if (this.channel != null) {
				try {
					val = this.channel.getValueAsString(name);
				} catch (CPAException ce) {
					val = null;
				}
			}
		}
		return val;
	}

	public Enumeration getContextDataKeys() {
		return this.contextData.keys();
	}

	public Hashtable resolveNamespace(String nameSpace) {
		String searchString = nameSpace + SEPARATOR;
		Hashtable hashtable = new Hashtable();
		Enumeration enumeration = getContextDataKeys();
		while (enumeration.hasMoreElements()) {
			String formerKey = (String) enumeration.nextElement();
			if (formerKey.startsWith(searchString)) {
				String key = formerKey.substring(searchString.length());
				hashtable.put(key, getContextData(formerKey));
			}
		}
		return hashtable;
	}
}

class MessageImpl implements Message {
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
