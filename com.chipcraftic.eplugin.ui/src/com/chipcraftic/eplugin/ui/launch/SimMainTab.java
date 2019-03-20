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
* File Name : SimMainTab.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.launch;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.launch.ui.CMainTab2;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.chipcraftic.eplugin.core.launch.SimLaunchDelegate;

public class SimMainTab extends CMainTab2 {
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		super.setDefaults(config);
		
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
				IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE_ATTACH);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, "gdbserver");
		config.setAttribute(IGDBLaunchConfigurationConstants.ATTR_REMOTE_TCP, false);
		config.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEV, 
				"| ${ENV:CCSDK_HOME}/tools/dbgserver.py"
				+ " --pipe -p pipe:" + SimLaunchDelegate.getDebugPipeName());
		
//		config.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
//				"${ENV:CCSDK_HOME}/toolchain/" + CcConstants.TOOLCHAIN_TRIPLET 
//				+ "/bin/" + CcConstants.TOOLCHAIN_TRIPLET + "-gdb");
		

		config.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, "CC Device");
		config.setAttribute(IGDBJtagConstants.ATTR_CONNECTION, "dummy");
		config.setAttribute(IGDBJtagConstants.ATTR_DO_HALT, false);
		config.setAttribute(IGDBJtagConstants.ATTR_DO_RESET, false);
		config.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE, false);
		config.setAttribute(IGDBJtagConstants.ATTR_LOAD_SYMBOLS, true);
		config.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE, true);
		config.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS, true);
		config.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE, false);
		config.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS, false);
	}
}
