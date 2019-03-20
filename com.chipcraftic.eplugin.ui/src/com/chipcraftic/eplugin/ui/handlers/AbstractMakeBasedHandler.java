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
* File Name : AbstractMakeBasedHandler.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import com.chipcraftic.eplugin.core.CcConstants;
import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.CcUtils;
import com.chipcraftic.eplugin.ui.console.ProcessConsole;
import com.chipcraftic.eplugin.ui.console.ProcessConsoleContext;
import com.chipcraftic.eplugin.ui.utils.ProcessInputRunnable;
import com.chipcraftic.eplugin.ui.utils.ProcessOutputRunnable;
import com.chipcraftic.eplugin.ui.utils.ProcessStatusRunnable;
import com.chipcraftic.eplugin.ui.utils.StreamUtils;

public abstract class AbstractMakeBasedHandler extends AbstractProjectBasedHandler {

	public static final RGB ERROR_COLOR = new RGB(255, 0, 0);
	public static final RGB STATUS_COLOR = new RGB(128, 128, 128);
	
	protected abstract String getMakeTarget();
	
	protected abstract IOConsole createConsole();
	
	protected abstract boolean isConsoleApplicable(IConsole console);
	
	protected boolean isReadOnly() {
		return true;
	}
	
	protected IOConsole findOrCreateConsole() {
		IConsoleManager consoleMgr = ConsolePlugin.getDefault().getConsoleManager();
		
		for (IConsole console : consoleMgr.getConsoles()) {
			if (isConsoleApplicable(console)) {
				return (IOConsole) console;
			}
		}

		IOConsole console = createConsole();
		consoleMgr.addConsoles(new IConsole[] { console });
		return console;
	}
	
	protected IOConsole getConsole() {
		IConsoleManager consoleMgr = ConsolePlugin.getDefault().getConsoleManager();
		IOConsole console = findOrCreateConsole();
		console.clearConsole();
		consoleMgr.showConsoleView(console);
		console.activate();
		return console;
	}
	
	protected void activateConsole(IOConsole console) {
		IConsoleManager consoleMgr = ConsolePlugin.getDefault().getConsoleManager();
		consoleMgr.showConsoleView(console);
		console.activate();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = getActiveProject(event, true);
		if (project == null) {
			return null;
		}
		System.out.println("Project path: " + project.getLocation().toOSString());
		executeCommand(project);
		return null;
	}

	private void setupEnvVars(Map<String, String> env, IProject project) {
	    IEnvironmentVariableManager envManager = CCorePlugin.getDefault().getBuildEnvironmentManager(); 
	    ICProjectDescription projDesc = CCorePlugin.getDefault().getProjectDescription(project);
	    ICConfigurationDescription configDesc = projDesc.getActiveConfiguration();
		IEnvironmentVariable[] variables = envManager.getContributedEnvironment().getVariables(configDesc);
	    for (IEnvironmentVariable variable : Arrays.asList(variables)) {
	    	env.put(variable.getName(), variable.getValue());
	    }
	}
	
	private ProcessBuilder prepareProcessBuilder(IProject project) {
		String sdkLocation = CcUtils.getSdkLocation(project);
		String makePath = new Path(sdkLocation + "/toolchain/" + CcConstants.TOOLCHAIN_TRIPLET + "/bin/make").toOSString();
		ProcessBuilder builder = new ProcessBuilder(makePath, getMakeTarget());
		builder.directory(project.getLocation().toFile());
		
		Map<String, String> env = builder.environment();
		setupEnvVars(env, project);
		return builder;
	}
	
	private void printProcessBuilderCommand(ProcessBuilder builder, IOConsoleOutputStream consoleStream) throws IOException {
		for (String cmd : builder.command()) {
			consoleStream.write(cmd + " ");
		}
		 // new line
		consoleStream.write("\n");
	}
	
	private void executeCommand(IProject project) {
		final IOConsole console = findOrCreateConsole();
		
		ProcessConsoleContext consoleContext = null;
		if (console instanceof ProcessConsole) {
			consoleContext = ((ProcessConsole)console).getContext();
			if (consoleContext.getProcess() != null) {
				System.out.println("Process is already running");
				return;
			}
		}
		
		console.clearConsole();
		activateConsole(console);
		
		final IOConsoleOutputStream outputConsoleStream = console.newOutputStream();
		final IOConsoleOutputStream errorConsoleStream = console.newOutputStream();
		final IOConsoleOutputStream statusConsoleStream = console.newOutputStream();
		
		Color statusColor = new Color(Display.getDefault(), STATUS_COLOR);
		statusConsoleStream.setColor(statusColor);
		Color errorColor = new Color(Display.getDefault(), ERROR_COLOR);
		errorConsoleStream.setColor(errorColor);
		
		final ProcessBuilder builder = prepareProcessBuilder(project);
		try {
			statusConsoleStream.write("Running: ");
			printProcessBuilderCommand(builder, statusConsoleStream);
		} catch (IOException e) {
			CcPlugin.log(e);
			return;
		}
		
		final Process process;
		try {
			process = builder.start();
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(new PrintWriter(errorConsoleStream));
			StreamUtils.closeStream(outputConsoleStream);
			StreamUtils.closeStream(errorConsoleStream);
			StreamUtils.closeStream(statusConsoleStream);
			return;
		}
		
		if (consoleContext != null) {
			consoleContext.setProcess(process);
			consoleContext.onProcessStart();
		}
		
		new Thread(new ProcessOutputRunnable(outputConsoleStream, process.getInputStream())).start();
		new Thread(new ProcessOutputRunnable(errorConsoleStream, process.getErrorStream())).start();
		new Thread(new ProcessStatusRunnable(process, statusConsoleStream, consoleContext)).start();
		
		if (!isReadOnly()) {
			new Thread(new ProcessInputRunnable(console.getInputStream(), process)).start();
		}
	}

}
