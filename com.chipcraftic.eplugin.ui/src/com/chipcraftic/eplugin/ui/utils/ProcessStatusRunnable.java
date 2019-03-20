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
* File Name : ProcessStatusRunnable.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsoleOutputStream;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.ui.console.ProcessConsoleContext;
import com.chipcraftic.eplugin.ui.handlers.AbstractMakeBasedHandler;

public class ProcessStatusRunnable implements Runnable {

	private final Process process;
	private final IOConsoleOutputStream statusStream;
	private final ProcessConsoleContext consoleContext;
	
	public ProcessStatusRunnable(Process process, IOConsoleOutputStream statusStream, ProcessConsoleContext consoleContext) {

		this.process = process;
		this.statusStream = statusStream;
		this.consoleContext = consoleContext;
	}
	
	@Override
	public void run() {
		try {
			int exitCode = process.waitFor();
			if (exitCode < 0) {
				Color color = new Color(Display.getDefault(), AbstractMakeBasedHandler.ERROR_COLOR);
				statusStream.setColor(color);
				statusStream.write("Process exited with status: " + exitCode + "\n");
			} else {
				statusStream.write("Process exited with status: " + exitCode + "\n");
			}
		} catch (Exception e) {
			CcPlugin.log(e);
		}
		StreamUtils.closeStream(statusStream);
		if (consoleContext != null) {
			consoleContext.onProcessExit();
			consoleContext.setProcess(null);
		}
	}

}
