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
* File Name : BuildConsole.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.console;

import org.eclipse.ui.console.MessageConsole;

import com.chipcraftic.eplugin.ui.CcUIPlugin;

public class BuildConsole extends MessageConsole implements ProcessConsole {
	
	private final ProcessConsoleContext context = new ProcessConsoleContext();
	
	public BuildConsole() {
		super("CC Build Console", CcUIPlugin.getImageDescriptor("icons/make_all.gif"));
	}
	
	@Override
	public ProcessConsoleContext getContext() {
		return context;
	}

}
