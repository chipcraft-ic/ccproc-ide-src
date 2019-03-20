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
* File Name : CcGdbLaunch.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.core.launch.gdb;

import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;

import com.chipcraftic.eplugin.core.CcConstants;
import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.CcUtils;

public class CcGdbLaunch extends GdbLaunch {

	public CcGdbLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
		super(launchConfiguration, mode, locator);
	}
	
	@Override
	public IPath getGDBPath() {
		try {
			IProject project = CcUtils.getProject(getLaunchConfiguration());
			String sdkLocation = CcUtils.getSdkLocation(project);
			String gdb = sdkLocation + "/toolchain/" + CcConstants.TOOLCHAIN_TRIPLET + "/bin/" + CcConstants.TOOLCHAIN_TRIPLET + "-gdb";
			return new Path(gdb);
		} catch (CoreException e) {
			CcPlugin.log(e);
			return super.getGDBPath();
		}
	}
}
