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
* File Name : CcUtils.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.core;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

public class CcUtils {
	private static String getEnvVar(String name, IProject project) {
		final IEnvironmentVariableManager envMgr = CCorePlugin.getDefault().getBuildEnvironmentManager();
		ICProjectDescription projDesc =
				CoreModel.getDefault().getProjectDescriptionManager().getProjectDescription(project, 0);
		
		try {
			IEnvironmentVariable var = envMgr.getContributedEnvironment().getVariable(name, projDesc.getActiveConfiguration());
			//IEnvironmentVariable var = envMgr.getVariable(name, project.getActiveBuildConfig(), true);
			return var.getValue();
		} catch (Exception e) {
			CcPlugin.log(e);
			return null;
		}
	}
	
	public static String getSdkLocation(IProject project) {
		return getEnvVar(CcConstants.ENV_SDK_HOME, project);
	}
	
	public static String getDebugPort(IProject project) {
		return getEnvVar(CcConstants.ENV_DBG_PORT, project);
	}
	
	public static String getUartPort(IProject project) {
		return getEnvVar(CcConstants.ENV_UART_PORT, project);
	}
	
	public static String getBoard(IProject project) {
		return getEnvVar(CcConstants.ENV_BOARD, project);
	}
	
	public static IProject getProject(ILaunchConfiguration config) throws CoreException {
		return (IProject) config.getMappedResources()[0];
	}
}
