package com.equalize.xpi.af.modules.util;

import java.lang.reflect.Constructor;

import com.equalize.xpi.af.modules.FormatConversionBean;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class ConverterFactory {

	// Private constructor
	private ConverterFactory() {		
	}

	public static ConverterFactory newInstance() {
		return new ConverterFactory();
	}

	public AbstractModuleConverter newConverter(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg) throws ModuleException {
		String converterClassName = param.getMandatoryParameter("converterClass");
		audit.addLog(AuditLogStatus.SUCCESS, "Conversion using class: " + converterClassName);

		try {
			// Dynamic loading and instantiation of converter class
			Class<?> converterClass = loadClass(converterClassName, audit);
			Constructor<?> constructor = converterClass.getConstructor(Message.class, ParameterHelper.class, AuditLogHelper.class, DynamicConfigurationHelper.class);
			AbstractModuleConverter conv = (AbstractModuleConverter) constructor.newInstance(msg, param, audit, dyncfg);
			return conv;
		} catch (Exception e) {
			audit.addLog(AuditLogStatus.ERROR, "Error initializing class: " + converterClassName);
			throw new ModuleException(e.getMessage(), e);
		}
	}

	private Class<?> loadClass(String className, AuditLogHelper audit)throws ClassNotFoundException {
		Class<?> klass = null;
		ClassLoader classloader = null;
		try {
			classloader = Thread.currentThread().getContextClassLoader();
			klass = Class.forName(className, true, classloader);
			return klass;
		} catch (ClassNotFoundException e) {
			try {
				audit.addLog(AuditLogStatus.WARNING, "Switching to FormatConversionBean class loader");
				classloader = FormatConversionBean.class.getClassLoader();
				klass = Class.forName(className, true, classloader);
				return klass;
			} catch (ClassNotFoundException e2) {
				audit.addLog(AuditLogStatus.WARNING, "Switching to default class loader");
				klass = Class.forName(className);
				return klass;
			}
		}
	}
}
