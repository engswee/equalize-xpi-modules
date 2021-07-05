/**
 * 
 */
package com.sap.aii.af.lib.mp.module;

import javax.ejb.EJBLocalHome;

/**
 * @author pgujjeti
 *
 */
public interface ModuleLocalHome extends EJBLocalHome {

	public com.sap.aii.af.lib.mp.module.ModuleLocal create() throws javax.ejb.CreateException;

}
