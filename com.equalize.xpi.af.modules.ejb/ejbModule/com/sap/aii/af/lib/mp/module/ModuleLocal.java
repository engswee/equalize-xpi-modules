/**
 * 
 */
package com.sap.aii.af.lib.mp.module;

import javax.ejb.EJBLocalObject;

/**
 * @author pgujjeti
 *
 */
public interface ModuleLocal extends EJBLocalObject {

	ModuleData process(ModuleContext context, ModuleData inputModuleData) throws ModuleException;

}
