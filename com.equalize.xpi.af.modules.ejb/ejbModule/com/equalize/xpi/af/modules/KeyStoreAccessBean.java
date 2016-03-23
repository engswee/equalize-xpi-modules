package com.equalize.xpi.af.modules;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import com.equalize.xpi.af.modules.util.AbstractModule;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.service.resource.SAPSecurityResources;
import com.sap.aii.security.lib.KeyStoreManager;
import com.sap.aii.security.lib.PermissionMode;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.security.api.ssf.ISsfPab;
import com.sap.security.api.ssf.ISsfProfile;

public class KeyStoreAccessBean extends AbstractModule {

	@Override
	protected void processModule() throws ModuleException {


		try {
			String permission = this.param.getParameter("permission", "system", true);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Permission type:" + permission);
			SAPSecurityResources resource = SAPSecurityResources.getInstance();
			KeyStoreManager ksMgr = null;
			String[] domain = new String[] {"equalize.com/com.equalize.xpi.af.modules.app"};
			if(permission.equalsIgnoreCase("system")) {
				ksMgr = resource.getKeyStoreManager(PermissionMode.SYSTEM_LEVEL, domain);
			} else if(permission.equalsIgnoreCase("nodomain")) {
				ksMgr = resource.getKeyStoreManager(PermissionMode.SYSTEM_LEVEL);		
			} else if(permission.equalsIgnoreCase("privilege")) {
				ksMgr = resource.getKeyStoreManager(PermissionMode.DO_PRIVILEGED, domain);
			}
			if(ksMgr == null ){
				throw new ModuleException("KSM null");
			}
			String view = this.param.getParameter("view", "WebServiceSecurity", false);
			String key = this.param.getParameter("key", "System-key", false);
			String cert = this.param.getParameter("cert", "System-cert", false);
			//listAliases(ksMgr, kview);	

			KeyStore ks = ksMgr.getKeyStore(view);
			// Get cert			
			if(ksMgr.isCertificateEntry(ks, cert)) {
				ISsfProfile profile = ksMgr.getISsfProfile(ks, cert, null);
				//this.audit.addLog(AuditLogStatus.SUCCESS, "Certificate Entry");
				//String subject = profile.getCertificate().getSubjectX500Principal().getName();
				//this.audit.addLog(AuditLogStatus.SUCCESS, "Subject Name: " + subject);
				Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
				Certificate profCert = profile.getCertificate();
				this.audit.addLog(AuditLogStatus.SUCCESS, "Cert class: " + profCert.getClass().getName());
				cipher.init(Cipher.ENCRYPT_MODE, profCert);
				byte[] encrypted = cipher.doFinal(this.payload.getContent());
				String b64 = DatatypeConverter.printBase64Binary(encrypted);
				this.payload.setText(b64);
			} else {
				throw new ModuleException(cert + " is not a certificate entry!");
			}
			
			// Get key			
			if(ksMgr.isKeyEntry(ks, key)) {
				ISsfProfile profile = ksMgr.getISsfProfile(ks, cert, null);
				//this.audit.addLog(AuditLogStatus.SUCCESS, "Key entry");
				//this.audit.addLog(AuditLogStatus.SUCCESS, "Format: " + profile.getPrivateKey().getFormat());
			} else {
				throw new ModuleException(cert + " is not a key entry!");
			}
			
/*			KeyStore ks = ksMgr.getKeyStore(kview);
			ISsfProfile privKeyProf = ksMgr.getISsfProfile(ks, alias, null);
			if(privKeyProf != null) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "View: " + kview + "; Alias: " + alias);
			}
			
			PrivateKey privKey = privKeyProf.getPrivateKey();
			if(privKey != null) {
				String algo = privKey.getAlgorithm();
				this.audit.addLog(AuditLogStatus.SUCCESS, "Algo: " + algo);
			}*/
			
/*			if(this.debug) {
				String[] views = ksMgr.getAllKeyStoreViews();
				this.audit.addLog(AuditLogStatus.SUCCESS, "Loop through key store views");
				for(String view : views) {
					this.audit.addLog(AuditLogStatus.SUCCESS, "View: " + view);
					listAliases(ksMgr, view);					
				}
			}*/

		} catch (KeyStoreException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} catch (IOException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}

		/*		try {	
			// Retrieve module parameters
			String mode = this.param.getMandatoryParameter("mode");
			if(mode.equalsIgnoreCase("nobody")) {
				String namespace = this.param.getParameter("namespace", "http://sap.com/xi/XI/System/File", true);
				String attribute = this.param.getParameter("attribute", "FileName", true);		

				String filename = this.dyncfg.get(namespace, attribute);
				if (filename == null) {
					throw new ModuleException("Dynamic Configuration for " + namespace + ":" + attribute + " is not set");
				}

				this.payload.setContentType("application/octet-stream;name=\"" + filename + "\"");
				this.audit.addLog(AuditLogStatus.SUCCESS, "Set content type to " + payload.getContentType());

				this.payload.setName(filename); 

				this.payload.setAttribute("content-disposition", "attachment;filename=\"" + filename + "\"");
			} else if (mode.equalsIgnoreCase("multi")) {

			}
		} catch (InvalidParamException e) {
			this.audit.addLog(AuditLogStatus.ERROR, e.getMessage());
			throw new ModuleException(e.getMessage(), e);
		}*/

	}

