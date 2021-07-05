package com.equalize.xpi.af.modules.tester.impl;

import java.util.Enumeration;
import java.util.Hashtable;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.service.cpa.Channel;

public class ModuleContextImpl implements ModuleContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5411762348958215886L;
	protected static String SEPARATOR = ".";
	protected String channelID;
	@SuppressWarnings("unchecked")
	protected Hashtable contextData;
	protected Channel channel;

	@SuppressWarnings("unchecked")
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

		/*if (fallback) {
			if (this.channel == null) {
				try {
					LookupManager lookupManager = LookupManager.getInstance();
					this.channel = ((Channel) lookupManager.getCPAObject(
							CPAObjectType.CHANNEL, this.channelID));
				} catch (CPAException ce) {
					this.channel = null;
				}
			}
			if (this.channel != null) {
				try {
					val = this.channel.getValueAsString(name);
				} catch (CPAException ce) {
					val = null;
				}
			}
		}*/
		return val;
	}

	@SuppressWarnings("unchecked")
	public Enumeration getContextDataKeys() {
		return this.contextData.keys();
	}

	@SuppressWarnings("unchecked")
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