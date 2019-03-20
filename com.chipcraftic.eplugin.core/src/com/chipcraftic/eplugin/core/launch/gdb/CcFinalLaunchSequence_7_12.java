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
* File Name : CcFinalLaunchSequence_7_12.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.core.launch.gdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.parser.util.StringUtil;
import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence_7_12;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.internal.core.DebugStringVariableSubstitutor;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.IGDBProcesses;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.MIProcesses;
import org.eclipse.cdt.dsf.mi.service.command.commands.CLICommand;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.CcUtils;
import com.chipcraftic.eplugin.core.board.BoardInfo;
import com.chipcraftic.eplugin.core.board.BoardInfoResolver;
import com.chipcraftic.eplugin.core.launch.CcLaunchAttributes;
import com.chipcraftic.eplugin.core.launch.SimLaunchDelegate;

public class CcFinalLaunchSequence_7_12 extends GDBJtagDSFFinalLaunchSequence_7_12 {

	private static final String LINESEP = System.getProperty("line.separator");
	
	private IGDBControl fCommandControl;
	private DsfServicesTracker fTracker;
	private IGDBProcesses fProcService;
	private ILaunchConfiguration fLaunchConfig;
	
	public CcFinalLaunchSequence_7_12(DsfSession session, Map<String, Object> attributes,
			ILaunchConfiguration launchConfig, RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
		fLaunchConfig = launchConfig;
	}
	
	@Override
	protected String[] getExecutionOrder(String group) {
		if (GROUP_JTAG.equals(group)) {
			// Initialize the list with the base class' steps
			// We need to create a list that we can modify, which is why we create our own ArrayList.
			List<String> orderList = new ArrayList<String>(Arrays.asList(super.getExecutionOrder(group)));

			// First, remove all steps of the base class that we don't want to use.
			orderList.removeAll(Arrays.asList(new String[] { 
					"stepConnectToTarget",   //$NON-NLS-1$
					"stepResetBoard",   //$NON-NLS-1$
					"stepDelayStartup",   //$NON-NLS-1$
					"stepHaltBoard",   //$NON-NLS-1$
			}));

			// Now insert our steps before the data model initialized event is sent
			// Note: stepResetDelayHalt is after stepLoadImage because programming requires MCU reset
			orderList.add(0, "stepCcInit");
			orderList.add(orderList.indexOf("stepLoadSymbols") + 1, "stepConnectToCcTarget");
			orderList.add(orderList.indexOf("stepLoadImage") + 1, "stepResetDelayHalt");
			orderList.add(orderList.indexOf("stepInitializeMemory") + 1, "stepSetupMemoryRegions");
			
			return orderList.toArray(new String[orderList.size()]);
		}
		
		// For any subgroups of the base class
		return super.getExecutionOrder(group);
	}

	@Execute
	public void stepCcInit(final RequestMonitor rm) {
		fTracker = new DsfServicesTracker(GdbPlugin.getBundleContext(), getSession().getId());
		fProcService = fTracker.getService(IGDBProcesses.class);
		fCommandControl = fTracker.getService(IGDBControl.class);
		// When we are starting to debug a new process, the container is the default process used by GDB.
        // We don't have a pid yet, so we can simply create the container with the UNIQUE_GROUP_ID
        setContainerContext(fProcService.createContainerContextFromGroupId(fCommandControl.getContext(), MIProcesses.UNIQUE_GROUP_ID));
		rm.done();
	}
	
	@Execute
	public void stepConnectToCcTarget(RequestMonitor rm) {
		String dbgServerCmd = getDbgServerCommand();
		System.out.println(dbgServerCmd);
		String targetSelectCmd = "-target-select remote | " + dbgServerCmd;
		queueCommand(targetSelectCmd, rm);
	}
	
