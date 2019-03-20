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
* File Name : ProcessConsoleContext.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.console;

import java.util.HashSet;
import java.util.Set;

public class ProcessConsoleContext implements ProcessConsoleListener {
	
	private Set<ProcessConsoleListener> listeners = new HashSet<ProcessConsoleListener>();
	private Process process;
	
	public void addListener(ProcessConsoleListener listener) {
		listeners.add(listener);
	}

	@Override
	public void onProcessStart() {
		for (ProcessConsoleListener listener : listeners) {
			listener.onProcessStart();
		}
	}

	@Override
	public void onProcessExit() {
		for (ProcessConsoleListener listener : listeners) {
			listener.onProcessExit();
		}
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	
}
