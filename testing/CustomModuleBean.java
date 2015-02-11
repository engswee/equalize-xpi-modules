package com.equalize.xpi.af.modules.testing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class CustomModuleBean implements SessionBean, Module {

 /**
  * 
  */
 private static final long serialVersionUID = 4538833485635759438L;
 private SessionContext myContext;
 private AuditAccess audit;
 
 @Override
 public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
  try {   
   // Get message
   Message msg = (Message) inputModuleData.getPrincipalData();

   // Get audit log
   MessageKey key = new MessageKey(msg.getMessageId(), msg.getMessageDirection());
   try {
    audit = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();      
   } catch (MessagingException e) {
    System.out.println("WARNING: Audit log not available in standalone testing");
   }
   
   addLog(key, AuditLogStatus.SUCCESS, "CustomFunctionBean: Module Called");

   // Get input stream
   XMLPayload payload = msg.getDocument();
   InputStream inStr = payload.getInputStream();   
   
   // Get the text content
   BufferedReader br = new BufferedReader(new InputStreamReader(inStr));  
    ArrayList<String> contents = new ArrayList<String>();
   String lineContent;
   while ((lineContent = br.readLine()) != null) {
    contents.add(lineContent);
   }
   br.close();  
    
   // Modify the root element name
   StringBuilder sb = new StringBuilder();
   for( String line: contents ) {
    if ( line.contains("MT_Calculator_Input")) {
     line = line.replace("MT_Calculator_Input", "New_Root");
     sb.append(line);
    } else {
     sb.append(line);
    }
    sb.append("\n");
   }
   
    // Set changed content
   payload.setContent(sb.toString().getBytes());
     
   // If PARAM1 is configured in the module parameters, add that as a prefix to the file name
   String param1 = moduleContext.getContextData("PARAM1");
   if (param1 != null) {
    // Get Dynamic Configuration
    MessagePropertyKey fileNameKey = new MessagePropertyKey("FileName", "http://sap.com/xi/XI/System/File");
    String fileName = msg.getMessageProperty(fileNameKey);
    // Set new value Dynamic Configuration
    msg.removeMessageProperty(fileNameKey);
    msg.setMessageProperty(fileNameKey, param1 + "_" + fileName );    
   }
   
   // Update the message
   inputModuleData.setPrincipalData(msg);
   
  } catch (Exception e) {
   throw new ModuleException(e.getClass() + ": " + e.getMessage());
  }

  return inputModuleData;
 }

 @Override
 public void ejbActivate() throws EJBException, RemoteException {
 }

 @Override
 public void ejbPassivate() throws EJBException, RemoteException {
 }

 @Override
 public void ejbRemove() throws EJBException, RemoteException {
 }

 @Override
 public void setSessionContext(SessionContext context) throws EJBException, RemoteException {
  myContext = context;
 }

 private void addLog (MessageKey msgKey, AuditLogStatus status, String message) {
  if (audit != null) {
   audit.addAuditLogEntry(msgKey, status, message); 
  } else {
   System.out.println( "Audit Log: " + message);
  }
 }
}