	private String getDbgServerCommand() {
		IProject project = getProject();
		String dbgServer = new Path(getSdkLocation() + "/tools/dbgserver.py").toOSString();
		String projectName = (String) getAttributes().get(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME);
		String dbgServerOpts = CDebugUtils.getAttribute(getAttributes(), CcLaunchAttributes.DBG_SERVER_OPTS, "");
		try {
			dbgServerOpts = new DebugStringVariableSubstitutor(projectName).performStringSubstitution(dbgServerOpts);
		} catch (CoreException e) {
			CcPlugin.log(e);
		}
		
		StringBuilder cmdBuilder = new StringBuilder(getPythonCmd())
				.append(" ")
				.append(dbgServer)
				.append(" --pipe ")
				.append(dbgServerOpts);
		if (isSimLaunch()) {
			String debugPipe = "pipe:" + SimLaunchDelegate.getDebugPipeName();
			cmdBuilder
				.append(" -p ")
				.append(debugPipe);
		} else {
			String board = CcUtils.getBoard(project);
			BoardInfo boardInfo = BoardInfoResolver.getBoardInfo(board, project);
			String debugPort = CcUtils.getDebugPort(project);
			cmdBuilder
				.append(" -p ")
				.append(debugPort)
				.append(" -b ")
				.append(boardInfo.getDbgBaudrate())
				.append(" --mcu ")
				.append(boardInfo.getMcu());
		}
		String cmd = cmdBuilder.toString();
		// Escape slashes
		return cmd.replace("\\", "\\\\");
	}
	
	private String getPythonCmd() {
		return CDebugUtils.getAttribute(getAttributes(), CcLaunchAttributes.DBG_SERVER_PYTHON, "python");
	}
	
	private boolean isSimLaunch() {
		String launchConfigTypeId = null;
		try {
			launchConfigTypeId = fLaunchConfig.getType().getIdentifier();
		} catch (CoreException e) {
			CcPlugin.log(e);
		}
		return SimLaunchDelegate.CONFIG_TYPE_ID.equals(launchConfigTypeId);
	}
	
	@Execute
	public void stepSetupMemoryRegions(final RequestMonitor rm) {
		// Mark ROM as Read-Only memory so GDB will use hardware breakpoints instead of software breakpoints
		queueCommand(
				"mem 0 0x20000000 ro" + LINESEP + 
				"mem 0x20000000 0 rw", rm);
	}

	@Execute
	public void stepResetDelayHalt(final RequestMonitor rm) {
		// Override stepResetBoard, stepDelayStartup and stepHaltBoard to add support for 'monitor reset halt'
		// command (when delay is 0)
		
		boolean doReset = CDebugUtils.getAttribute(getAttributes(), IGDBJtagConstants.ATTR_DO_RESET,
				IGDBJtagConstants.DEFAULT_DO_RESET);
		boolean doHalt = CDebugUtils.getAttribute(getAttributes(), IGDBJtagConstants.ATTR_DO_HALT, 
				IGDBJtagConstants.DEFAULT_DO_HALT);
		int delaySec = CDebugUtils.getAttribute(getAttributes(), IGDBJtagConstants.ATTR_DELAY, 0);
		boolean loadImage = CDebugUtils.getAttribute(getAttributes(), IGDBJtagConstants.ATTR_LOAD_IMAGE, 
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE);

		List<String> commands = new ArrayList<>();
		if (doReset && doHalt && delaySec == 0) {
			// When "Load Image" is enabled processor is reset and stops at address 0
			if (!loadImage) {
				commands.add("monitor reset halt");
				commands.add("monitor gdb_sync");
				commands.add("stepi");
			}
		} else {
			if (doReset) {
				int delayMs = delaySec * 1000;
				commands.add("monitor reset run");
				if (delayMs > 0) {
					commands.add("monitor delay " + delayMs);
				}
			}
			if (doHalt) {
				commands.add("monitor halt");
				commands.add("monitor gdb_sync");
				commands.add("stepi");
			}
		}
		
		if (commands.isEmpty()) {
			rm.done();
		} else {
			queueCommand(StringUtil.join(commands, LINESEP), rm);
		}
	}
	
	private IProject getProject() {
		String projectName = CDebugUtils.getAttribute(
				getAttributes(),
				ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		if (projectName.length() == 0) {
			return null;
		}
		return CCorePlugin.getWorkspace().getRoot().getProject(projectName);
	}
	
	private String getSdkLocation() {
		IProject project = getProject();
		return CcUtils.getSdkLocation(project);
	}
	
	/** utility method; cuts down on clutter */
	private void queueCommand(String command, RequestMonitor rm) {
		fCommandControl.queueCommand(
				new CLICommand<MIInfo>(fCommandControl.getContext(), command),
				new DataRequestMonitor<MIInfo>(getExecutor(), rm));
	}
}
