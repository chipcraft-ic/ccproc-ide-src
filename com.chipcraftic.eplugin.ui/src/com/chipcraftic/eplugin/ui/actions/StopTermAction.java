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
* File Name : StopTermAction.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.actions;

import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.ui.CcUIPlugin;
import com.chipcraftic.eplugin.ui.console.ProcessConsoleContext;

public class StopTermAction extends AbstractStopAction {
	
	public StopTermAction(ProcessConsoleContext consoleContext) {
		super("Stop Serial Terminal", getIcon(), consoleContext);
	}
	
	@Override
	public void run() {
		System.out.println("Stopping Serial Terminal");
		try {
			final ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "pkill -f ccterm");
			builder.start();
		} catch (IOException e) {
			CcPlugin.log(e);
		}
	}
	
	protected static ImageDescriptor getIcon() {
		return CcUIPlugin.getImageDescriptor("icons/stop_debug_server.gif");
	}

}
