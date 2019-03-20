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
* File Name : AbstractStopAction.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.chipcraftic.eplugin.ui.console.ProcessConsoleContext;
import com.chipcraftic.eplugin.ui.console.ProcessConsoleListener;

public abstract class AbstractStopAction extends Action implements ProcessConsoleListener {

	private ProcessConsoleContext consoleContext;
	
	public AbstractStopAction(String text, ImageDescriptor image, ProcessConsoleContext consoleContext) {
		super(text, image);
		this.consoleContext = consoleContext;
	}
	
	@Override
	public void run() {
		Process process = consoleContext.getProcess();
		if (process != null) {
			// Note: it doesn't kill children so won't work properly for make process
			process.destroy();
		}
	}
	
	@Override
	public void onProcessStart() {
		setEnabled(true);
	}

	@Override
	public void onProcessExit() {
		setEnabled(false);
	}

}
