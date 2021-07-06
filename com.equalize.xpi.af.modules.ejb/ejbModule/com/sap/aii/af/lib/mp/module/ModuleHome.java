/**
 * 
 */
package com.sap.aii.af.lib.mp.module;

import javax.ejb.EJBHome;

/**
 * @author pgujjeti
 *
 */
public interface ModuleHome extends EJBHome {

	public com.sap.aii.af.lib.mp.module.ModuleRemote create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
