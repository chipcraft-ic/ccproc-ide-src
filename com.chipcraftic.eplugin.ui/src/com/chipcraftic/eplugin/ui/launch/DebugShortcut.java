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
* File Name : DebugShortcut.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.launch;

import org.eclipse.core.resources.IProject;

import com.chipcraftic.eplugin.core.launch.CcHwLaunchDelegate;

/**
 * Based on CApplicationLaunchShortcut.
 * @author Rafał Harabień
 *
 */
public class DebugShortcut extends AbstractCcLaunchShortcut {

	private static final String LAUNCH_NAME_SUFFIX = "-debug";

	@Override
	protected String getLaunchConfigurationTypeId() {
		return CcHwLaunchDelegate.CONFIG_TYPE_ID;
	}

	@Override
	protected String getLaunchConfigurationNamePrefix(IProject project) {
		return project.getName() + LAUNCH_NAME_SUFFIX;
	}
}