	private void listAliases(KeyStoreManager mgr, String view) throws KeyStoreException {
		KeyStore ks = mgr.getKeyStore(view);
/*		ISsfPab pab = mgr.getISsfPab(new KeyStore[] {ks});
		X509Certificate[] certs = pab.getCertificates();
		for(X509Certificate cert: certs) {
			PublicKey pb = cert.getPublicKey();
			this.audit.addLog(AuditLogStatus.SUCCESS,"Subject Principal:- " + cert.getSubjectX500Principal().getName());
			this.audit.addLog(AuditLogStatus.SUCCESS,"Cert type:- " + cert.getType());
			this.audit.addLog(AuditLogStatus.SUCCESS,"Public Key:- " + pb.getClass().getName());
			this.audit.addLog(AuditLogStatus.SUCCESS,"Algorithm:- " + pb.getAlgorithm());
		}*/
		String[] aliases = mgr.getKeyStoreAliases(ks);
		if(aliases != null) {
			for (String alias : aliases) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "View: " + view + "; Alias: " + alias);
				ISsfProfile profile = mgr.getISsfProfile(ks, alias, null);
				if(mgr.isCertificateEntry(ks, alias)) {
					this.audit.addLog(AuditLogStatus.SUCCESS, "Certificate Entry");
					String subject = profile.getCertificate().getSubjectX500Principal().getName();
					this.audit.addLog(AuditLogStatus.SUCCESS, "Subject Name: " + subject);
				}
				if(mgr.isKeyEntry(ks, alias)) {
					this.audit.addLog(AuditLogStatus.SUCCESS, "Key entry");
					this.audit.addLog(AuditLogStatus.SUCCESS, "Format: " + profile.getPrivateKey().getFormat());
				}
			}
		}
/*		String[] aliases = mgr.getKeyStoreAliases(ks);
		if(aliases != null) {
			for (String alias : aliases) {
				//this.audit.addLog(AuditLogStatus.SUCCESS, "Alias: " + alias);
				if(mgr.isCertificateEntry(ks, alias)) {
					this.audit.addLog(AuditLogStatus.SUCCESS, "View: " + view + "; Alias: " + alias);
					Certificate cert = ks.getCertificate(alias);
					if(cert == null) {
						this.audit.addLog(AuditLogStatus.SUCCESS, "Null cert");
					} else {
						PublicKey pb = cert.getPublicKey();
						this.audit.addLog(AuditLogStatus.SUCCESS,"Public Key:- " + pb.getClass().getName());
						this.audit.addLog(AuditLogStatus.SUCCESS,"Algorithm:- " + pb.getAlgorithm());
					}
				}
			}
		}*/
	}
}
