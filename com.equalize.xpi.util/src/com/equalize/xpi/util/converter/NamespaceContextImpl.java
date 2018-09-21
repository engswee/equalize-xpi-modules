package com.equalize.xpi.util.converter;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class NamespaceContextImpl implements NamespaceContext {
    private HashMap<String, String> namespaceMapping;
    
    public NamespaceContextImpl(HashMap<String, String> namespaceMapping) {
      this.namespaceMapping = namespaceMapping;
    }
    
	@Override
	public String getNamespaceURI(String prefix) {
		return (String)this.namespaceMapping.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return (String)this.namespaceMapping.get(namespaceURI);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		return null;
	}
}
