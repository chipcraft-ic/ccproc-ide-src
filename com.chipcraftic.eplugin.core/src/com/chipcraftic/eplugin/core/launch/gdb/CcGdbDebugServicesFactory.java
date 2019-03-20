/*H*****************************************************************************
*
* Copyright (c) 2018 ChipCraft Sp. z o.o. All rights reserved
*
* This software is subject to the terms of the Eclipse Public License v2.0
* You must accept the terms of that agreement to use this software.
* A copy of the License is available at
* https://www.eclipse.org/legal/epl-2.0
*
* ******************************************************************************
* File Name : CcGdbDebugServicesFactory.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.core.launch.gdb;

import org.eclipse.cdt.dsf.debug.service.IMemory;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.service.command.CommandFactory_6_8;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.chipcraftic.eplugin.core.dsf.CcFocusService;
import com.chipcraftic.eplugin.core.dsf.ICcFocusService;

/**
 * Override some core DSF services.
 * Note: only GDB 7.12 is supported (it is bundled in CCSDK).
 * Therefore there is no need to make different classes for each version.
 * @author rafalh
 *
 */
public class CcGdbDebugServicesFactory extends GdbDebugServicesFactory {

	public CcGdbDebugServicesFactory(String version, ILaunchConfiguration config) {
		super(version, config);
	}
	
	@Override
	protected ICommandControl createCommandControl(DsfSession session, ILaunchConfiguration config) {
		return new CcGDBControl_7_12(session, config, new CommandFactory_6_8());
	}
	
	@Override
	protected IMemory createMemoryService(DsfSession session) {
		return new CcGDBMemory(session);
	}
	
	protected ICcFocusService createThreadTracker(DsfSession session) {
		return new CcFocusService(session);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <V> V createService(Class<V> clazz, DsfSession session, Object... optionalArguments) {
		if (ICcFocusService.class.isAssignableFrom(clazz)) {
			return (V)createThreadTracker(session);
		} 
		return super.createService(clazz, session, optionalArguments);
	}

}
