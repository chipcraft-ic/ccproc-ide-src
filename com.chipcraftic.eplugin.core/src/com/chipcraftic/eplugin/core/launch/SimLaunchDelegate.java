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
* File Name : SimLaunchDelegate.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.core.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.utils.pty.PTY;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.osgi.service.environment.Constants;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.CcUtils;

public class SimLaunchDelegate extends AbstractCcLaunchDelegate {
	
	public static final String CONFIG_TYPE_ID = "com.chipcraftic.eplugin.core.launch.simConfigurationType";
	
	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		IProject project = (IProject) config.getMappedResources()[0];
		launchSim(project, launch, mode);
		
		super.launch(config, mode, launch, monitor);
		
	}
	
	private IProcess launchSim(IProject project, ILaunch launch, String mode) throws CoreException {
		// Build simulator path
		String simPath = getSimulatorPath(project);
		CcPlugin.logInfo("Starting simulator: " + simPath);
		if (!new File(simPath).exists()) {
			throw new CoreException(new Status(Status.ERROR, CcPlugin.PLUGIN_ID, "CCSDK simulator not found: " + simPath));
		}
		
		// Build SREC path
		String programPath = ((GdbLaunch)launch).getProgramPath();
		String srecPath = new Path(programPath).removeFileExtension().addFileExtension("srec").toOSString();
		if (!new File(srecPath).exists()) {
			throw new CoreException(new Status(Status.ERROR, CcPlugin.PLUGIN_ID, "SREC file not found: " + srecPath));
		}
		
		try {
			List<String> cmdLineList = new ArrayList<>(Arrays.asList(simPath, srecPath));
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				String debugPipe = getDebugPipeName();
				cleanDebugPipe(debugPipe);
				cmdLineList.add("-d");
				cmdLineList.add(debugPipe);
			}
			
			String[] cmdLine = cmdLineList.toArray(new String[cmdLineList.size()]);
			String[] environ = null;
			File workingDirectory = project.getLocation().toFile();
			Process process;
			if (PTY.isSupported()) {
				process = ProcessFactory.getFactory().exec(cmdLine, environ, workingDirectory, new PTY());
			} else {
				// Note: no process output is visible without PTY
				process = ProcessFactory.getFactory().exec(cmdLine, environ, workingDirectory);
			}
			final String simProcessLabel = "ccsim";
			return DebugPlugin.newProcess(launch, process, simProcessLabel);
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, CcPlugin.PLUGIN_ID, "Failed to start CCSDK simulator", e));
		}
	}
	
	private String getSimulatorPath(IProject project) {
		String sdkLocation = CcUtils.getSdkLocation(project);
		if (Platform.getOS().equals(Constants.OS_WIN32)) {
			return new Path(sdkLocation + "/tools/ccsim.exe").toOSString();
		} else {
			return new Path(sdkLocation + "/tools/ccsim").toOSString();
		}
	}
	
	public static String getDebugPipeName() {
		if (Platform.getOS().equals(Constants.OS_WIN32)) {
			return "ccsim-dbg-pipe";
		} else {
			return "/tmp/ccsim-dbg-pipe-" + System.getProperty("user.name");
		}
	}
	
	private void cleanDebugPipe(String pipeName) {
		if (!Platform.getOS().equals(Constants.OS_WIN32)) {
			System.out.println("removing " + pipeName);
			File inPipe = new File(pipeName + ".in");
			if (inPipe.exists()) {
				inPipe.delete();
			}
			File outPipe = new File(pipeName + ".out");
			if (outPipe.exists()) {
				outPipe.delete();
			}
		}
	}
}
